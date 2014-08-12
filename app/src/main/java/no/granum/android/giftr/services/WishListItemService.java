package no.granum.android.giftr.services;

import com.parse.ParseException;
import com.parse.ParseObject;

import no.granum.android.giftr.models.WishListItem;

public class WishListItemService {
    private static WishListItemService wishListItemService;

    private WishListItemService() {
    }

    public static WishListItemService GetInstance() {
        if (wishListItemService == null) {
            wishListItemService = new WishListItemService();
        }

        return wishListItemService;
    }

    public void AddWishListItem(String listId, WishListItem item) {
        item.put("list", ParseObject.createWithoutData("WishList", listId));

        try {
            item.save();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddWishListItem(String listId, String description, String url) {
        WishListItem item = new WishListItem();
        item.setDescription(description);
        item.setUri(url);
        AddWishListItem(listId, item);
    }
}
