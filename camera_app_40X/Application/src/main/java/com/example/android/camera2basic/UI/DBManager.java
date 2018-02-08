package com.example.android.camera2basic.UI;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.android.camera2basic.Databases.Clients.ClientsDatabaseHandler;
import com.example.android.camera2basic.Databases.Clients.Folders;
import com.example.android.camera2basic.Databases.Clients.Images;
import com.example.android.camera2basic.R;

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

    // Database
    ClientsDatabaseHandler clientsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmanager);
        // Database
        clientsDB = new ClientsDatabaseHandler(this);
        // UI Elements
        tablesSpinner = (Spinner) findViewById(R.id.tables_spinner);
        dataSpinner = (Spinner) findViewById(R.id.data_in_table_spinner);
        selectTableButton = (Button) findViewById(R.id.select_table_button);
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
                                + ", " + String.valueOf(fold.getfolderName())
                                + ", " + String.valueOf(fold.getCompleted());
                        list.add(toString);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                        android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dataSpinner.setAdapter(dataAdapter);
                    }
                } else if (selectedTable.equals("images")){
                    // Get the images
                    List <Images> imgs = clientsDB.readAllImages();
                    Log.i(TAG, "Amount of images: " + String.valueOf(imgs.size()));
                    // Load the data to a String list
                    List<String> list = new ArrayList<String>();
                    for (Images img: imgs){
                        String toString = img.getId() + "," + img.getImageName()
                                + "," + img.getFolderName();
                        list.add(toString);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DBManager.this,
                                android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dataSpinner.setAdapter(dataAdapter);
                    }
                } else{
                    // Do nothing
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

}
