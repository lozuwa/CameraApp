package com.example.android.labomatic.Databases.Clients;

/**
 * Folders table.
 */

public class Folders {
    // Fields
    public int id;
    public String folderName;

    public Folders() {
    }

    public Folders(String folderName) {
        this.folderName = folderName;
    }

    public Folders(int id, String folderName) {
        this.id = id;
        this.folderName = folderName;
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
}
