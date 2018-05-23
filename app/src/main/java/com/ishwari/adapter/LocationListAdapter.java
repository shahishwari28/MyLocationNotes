package com.ishwari.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ishwari.assignment.myapplication.DBManager;
import com.ishwari.assignment.myapplication.MakeListActivity;
import com.ishwari.assignment.myapplication.R;
import com.ishwari.assignment.myapplication.ShowListDetails;
import com.ishwari.sqlite.helper.MarkerPlace;

import java.util.List;

/**
 * Created by xyz on 12/29/2017.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.Myholder> {
    List<MarkerPlace> dataModelArrayList;
    private DBManager dbManager;
private Context mContext;
    public LocationListAdapter(List<MarkerPlace> dataModelArrayList, Context makeListActivity) {
        this.dataModelArrayList = dataModelArrayList;
        this.mContext=  makeListActivity;
    }


    class Myholder extends RecyclerView.ViewHolder{
        TextView name;
        ImageButton viewDetails;
        ImageButton cancelItem;
        LinearLayout row_ll;


        public Myholder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_name);
            viewDetails=(ImageButton)itemView.findViewById(R.id.view_items);
row_ll=(LinearLayout)itemView.findViewById(R.id.row_ll);
            cancelItem=(ImageButton)itemView.findViewById(R.id.cancel_items);
        }
    }


    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,null);
        dbManager = new DBManager(parent.getContext());
        dbManager.open();
        return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(Myholder holder, final int position) {
        final MarkerPlace dataModel = dataModelArrayList.get(position);
        holder.name.setText(dataModel.getPlaceName());
        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Clicked--ID---" + dataModel.getPlaceId(), Toast.LENGTH_LONG).show();
                Intent i = new Intent(v.getContext(), ShowListDetails.class);
                i.putExtra("name", dataModel.getPlaceName());
                i.putExtra("id", dataModel.getPlaceId());
                i.putExtra("lat", dataModel.getLatitude());
                i.putExtra("long", dataModel.getLongitude());
                v.getContext().startActivity(i);
            }
        });
        holder.row_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                LayoutInflater inflater = mContext.getLayoutInflater();
                View alertLayout = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_location_name, null);
final EditText loc_name=(EditText)alertLayout.findViewById(R.id.location_name_id);
                loc_name.setText(dataModel.getPlaceName());
                loc_name.setSelection(loc_name.getText().toString().trim().length());
                final Button update_button = alertLayout.findViewById(R.id.btn_update_id);
final  Button cancel_button=alertLayout.findViewById(R.id.btn_cancel);
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setView(alertLayout);
                alert.setCancelable(false);
                final AlertDialog dialog = alert.create();
                update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
dbManager.updateLocationName(dataModel.getPlaceId(),loc_name.getText().toString().trim());
                        updateLocationList();
                        dialog.cancel();
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();

            }
        });
holder.cancelItem.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder( view.getContext());

        alertDialog.setTitle("Delete this Location?");
        alertDialog.setMessage("Are you sure you want to delete Location?");
        alertDialog.setIcon(R.mipmap.icon);

        alertDialog.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbManager.deleteLocation(dataModel.getPlaceId());
                        updateLocationList();
                        dialog.dismiss();
                    }
                }
        );


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }
});
    }
    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public void setLocations(List<MarkerPlace> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }

    public  void showDeleteDailog( final String placeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dbManager = new DBManager(mContext);
        dbManager.open();
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure to delete Location?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dbManager.deleteLocation(placeId);
                updateLocationList();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public  void updateLocationList(){
        dataModelArrayList.clear();
        dataModelArrayList = dbManager.fetchPlaces();
//        System.out.println("After adding item---->"+itemArray_List.size());
        setLocations(dataModelArrayList);
       notifyDataSetChanged();

    }


}
