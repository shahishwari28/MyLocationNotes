package com.ishwari.assignment.myapplication;

/**
 * Created by anupamchugh on 19/10/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import com.ishwari.sqlite.helper.Item;
import com.ishwari.sqlite.helper.List;
import com.ishwari.sqlite.helper.MarkerPlace;

import java.util.ArrayList;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

//    public void insert(String name, String desc) {
//        ContentValues contentValue = new ContentValues();
//        contentValue.put(DatabaseHelper.NAME, name);
//        contentValue.put(DatabaseHelper._DESC, desc);
//        long i=database.insert(DatabaseHelper.TABLE_MYNAME, null, contentValue);
//        System.out.println("value---***->"+i);
//    }

    public long insertLocation(MarkerPlace markerPlace){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.LOC_NAME, markerPlace.getPlaceName());
        contentValue.put(DatabaseHelper.LOC_LAT, markerPlace.getLatitude());
        contentValue.put(DatabaseHelper.LOC_LONG, markerPlace.getLongitude());
       long a= database.insert(DatabaseHelper.TABLE_LOCATION, null, contentValue);
//        System.out.println("value---->"+a);
        return a;
    }

//    public void insertList(List myList){
//        ContentValues contentValue = new ContentValues();
//        contentValue.put(DatabaseHelper.LIST_NAME, myList.getListName());
//        contentValue.put(DatabaseHelper.LIST_NOTE, myList.getNote());
//        contentValue.put(DatabaseHelper.LIST_LOC_ID, myList.getPlaceId_fk());
//        long a= database.insert(DatabaseHelper.TABLE_LIST, null, contentValue);
//        System.out.println("value---->"+a);
//    }

    public void insertItem(Item item){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ITEM_NAME, item.getItemName());
        contentValue.put(DatabaseHelper.ITEM_QTY, item.getItem_qty());

        contentValue.put(DatabaseHelper.ITEM_PLACE_ID, item.getPlaceId_fk());
        long a= database.insert(DatabaseHelper.TABLE_ITEMS, null, contentValue);
//        System.out.println("value---->"+a);
    }

    public ArrayList<MarkerPlace> fetchPlaces() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_LOCATION, null, null, null, null, null, null);
        ArrayList<MarkerPlace> markerList = new ArrayList<MarkerPlace>();
        MarkerPlace markerModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                markerModel = new MarkerPlace();
                markerModel.setPlaceId(cursor.getString(0));
                markerModel.setPlaceName(cursor.getString(1));
                markerModel.setLatitude(cursor.getString(2));
                markerModel.setLongitude(cursor.getString(3));
                markerList.add(markerModel);
            }
        }
        cursor.close();
        return markerList;
    }

    public int updateLocationName(long _id, MarkerPlace markerPlace) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LOC_NAME, markerPlace.getPlaceName());
        int i = database.update(DatabaseHelper.TABLE_LOCATION, contentValues, DatabaseHelper.LIST_ID + " = " + _id, null);
        return i;
    }
    public void updateLocationLatLong(String _id, String latitude,String longitude) {
//        System.out.println("in db----"+_id+"  lat--"+latitude+"   long-->"+longitude);

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.LOC_LAT, latitude);
        contentValues.put(DatabaseHelper.LOC_LONG, longitude);
        int i = database.update(DatabaseHelper.TABLE_LOCATION, contentValues, DatabaseHelper.LOC_ID + " = ?", new String[]{String.valueOf(_id)});

//        System.out.println("Update result----"+i);
    }
    public ArrayList<Item> fetchItems(String placeID){
        Cursor cursor = database.query(DatabaseHelper.TABLE_ITEMS, new String[] { DatabaseHelper.ITEM_ID,
               DatabaseHelper.ITEM_NAME,DatabaseHelper.ITEM_QTY}, DatabaseHelper.ITEM_PLACE_ID + "=?",
                new String[] {placeID }, null, null, null, null);
        ArrayList<Item> itemList = new ArrayList<Item>();
        Item itemModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                itemModel = new Item();
                itemModel.setItemId(cursor.getString(0));
                itemModel.setItemName(cursor.getString(1));
                itemModel.setItem_qty(cursor.getString(2));
                System.out.println("Item---"+i+"  "+itemModel.getItemId()+"   "+itemModel.getItemName()+"  "+itemModel.getItem_qty());
                itemList.add(itemModel);
            }
        }
        cursor.close();
        return itemList;
    }
    public void updateItem(String _id, String name,String qty) {
//        System.out.println("in db----"+_id+"  name--"+name+"   qty-->"+qty);

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.ITEM_NAME, name);
        contentValues.put(DatabaseHelper.ITEM_QTY, qty);
        int i = database.update(DatabaseHelper.TABLE_ITEMS, contentValues, DatabaseHelper.ITEM_ID + " = ?", new String[]{String.valueOf(_id)});

//        System.out.println("Update result----"+i);
    }

    public void deleteItem(String _id) {
        database.delete(DatabaseHelper.TABLE_ITEMS, DatabaseHelper.ITEM_ID + "=" + _id, null);
    }

    public void deleteLocation(String _id) {

        database.delete(DatabaseHelper.TABLE_LOCATION, DatabaseHelper.LOC_ID + "=" + _id, null);

    }
    public void updateLocationName(String id,String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ITEM_NAME, name);

        int i = database.update(DatabaseHelper.TABLE_LOCATION, contentValues, DatabaseHelper.LOC_ID + " = ?", new String[]{String.valueOf(id)});
    }

}
