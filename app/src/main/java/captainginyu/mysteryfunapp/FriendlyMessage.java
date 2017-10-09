package captainginyu.mysteryfunapp;

/**
 * Created by Kevin on 10/8/2017.
 */

public class FriendlyMessage {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private Object timestamp;

    public FriendlyMessage() {

    }

    public FriendlyMessage(String text, String name, String photoUrl, Object timestamp) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
