package io.irontest;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import org.h2.api.ErrorCode;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 24/04/2016.
 */
public class IronTestLoggingExceptionMapper extends LoggingExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IronTestLoggingExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        long id = logException(exception);
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String errorDetails = exception.getMessage();

        //  change error details if the exception is DB unique-name-constraint-violation
        if (exception instanceof UnableToExecuteStatementException) {
            SQLException se = (SQLException) exception.getCause();
            if (se.getErrorCode() == ErrorCode.DUPLICATE_KEY_1 &&
                    se.getMessage().contains("_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX)) {
                errorDetails = "Duplicate name.";
            }
        }

        ErrorMessage errorMessage = new ErrorMessage(status, formatErrorMessage(id, exception), errorDetails);
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
