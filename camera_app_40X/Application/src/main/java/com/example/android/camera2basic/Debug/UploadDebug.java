package com.example.android.camera2basic.Debug;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.camera2basic.Databases.Clients.ClientsDatabaseHandler;
import com.example.android.camera2basic.Databases.Clients.Folders;
import com.example.android.camera2basic.Databases.Clients.Images;
import com.example.android.camera2basic.R;
import com.example.android.camera2basic.UI.CreatePatient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadDebug extends Activity {

    // Constants
    private static final String TAG = "UPLOAD_DEBUG:";

    // UI elements
    public Button readFoldersButton;
    public Button signInButton;

    // Database handler instance
    ClientsDatabaseHandler db;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_debug);
        // UI elements
        readFoldersButton = (Button) findViewById(R.id.read_folders_button);
        readFoldersButton.setEnabled(false);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        // Firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // Init db
        db = new ClientsDatabaseHandler(this);
        // Listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Authenticate to firebase
                signInToFirebase("lozuwaucb@gmail.com", "lozapython35");
            }
        });

        readFoldersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Data structures
                List<Folders> notCompletedFolders = new ArrayList<Folders>();
                // Read all folders
                List<Folders> allFolders= readFolders(false);
                // Filter the folders that are not complete
                for (Folders fold: allFolders){
                    if (fold.getCompleted() == 0){
                        notCompletedFolders.add(fold);
                    } else{
                        // do nothing
                    }
                }
                // Iterate over the not completed folders and query the images
                for (Folders fold: notCompletedFolders){
                    // Get folder name
                    String folderName = fold.getfolderName();
                    // Query the images associated with this folder name
                    List<Images> imgs = db.readImagesByFolderName(folderName);
                    // Upload each image to firestore
                    for (Images img: imgs){
                        uploadImage(img);
                    }
                }
                Intent createPatientActivity = new Intent(UploadDebug.this, CreatePatient.class);
                startActivity(createPatientActivity);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void uploadImage(Images img){
        // Variables
        final String imageName = img.getImageName();
        final String folderName = img.getFolderName();
        final String pathToFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + folderName + File.separator + imageName;
        Log.d(TAG, imageName + "," + folderName);
        // Set a Uri
        Uri file = Uri.fromFile(new File(pathToFile));
        // Create a reference
        StorageReference imgRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "Failed to upload image.");
//                showToast("Failed to upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                // Get download URI
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.i(TAG, "Success " + downloadUrl.toString());
                // Remove the file physically
                File imageFile = new File(pathToFile);
                boolean deleted = imageFile.delete();
                if (deleted){
                    Log.i(TAG, "File succesfully deleted.");
                    // Given the file was removed, now delete its field from the db
                    db.deleteImage(new Images(imageName, folderName));
                } else{
                    Log.i(TAG, "File could not be deleted.");
                }
            }
        });
    }

    public List<Folders> readFolders(boolean show){
        List<Folders> allFolders = new ArrayList<Folders>();
        allFolders = db.readAllFolders();
        if (show) {
            for (Folders fold : allFolders) {
                String log = "Id: " + fold.getId() + " Folder name: " + fold.getfolderName()
                        + " Completed: " + fold.getCompleted();
                // showToast(log);
            }
        } else {
            // do nothing
        }
        return allFolders;
    }

    public void showToast(String message){
        Toast.makeText(UploadDebug.this, message, Toast.LENGTH_SHORT).show();
    }

    public void signInToFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG:AUTH:FIREBASE", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            readFoldersButton.setEnabled(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DEBUG:AUTH:FIREBASE", "signInWithEmail:failure", task.getException());
                            Toast.makeText(UploadDebug.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            readFoldersButton.setEnabled(false);
                        }
                    }
                });
    }

    // Firebase storage
    public FirebaseStorage storage;
    public StorageReference storageRef;

}
