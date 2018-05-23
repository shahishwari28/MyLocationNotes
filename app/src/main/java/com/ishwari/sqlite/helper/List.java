package com.ishwari.sqlite.helper;

/**
 * Created by xyz on 11/30/2017.
 */

public class List {

    int listId;
    String listName;
    int placeId_fk;
    String note;

    public List(){

    }


    public List(int placeId_fk, String listName,String note) {

        this.listName = listName;
this.placeId_fk=placeId_fk;
        this.note=note;
    }


    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }


    public int getPlaceId_fk() {
        return placeId_fk;
    }

    public void setPlaceId_fk(int placeId_fk) {
        this.placeId_fk = placeId_fk;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }




}
