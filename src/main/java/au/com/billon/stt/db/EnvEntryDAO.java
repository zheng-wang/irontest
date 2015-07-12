package au.com.billon.stt.db;

import au.com.billon.stt.models.EnvEntry;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 7/5/15.
 */
@RegisterMapper(EnvEntryMapper.class)
public interface EnvEntryDAO {
    @SqlUpdate("create table IF NOT EXISTS enventry (id INT PRIMARY KEY auto_increment, environmentId int, intfaceId int, endpointId int, " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT cons_enventry_1 unique (environmentId, intfaceId, endpointId), " +
            "FOREIGN KEY (environmentId) REFERENCES environment(id), " +
            "FOREIGN KEY (intfaceId) REFERENCES intface(id), " +
            "FOREIGN KEY (endpointId) REFERENCES endpoint(id))")
    void createTableIfNotExists();

    @SqlUpdate("insert into enventry (environmentId, intfaceId, endpointId) values (:environmentId, :intfaceId, :endpointId)")
    @GetGeneratedKeys
    long insert(@BindBean EnvEntry enventry);

    @SqlUpdate("update enventry set intfaceId = :intfaceId, endpointId = :endpointId, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean EnvEntry enventry);

    @SqlUpdate("delete from enventry where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlUpdate("delete from enventry where environmentId = :environmentId")
    void deleteByEnv(@Bind("environmentId") long environmentId);

    @SqlQuery("select ENVENTRY.*, " +
            "ENVIRONMENT.NAME as environmentname, ENVIRONMENT.description as environmentdesc, " +
            "intface.name as intfacename, intface.description as intfacedesc, " +
            "endpoint.name as endpointname, endpoint.description as endpointdesc " +
            "from ENVENTRY " +
            "left outer join ENVIRONMENT on ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID " +
            "left outer join intface on ENVENTRY.intfaceid = intface.id " +
            "left outer join endpoint on ENVENTRY.endpointid = endpoint.id")
    List<EnvEntry> findAll();

    @SqlQuery("select ENVENTRY.*, " +
            "ENVIRONMENT.NAME as environmentname, ENVIRONMENT.description as environmentdesc, " +
            "intface.name as intfacename, intface.description as intfacedesc, " +
            "endpoint.name as endpointname, endpoint.description as endpointdesc " +
            "from ENVENTRY " +
            "left outer join ENVIRONMENT on ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID " +
            "left outer join intface on ENVENTRY.intfaceid = intface.id " +
            "left outer join endpoint on ENVENTRY.endpointid = endpoint.id " +
            "where ENVENTRY.id = :id")
    EnvEntry findById(@Bind("id") long id);

    @SqlQuery("select ENVENTRY.*, " +
            "ENVIRONMENT.NAME as environmentname, ENVIRONMENT.description as environmentdesc, " +
            "intface.name as intfacename, intface.description as intfacedesc, " +
            "endpoint.name as endpointname, endpoint.description as endpointdesc " +
            "from ENVENTRY " +
            "left outer join ENVIRONMENT on ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID " +
            "left outer join intface on ENVENTRY.intfaceid = intface.id " +
            "left outer join endpoint on ENVENTRY.endpointid = endpoint.id " +
            "where ENVENTRY.environmentId = :environmentId")
    List<EnvEntry> findByEnv(@Bind("environmentId") long environmentId);
}
