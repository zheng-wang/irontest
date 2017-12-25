package io.irontest.resources;

import io.irontest.db.UserDAO;
import io.irontest.models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 24/12/2017.
 */
@Path("/users") @Produces({ MediaType.APPLICATION_JSON })
public class UserResource {
    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @GET @Path("{userId}")
    public User findById(@PathParam("userId") long userId) {
        return userDAO.findById(userId);
    }

    @POST
    public User create(User user) {
        return userDAO.insert(user.getUsername());
    }

    @DELETE @Path("{userId}")
    public void delete(@PathParam("userId") long userId) {
        userDAO.deleteById(userId);
    }
}
