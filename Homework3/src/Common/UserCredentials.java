package Common;

import java.io.Serializable;

/**
 * Created by Chosrat on 2017-11-28.
 */

//Klass som hanterar användarens attribut
public class UserCredentials implements Serializable {

    private final String username;
    private final String password;
    private boolean loggedin = false;
    private int id;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setStatus(boolean status) {
        this.loggedin = status;
    }

    public boolean getStatus() {
        return loggedin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
