package io.irontest.models;

/**
 * Created by Zheng on 10/09/2016.
 */
public class FolderTreeNode {
    private Long idPerType;
    private Long parent;
    private String text;
    private FolderTreeNodeType type;

    public FolderTreeNode(Long idPerType, Long parent, String text, FolderTreeNodeType type) {
        this.idPerType = idPerType;
        this.parent = parent;
        this.text = text;
        this.type = type;
    }

    public Long getIdPerType() {
        return idPerType;
    }

    public void setIdPerType(Long idPerType) {
        this.idPerType = idPerType;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
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
