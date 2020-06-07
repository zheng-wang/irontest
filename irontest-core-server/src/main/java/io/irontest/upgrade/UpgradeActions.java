package io.irontest.upgrade;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.Set;
import java.util.regex.Pattern;

public class UpgradeActions {
    public boolean hasBackupReliantSteps(String oldVersion, String newVersion) {
        Reflections reflections = new Reflections(getClass().getPackage().getName(), new ResourcesScanner());
        Set<String> systemDatabaseUpgradeSqlFiles =
                reflections.getResources(Pattern.compile(".*\\.sql"));
        System.out.println(systemDatabaseUpgradeSqlFiles);
        return true;
    }
}
