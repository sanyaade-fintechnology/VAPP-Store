package com.vasilitate.entertainmentstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * A splash screen that is displayed for 3 seconds on initial startup of the app
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        new Handler().postDelayed(new Runnable() { // launch main activity with delay
            @Override public void run() {
                startActivity(new Intent(SplashActivity.this, StoreActivity.class));
            }
        }, SPLASH_DELAY_MS);
    }
}
