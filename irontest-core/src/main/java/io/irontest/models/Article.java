package io.irontest.models;

import java.util.Date;

/**
 * Created by Zheng on 20/06/2015.
 */
public class Article {
    private long id;
    private String title;
    private String content;
    private Date created;
    private Date updated;

    public Article() {}

    public Article(long id, String title, String content, Date created, Date updated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = created;
        this.updated = updated;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
