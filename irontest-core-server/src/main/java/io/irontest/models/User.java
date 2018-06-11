package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public class User {
    private long id;
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    private Set<String> roles = new HashSet<String>();

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

    public String getPassword() {
        return password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
