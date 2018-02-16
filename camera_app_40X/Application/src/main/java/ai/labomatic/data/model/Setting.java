package ai.labomatic.data.model;

/**
 * Created by root on 2/9/18.
 */

public class Setting {

    // Columns
    public int id;
    public String configName;
    public String value;

    // Constructors
    public Setting() {
    }

    public Setting(String configName, String value) {
        this.configName = configName;
        this.value = value;
    }

    public Setting(int id, String configName, String value) {
        this.id = id;
        this.configName = configName;
        this.value = value;
    }

    // Getter and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
