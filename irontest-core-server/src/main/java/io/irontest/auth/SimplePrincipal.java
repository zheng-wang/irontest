package io.irontest.auth;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class SimplePrincipal implements Principal {
    private final String name;

    private final Set<String> roles = new HashSet<>();

    public SimplePrincipal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
