package io.irontest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 10/07/2016.
 */
public class MQRFH2Header {
    private boolean enabled;
    private List<MQRFH2Folder> folders = new ArrayList<MQRFH2Folder>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<MQRFH2Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<MQRFH2Folder> folders) {
        this.folders = folders;
    }
}
