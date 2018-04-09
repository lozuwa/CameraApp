package ai.labomatic.data.remote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ai.labomatic.data.local.ClientsDatabaseHandler;
import ai.labomatic.data.local.SettingsDatabaseHandler;
import ai.labomatic.data.model.Folder;
import ai.labomatic.data.model.Image;
import ai.labomatic.R;
import ai.labomatic.data.model.NameSettings;
import ai.labomatic.data.model.Setting;
import ai.labomatic.ui.NavigationMenu;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class UploadImages extends Activity {

    // Constants
    private static final String TAG = "UploadImagesActivity::";
    public String FOLDER_NAME = null;

    // UI elements
    public Button uploadFilesButton;

    // Database handler instance
    public ClientsDatabaseHandler clientsDB;
    public SettingsDatabaseHandler settingsDB;

    // ProgressDialog
    private ProgressDialog progressDialog;

    // Firebase storage
    public FirebaseStorage storage;
    public StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // UI elements
        progressDialog = new ProgressDialog(UploadImages.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        uploadFilesButton = (Button) findViewById(R.id.read_folders_button);
        // Firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // Create database references
        clientsDB = new ClientsDatabaseHandler(this);
        settingsDB = new SettingsDatabaseHandler(this);
        // Initialize states
        Setting setting = settingsDB.readSettingByName("tmpFolderName");
        FOLDER_NAME = setting.getValue();
        Log.i(TAG, "Folder to read: " + FOLDER_NAME);
        setting = settingsDB.readSettingByName(NameSettings.AUTOMATIC_UPLOAD);
        String automaticUpload = setting.getValue();
        Log.i(TAG, "Automatic upload: " + automaticUpload);
        if (automaticUpload.equals("0")){
            // Nothing
        } else if (automaticUpload.equals("1")){
            uploadData();
        } else{
            Log.e(TAG, "Configuration value not recognized: " + automaticUpload);
            // Nothing
        }
        // Listeners
        uploadFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(UploadImages.this, NavigationMenu.class);
        startActivity(intent);
    }

    public void uploadData(){
        // Set a progressDialog
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        // List of Images
        List<Image> imgs = clientsDB.readImagesByFolderName(FOLDER_NAME);
        if (imgs.size() == 0){
            Log.e(TAG, "No images to upload");
            /**
             * TODO: Add message telling the user that the machine has failed and will not
             * perform the diagnostic.
             */
            Intent createPatientActivity = new Intent(UploadImages.this,
                    NavigationMenu.class);
            startActivity(createPatientActivity);
        } else{
            // Upload each image
            for (Image img: imgs){
                uploadImageWithProgressBar(img);
            }
        }
    }

    public void uploadImageWithProgressBar(Image img) {
        // Variables
        final String imageName = img.getImageName();
        final String folderName = img.getFolderName();
        final String pathToFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + folderName + File.separator + imageName;
        Log.d(TAG, imageName + "," + folderName);
        // Set a Uri
        Uri file = Uri.fromFile(new File(pathToFile));
        // Create a storage reference
        StorageReference imgRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to upload image.");
                // showToast("Failed to upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
                // and download URL.
                // Get download URI
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.i(TAG, "Success " + downloadUrl.toString());
                // Remove the file physically
                File imageFile = new File(pathToFile);
                boolean deleted = imageFile.delete();
                if (deleted){
                    Log.i(TAG, "File succesfully deleted.");
                    // Given the file was removed, now delete its field from the clientsDB
                    clientsDB.deleteImage(new Image(imageName, folderName));
                } else{
                    Log.i(TAG, "File could not be deleted.");
                }
                // Check if the database is finally clean, if so then start create patient activity
                List<Image> imgs = clientsDB.readImagesByFolderName(FOLDER_NAME);
                // If there are no more images associated with the current folder,
                // then start CreatePatientActivity and remove the folder from the clientsDB.
                if (imgs.size() == 0){
                    // Remove folder from clientsDB
                    clientsDB.deleteFolder(new Folder(FOLDER_NAME));
                    // Take out progressDialog
                    progressDialog.dismiss();
                    // Start CreatePatientActivity
                    Intent createPatientActivity = new Intent(UploadImages.this,
                            NavigationMenu.class);
                    startActivity(createPatientActivity);
                } else{
                    // Keep uploading the images
                    Log.d(TAG, "Images that are still missing: " + String.valueOf(imgs.size()));
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                showToast("Upload is " + String.valueOf(progress) + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Upload is paused");
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(UploadImages.this, message, Toast.LENGTH_SHORT).show();
    }

}
