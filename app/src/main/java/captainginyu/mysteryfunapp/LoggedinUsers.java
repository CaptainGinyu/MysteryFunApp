package captainginyu.mysteryfunapp;

/**
 * Created by Kevin on 10/9/2017.
 */

public class LoggedinUsers {

    private String id;
    private String email;
    private String name;

    public LoggedinUsers(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
