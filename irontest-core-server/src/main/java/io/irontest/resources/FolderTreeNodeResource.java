package io.irontest.resources;

import io.irontest.db.FolderTreeNodeDAO;
import io.irontest.models.FolderTreeNode;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 10/09/2015.
 */
@Path("/foldertreenodes") @Produces({ MediaType.APPLICATION_JSON })
public class FolderTreeNodeResource {
    private final FolderTreeNodeDAO folderTreeNodeDAO;

    public FolderTreeNodeResource(FolderTreeNodeDAO folderTreeNodeDAO) {
        this.folderTreeNodeDAO = folderTreeNodeDAO;
    }

    @GET
    public List<FolderTreeNode> findAll() {
        return folderTreeNodeDAO.findAll();
    }

    @PUT
    @Path("{type}.{idPerType}")   //  composite id
    public void update(FolderTreeNode node) {
        folderTreeNodeDAO.update(node);
    }
}
