package in.vasista.inventory;

import in.vasista.vsales.DashboardActivity;
import in.vasista.vsales.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class InventoryActivity extends DashboardActivity 
{

/**
 * onCreate
 *
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 * @param savedInstanceState Bundle
 */

protected void onCreate(Bundle savedInstanceState)  
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f5);
    //setTitleFromActivityLabel (R.id.title_text); 
	setContentView(R.layout.inventory_layout);

}

/**
 * onResume
 * Called when the activity will start interacting with the user. 
 * At this point your activity is at the top of the activity stack, with user input going to it.
 * Always followed by onPause().
 *
 */

protected void onResume ()
{
   super.onResume ();
	final FrameLayout inputSearchFrame = (FrameLayout) findViewById(R.id.inputSearchFrame);   
	//final EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
	if (inputSearchFrame != null) {
		inputSearchFrame.setVisibility(View.GONE);	
		//inputSearchFrame.setText("");
	}
   
}
    
} // end class
