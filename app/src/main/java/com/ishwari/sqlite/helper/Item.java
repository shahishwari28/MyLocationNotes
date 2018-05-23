package com.ishwari.sqlite.helper;

/**
 * Created by xyz on 11/30/2017.
 */

public class Item {
    String itemId;
//    int listId_fk;
    String placeId_fk;
    String itemName;
    String item_qty;

    public Item(){

    }
    public Item(String placeId_fk, String itemName, String item_qty) {

        this.placeId_fk=placeId_fk;

        this.itemName = itemName;
        this.item_qty = item_qty;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPlaceId_fk() {
        return placeId_fk;
    }

    public void setPlaceId_fk(String placeId_fk) {
        this.placeId_fk = placeId_fk;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }


}
