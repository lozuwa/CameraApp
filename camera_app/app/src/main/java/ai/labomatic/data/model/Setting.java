package ai.labomatic.data.model;

/**
 * Settings table for the settings database.
 */

public class Setting {

    // Columns
    public int id;
    public String name;
    public String value;

    // Constructors
    public Setting() {
    }

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Setting(int id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    // Getter and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
