package com.example.android.camera2basic;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class CreatePatient extends Activity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView nameUserEditText;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);
        /** Instantiate UI elements */
        nameUserEditText = (AutoCompleteTextView) findViewById(R.id.patient_name_editText);
        /** Create user */
        Button createPatientButton = (Button) findViewById(R.id.create_patient_button);
        createPatientButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUserCreation();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptUserCreation() {
        
        // Reset errors.
        nameUserEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = nameUserEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            nameUserEditText.setError(getString(R.string.error_field_required));
            focusView = nameUserEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
        }
    }

    public void showToast(String message){
        Toast.makeText(CreatePatient.this, "Message", Toast.LENGTH_SHORT).show();
    }


    /**
     SQLiteDatabase db = mDbHelper.getWritableDatabase();

     // New value for one column
     ContentValues values = new ContentValues();
     values.put(FeedEntry.COLUMN_NAME_TITLE, title);

     // Which row to update, based on the title
     String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
     String[] selectionArgs = { "MyTitle" };

     int count = db.update(
     FeedReaderDbHelper.FeedEntry.TABLE_NAME,
     values,
     selection,
     selectionArgs);
     **/

}

