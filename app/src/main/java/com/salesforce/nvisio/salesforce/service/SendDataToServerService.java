package com.salesforce.nvisio.salesforce.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by USER on 27-Dec-17.
 */

public class SendDataToServerService extends IntentService {
    public SendDataToServerService() {
        super("SendDataToServer");
    }
    public SendDataToServerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
