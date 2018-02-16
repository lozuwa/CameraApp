package ai.labomatic.util.LabomaticCamera;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by root on 2/16/18.
 */

public class FileUtils {
    // TAG
    public static final String TAG = "FileUtils::";

    // Constructor
    public FileUtils(){
    }

    /**
     * Creates a physical folder folder in the apps's directory
     * @param folderName: string that contains the name of the folder to be created
     * */
    public static boolean createFolder(String folderName) {
        // Local variables
        boolean success = false;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + folderName;
        // Create file container
        File folder = new File(path);
        // Check path to folder exists
        if (!folder.exists()) {
            success = folder.mkdir();
        } else {
            success = true;
            Log.i(TAG, "Folder already exists " + folderName);
        }
        // If the folder was created, then write to db
        if (success) {
            // Debug console
            Log.i(TAG, "Folder successfully created :: "
                    + folder.getAbsolutePath());
        } else {
            Log.e(TAG, "Folder not created");
        }
        return success;
    }

    /**
     * Validates the name of the folder according to some parameters.
     * @param foldername: a string that contains a name to be analysed.
     * */
    public static boolean validateFolderName(String foldername){
        boolean isNameOk = true;
        if (foldername.isEmpty()) {
            Log.i(TAG, "Folder is empty.");
            isNameOk = false;
        }
        if (foldername.length() < 4){
            Log.i(TAG, "Folder is too short.");
            isNameOk = false;
        }
        return isNameOk;
    }

}
