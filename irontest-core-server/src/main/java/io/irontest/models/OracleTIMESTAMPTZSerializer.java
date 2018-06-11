package io.irontest.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Jackson does not have a built-in serializer for oracle.sql.TIMESTAMPTZ.
 */
public class OracleTIMESTAMPTZSerializer extends JsonSerializer {
    private Class clazz;

    public OracleTIMESTAMPTZSerializer(Class clazz) {
        this.clazz = clazz;
    }

    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        String result = null;
        Method method = null;
        try {
            //  TIMESTAMPTZ.toJdbc() always returns null as there is no JDBC representation for Oracle TIMESTAMPTZ
            method = clazz.getDeclaredMethod("stringValue", Connection.class);
            result = (String) method.invoke(value, new Object[] { null });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        gen.writeString(result);
    }
}
