package ai.labomatic.ui;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import ai.labomatic.R;
import ai.labomatic.data.local.UsersDatabaseHandler;
import ai.labomatic.data.model.User;
import ai.labomatic.ui.LabomaticCamera.base.AutomaticAnalysisScreenFragment;
import ai.labomatic.ui.LabomaticCamera.base.ManualAnalysisFragment;
import ai.labomatic.ui.Settings.SettingsFragment;

public class NavigationMenu extends AppCompatActivity {

    // Databases
    public UsersDatabaseHandler usersDB;

    // UI elements
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView usernameTextView;

    // Permission variables
    public int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    public int PERMISSION_CAMERA = 2;
    public int PERMISSION_READ_EXTERNAL_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Database
        usersDB = new UsersDatabaseHandler(this);

        // Grant permissions
        grantPermissions();

        // UI elements
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Email textview
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        usernameTextView = (TextView) header.findViewById(R.id.username_text_view);

        // Load username
        User user = usersDB.readUserById(1);
        usernameTextView.setText(user.getEmail());

        // Drawer layout
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        final NavigationView nvDrawer = (NavigationView) findViewById(R.id.nv);
        // Setup listeners
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);
        // Select by default the first state
        //nvDrawer.getMenu().findItem(R.id.automatic_analysis_screen).setChecked(true);
        setFirstItemNavigationView(nvDrawer);
    }

    private void setFirstItemNavigationView(NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.automatic_analysis_screen);
        navigationView.getMenu().performIdentifierAction(R.id.automatic_analysis_screen, 0);
    }

    public void selecIterDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()){
            case R.id.automatic_analysis_screen:
                fragmentClass = AutomaticAnalysisScreenFragment.class;
                break;
            case R.id.manual_analysis_screen:
                fragmentClass = ManualAnalysisFragment.class;
                break;
            case R.id.settings_screen:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = AutomaticAnalysisScreenFragment.class;
                break;
        }
        try{
            myFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flcontent, myFragment)
                .commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView){
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selecIterDrawer(item);
                    return false;
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Request permissions
    public void grantPermissions(){
        // Permission for External Storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
        // Permission for Internal Storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }
        // Permission for camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    // Whether permissions are allowed or not
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Write external storage permission granted");
                } else {
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Camera permission granted");
                } else {
                }
                return;
            }
            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Read external storage permission granted");
                } else {
                }
                return;
            }
        }
    }

    /**
     * Shows a specific message in a toast
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message) {
        Toast.makeText(NavigationMenu.this, message, Toast.LENGTH_SHORT).show();
    }

}
