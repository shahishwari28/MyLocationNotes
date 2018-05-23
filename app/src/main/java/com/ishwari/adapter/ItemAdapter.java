package com.ishwari.adapter;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ishwari.assignment.myapplication.R;
import com.ishwari.sqlite.helper.Item;

import java.util.List;

/**
 * Created by xyz on 12/29/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Myholder> {
    List<Item> dataModelArrayList;


    public ItemAdapter(List<Item> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }



    class Myholder extends RecyclerView.ViewHolder{
        TextView name;
        TextView qty;

        public Myholder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.title);
            qty=(TextView)itemView.findViewById(R.id.qty);

        }


    }

    public void setItems(List<Item> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }
    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_list_item,null);

        return new Myholder(view);

    }

       @Override
    public void onBindViewHolder(Myholder holder, final int position) {
        final Item dataModel=dataModelArrayList.get(position);
        holder.name.setText(dataModel.getItemName());
        holder.qty.setText(dataModel.getItem_qty());
//           System.out.println("in ITEM Adapter---->"+dataModel.getItemId()+" "+dataModel.getItemName()+"  "+dataModel.getItem_qty());
    }


    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }
}

