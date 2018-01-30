package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 30-Jan-18.
 */

public class Credential {
    public Credential() {
    }

    private String userId,Password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
