package io.irontest.handlers;

import io.irontest.models.Endpoint;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class DBHandler implements IronTestHandler {
    public DBHandler() { }

    public Object invoke(String request, Endpoint endpoint) throws Exception {
        DBHandlerResponse response = new DBHandlerResponse();
        DBI jdbi = new DBI(endpoint.getUrl(), endpoint.getUsername(), endpoint.getPassword());
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

        return response;
    }
}
