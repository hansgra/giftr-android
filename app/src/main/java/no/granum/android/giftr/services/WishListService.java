package no.granum.android.giftr.services;

import com.parse.ParseException;
import com.parse.ParseUser;

import no.granum.android.giftr.models.WishList;

public class WishListService {
    private static WishListService wishListService;

    private WishListService() {
    }

    public static WishListService GetInstance() {
        if (wishListService == null) {
            wishListService = new WishListService();
        }

        return wishListService;
    }

    public void AddWishList(String name) {
        WishList wishList = new WishList();
        wishList.setName(name);
        wishList.setOwner(ParseUser.getCurrentUser());
        try {
            wishList.save();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
