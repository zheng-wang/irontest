package io.irontest.models;

/**
 * Created by Zheng on 10/09/2016.
 */
public class FolderTreeNode {
    private Long id;
    private Long parent;
    private String text;
    private FolderTreeNodeType type;
    private Long testcaseId;

    public FolderTreeNode(Long id, Long parent, String text, FolderTreeNodeType type, Long testcaseId) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        this.type = type;
        this.testcaseId = testcaseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }
}
