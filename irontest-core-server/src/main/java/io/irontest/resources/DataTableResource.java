package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.db.DataTableDAO;
import io.irontest.models.DataTable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 26/03/2018.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class DataTableResource {
    private DataTableDAO dataTableDAO;

    public DataTableResource(DataTableDAO dataTableDAO) {
        this.dataTableDAO = dataTableDAO;
    }

    @GET
    @Path("testcases/{testcaseId}/datatable")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST
    @Path("testcases/{testcaseId}/datatable/addColumn")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable addColumn(@PathParam("testcaseId") long testcaseId,
                               @QueryParam("columnType") String columnType) {
        dataTableDAO.addColumn(testcaseId, columnType);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }
}
