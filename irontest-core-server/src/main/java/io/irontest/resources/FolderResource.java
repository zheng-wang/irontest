package io.irontest.resources;

import io.irontest.db.FolderDAO;
import io.irontest.models.Folder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 3/02/2017.
 */
@Path("/folders") @Produces({ MediaType.APPLICATION_JSON })
public class FolderResource {
    private final FolderDAO folderDAO;

    public FolderResource(FolderDAO folderDAO) {
        this.folderDAO = folderDAO;
    }

    @PUT @Path("{folderId}")
    public Folder update(Folder folder) {
        folderDAO.update(folder);
        return folderDAO._findById(folder.getId());
    }

    @GET @Path("{folderId}")
    public Folder findById(@PathParam("folderId") long folderId) {
        return folderDAO._findById(folderId);
    }
}
