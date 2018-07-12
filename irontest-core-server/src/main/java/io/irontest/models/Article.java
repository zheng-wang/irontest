package io.irontest.models;

public class Article {
    private long id;
    private String title;
    private String content;

    public Article() {}

    public Article(long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
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
}
