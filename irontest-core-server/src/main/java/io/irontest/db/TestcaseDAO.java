package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.DataTable;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.teststep.Teststep;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(TestcaseMapper.class)
public interface TestcaseDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase (id BIGINT DEFAULT testcase_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, description CLOB, " +
            "parent_folder_id BIGINT NOT NULL, check_http_stubs_hit_order BOOLEAN NOT NULL DEFAULT FALSE, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_folder_id) REFERENCES folder(id), " +
            "CONSTRAINT TESTCASE_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(parent_folder_id, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into testcase (description, parent_folder_id) values (:description, :parentFolderId)")
    @GetGeneratedKeys
    long _insertWithoutName(@BindBean Testcase testcase);

    @SqlUpdate("insert into testcase (name, description, parent_folder_id, check_http_stubs_hit_order) values (" +
            ":name, :description, :parentFolderId, :checkHTTPStubsHitOrder)")
    @GetGeneratedKeys
    long _insertWithName(@BindBean Testcase testcase);

    @SqlUpdate("update testcase set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default Testcase insert(Testcase testcase) {
        long id = _insertWithoutName(testcase);
        updateNameForInsert(id, "Case " + id);
        return _findById(id);
    }

    @SqlUpdate("update testcase set name = :name, description = :description, " +
            "check_http_stubs_hit_order = :checkHTTPStubsHitOrder, updated = CURRENT_TIMESTAMP where id = :id")
    void update(@BindBean Testcase testcase);

    @SqlQuery("select * from testcase where id = :id")
    Testcase _findById(@Bind("id") long id);

    @Transaction
    default Testcase findById_TestcaseEditView(long id) {
        Testcase result = _findById(id);
        if (result != null) {
            List<Teststep> teststeps = teststepDAO().findByTestcaseId_TestcaseEditView(id);
            result.setTeststeps(teststeps);
        }
        return result;
    }

    /**
     * @param testcaseId
     * @return folder path of the testcase
     */
    @SqlQuery("WITH RECURSIVE T(parent_folder_id, path) AS (" +
                  "SELECT parent_folder_id, name AS path " +
                  "FROM folder WHERE id = (SELECT parent_folder_id FROM testcase WHERE id = :testcaseId) " +
                  "UNION ALL " +
                  "SELECT T2.parent_folder_id, (T2.name || '/' || T.path) AS path " +
                  "FROM T INNER JOIN folder AS T2 ON T.parent_folder_id = T2.id " +
              ") SELECT path FROM T WHERE parent_folder_id IS NULL")
    String getFolderPath(@Bind("testcaseId") long testcaseId);

    @Transaction
    default Testcase findById_Complete(long id) {
        Testcase result = _findById(id);

        result.setFolderPath(getFolderPath(id));

        List<UserDefinedProperty> udps = udpDAO().findByTestcaseId(id);
        result.setUdps(udps);

        List<Teststep> teststeps = teststepDAO().findByTestcaseId_Complete(id);
        result.setTeststeps(teststeps);

        DataTable dataTable = dataTableDAO().getTestcaseDataTable(id, false);
        result.setDataTable(dataTable);

        List<HTTPStubMapping> httpStubMappings = httpStubMappingDAO().findByTestcaseId(id);
        result.setHttpStubMappings(httpStubMappings);

        return result;
    }

    @SqlQuery("select count(*) = 1 from testcase where name = :name and parent_folder_id = :parentFolderId")
    boolean _nameExistsInFolder(@Bind("name") String name,
                                @Bind("parentFolderId") long parentFolderId);

    @SqlUpdate("insert into testcase (name, description, parent_folder_id, check_http_stubs_hit_order) " +
            "select :name, description, :parentFolderId, check_http_stubs_hit_order from testcase where id = :sourceTestcaseId")
    @GetGeneratedKeys
    long duplicateById(@Bind("name") String name, @Bind("parentFolderId") long parentFolderId,
                       @Bind("sourceTestcaseId") long sourceTestcaseId);

    /**
     * Clone/copy test case in the same system database.
     * @param sourceTestcaseId id of the test case to be cloned
     * @param targetFolderId id of the folder in which the new test case will be created
     * @return ID of the new test case
     */
    @Transaction
    default long duplicate(long sourceTestcaseId, long targetFolderId) {
        Testcase oldTestcaseRecord = _findById(sourceTestcaseId);

        //  resolve new test case name
        String newTestcaseName = oldTestcaseRecord.getName();
        if (oldTestcaseRecord.getParentFolderId() == targetFolderId) {
            int copyIndex = 1;
            newTestcaseName = oldTestcaseRecord.getName() + " - Copy";
            while (_nameExistsInFolder(newTestcaseName, targetFolderId)) {
                copyIndex++;
                newTestcaseName = oldTestcaseRecord.getName() + " - Copy (" + copyIndex + ")";
            }
        }

        //  duplicate the test case record
        long newTestcaseId = duplicateById(newTestcaseName, targetFolderId, sourceTestcaseId);

        //  duplicate user defined properties
        udpDAO().duplicateByTestcase(sourceTestcaseId, newTestcaseId);

        //  duplicate test steps
        teststepDAO().duplicateByTestcase(sourceTestcaseId, newTestcaseId);

        //  duplicate data table
        dataTableDAO().duplicateByTestcase(sourceTestcaseId, newTestcaseId);

        //  duplicate HTTP stubs
        httpStubMappingDAO().duplicateByTestcase(sourceTestcaseId, newTestcaseId);

        return newTestcaseId;
    }

    @Transaction
    default long createByImport(Testcase testcase, long targetFolderId) throws JsonProcessingException {
        if (_nameExistsInFolder(testcase.getName(), targetFolderId)) {
            throw new RuntimeException("Duplicate test case name: " + testcase.getName());
        }

        //  insert the test case record
        testcase.setParentFolderId(targetFolderId);
        long testcaseId = _insertWithName(testcase);

        //  insert UDPs
        for (UserDefinedProperty udp: testcase.getUdps()) {
            udpDAO()._insertWithName(testcaseId, udp.getName(), udp.getValue());
        }

        //  insert test steps
        for (Teststep teststep : testcase.getTeststeps()) {
            teststep.setTestcaseId(testcaseId);
            teststepDAO().insertByImport(teststep);
        }

        //  insert data table
        dataTableDAO().insertByImport(testcaseId, testcase.getDataTable());

        //  insert HTTP stubs
        for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
            httpStubMappingDAO().insertByImport(testcaseId, stub);
        }

        return testcaseId;
    }
}
