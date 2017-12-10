package io.irontest.resources;

import io.irontest.models.AppInfo;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A couple of system wide resource methods.
 *
 * Created by Zheng on 3/12/2017.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class SystemResource {
    private AppInfo appInfo;

    public SystemResource(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @GET @Path("appinfo")
    public AppInfo getAppInfo() {
        return appInfo;
    }

    /**
     * Return HTTP status code 204 if authenticated; 401 otherwise.
     */
    @GET @Path("authenticated")
    @PermitAll
    public void authenticated() {}
}
