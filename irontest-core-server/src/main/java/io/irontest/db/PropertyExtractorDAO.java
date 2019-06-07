package io.irontest.db;

import io.irontest.models.teststep.PropertyExtractor;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

import static io.irontest.IronTestConstants.*;

@RegisterRowMapper(PropertyExtractorMapper.class)
public interface PropertyExtractorDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS property_extractor_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS property_extractor (" +
            "id BIGINT DEFAULT property_extractor_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT NOT NULL, " +
            "property_name VARCHAR(200) NOT NULL, type VARCHAR(50) NOT NULL, path VARCHAR(1000)," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT PROPERTY_EXTRACTOR_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, property_name), " +
            "CONSTRAINT PROPERTY_EXTRACTOR_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" + CUSTOM_PROPERTY_NAME_CHECK2 + "))")
    void createTableIfNotExists();

    @SqlQuery("select * from property_extractor where id = :id")
    PropertyExtractor findById(@Bind("id") long id);

    @SqlUpdate("insert into property_extractor (teststep_id, property_name, type, path) values " +
            "(:teststepId, :p.propertyName, :p.type, :p.path)")
    @GetGeneratedKeys
    long insert(@Bind("teststepId") long teststepId, @BindBean("p") PropertyExtractor propertyExtractor);

    @SqlQuery("select * from property_extractor where teststep_id = :teststepId")
    List<PropertyExtractor> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlUpdate("update property_extractor set property_name = :propertyName, path = :path, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    void update(@BindBean PropertyExtractor propertyExtractor);

    @SqlUpdate("delete from property_extractor where id = :id")
    void deleteById(@Bind("id") long id);
}
