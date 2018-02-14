package com.example.android.labomatic.Databases.Users;

/**
 * Created by HP on 23/10/2017.
 */

public class User {

    // fields
    private int id;
    private String email;
    private String password;
    
    // Constructors
    public User(){

    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(int id, String email, String password){
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
