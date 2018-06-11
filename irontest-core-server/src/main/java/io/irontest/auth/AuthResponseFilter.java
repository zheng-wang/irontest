package io.irontest.auth;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class AuthResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        //  this is to prevent browser native basic auth dialog from popping up on 401 response
        if (responseContext.getStatusInfo() == Response.Status.UNAUTHORIZED) {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.putSingle(HttpHeaders.WWW_AUTHENTICATE,
                    ((String) headers.getFirst(HttpHeaders.WWW_AUTHENTICATE)).replace("Basic", "xBasic"));
        }
    }
}
