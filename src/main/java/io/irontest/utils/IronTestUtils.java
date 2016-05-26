package io.irontest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.ContainsAssertionProperties;
import io.irontest.models.assertion.DSFieldAssertionProperties;
import io.irontest.models.assertion.XPathAssertionProperties;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Zheng on 12/07/2015.
 */
public class IronTestUtils {
    public static Class getAssertionPropertiesClassByType(String assertionType) {
        if (Assertion.ASSERTION_TYPE_CONTAINS.equals(assertionType)) {
            return ContainsAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_XPATH.equals(assertionType)) {
            return XPathAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_DSFIELD.equals(assertionType)) {
            return DSFieldAssertionProperties.class;
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }
    }

    /**
     * @param rs
     * @return a list of lower case column names present in the result set.
     * @throws SQLException
     */
    public static List<String> getFieldsPresentInResultSet(ResultSet rs) throws SQLException {
        List<String> fieldsPresentInResultSet = new ArrayList<String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for(int index =1; index <= metaData.getColumnCount(); index++) {
            fieldsPresentInResultSet.add(metaData.getColumnLabel(index).toLowerCase());
        }
        return fieldsPresentInResultSet;
    }

    public static String serializeToJSONWithOnlyCertainFields(Object object, String[] fields)
            throws JsonProcessingException {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("myFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(Arrays.asList(fields))));
        return new ObjectMapper().writer(filterProvider).writeValueAsString(object);
    }
}
