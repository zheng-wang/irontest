package io.irontest.upgrade.copyfiles;

import io.irontest.upgrade.CopyFiles;

import java.util.HashMap;
import java.util.Map;

public class CopyFiles_0_14_0_To_0_15_0 implements CopyFiles {
    private Map<String, String> filePathMap = new HashMap<>();

    public CopyFiles_0_14_0_To_0_15_0() {
        filePathMap.put("start.bat", "start.bat");
        filePathMap.put("start-team.bat", "start-team.bat");
    }

    @Override
    public Map<String, String> getFilePathMap() {
        return filePathMap;
    }
}
