package com.mobdeve.s11.group19.bon_inventaire;

public class Item {
    private String itemName, itemList, itemNote, itemExpireDate;
    private int itemNumStocks, itemID;

    public Item(){}

    // Creates an item providing all information. This used in the DataHelper class.
    public Item(String itemName, String itemList, String itemNote, int itemNumStocks, String itemExpireDate, int itemID) {
        this.itemName = itemName;
        this.itemList = itemList;
        this.itemNote = itemNote;
        this.itemNumStocks = itemNumStocks;
        this.itemExpireDate = itemExpireDate;
        this.itemID = itemID;
    }

    public String getItemName() {
        return this.itemName;
    }

    public String getItemList() {
        return this.itemList;
    }

    public String getItemNote() {
        return this.itemNote;
    }

    public int getItemNumStocks() {
        return this.itemNumStocks;
    }

    public String getItemExpireDate() {
        return this.itemExpireDate;
    }

    public int getItemID() { return this.itemID; }

    public void setItemID(int id) { this.itemID = id;}

    public void setItemList(String itemList) { this.itemList = itemList;}
}
