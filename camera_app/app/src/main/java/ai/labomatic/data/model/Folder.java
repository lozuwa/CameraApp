package ai.labomatic.data.model;

/**
 * Folders table.
 */

public class Folder {
    // Fields
    public int id;
    public String folderName;

    public Folder() {
    }

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    public Folder(int id, String folderName) {
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
