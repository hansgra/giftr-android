package no.granum.android.giftr.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("WishListItem")
public class WishListItem extends ParseObject {

    /**
     * @return the description
     */
    public String getDescription() {
        return getString("description");
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        put("description", description);
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return getString("uri");
    }

    /**
     * @param url the uri to set
     */
    public void setUri(String uri) {
        put("uri", uri);
    }

    /**
     * @return the imgUri
     */
    public ParseFile getImage() {
        return getParseFile("image");
    }

    /**
     * @param imgUri the imgUri to set
     */
    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setList(String listId) {
        put("list", ParseObject.createWithoutData("WishList", listId));
    }
}
