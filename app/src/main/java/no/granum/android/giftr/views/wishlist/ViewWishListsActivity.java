package no.granum.android.giftr.views.wishlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import no.granum.android.giftr.R;
import no.granum.android.giftr.views.MainActivity;
import no.granum.android.giftr.views.wishlistitem.ViewWishListItemsActivity;

public class ViewWishListsActivity extends Activity {
    private no.granum.android.giftr.services.WishListService listService;
    private ProfilePictureView profilePictureView;
    private TextView profileNameView;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_wishlists);

        ParseLoginBuilder builder = new ParseLoginBuilder(ViewWishListsActivity.this);
        startActivityForResult(builder.build(), 0);

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        profileNameView = (TextView) findViewById(R.id.profileName);


        // Fetch Facebook user info if the session is active
/*
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        }
*/

        final ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ParseObject wishList = (ParseObject) parent.getAdapter().getItem(position);

                Intent intent = new Intent(ViewWishListsActivity.this, ViewWishListItemsActivity.class);
                intent.putExtra("list_id", wishList.getObjectId());
                startActivity(intent);

            }
        });

        ParseQueryAdapter<ParseObject> adapter =
                new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {
                        // Here we can configure a ParseQuery to our heart's desire.
                        ParseQuery query = new ParseQuery("WishList");
                        query.whereEqualTo("owner", ParseUser.getCurrentUser());
                        return query;
                    }
                });

        adapter.setTextKey("name");
        //adapter.setImageKey("photo");

        listView.setAdapter(adapter);

        ParseQueryAdapter<ParseObject> adapter =
                new ParseQueryAdapter<ParseObject>(this, getFacebookFriendsInBackground());

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            setupActionBar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newWishList(View view) {
        Intent intent = new Intent(this, AddWishListActivity.class);
        startActivity(intent);
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId")
                            .toString();
                    profilePictureView.setProfileId(facebookId);
                } else {
                    // Show the default, blank user profile picture
                    profilePictureView.setProfileId(null);
                }
                if (userProfile.getString("name") != null) {
                    profileNameView.setText(userProfile.getString("name"));
                }

            } catch (JSONException e) {
                Log.d("HELLO",
                        "Error parsing saved user data.");
            }

        }
    }

    private void onLogoutButtonClicked() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private static void getFacebookIdInBackground() {
        Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    ParseUser.getCurrentUser().put("fbId", user.getId());
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        }).executeAsync();
    }

    private static void getFacebookFriendsInBackground() {
        Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                if (users != null) {
                    List<String> friendsList = new ArrayList<String>();
                    for (GraphUser user : users) {
                        friendsList.add(user.getId());
                    }

                    // Construct a ParseUser query that will find friends whose
                    // facebook IDs are contained in the current user's friend list.
                    ParseQuery friendQuery = ParseQuery.getUserQuery();
                    friendQuery.whereContainedIn("fbId", friendsList);

                    // findObjects will return a list of ParseUsers that are friends with
                    // the current user
                    List<ParseObject> friendUsers = friendQuery.find();
                }
            }
        }).executeAsync();
    }
}
