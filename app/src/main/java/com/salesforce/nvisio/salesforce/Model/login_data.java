package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 08-May-17.
 */

public class login_data {
    private String loginTime, logoutTime, loginDate, duration;

    public login_data() {
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public login_data(String duration, String logoutTime, String loginDate, String loginTime) {
        this.duration = duration;
        this.logoutTime = logoutTime;
        this.loginDate = loginDate;
        this.loginTime = loginTime;
    }
}
