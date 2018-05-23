package com.ishwari.assignment.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ishwari.adapter.ItemAdapter;
import com.ishwari.sqlite.helper.Item;

import java.util.ArrayList;

/**
 * Created by xyz on 12/29/2017.
 */

public class ShowListDetails extends AppCompatActivity {
    Toolbar toolbar;
    String placeName,placeId,placeLat,placeLong;
    final Context mContext = this;
    private DBManager dbManager;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    ArrayList<Item> itemArray_List;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DBManager(this);
        dbManager.open();

        setContentView(R.layout.show_list_details);
        placeName=getIntent().getStringExtra("name");
        placeId=getIntent().getStringExtra("id");
        placeLat=getIntent().getStringExtra("lat");
        placeLong=getIntent().getStringExtra("long");
       initToolBar();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        itemArray_List = dbManager.fetchItems(placeId);
//        System.out.println("Cnt----------->"+itemArray_List.size());

        itemAdapter =new ItemAdapter(itemArray_List);
        RecyclerView.LayoutManager reLayoutManager =new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(itemAdapter);
//        itemArray_List=_itemArrayList;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item item = itemArray_List.get(position);

                showDialogModify(item.getPlaceId_fk(),item.getItem_qty(),item.getItemName(),item.getItemId());
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        fab.setOnClickListener(buttonOnClickListener);

    }
    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(placeName);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowListDetails.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ShowListDetails.this,MakeListActivity.class);
                v.getContext().startActivity(i);
            }
        }
        );

    }

private void showDialogModify(final String place_Id, final String item_qty, final String item_name, final String id){
    LayoutInflater li = LayoutInflater.from(mContext);
    View dialogView = li.inflate(R.layout.activity_modify_items, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            mContext);
    alertDialogBuilder.setView(dialogView);
    final AlertDialog alertDialog = alertDialogBuilder.create();
    final EditText name_text = (EditText) dialogView.findViewById(R.id.name_edittext);
    final EditText qty_text = (EditText) dialogView.findViewById(R.id.qty_edittext);
    name_text.setText(item_name);
    qty_text.setText(item_qty);

    Button updateButton = (Button) dialogView.findViewById(R.id.btn_update);
    Button deleteButton =(Button)dialogView.findViewById(R.id.btn_delete);
    // if button is clicked, close the custom dialog
    updateButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(name_text.getText().toString().trim().length()!=0) {
                if (qty_text.getText().toString().trim().length()!=0) {
                    dbManager.updateItem(id,name_text.getText().toString(),qty_text.getText().toString());

                    alertDialog.dismiss();
                    updateItemList();
                }else
                    Toast.makeText(mContext,"Please enter description.",Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(mContext,"Please enter name.",Toast.LENGTH_LONG).show();


        }
    });

    deleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dbManager.deleteItem(id);
            alertDialog.dismiss();
            updateItemList();

        }
    });

    alertDialog.show();

}

    private void addItemsDailog() {
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li.inflate(R.layout.custom_dailog_add_item, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

//        alertDialogBuilder.setTitle(placeName);
//        alertDialogBuilder.setIcon(R.mipmap.icon);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        ((TextView)dialogView.findViewById(R.id.location_name)).setText(placeName);
        final EditText place_name = (EditText) dialogView
                .findViewById(R.id.item_name);
        final EditText qty=(EditText)dialogView.findViewById(R.id.item_qty);
        final Button cancel_btn=(Button)dialogView.findViewById(R.id.cancel_id);
        final Button ok_id=(Button)dialogView.findViewById(R.id.ok_id);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
        ok_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(place_name.getText().toString().trim().length()!=0) {
                    if (qty.getText().toString().trim().length()!=0) {
                        addToDb(place_name.getText().toString(), qty.getText().toString());
                        alertDialog.dismiss();
                    }else
                        Toast.makeText(mContext,"Enter description before adding new Item",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(mContext,"Enter name before adding new Item",Toast.LENGTH_LONG).show();
            }
        });
       alertDialog.show();
    }

    private void addToDb(String item_name,String qty) {
      dbManager.insertItem(new com.ishwari.sqlite.helper.Item(placeId,item_name,qty));
//        System.out.println("After adding item---->"+itemArray_List.size());
updateItemList();
    }
    public void updateItemList(){
        itemArray_List.clear();
        itemArray_List = dbManager.fetchItems(placeId);
//        System.out.println("After adding item---->"+itemArray_List.size());
        itemAdapter.setItems(itemArray_List);
        itemAdapter.notifyDataSetChanged();

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ShowListDetails.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShowListDetails.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
    public View.OnClickListener buttonOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    addItemsDailog();
                    break;
            }
        }

    };
}
