package io.irontest.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.db.FolderDAO;
import io.irontest.db.TestcaseDAO;
import io.irontest.models.Folder;
import io.irontest.models.Testcase;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

@Path("/folders") @Produces({ MediaType.APPLICATION_JSON })
public class FolderResource {
    private final FolderDAO folderDAO;
    private final TestcaseDAO testcaseDAO;

    public FolderResource(FolderDAO folderDAO, TestcaseDAO testcaseDAO) {
        this.folderDAO = folderDAO;
        this.testcaseDAO = testcaseDAO;
    }

    @PUT @Path("{folderId}")
    @PermitAll
    public Folder update(Folder folder) {
        folderDAO.update(folder);
        return folderDAO._findById(folder.getId());
    }

    @GET @Path("{folderId}")
    public Folder findById(@PathParam("folderId") long folderId) {
        return folderDAO._findById(folderId);
    }

    @POST @Path("{folderId}/importTestcase")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    public Testcase importTestcase(@PathParam("folderId") long folderId, @FormDataParam("file") InputStream inputStream,
                               @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Testcase testcase = objectMapper.readValue(inputStream, Testcase.class);
        long testcaseId = testcaseDAO.createByImport(testcase, folderId);
        Testcase result = new Testcase();
        result.setId(testcaseId);
        return result;
    }
}
