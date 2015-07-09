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
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
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

    @SqlQuery("select ENVENTRY.*, ENVIRONMENT.NAME as environmentname, intface.name as intfacename, endpoint.name as endpointname from ENVENTRY, ENVIRONMENT, intface, endpoint " +
            "where ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID and ENVENTRY.intfaceid = intface.id and ENVENTRY.endpointid = endpoint.id")
    List<EnvEntry> findAll();

    @SqlQuery("select ENVENTRY.*, ENVIRONMENT.NAME as environmentname, intface.name as intfacename, endpoint.name as endpointname from enventry, ENVIRONMENT, intface, endpoint " +
            "where ENVENTRY.id = :id and ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID and ENVENTRY.intfaceid = intface.id and ENVENTRY.endpointid = endpoint.id")
    EnvEntry findById(@Bind("id") long id);

    @SqlQuery("select ENVENTRY.*, ENVIRONMENT.NAME as environmentname, intface.name as intfacename, endpoint.name as endpointname from enventry, ENVIRONMENT, intface, endpoint " +
            "where ENVENTRY.environmentId = :environmentId and ENVENTRY.ENVIRONMENTID = ENVIRONMENT.ID and ENVENTRY.intfaceid = intface.id and ENVENTRY.endpointid = endpoint.id")
    List<EnvEntry> findByEnv(@Bind("environmentId") long environmentId);
}
