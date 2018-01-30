package com.salesforce.nvisio.salesforce.Model;

/**
 * Created by USER on 15-May-17.
 */

public class credential_model {
    public String email;
    public long phone, status;

    public credential_model(String email, long phone, long status) {
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
}
