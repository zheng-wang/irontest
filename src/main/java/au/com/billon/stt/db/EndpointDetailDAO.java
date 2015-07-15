package au.com.billon.stt.db;

import au.com.billon.stt.models.EndpointDetail;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(EndpointDetailMapper.class)
public interface EndpointDetailDAO {
    @SqlUpdate("create table IF NOT EXISTS endpointdtl (id INT PRIMARY KEY auto_increment, endpointId INT, " +
            "name varchar(50), value varchar(200), created timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (endpointId) REFERENCES endpoint(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into endpointdtl (endpointId, name, value) values (:endpointId, :name, :value)")
    @GetGeneratedKeys
    long insert(@BindBean EndpointDetail endpontdtl);

    @SqlUpdate("update endpointdtl set name = :name, value = :value, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean EndpointDetail endpontdtl);

    @SqlQuery("select * from endpointdtl where endpointId = :endpointId")
    List<EndpointDetail> findByEndpoint(@Bind("endpointId") long endpointId);
}
