package io.irontest.models;

/**
 * Created by Zheng on 3/12/2017.
 */
public class AppInfo {
    private AppMode appMode = AppMode.LOCAL;

    public AppMode getAppMode() {
        return appMode;
    }

    public void setAppMode(AppMode appMode) {
        this.appMode = appMode;
    }
}
