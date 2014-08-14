package no.granum.android.giftr.views.wishlistitem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.etsy.android.grid.StaggeredGridView;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.granum.android.giftr.R;
import no.granum.android.giftr.models.WishListItem;
import no.granum.android.giftr.views.wishlist.WishListItemAdapter;

public class ViewWishListItemsActivity extends Activity {
    private String listId;
    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Get listID from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listId = extras.getString("list_id");
        }

        setContentView(R.layout.activity_view_wishlist_items);

        final StaggeredGridView listView = (StaggeredGridView) findViewById(R.id.grid_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewWishListItemsActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Session session = Session.getActiveSession();

                        if (session != null) {

                            // Check for publish permissions
                            List<String> permissions = session.getPermissions();
                            if (!permissions.contains("publish_actions")) {
                                //pendingPublishReauthorization = true;
                                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                                        ViewWishListItemsActivity.this, (List<String>) Arrays.asList("publish_actions"));
                                session.requestNewPublishPermissions(newPermissionsRequest);
                                return;
                            }
                        }

                        WishListItem item = (WishListItem) listView.getAdapter().getItem(position);
                        Log.d("HELLO!!!!!!", item.getDescription());

                        OpenGraphObject wish = OpenGraphObject.Factory.createForPost("cooking-app:meal");
                        wish.setProperty("title", item.getDescription());
                        //meal.setProperty("image", "http://example.com/cooking-app/images/buffalo-tacos.png");
                        //wish.setProperty("url", "giftr.com");
                        //meal.setProperty("description", "Leaner than beef and great flavor.");

                        List<Bitmap> images = new ArrayList<Bitmap>();
                        try {
                            ParseFile file = item.getImage();

                            if (file != null) {
                                byte[] bytes = file.getData();
                                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                image = Bitmap.createBitmap(image, 0, 0, 480, 480);
                                images.add(image);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
                        action.setProperty("wish", wish);
                        action.setType("giftrapp_no:make");

                        FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(ViewWishListItemsActivity.this, action, "wish")
                                .setImageAttachmentsForObject("wish", images, true)
                                .build();

                        uiHelper.trackPendingDialogCall(shareDialog.present());

                        // Do nothing but close the dialog
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        WishListItemAdapter adapter = new WishListItemAdapter(this, listId);
        listView.setAdapter(adapter);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            setupActionBar();
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
        getMenuInflater().inflate(R.menu.add_wish_list_item, menu);
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

            case R.id.action_additem:
                newWishListItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void itemSelected() {

    }

    public void newWishListItem() {
        Intent intent = new Intent(this, AddWishListItemActivity.class);
        intent.putExtra("list_id", listId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("HELLO", "Logged in...");
        } else if (state.isClosed()) {
            Log.i("HELLO", "Logged out...");
        }
    }

}
