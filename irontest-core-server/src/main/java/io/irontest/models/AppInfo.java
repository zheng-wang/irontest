package io.irontest.models;

public class AppInfo {
    private AppMode appMode = AppMode.LOCAL;

    public AppMode getAppMode() {
        return appMode;
    }

    public void setAppMode(AppMode appMode) {
        this.appMode = appMode;
    }
}
