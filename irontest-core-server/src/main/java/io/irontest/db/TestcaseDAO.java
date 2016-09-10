package io.irontest.db;

import io.irontest.models.Testcase;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 1/07/2015.
 */
@RegisterMapper(TestcaseMapper.class)
public abstract class TestcaseDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase (id BIGINT DEFAULT testcase_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, description CLOB, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT TESTCASE_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(name))")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract TeststepDAO teststepDAO();

    @SqlUpdate("insert into testcase values ()")
    @GetGeneratedKeys
    protected abstract long _insert();

    @SqlUpdate("update testcase set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    public long insert() {
        long id = _insert();
        updateNameForInsert(id, "Case " + id);
        return id;
    }

    @SqlUpdate("update testcase set name = :name, description = :description, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Testcase testcase);

    @SqlUpdate("delete from testcase where id = :id")
    public abstract void _deleteById(@Bind("id") long id);

    @Transaction
    public void deleteById(long id) {
        TeststepDAO teststepDAO = teststepDAO();
        List<Teststep> teststeps = teststepDAO.findByTestcaseId_PrimaryProperties(id);
        for (Teststep teststep: teststeps) {
            teststepDAO.deleteById_NoTransaction(teststep.getId());
        }
        _deleteById(id);
    }

    @SqlQuery("select * from testcase")
    public abstract List<Testcase> findAll();

    @SqlQuery("select * from testcase where testcase.id = :id")
    protected abstract Testcase _findById(@Bind("id") long id);

    @Transaction
    public Testcase findById_Mini(long id) {
        Testcase result = _findById(id);
        List<Teststep> teststeps = teststepDAO().findByTestcaseId_PrimaryProperties(id);
        result.setTeststeps(teststeps);
        return result;
    }

    @Transaction
    public Testcase findById_Complete(long id) {
        Testcase result = _findById(id);
        List<Teststep> teststeps = teststepDAO().findByTestcaseId(id);
        result.setTeststeps(teststeps);
        return result;
    }
}
