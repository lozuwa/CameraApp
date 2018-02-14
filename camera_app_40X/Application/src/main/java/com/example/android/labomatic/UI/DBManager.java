package com.example.android.labomatic.UI;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.labomatic.Databases.Clients.ClientsDatabaseHandler;
import com.example.android.labomatic.Databases.Clients.Folders;
import com.example.android.labomatic.Databases.Clients.Images;
import com.example.android.labomatic.Databases.Users.User;
import com.example.android.labomatic.Databases.Users.UsersDatabaseHandler;
import com.example.android.labomatic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DBManager extends Activity implements AdapterView.OnItemSelectedListener{

    // Static strings
    private static final String TAG = "DatabaseManager:";

    // Dynamic variables
    public String selectedTable;

    // UI elements
    private Spinner tablesSpinner;
    private Spinner dataSpinner;
    private Button selectTableButton;
    private Button dropTableButton;
    private Button removeButton;

    // Database clients
    ClientsDatabaseHandler clientsDB;
    // Database users
    UsersDatabaseHandler usersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmanager);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Database
        clientsDB = new ClientsDatabaseHandler(this);
        usersDB = new UsersDatabaseHandler(this);
        // UI Elements
        tablesSpinner = (Spinner) findViewById(R.id.tables_spinner);
        dataSpinner = (Spinner) findViewById(R.id.data_in_table_spinner);
        selectTableButton = (Button) findViewById(R.id.select_table_button);
        dropTableButton = (Button) findViewById(R.id.drop_table_button);
        loadTablesSpinner();
        // Click listeners
        selectTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load the data in the dataSpinner
                if (selectedTable.equals("None")){
                    List<String> list = new ArrayList<String>();
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("folders")){
                    // Get the folders
                    List<Folders> folders = clientsDB.readAllFolders();
                    Log.i(TAG, "Amount of folders: " + String.valueOf(folders.size()));
                    // Load the data as a string
                    List<String> list = new ArrayList<String>();
                    // Add the data to the String list
                    for (Folders fold: folders){
                        String toString = String.valueOf(fold.getId())
                                + ", " + String.valueOf(fold.getfolderName());
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("images")){
                    // Get the images
                    List <Images> imgs = clientsDB.readAllImages();
                    Log.i(TAG, "Amount of images: " + String.valueOf(imgs.size()));
                    // Load the data to a String list
                    List<String> list = new ArrayList<String>();
                    for (Images img: imgs){
                        String toString = img.getId() + "," + img.getImageName().split("ple")[0]
                                + "," + img.getFolderName();
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("users")){
                    // Get the users
                    List<User> allUsers = usersDB.readAllUsers();
                    Log.i(TAG, "Amount of users: " + String.valueOf(allUsers.size()));
                    // Load users into a list
                    List<String> list = new ArrayList<String>();
                    for (User user: allUsers){
                        String toString = user.getId() + ","
                                + user.getEmail() + "," + user.getPassword();
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else{
                    // Do nothing
                }
            }
        });

        dropTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTable.equals("images")){
                    clientsDB.dropImagesTable();
                } else if (selectedTable.equals("folders")){
                    clientsDB.dropFoldersTable();
                } else if (selectedTable.equals("users")){
                    // Get users
                    List<User> allUsers = usersDB.readAllUsers();
                    if (allUsers.size() == 0){
                        // Do nothing
                    } else{
                        User userr = allUsers.get(0);
                        // Remove user from firebase
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // Get auth credentials from the user for re-authentication. The example below shows
                        // email and password credentials but there are multiple possible providers,
                        // such as GoogleAuthProvider or FacebookAuthProvider.
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userr.getEmail(), userr.getPassword());

                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");
                                                            usersDB.dropTableUsers();
                                                        } else{
                                                            Log.d(TAG,
                                                                    "User account not deleted.",
                                                                    task.getException());
                                                        }
                                                    }
                                                });

                                    }
                                });
                    }
                }else{
                    String columns[] = clientsDB.readColumnsImagesTable();
                    for (String col: columns){
                        showToast(col);
                    }
                }
            }
        });

        dataSpinner.setOnItemSelectedListener(this);
        tablesSpinner.setOnItemSelectedListener(this);
    }

    public void loadTablesSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("None");
        list.add("folders");
        list.add("images");
        list.add("users");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tablesSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.tables_spinner) {
            selectedTable = adapterView.getItemAtPosition(i).toString();
            Log.i(TAG, "Selected table: " + selectedTable);
        } else if (spinner.getId() == R.id.data_in_table_spinner){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void showToast(String message){
        Toast.makeText(DBManager.this, message, Toast.LENGTH_SHORT).show();
    }

}
