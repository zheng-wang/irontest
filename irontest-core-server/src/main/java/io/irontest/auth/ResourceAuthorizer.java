package io.irontest.auth;

import io.dropwizard.auth.Authorizer;

/**
 * Created by Zheng on 25/12/2017.
 */
public class ResourceAuthorizer implements Authorizer<SimplePrincipal> {
    @Override
    public boolean authorize(SimplePrincipal principal, String role) {
        return principal.getRoles().contains(role);
    }
}
