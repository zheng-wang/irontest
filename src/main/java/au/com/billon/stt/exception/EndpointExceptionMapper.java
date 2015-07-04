package au.com.billon.stt.exception;

import org.skife.jdbi.v2.exceptions.DBIException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Iterates through a DBIException's cause if it's a SQLException otherwise log as normal.
 */
@Provider
public class EndpointExceptionMapper implements ExceptionMapper<DBIException> {

    public Response toResponse(DBIException exception) {
        if (exception.getMessage().indexOf("Unique index or primary key violation") > -1) {
            return Response.status(Response.Status.CONFLICT).type("text/plain").entity("The name is already taken").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("The request is not valid").build();
    }

}
