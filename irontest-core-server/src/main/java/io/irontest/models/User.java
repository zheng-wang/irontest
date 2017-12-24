package io.irontest.models;

/**
 * Created by Zheng on 2/12/2017.
 */
public class User {
    private String username;
    private String password;
    private String salt;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }
}
