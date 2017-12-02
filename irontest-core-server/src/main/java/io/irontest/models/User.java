package io.irontest.models;

import java.security.Principal;

/**
 * Created by Zheng on 2/12/2017.
 */
public class User implements Principal {
    private final String name;

//    private final Set<String> roles;

    public User(String name) {
        this.name = name;
//        this.roles = null;
    }

//    public User(String name, Set<String> roles) {
//        this.name = name;
//        this.roles = roles;
//    }

    public String getName() {
        return name;
    }

//    public int getId() {
//        return (int) (Math.random() * 100);
//    }
//
//    public Set<String> getRoles() {
//        return roles;
//    }
}
