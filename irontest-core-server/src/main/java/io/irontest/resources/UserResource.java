package io.irontest.resources;

import io.irontest.db.UserDAO;
import io.irontest.models.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
}
