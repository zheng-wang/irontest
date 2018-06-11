package io.irontest.models;

public class FolderTreeNode {
    private Long idPerType;
    private Long parentFolderId;
    private String text;
    private FolderTreeNodeType type;

    public FolderTreeNode() {}

    public FolderTreeNode(Long idPerType, Long parentFolderId, String text, FolderTreeNodeType type) {
        this.idPerType = idPerType;
        this.parentFolderId = parentFolderId;
        this.text = text;
        this.type = type;
    }

    public Long getIdPerType() {
        return idPerType;
    }

    public void setIdPerType(Long idPerType) {
        this.idPerType = idPerType;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(Long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FolderTreeNodeType getType() {
        return type;
    }

    public void setType(FolderTreeNodeType type) {
        this.type = type;
    }
}
