package io.irontest.db;

import org.jdbi.v3.sqlobject.CreateSqlObject;

public interface CrossReferenceDAO {
    @CreateSqlObject
    TestcaseDAO testcaseDAO();

    @CreateSqlObject
    FolderDAO folderDAO();

    @CreateSqlObject
    DataTableDAO dataTableDAO();

    @CreateSqlObject
    DataTableColumnDAO dataTableColumnDAO();

    @CreateSqlObject
    DataTableCellDAO dataTableCellDAO();

    @CreateSqlObject
    EndpointDAO endpointDAO();

    @CreateSqlObject
    AssertionDAO assertionDAO();

    @CreateSqlObject
    PropertyExtractorDAO propertyExtractorDAO();

    @CreateSqlObject
    TeststepRunDAO teststepRunDAO();

    @CreateSqlObject
    TestcaseIndividualRunDAO testcaseIndividualRunDAO();

    @CreateSqlObject
    UserDefinedPropertyDAO udpDAO();

    @CreateSqlObject
    TeststepDAO teststepDAO();

    @CreateSqlObject
    HTTPStubMappingDAO httpStubMappingDAO();
}
