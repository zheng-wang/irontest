package io.irontest.models;

/**
 * Created by Zheng on 22/06/2016.
 */
public class ManagedFile {
    private long id;
    private String name;

    public ManagedFile() {}

    public ManagedFile(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
