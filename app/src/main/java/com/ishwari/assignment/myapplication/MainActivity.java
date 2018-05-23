package com.ishwari.assignment.myapplication;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {
 Button setLoationbtn,makeListbtn;
    ImageView help_icon;
    final Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLoationbtn= (Button)findViewById(R.id.setlocation_btn);
        makeListbtn=(Button)findViewById(R.id.make_list_btn);
help_icon=(ImageView)findViewById(R.id.help_icon);
        Animation LeftSwipe = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_left);
        setLoationbtn.startAnimation(LeftSwipe);

        Animation RightSwipe = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right);
        makeListbtn.startAnimation(RightSwipe);

        setLoationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,CurrentLocation.class);
                startActivity(i);
//                Intent intent = new Intent(this, SecondScreen.class);

            }
        });

        makeListbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,MakeListActivity.class);
                startActivity(i);
            }
        });
        help_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li.inflate(R.layout.exit_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button yesButton = (Button) dialogView.findViewById(R.id.btn_yes);
        Button noButton =(Button)dialogView.findViewById(R.id.btn_no);
        // if button is clicked, close the custom dialog
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MainActivity.this.finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
