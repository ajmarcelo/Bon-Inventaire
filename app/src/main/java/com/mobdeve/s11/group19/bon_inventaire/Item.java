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
     * @return Returns a String containing the item's name
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * Returns the name of the list where the item belongs to.
     * @return Returns a String containing the list's name
     */
    public String getItemList() {
        return this.itemList;
    }

    /**
     * Returns the note of the item.
     * @return Returns a String containing the item's note
     */
    public String getItemNote() {
        return this.itemNote;
    }

    /**
     * Returns the number of stocks of the item.
     * @return Returns an integer value of the item's number of stock
     */
    public int getItemNumStocks() {
        return this.itemNumStocks;
    }

    /**
     * Returns the expiration date of the item.
     * @return Returns a String containing the item's expiration date
     */
    public String getItemExpireDate() {
        return this.itemExpireDate;
    }

    /**
     * Returns the ID of the item.
     * @return Returns an integer value of the item's ID
     */
    public int getItemID() { return this.itemID; }

    /**
     * Sets the ID of the item.
     * @param id The unique ID to be assigned on the item
     */
    public void setItemID(int id) { this.itemID = id;}

    /**
     * Sets the name of the list where the item belongs to.
     * @param itemList The name of the list to be assigned
     */
    public void setItemList(String itemList) { this.itemList = itemList;}
}
