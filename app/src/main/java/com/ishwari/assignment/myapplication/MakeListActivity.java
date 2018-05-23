package com.ishwari.assignment.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ishwari.adapter.LocationListAdapter;
import com.ishwari.sqlite.helper.Item;
import com.ishwari.sqlite.helper.MarkerPlace;

import java.util.ArrayList;
import java.util.List;

public class MakeListActivity extends Activity {

    private DBManager dbManager;

    RecyclerView recyclerView;
    static LocationListAdapter recycler;
//    public DBManager dbManager;
static List<MarkerPlace> markerList;
//    Toolbar toolbar;
    LinearLayout toolbar;
    ImageButton backarrow;
    TextView txt_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_list);
        initToolBar();
        dbManager = new DBManager(this);
        dbManager.open();

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
         markerList  = dbManager.fetchPlaces();
        if(markerList.size()==0){
            showEmpltyDailog();
        }else {
            recycler = new LocationListAdapter(markerList, getApplicationContext());
            RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(reLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recycler);
        }
    }
    public void initToolBar(){
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        backarrow=(ImageButton)findViewById(R.id.backarrow);
        txt_name=(TextView)findViewById(R.id.text_name);
        txt_name.setText(R.string.locationlist);

        backarrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i=new Intent(MakeListActivity.this,MainActivity.class);
                        v.getContext().startActivity(i);
                    }
                }
        );

    }
    static Intent makeNotificationIntent(Context geofenceService, String msg)
    {
//        Log.d(TAG,msg);
        return new Intent(geofenceService,MakeListActivity.class);
    }

    public void showEmpltyDailog(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.empty_location_list, null);

        final Button ok_button = alertLayout.findViewById(R.id.btn_ok);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MakeListActivity.this,MainActivity.class);
                startActivity(i);

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
