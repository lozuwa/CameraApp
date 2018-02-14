package com.example.android.camera2basic.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.camera2basic.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    // Background animation
    public LinearLayout container;
    public AnimationDrawable anim;

    // UI elements
    public Button signInButton;
    public Button reportProblemButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // UI elements
        signInButton = (Button) findViewById(R.id.sign_in_button);
        reportProblemButton = (Button) findViewById(R.id.report_problem_button);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        // Button animations
        final Animation animTranslateReportProblem = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha);
        // Background animation
        container = (LinearLayout) findViewById(R.id.main_container);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(5000);

        // Click listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a progress bar to the user while authenticating firebase
                // In case of success follow the next screen
//                Intent menuActivity = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(menuActivity);
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

    public void showToast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}

