package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.db.UtilsDAO;
import io.irontest.models.DataTable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 26/03/2018.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class DataTableResource {
    private UtilsDAO utilsDAO;

    public DataTableResource(UtilsDAO utilsDAO) {
        this.utilsDAO = utilsDAO;
    }

    @GET
    @Path("testcases/{testcaseId}/datatable")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return utilsDAO.getTestcaseDataTable(testcaseId, false);
    }
}
