package io.irontest.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Created by Zheng on 10/09/2016.
 */
public class FolderTreeNode {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parent;
    private String text;
    private FolderTreeNodeType type;
    private String data;

    public FolderTreeNode(Long id, Long parent, String text, FolderTreeNodeType type, String data) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        this.type = type;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
