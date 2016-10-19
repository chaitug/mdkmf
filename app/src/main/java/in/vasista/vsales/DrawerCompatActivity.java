package in.vasista.vsales;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import in.vasista.milkosoft.mdkmf.R;

import in.vasista.changePassword.ChangePassword;
import in.vasista.inventory.InventoryActivity;
import in.vasista.location.LocationActivity;
import in.vasista.vsales.preference.FragmentPreferences;


/**
 * @author Upendra
 *
 */
public class DrawerCompatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerCompatActivity() {
        super();
    }

    private static final int SHOW_PREFERENCES = 1;
    static Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_compact);
    }

    @Override
    public void setContentView(int layoutResID) {

        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.setDrawerListener(toggle);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Hiding location item
        //navigationView.getMenu().getItem(1).setVisible(false);

        TextView profileName,contactNumber;

        profileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profileName);
        contactNumber = (TextView) navigationView.getHeaderView(0).findViewById(R.id.contact_number);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        profileName.setText(prefs.getString(MainActivity.USER_FULLNAME, "User Name"));

        String contact = prefs.getString(MainActivity.USER_MOBILE, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contactNumber.setText("Contact :  " + PhoneNumberUtils.formatNumber(contact,"IN"));
        }else{
            contactNumber.setText("Contact :  " + PhoneNumberUtils.formatNumber(contact));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public AppCompatDelegate getDelegate() {
        return super.getDelegate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    /**
     * ========================================
     * Custom methods for ActionBar - AppCompat
     * ========================================
     */
    public void actionBarHomeEnabled(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void setContentChildView(int layoutResID) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        View hiddenInfo = getLayoutInflater().inflate(layoutResID, coordinatorLayout, false);
        // Added child view at second position of the childs  -   ** second position - 1 **
        coordinatorLayout.addView(hiddenInfo,1);
    }
    public void setSalesDashboardTitle(int title){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBarHomeEnabled();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String retailerId = prefs.getString("storeId", "");
        getSupportActionBar().setTitle(retailerId + " : " + getResources().getString(title));
    }
    public void setPageTitle(int title){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBarHomeEnabled();
        getSupportActionBar().setTitle(getResources().getString(title));
    }
    public void setPageTitle(String title){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBarHomeEnabled();
        getSupportActionBar().setTitle(title);
    }
    /**
     * ==============================================
     * Ended Custom methods for ActionBar - AppCompat
     * ==============================================
     */
    public void hideKeyboard(){
        // Hide the Keyboard
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void showSnackBar(String message){
        CoordinatorLayout coordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Handle the click on the search button.
     * @param v view
     */
    public void onClickHome (View v)
    {
        goHome(this);
    }

    /**
     * Handle the click on the search button.
     *
     * @param v View
     *
     */
    public void onClickSearch (View v)
    {
        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }

    /**
     * Handle the click on the About button.
     */

    public void onClickAbout ()
    {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));

    }

    public void onClickSettings(){
        startActivityForResult( new Intent(getApplicationContext(), FragmentPreferences.class), SHOW_PREFERENCES);
    }


    public void onClickPlace ()
    {
        startActivity (new Intent(getApplicationContext(), LocationActivity.class));
    }
    /**
     * Handle the click of a Feature button.
     *
     * @param v View
     *
     */

    public void onClickFeature (View v)
    {
        int id = v.getId ();
        switch (id) {
            case R.id.home_btn_sales :
                startActivity (new Intent(getApplicationContext(), SalesDashboardActivity.class));
                break;
            case R.id.home_btn_hr :
                //startActivity (new Intent(getApplicationContext(), EmployeeActivity.class));
                startActivity (new Intent(getApplicationContext(), HRDashboardActivity.class));
                break;
            case R.id.home_btn_inventory :
                startActivity (new Intent(getApplicationContext(), InventoryActivity.class));
                break;
/*      case R.id.home_btn_ticket :
          startActivity (new Intent(getApplicationContext(), TicketDashboardActivity.class));
          break;  */
            default:
                break;
        }
    }

    /**
     */
    // More Methods

    /**
     * Go back to the home activity.
     *
     * @param context Context
     *
     */

    public void goHome(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String onlyHRDashboard = prefs.getString("onlyHRDashboard", "N");
        String onlySalesDashboard = prefs.getString("onlySalesDashboard", "N");
        Intent intent = new Intent(context, MainActivity.class);
        if (onlyHRDashboard.equals("Y")) {
            intent = new Intent(context, HRDashboardActivity.class);
        }
        if (onlySalesDashboard.equals("Y")) {
            intent = new Intent(context, SalesDashboardActivity.class);
        }
        intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

//        Fragment fragment = null;

        switch (id){
            case R.id.action_about :
                onClickAbout();
//                fragment = new AboutFragment();
//
//                if (fragment != null) {
//                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.frame_container, fragment).commit();
//
//                }
                break;
            case R.id.action_settings :

                 onClickSettings();
                break;
            case R.id.action_location:onClickPlace();break;
            case R.id.action_change_password:
                ChangePassword changePassword = new ChangePassword();
                //changePassword.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                changePassword.show(getFragmentManager(), "changePassword");
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
