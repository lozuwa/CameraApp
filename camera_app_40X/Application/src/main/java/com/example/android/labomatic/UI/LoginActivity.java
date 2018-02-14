package com.example.android.labomatic.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.labomatic.Databases.Users.User;
import com.example.android.labomatic.Databases.Users.UsersDatabaseHandler;
import com.example.android.labomatic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    // TAG
    private static final String TAG = "LoginActivity::";

    // Database
    public UsersDatabaseHandler db;

    // Background animation
    public LinearLayout container;
    public AnimationDrawable anim;

    // UI elements
    public Button logInButton;
    public Button reportProblemButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView signInTextView;
    private ProgressDialog progressDialog;

    // Secret access keys
    private final String secretUsername = "labo@";
    private final String secretPassword = "labom";

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Database
        db = new UsersDatabaseHandler(this);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        // UI elements
        logInButton = (Button) findViewById(R.id.log_in_button);
        reportProblemButton = (Button) findViewById(R.id.report_problem_button);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        signInTextView = (TextView) findViewById(R.id.sign_in_text_view);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        // Button animations
        final Animation animTranslateReportProblem = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha);
        // Background animation
        container = (LinearLayout) findViewById(R.id.main_container);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(5000);
        // How do we initialize the activity
        final String AutomaticLogin = getIntent().getStringExtra("AutomaticLogin");
        /**
         * If there is an user already registered, then load its data
         * from the beginning.
         * */
        if (AutomaticLogin == null){
            List<User> allUsers = db.readAllUsers();
            if (allUsers.size() == 0){
                // Do nothing
            } else{
                // Get the username and load its data in the EditTexts
                User user = allUsers.get(0);
                usernameEditText.setText(user.getEmail());
                passwordEditText.setText(user.getPassword());
                // Show a progress bar to the user while authenticating firebase
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                // Validate username and password texts
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // True if the editTexts are ok, otherwise false
                boolean resultValidationUsername = validateUsername(username);
                boolean resultValidationPassword = validatePassword(password);
                if (resultValidationUsername && resultValidationPassword){
                    firebaseSignInWithEmailAndPassword(username, password);
                } else{
                    showToast("EditTexts are not valid.");
                }
            }
        } else{
            // Start with empty textFields
        }
        // Click listeners
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate username and password texts
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // True if the editTexts are ok, otherwise false
                boolean resultValidationUsername = validateUsername(username);
                boolean resultValidationPassword = validatePassword(password);
                if (resultValidationUsername && resultValidationPassword){
                    // Show a progress bar to the user while authenticating firebase
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating ...");
                    progressDialog.show();
                    // In case of success follow the next screen
                    boolean resultAdmin = attemptAdminLogIn(username, password);
                    if (resultAdmin){
                        progressDialog.dismiss();
                        Intent menuActivity = new Intent(LoginActivity.this, DBManager.class);
                        startActivity(menuActivity);
                    } else{
                        firebaseSignInWithEmailAndPassword(username, password);
                    }
                } else{
                    showToast("Username or password are not valid.");
                }
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate username and password texts
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // True if the editTexts are ok, otherwise false
                boolean resultValidationUsername = validateUsername(username);
                boolean resultValidationPassword = validatePassword(password);
                if (resultValidationUsername && resultValidationPassword){
                    // Show a progress bar to the user while authenticating firebase
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Signing in ...");
                    progressDialog.show();
                    // Check how many users are available, only one is allowed
                    List<User> allUsers= db.readAllUsers();
                    if (allUsers.size() == 0){
                        // Proceed to create user
                        firebaseCreateuserWithEmailAndPassword(username, password);
                    } else{
                        progressDialog.dismiss();
                        showToast("There is already an user, cannot create another one.");
                    }
                } else {
                    showToast("Username or password are not valid.");
                }
            }
        });

        reportProblemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animTranslateReportProblem);
            }
        });

        // Animation listeners
        animTranslateReportProblem.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO: Start implicit activity to send email
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    public boolean validatePassword(String password){
        // Reset errors
        usernameEditText.setError(null);
        boolean cancel = false;
        View focusView = null;
        // Determine errors
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password_empty));
            focusView = passwordEditText;
            cancel = true;
        } else{}
        if (password.length()<4){
            Log.i(TAG, String.valueOf(password.length()));
            passwordEditText.setError(getString(R.string.error_invalid_password_short));
            focusView = passwordEditText;
            cancel = true;
        } else{}
        // Finally
        if (cancel){
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateUsername(String username){
        // Reset errors.
        passwordEditText.setError(null);
        // Determine errors
        boolean cancel = false;
        View focusView = null;
        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            focusView = usernameEditText;
            cancel = true;
        } else{}
        if (username.length()<4){
            usernameEditText.setError(getString(R.string.error_invalid_username));
            focusView = usernameEditText;
            cancel = true;
        } else{}
        if (!username.contains("@")){
            usernameEditText.setError(getString(R.string.error_not_valid_email));
            focusView = usernameEditText;
            cancel = true;
        } else{}
        // Finally
        if (cancel){
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean attemptAdminLogIn(String username, String password){
        // In case the admin wants to log in
        if (username.equals(secretUsername) && password.equals(secretPassword)){
            showToast("Welcome admin");
            return true;
        }
        return false;
    }

    public boolean userExists(String username){
        boolean exists = false;
        List<User> allUsers = db.readAllUsers();
        if (allUsers.size() == 0){
            // Continue
        } else{
            User user = db.readUserByEmail(username);
            if (user.getEmail().isEmpty()){
                // The user does not exist
            } else{
                exists = true;
            }
        }
        return exists;
    }

    public void firebaseSignInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            Intent menuActivity = new Intent(LoginActivity.this, CreatePatient.class);
                            startActivity(menuActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed. Email does not exist or internet" +
                                            " is not available.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                }
        });
    }

    public void firebaseCreateuserWithEmailAndPassword(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Save in local database
                            db.createUser(new User(email, password));
                            // Kill progressDialog
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Failed to create user.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showToast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}

