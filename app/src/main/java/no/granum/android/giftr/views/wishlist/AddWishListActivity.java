package no.granum.android.giftr.views.wishlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import no.granum.android.giftr.R;

public class AddWishListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_wish_list, menu);
        return true;
    }

    public void addList(View view) {

        EditText nameText = (EditText) findViewById(R.id.list_name);

        ParseObject wishList = new ParseObject("WishList");
        wishList.put("name", nameText.getText().toString());
        wishList.put("owner", ParseUser.getCurrentUser());

        try {
            wishList.save();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ViewWishListsActivity.class);
        startActivity(intent);
    }

}
