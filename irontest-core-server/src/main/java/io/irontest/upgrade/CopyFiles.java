package io.irontest.upgrade;

import java.util.Map;

public interface CopyFiles {
    /**
     *
     * @return A map of sourceFileInDistFolder -> targetFileInIronTestHome
     */
    Map<String, String> getFilePathMap();
}
