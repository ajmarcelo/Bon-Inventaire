package com.mobdeve.s11.group19.bon_inventaire;

public class Item {
    private String itemName, itemList, itemNote, itemExpireDate;
    private int itemNumStocks, itemID;

    public Item(){}

    // Creates an item providing all information.
    public Item(String itemName, String itemList, String itemNote, int itemNumStocks, String itemExpireDate, int itemID) {
        this.itemName = itemName;
        this.itemList = itemList;
        this.itemNote = itemNote;
        this.itemNumStocks = itemNumStocks;
        this.itemExpireDate = itemExpireDate;
        this.itemID = itemID;
    }

    /**
     * Returns the name of the item.
     * @return
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * Returns the name of the list where the item belongs to.
     * @return
     */
    public String getItemList() {
        return this.itemList;
    }

    /**
     * Returns the note of the item.
     * @return
     */
    public String getItemNote() {
        return this.itemNote;
    }

    /**
     * Returns the number of stocks of the item.
     * @return
     */
    public int getItemNumStocks() {
        return this.itemNumStocks;
    }

    /**
     * Returns the expiration date of the item.
     * @return
     */
    public String getItemExpireDate() {
        return this.itemExpireDate;
    }

    /**
     * Returns the ID of the item.
     * @return
     */
    public int getItemID() { return this.itemID; }

    /**
     * Sets the ID of the item.
     * @param id
     */
    public void setItemID(int id) { this.itemID = id;}

    /**
     * Sets the name of the list where the item belongs to.
     * @param itemList
     */
    public void setItemList(String itemList) { this.itemList = itemList;}
}
