package io.irontest;

import com.google.common.base.Optional;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentials;
import io.irontest.db.UserDAO;
import io.irontest.models.User;
import io.irontest.utils.PasswordUtils;

/**
 * Created by Zheng on 2/12/2017.
 */
public class IronTestResourceAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {
    private UserDAO userDAO;

    public IronTestResourceAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) {
        User user = userDAO.findByUsername(credentials.getUsername());
        if (user != null && user.getPassword().equals(
                PasswordUtils.hashPassword(credentials.getPassword(), user.getSalt()))) {
            return Optional.of(new PrincipalImpl(credentials.getUsername()));
        }
        return Optional.absent();
    }
}