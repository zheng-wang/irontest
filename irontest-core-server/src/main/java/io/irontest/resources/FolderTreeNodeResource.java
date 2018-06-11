package io.irontest.resources;

import io.irontest.db.FolderTreeNodeDAO;
import io.irontest.models.FolderTreeNode;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/foldertreenodes") @Produces({ MediaType.APPLICATION_JSON })
public class FolderTreeNodeResource {
    private final FolderTreeNodeDAO folderTreeNodeDAO;

    public FolderTreeNodeResource(FolderTreeNodeDAO folderTreeNodeDAO) {
        this.folderTreeNodeDAO = folderTreeNodeDAO;
    }

    @POST
    @PermitAll
    public FolderTreeNode create(FolderTreeNode node) {
        return folderTreeNodeDAO.insert(node);
    }

    @GET
    public List<FolderTreeNode> findAll() {
        return folderTreeNodeDAO.findAll();
    }

    @PUT
    @PermitAll
    public void update(FolderTreeNode node) {
        folderTreeNodeDAO.update(node);
    }
}
