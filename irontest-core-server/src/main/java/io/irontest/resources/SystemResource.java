package io.irontest.resources;

import io.irontest.models.AppInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A couple of system wide resource methods.
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
}