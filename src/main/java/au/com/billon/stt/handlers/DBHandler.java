package au.com.billon.stt.handlers;

import au.com.billon.stt.models.Endpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class DBHandler implements STTHandler {
    private Endpoint endpoint;

    public DBHandler(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String invoke(String request) throws Exception {
        DBI jdbi = new DBI(endpoint.getUrl(), endpoint.getUsername(), endpoint.getPassword());
        Handle handle = jdbi.open();

        Query<Map<String, Object>> query = handle.createQuery(request);
        List<Map<String, Object>> results = query.list();

        ObjectMapper mapper = new ObjectMapper();

        StringWriter responseWriter = new StringWriter();

        mapper.writeValue(responseWriter, results);

        handle.close();

        return responseWriter.toString();
    }
}
