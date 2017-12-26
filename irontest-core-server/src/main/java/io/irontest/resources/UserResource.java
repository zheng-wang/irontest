package io.irontest.resources;

import io.irontest.IronTestConstants;
import io.irontest.db.UserDAO;
import io.irontest.models.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Notice that class level @RolesAllowed is supported since Dropwizard 1.0.
 * Created by Zheng on 24/12/2017.
 */
@Path("/users") @Produces({ MediaType.APPLICATION_JSON })
public class UserResource {
    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @RolesAllowed(IronTestConstants.USER_ROLE_ADMIN)
    public List<User> findAll() {
        return userDAO.findAll();
    }

    /*@GET @Path("{userId}")
    public User findById(@PathParam("userId") long userId) {
        return userDAO.findById(userId);
    }*/

    @POST
    @RolesAllowed(IronTestConstants.USER_ROLE_ADMIN)
    public User create(User user) {
        return userDAO.insert(user.getUsername());
    }

    @DELETE @Path("{userId}")
    @RolesAllowed(IronTestConstants.USER_ROLE_ADMIN)
    public void delete(@PathParam("userId") long userId) {
        User user = userDAO.findById(userId);
        if (user != null && IronTestConstants.SYSADMIN_USER.equals(user.getUsername())) {
            throw new RuntimeException("Can not delete " + IronTestConstants.SYSADMIN_USER);
        }

        userDAO.deleteById(userId);
    }
}
