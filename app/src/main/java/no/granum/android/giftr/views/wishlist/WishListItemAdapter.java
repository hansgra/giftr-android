package no.granum.android.giftr.views.wishlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import no.granum.android.giftr.R;
import no.granum.android.giftr.models.WishListItem;

public class WishListItemAdapter extends ParseQueryAdapter<WishListItem> {

    public WishListItemAdapter(Context context, final String listId) {
        super(context, new ParseQueryAdapter.QueryFactory<WishListItem>() {
            @Override
            public ParseQuery<WishListItem> create() {
                // Here we can configure a ParseQuery to our heart's desire.
                ParseQuery query = new ParseQuery("WishListItem");
                query.whereEqualTo("list",
                        ParseObject.createWithoutData("WishList", listId));
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(WishListItem item, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.wish_list_item_adapter,
                    null);
        }

        super.getItemView(item, v, parent);

        final DynamicHeightImageView image = (DynamicHeightImageView)v.findViewById(R.id.imgView);
        final ViewSwitcher switcher = (ViewSwitcher)v.findViewById(R.id.switcher);

        // Add and download the image
        ParseFile imageFile = item.getImage();
        if (imageFile != null) {
            try {
                imageFile.getDataInBackground(
                        new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                switcher.showNext();
                            }
                        }
                );
            } catch (Exception e) {

            }
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.text1);
        titleTextView.setText(item.getDescription());

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
        timestampView.setText(item.getCreatedAt().toString());

        return v;
    }
}