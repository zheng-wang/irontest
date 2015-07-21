package au.com.billon.stt.db;

import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.Teststep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(TeststepMapper.class)
public abstract class TeststepDAO {
    @SqlUpdate("create table IF NOT EXISTS teststep (" +
            "id INT PRIMARY KEY auto_increment, testcase_id INT, name varchar(200), description clob, " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "type varchar(20), request clob, properties clob, intfaceId int, " +
            "FOREIGN KEY (intfaceId) REFERENCES intface(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into teststep (testcase_id, name, type, description, request, properties, intfaceId) values " +
            "(:testcaseId, :name, :type, :description, :request, :properties, :intfaceId)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("testcaseId") long testcaseId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("description") String description,
                                @Bind("request") String request, @Bind("properties") String properties,
                                @Bind("intfaceId") Long intfaceId);

    public long insert(Teststep teststep) throws JsonProcessingException {
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            SOAPTeststepProperties properties = (SOAPTeststepProperties) teststep.getProperties();

            //  create sample soap request
            Wsdl wsdl = Wsdl.parse(properties.getWsdlUrl());
            SoapBuilder builder = wsdl.binding().localPart(properties.getWsdlBindingName()).find();
            SoapOperation operation = builder.operation().name(properties.getWsdlOperationName()).find();
            teststep.setRequest(builder.buildInputMessage(operation));

            properties.setSoapAddress(builder.getServiceUrls().get(0));
        }

        return insert(teststep.getTestcaseId(), teststep.getName(), teststep.getType(), teststep.getDescription(),
                teststep.getRequest(), new ObjectMapper().writeValueAsString(teststep.getProperties()),
                teststep.getIntfaceId() == 0 ? null : teststep.getIntfaceId());
    }

    @SqlUpdate("update teststep set name = :name, description = :description, request = :request, intfaceId = :intfaceId, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Teststep teststep);

    @SqlUpdate("delete from teststep where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select teststep.*, intface.name as intfaceName from teststep left outer join intface on teststep.intfaceId = intface.id " +
            "where teststep.id = :id")
    public abstract Teststep findById(@Bind("id") long id);

    @SqlQuery("select teststep.*, intface.name as intfaceName from teststep left outer join intface on teststep.intfaceId = intface.id " +
            "where teststep.testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId(@Bind("testcaseId") long testcaseId);
}
