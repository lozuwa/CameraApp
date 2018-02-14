package com.example.android.labomatic.Databases.Clients;

/**
 * Created by root on 2/5/18.
 */

public class Images {
    // Fields
    public int id;
    public String imageName;
    public String folderName;

    // Constructors
    public Images() {
    }

    public Images(String imageName, String folderName) {
        this.imageName = imageName;
        this.folderName = folderName;
    }

    public Images(int id, String imageName, String folderName) {
        this.id = id;
        this.imageName = imageName;
        this.folderName = folderName;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
