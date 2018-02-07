package com.example.android.camera2basic.Debug;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.camera2basic.Databases.Clients.ClientsDatabaseHandler;
import com.example.android.camera2basic.Databases.Clients.Folders;
import com.example.android.camera2basic.R;

import java.util.List;

public class MainActivity extends Activity {

    public Button read_button;
    public Button read_all_button;
    public Button write_button;
    public Button remove_tables_button;

    public EditText image_name;
    public EditText folder_name;
    public EditText completed;

    public ClientsDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create db instance
        db = new ClientsDatabaseHandler(getApplicationContext());

        // UI elements
        read_all_button = (Button) findViewById(R.id.read_all_button);
        read_button = (Button) findViewById(R.id.read_button);
        write_button = (Button) findViewById(R.id.write_button);
        remove_tables_button = (Button) findViewById(R.id.remove_tables);
        image_name = (EditText) findViewById(R.id.image_name);
        folder_name = (EditText) findViewById(R.id.folder_name);
        completed = (EditText) findViewById(R.id.completed);

        db.createFolder(new Folders("Rodrigo", 0));

        // Click listeners
        read_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Folders> folders = db.readAllFolders();
//                for (Folders fold: folders){
//                    Toast.makeText(MainActivity.this, fold.getfolderName(), Toast.LENGTH_SHORT).show();
//                }
            }
        });

        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Read button");
            }
        });

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Folders folder = new Folders();
//                folder.setfolderName("Rodrigo");
//                folder.setCompleted(0);
//                db.createFolder(folder);
            }
        });

        remove_tables_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                db.dropTables();
            }
        });

    }

    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}

