package io.irontest.resources;

import io.irontest.db.FileDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Zheng on 22/06/2016.
 */
@Path("/files")
public class FileResource {
    private final FileDAO fileDao;

    public FileResource(FileDAO fileDao) {
        this.fileDao = fileDao;
    }

    @GET @Path("{fileId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFileById(@PathParam("fileId") long fileId) throws Exception {
        InputStream is = new FileInputStream("c:\\temp\\file.txt");
        return Response.ok(is)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file.txt\"")
                .build();
    }
}
