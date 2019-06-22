package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class MQRFH2Header {
    private List<MQRFH2Folder> folders = new ArrayList<MQRFH2Folder>();

    public List<MQRFH2Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<MQRFH2Folder> folders) {
        this.folders = folders;
    }

    @JsonIgnore
    public String[] getFolderStrings() {
        List<String> folderStringList = new ArrayList<String>();
        for (MQRFH2Folder folder : folders) {
            folderStringList.add(folder.getString());
        }
        return folderStringList.toArray(new String[folderStringList.size()]);
    }
}
