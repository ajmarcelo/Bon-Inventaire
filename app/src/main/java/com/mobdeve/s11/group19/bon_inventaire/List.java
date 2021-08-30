package com.mobdeve.s11.group19.bon_inventaire;

public class List {
    private String listName, listDescription;
    private int listID;

    public List(String listName, String listDescription, int listID) {
        this.listName = listName;
        this.listDescription = listDescription;
        this.listID = listID;
    }

    public List(){}

    public String getListName() {
        return this.listName;
    }

    public String getListDescription() {
        return this.listDescription;
    }

    public int getListID() {
        return this.listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }
}
