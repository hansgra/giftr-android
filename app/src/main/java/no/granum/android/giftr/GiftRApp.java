package no.granum.android.giftr;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import no.granum.android.giftr.models.WishList;
import no.granum.android.giftr.models.WishListItem;

public class GiftRApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(WishList.class);
        ParseObject.registerSubclass(WishListItem.class);
        Parse.initialize(this, "h1xSkmR6Q3ju8niYhtTcEQp35hJzFMrJIZvovCeX", "175EQT8Uz3FZdkOXMWjkLLDZRIH8dN1GJFdYJgtl");

        // Set your Facebook App Id in strings.xml
        ParseFacebookUtils.initialize(getString(R.string.app_id));
    }
}
