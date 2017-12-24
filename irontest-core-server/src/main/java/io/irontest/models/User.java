package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Zheng on 2/12/2017.
 */
public class User {
    private long id;
    private String username;
    private String password;
    @JsonIgnore
    private String salt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
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
