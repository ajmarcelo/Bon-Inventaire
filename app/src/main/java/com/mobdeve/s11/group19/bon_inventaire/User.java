package com.mobdeve.s11.group19.bon_inventaire;

public class User {
    private String email;
    private String password;

    //Creates a user providing all information for authentication.
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Returns the email of the user.
     * @return The String containing the user's email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the password of the user.
     * @return The String containing the user's password
     */
    public String getPassword() {
        return this.password;
    }

}
