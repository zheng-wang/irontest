package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.Testcase;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 1/07/2015.
 */
@UseStringTemplate3StatementLocator
@RegisterMapper(TestcaseMapper.class)
public abstract class TestcaseDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase (id BIGINT DEFAULT testcase_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, description CLOB, " +
            "parent_folder_id BIGINT NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_folder_id) REFERENCES folder(id), " +
            "CONSTRAINT TESTCASE_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(parent_folder_id, name))")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract UserDefinedPropertyDAO udpDAO();

    @CreateSqlObject
    protected abstract TeststepDAO teststepDAO();

    @CreateSqlObject
    protected abstract AssertionDAO assertionDAO();

    @SqlUpdate("insert into testcase (description, parent_folder_id) values (:description, :parentFolderId)")
    @GetGeneratedKeys
    protected abstract long _insert(@BindBean Testcase testcase);

    @SqlUpdate("update testcase set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    private Testcase insert_NoTransaction(Testcase testcase) {
        long id = _insert(testcase);
        if (testcase.getName() == null) {
            testcase.setName("Case " + id);
        }
        updateNameForInsert(id, testcase.getName());
        return _findById(id);
    }

    @Transaction
    public Testcase insert(Testcase testcase) {
        return insert_NoTransaction(testcase);
    }

    @SqlUpdate("update testcase set name = :name, description = :description, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Testcase testcase);

    /*@SqlUpdate("delete from testcase where id = :id")
    public abstract void _deleteById(@Bind("id") long id);

    @Transaction
    public void deleteById(long id) {
        TeststepDAO teststepDAO = teststepDAO();
        List<Teststep> teststeps = teststepDAO.findByTestcaseId_TestcaseEditView(id);
        for (Teststep teststep: teststeps) {
            teststepDAO.deleteById_NoTransaction(teststep.getId());
        }
        _deleteById(id);
    }*/

    @SqlQuery("select * from testcase where id = :id")
    protected abstract Testcase _findById(@Bind("id") long id);

    @Transaction
    public Testcase findById_TestcaseEditView(long id) {
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
                  "FROM folder WHERE id = (SELECT parent_folder_id FROM testcase WHERE id = <testcaseId>) " +
                  "UNION ALL " +
                  "SELECT T2.parent_folder_id, (T2.name || '/' || T.path) AS path " +
                  "FROM T INNER JOIN folder AS T2 ON T.parent_folder_id = T2.id " +
              ") SELECT path FROM T WHERE parent_folder_id IS NULL")
    protected abstract String getFolderPath(@Define("testcaseId") long testcaseId);

    @Transaction
    public Testcase findById_Complete(long id) {
        return findById_Complete_NoTransaction(id);
    }

    /**
     * User defined properties are not included.
     * @param id
     * @return
     */
    public Testcase findById_Complete_NoTransaction(long id) {
        Testcase result = _findById(id);
        result.setFolderPath(getFolderPath(id));
        List<Teststep> teststeps = teststepDAO().findByTestcaseId(id);
        result.setTeststeps(teststeps);
        return result;
    }

    @SqlQuery("select count(*) = 1 from testcase where name = :name and parent_folder_id = :parentFolderId")
    protected abstract boolean _nameExistsInFolder(@Bind("name") String name,
                                                   @Bind("parentFolderId") long parentFolderId);

    /**
     * Clone the test case and its contents.
     * @param oldTestcaseId id of the test case to be cloned
     * @param targetFolderId id of the folder in which the new test case will be created
     * @return ID of the new test case
     */
    @Transaction
    public long duplicate(long oldTestcaseId, long targetFolderId) throws JsonProcessingException {
        Testcase oldTestcase = findById_Complete_NoTransaction(oldTestcaseId);

        //  resolve new test case name
        String newTestcaseName = oldTestcase.getName();
        if (oldTestcase.getParentFolderId() == targetFolderId) {
            int copyIndex = 1;
            newTestcaseName = oldTestcase.getName() + " - Copy";
            while (_nameExistsInFolder(newTestcaseName, targetFolderId)) {
                copyIndex++;
                newTestcaseName = oldTestcase.getName() + " - Copy (" + copyIndex + ")";
            }
        }

        //  duplicate the test case record
        Testcase newTestcase = new Testcase();
        newTestcase.setName(newTestcaseName);
        newTestcase.setDescription(oldTestcase.getDescription());
        newTestcase.setParentFolderId(targetFolderId);
        newTestcase = insert_NoTransaction(newTestcase);

        //  duplicate user defined properties
        udpDAO().duplicateByTestcase(oldTestcaseId, newTestcase.getId());

        //  duplicate test steps
        for (Teststep oldTeststep : oldTestcase.getTeststeps()) {
            Teststep newTeststep = new Teststep();
            newTeststep.setName(oldTeststep.getName());
            newTeststep.setTestcaseId(newTestcase.getId());
            newTeststep.setSequence(oldTeststep.getSequence());
            newTeststep.setType(oldTeststep.getType());
            newTeststep.setDescription(oldTeststep.getDescription());
            newTeststep.setAction(oldTeststep.getAction());
            if (oldTeststep.getRequestType() == TeststepRequestType.TEXT) {
                newTeststep.setRequest(oldTeststep.getRequest());
            } else {
                newTeststep.setRequest(teststepDAO().getBinaryRequestById(oldTeststep.getId()));
            }
            newTeststep.setRequestType(oldTeststep.getRequestType());
            newTeststep.setRequestFilename(oldTeststep.getRequestFilename());
            newTeststep.setOtherProperties(oldTeststep.getOtherProperties());
            Endpoint oldEndpoint = oldTeststep.getEndpoint();
            if (oldEndpoint != null) {
                Endpoint newEndpoint = new Endpoint();
                newTeststep.setEndpoint(newEndpoint);
                if (oldEndpoint.isManaged()) {
                    newEndpoint.setId(oldEndpoint.getId());
                } else {
                    newEndpoint.setName(oldEndpoint.getName());
                    newEndpoint.setType(oldEndpoint.getType());
                    newEndpoint.setDescription(oldEndpoint.getDescription());
                    newEndpoint.setUrl(oldEndpoint.getUrl());
                    newEndpoint.setUsername(oldEndpoint.getUsername());
                    newEndpoint.setPassword(oldEndpoint.getPassword());
                    newEndpoint.setOtherProperties(oldEndpoint.getOtherProperties());
                }
            }
            long newTeststepId = teststepDAO().insert_NoTransaction(newTeststep, null);

            //  duplicate assertions
            for (Assertion oldAssertion : oldTeststep.getAssertions()) {
                Assertion newAssertion = new Assertion();
                newAssertion.setTeststepId(newTeststepId);
                newAssertion.setName(oldAssertion.getName());
                newAssertion.setType(oldAssertion.getType());
                newAssertion.setOtherProperties(oldAssertion.getOtherProperties());
                assertionDAO().insert_NoTransaction(newAssertion);
            }
        }

        return newTestcase.getId();
    }
}
