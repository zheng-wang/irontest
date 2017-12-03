package io.irontest.models;

/**
 * Created by Zheng on 3/12/2017.
 */
public class AppInfo {
    public static final String APP_MODE_TEAM = "team";
    private String appMode = "local";

    public String getAppMode() {
        return appMode;
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
    }
}
