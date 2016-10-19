package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.hr.leave.Leave;
import in.vasista.milkosoft.mdkmf.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;



public class LeaveAdapter extends ArrayAdapter<Leave> {
	  int resource;

	  public LeaveAdapter(Context context,
	                         int resource,
	                         List<Leave> items) {
	    super(context, resource, items);
	    this.resource = resource;                   
	  } 

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout leaveView;

	   Leave item = getItem(position);

	    String leaveType = item.getLeaveTypeId();
	    String leaveStatus = item.getLeaveStatus();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");	    
	    Date fromDate = item.getFromDate();
	    String fromDateStr = dateFormat.format(fromDate);
	    Date thruDate = item.getThruDate();
	    String thruDateStr = dateFormat.format(thruDate);


	    if (convertView == null) {
	    	leaveView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, leaveView, true);
	    } else {
	    	leaveView = (LinearLayout) convertView;
	    }     

	    TextView typeView = (TextView)leaveView.findViewById(R.id.leaveRowType);
	    TextView statusView = (TextView)leaveView.findViewById(R.id.leaveRowStatus);
	    TextView fromDateView = (TextView)leaveView.findViewById(R.id.leaveRowFromDate);	    
	    TextView thruDateView = (TextView)leaveView.findViewById(R.id.leaveRowThruDate);
	    
	    typeView.setText(leaveType);
	    statusView.setText(leaveStatus);
	    fromDateView.setText(fromDateStr);
	    thruDateView.setText(thruDateStr);
	    return leaveView;
	  }
}
