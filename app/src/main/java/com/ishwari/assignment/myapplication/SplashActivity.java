package com.ishwari.assignment.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView logo=(ImageView)findViewById(R.id.logo);
        TextView app_txt=(TextView)findViewById(R.id.app_txt);

        Animation top_animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_top);
        logo.startAnimation(top_animation);

        Animation bottom_animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottom);
        app_txt.startAnimation(bottom_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
