package io.irontest.resources;

import io.irontest.models.AppInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 3/12/2017.
 */
@Path("/appinfo") @Produces({ MediaType.APPLICATION_JSON })
public class AppInfoResource {
    private AppInfo appMode;

    public AppInfoResource(AppInfo appMode) {
        this.appMode = appMode;
    }

    @GET
    public AppInfo get() {
        return appMode;
    }
}
