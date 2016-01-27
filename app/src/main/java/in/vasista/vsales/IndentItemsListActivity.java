package in.vasista.vsales;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import in.vasista.vsales.indent.IndentItemsListFragment;

public class IndentItemsListActivity extends DashboardAppCompatActivity {

	IndentItemsListFragment indentItemsListFragment;
	FloatingActionButton fab;
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view 
		setContentChildView(R.layout.indentitems_layout);
		setSalesDashboardTitle(R.string.title_feature1_list);
		FragmentManager fm = getFragmentManager();
		indentItemsListFragment= (IndentItemsListFragment) fm.findFragmentById(R.id.indentitems_list_fragment);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.show();
		//fab.setImageResource(R.drawable.title_upload);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				editMode = true;
				invalidateOptionsMenu();
				indentItemsListFragment.editIndentAction();
				fab.hide();
			}
		});
		
/*        if (savedInstanceState == null) {
    		PayslipItemsListFragment orderItemsFragment = 
    				(PayslipItemsListFragment)getSupportFragmentManager().findFragmentById(R.id.orderitems_list_fragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.layout.orderitems_layout, orderItemsFragment).commit();
        }*/		
	}
	boolean editMode = false;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.indent_menu, menu);
		if (editMode){
			menu.findItem(R.id.action_indent_upload).setVisible(false);
		}else{
			menu.findItem(R.id.action_indent_done).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.action_indent_done:
				editMode = false;
				fab.show();
				invalidateOptionsMenu();
				indentItemsListFragment.doneIndentAction();
				return true;
			case R.id.action_indent_upload:
				editMode = false;
				invalidateOptionsMenu();
				indentItemsListFragment.uploadIndentAction(item);
				return true;
		}
		return false;
	}
	
}
