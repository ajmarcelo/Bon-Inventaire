package com.mobdeve.s11.group19.bon_inventaire;

public class List {
    private String listName, listDescription;
    private int listID;

    // Creates a list providing all information.
    public List(String listName, String listDescription, int listID) {
        this.listName = listName;
        this.listDescription = listDescription;
        this.listID = listID;
    }
    // Creates a list.
    public List(){}

    /**
     * Returns the name of the list.
     * @return Returns a String containing the list's name
     */
    public String getListName() {
        return this.listName;
    }

    /**
     * Returns the description of the list.
     * @return Returns a String containing the list's description
     */
    public String getListDescription() {
        return this.listDescription;
    }

    /**
     * Returns the ID of the list
     * @return Returns a String containing the list's ID
     */
    public int getListID() {
        return this.listID;
    }

    /**
     * Sets the ID of the list.
     * @param listID The ID of the list to be assigned
     */
    public void setListID(int listID) {
        this.listID = listID;
    }
}
