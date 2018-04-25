package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.db.DataTableCellDAO;
import io.irontest.db.DataTableColumnDAO;
import io.irontest.db.DataTableDAO;
import io.irontest.models.DataTable;
import io.irontest.models.DataTableCell;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 26/03/2018.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class DataTableResource {
    private DataTableDAO dataTableDAO;
    private DataTableColumnDAO dataTableColumnDAO;
    private DataTableCellDAO dataTableCellDAO;

    public DataTableResource(DataTableDAO dataTableDAO, DataTableColumnDAO dataTableColumnDAO,
                             DataTableCellDAO dataTableCellDAO) {
        this.dataTableDAO = dataTableDAO;
        this.dataTableColumnDAO = dataTableColumnDAO;
        this.dataTableCellDAO = dataTableCellDAO;
    }

    @GET
    @Path("testcases/{testcaseId}/datatable")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/addColumn")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable addColumn(@PathParam("testcaseId") long testcaseId, @QueryParam("columnType") String columnType) {
        dataTableColumnDAO.insert(testcaseId, columnType);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/renameColumn")
    public void renameColumn(@QueryParam("columnId") long columnId, @QueryParam("newName") String newName) {
        dataTableColumnDAO.rename(columnId, newName);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/addRow")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable addRow(@PathParam("testcaseId") long testcaseId) {
        dataTableCellDAO.addRow(testcaseId);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/updateCell")
    public void updateCell(DataTableCell dataTableCell, @QueryParam("columnId") long columnId,
                                      @QueryParam("rowIndex") short rowIndex) {
        dataTableCellDAO.update(columnId, rowIndex, dataTableCell);
    }
}
