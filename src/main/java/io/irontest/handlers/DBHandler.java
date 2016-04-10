package io.irontest.handlers;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.exceptions.NoResultsException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class DBHandler implements IronTestHandler {
    public DBHandler() { }

    public Object invoke(String request, Map<String, String> details) throws Exception {
        DBHandlerResponse response = new DBHandlerResponse();
        DBI jdbi = new DBI(details.get("url"), details.get("username"), details.get("password"));
        Handle handle = jdbi.open();

        //  assume the request SQL is an insert/update/delete statement first
        Update update = handle.createStatement(request);
        int numberOfRowsModified = update.execute();
        response.setNumberOfRowsModified(numberOfRowsModified);

        if (numberOfRowsModified == -1) {    // the request SQL is a select statement
            Query<Map<String, Object>> query = handle.createQuery(request);
            List<Map<String, Object>> resultSet = query.list();
            response.setResultSet(resultSet);
        }

        handle.close();

        // ObjectMapper mapper = new ObjectMapper();
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // StringWriter responseWriter = new StringWriter();
        // mapper.writeValue(responseWriter, results);

        return response;
    }

    public List<String> getProperties() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
