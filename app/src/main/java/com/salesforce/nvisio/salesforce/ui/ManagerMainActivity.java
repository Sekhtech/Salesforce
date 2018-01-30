package com.salesforce.nvisio.salesforce.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.salesforce.nvisio.salesforce.R;

public class ManagerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_main_layout);
    }

    public void AppointmentClicked(View view) {
    }

    public void WorktimeClicked(View view) {
    }

    /*Map<String, User> users = new HashMap<>();
    users.put("alanisawesome", new User("June 23, 1912", "Alan Turing"));
    users.put("gracehop", new User("December 9, 1906", "Grace Hopper"));

    usersRef.setValueAsync(users);*/

    /*{
  "users": {
    "alanisawesome": {
      "date_of_birth": "June 23, 1912",
      "full_name": "Alan Turing"
    },
    "gracehop": {
      "date_of_birth": "December 9, 1906",
      "full_name": "Grace Hopper"
    }
  }
}*/
}
