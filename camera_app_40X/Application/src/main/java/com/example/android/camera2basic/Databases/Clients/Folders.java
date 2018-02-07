package com.example.android.camera2basic.Databases.Clients;

/**
 * Folders table.
 */

public class Folders {
    // Fields
    public int id;
    public String folderName;
    public int completed;

    public Folders() {
    }

    public Folders(String folderName, int completed) {
        this.folderName = folderName;
        this.completed = completed;
    }

    public Folders(int id, String folderName, int completed) {
        this.id = id;
        this.folderName = folderName;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfolderName() {
        return folderName;
    }

    public void setfolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
