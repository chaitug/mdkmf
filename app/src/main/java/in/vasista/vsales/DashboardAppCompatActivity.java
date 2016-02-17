package in.vasista.vsales;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.vasista.inventory.InventoryActivity;


/**
 * @author Upendra
 *
 */
public class DashboardAppCompatActivity extends AppCompatActivity {
    public DashboardAppCompatActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app_compact);
    }

    @Override
    public void setContentView(int layoutResID) {

        super.setContentView(layoutResID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public void onClickSettings ()
    {
        //startActivity(new Intent(getApplicationContext(), AboutActivity.class));
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

}
