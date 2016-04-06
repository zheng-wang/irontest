package io.irontest.exceptions;

import io.dropwizard.jdbi.jersey.LoggingDBIExceptionMapper;
import org.skife.jdbi.v2.exceptions.DBIException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Created by Trevor Li on 7/4/15.
 */
@Provider
public class IronTestDBIExceptionMapper extends LoggingDBIExceptionMapper {

    @Override
    public Response toResponse(DBIException exception) {
        if (exception.getMessage().indexOf("Unique index or primary key violation") > -1) {
            return Response.status(Response.Status.CONFLICT).type("text/plain").entity("The name is already taken").build();
        }
        return super.toResponse(exception);
    }

}
