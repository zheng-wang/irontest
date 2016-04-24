package io.irontest;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Zheng on 24/04/2016.
 */
public class IronTestLoggingExceptionMapper extends LoggingExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IronTestLoggingExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        long id = logException(exception);
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        ErrorMessage errorMessage = new ErrorMessage(status, formatErrorMessage(id, exception), exception.getMessage());
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorMessage)
                .build();
    }

    @Override
    protected void logException(long id, Throwable exception) {
        LOGGER.error(formatLogMessage(id, exception), exception);
    }
}
