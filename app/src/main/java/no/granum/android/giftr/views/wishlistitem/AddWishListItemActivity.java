package no.granum.android.giftr.views.wishlistitem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import no.granum.android.giftr.R;
import no.granum.android.giftr.models.WishListItem;

public class AddWishListItemActivity extends FragmentActivity {
    private String listId = "";
    private WishListItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = new WishListItem();

        // Get List ID from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listId = extras.getString("list_id");
        }

        item.setList(listId);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish_list_item);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new NewWishListItemFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    public WishListItem getCurrentItem() {
        return item;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_wish_list, menu);
        return true;
    }

}
