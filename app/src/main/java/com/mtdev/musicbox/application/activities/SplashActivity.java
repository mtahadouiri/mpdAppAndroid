

package com.mtdev.musicbox.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mtdev.musicbox.Client.Activities.Login;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        Intent intent = new Intent(this, Login.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
        finish();
    }
}
