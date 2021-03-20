package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class CopyFiles {
    private List<CopyFilesForOneVersionUpgrade> allFiles = new ArrayList();

    public CopyFiles() {
        CopyFilesForOneVersionUpgrade filesForOneVersion = new CopyFilesForOneVersionUpgrade(
                new DefaultArtifactVersion("0.14.0"), new DefaultArtifactVersion("0.15.0"));
        filesForOneVersion.getFilePathMap().put("start.bat", "start.bat");
        filesForOneVersion.getFilePathMap().put("start-team.bat", "start-team.bat");
        allFiles.add(filesForOneVersion);

        filesForOneVersion = new CopyFilesForOneVersionUpgrade(
                new DefaultArtifactVersion("0.16.1"), new DefaultArtifactVersion("0.16.2"));
        filesForOneVersion.getFilePathMap().put("start.bat", "start.bat");
        allFiles.add(filesForOneVersion);
    }

    public List<CopyFilesForOneVersionUpgrade> getApplicableCopyFiles(DefaultArtifactVersion oldVersion,
                                                                      DefaultArtifactVersion newVersion) {
        List<CopyFilesForOneVersionUpgrade> result = new ArrayList<>();
        for (CopyFilesForOneVersionUpgrade filesForOneVersionUpgrade: allFiles) {
            if (filesForOneVersionUpgrade.getFromVersion().compareTo(oldVersion) >= 0 &&
                    filesForOneVersionUpgrade.getToVersion().compareTo(newVersion) <=0) {
                result.add(filesForOneVersionUpgrade);
            }
        }
        return result;
    }
}
