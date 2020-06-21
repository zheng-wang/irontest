package io.irontest;

import java.text.SimpleDateFormat;

public interface IronTestConstants {
    String IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME = "Test_Case_Start_Time";
    String IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME = "Test_Case_Individual_Start_Time";
    String IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME = "Test_Step_Start_Time";
    SimpleDateFormat IMPLICIT_PROPERTY_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    String DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX = "UNIQUE_NAME_CONSTRAINT";
    String DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX = "PROPERTY_NAME_CONSTRAINT";
    String DB_USERNAME_CONSTRAINT_NAME_SUFFIX = "USERNAME_CONSTRAINT";
    String CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_NOTIN = "NOT IN ('" +
            IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME + "', '" +
            IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME + "', '" +
            IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME + "')";
    String CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_REGEXP = "'^[a-zA-Z_$][a-zA-Z_$0-9]*$'";
    String CUSTOM_PROPERTY_NAME_CHECK = "name " + CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_NOTIN +
            " AND REGEXP_LIKE(name, " + CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_REGEXP + ")";
    String CUSTOM_PROPERTY_NAME_CHECK2 = "property_name " + CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_NOTIN +
            " AND REGEXP_LIKE(property_name, " + CUSTOM_PROPERTY_NAME_CHECK_COMMON_FRAG_REGEXP + ")";

    String ENDPOINT_PASSWORD_ENCRYPTION_KEY = "8888";

    String SYSADMIN_USER = "sysadmin";
    String USER_DEFAULT_PASSWORD = "password";
    int PASSWORD_SALT_LENGTH_IN_BYTES = 32;
    String DEFAULT_PASSWORD_HASHING_ALGORITHM = "PBKDF2WithHmacSHA512";
    int DEFAULT_KDF_ITERATIONS = 10000;
    int KDF_KEY_LENGTH = 256;
    String USER_ROLE_ADMIN = "admin";

    String WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID = "ironTestId";
    String WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_NUMBER = "ironTestNumber";

    String PROMPT_TEXT_WHEN_SYSTEM_DB_VERSION_IS_BIGGER_THAN_JAR_VERSION =
            "The system database version %1$s is bigger than the jar file version %2$s. Please%n" +
            "  download and build the latest version of Iron Test,%n" +
            "  copy the jar file from the dist folder to your current <IronTest_Home> directory, and%n" +
            "  start the new version of Iron Test in your current <IronTest_Home> (like by running the start.bat).";
}
