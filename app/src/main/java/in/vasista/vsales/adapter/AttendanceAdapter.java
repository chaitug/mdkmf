package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.hr.attendance.Attendance;
import in.vasista.milkosoft.mdkmf.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;



public class AttendanceAdapter extends ArrayAdapter<Attendance> {
	  int resource;

	  public AttendanceAdapter(Context context,
	                         int resource,
	                         List<Attendance> items) {
	    super(context, resource, items);
	    this.resource = resource;                   
	  } 

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout attendanceView;

	    Attendance item = getItem(position);

	    String inTime = item.getInTime();
	    String outTime = item.getOutTime();
	    String duration = item.getDuration();

	    if (convertView == null) {
	    	attendanceView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, attendanceView, true);
	    } else {
	    	attendanceView = (LinearLayout) convertView; 
	    }     

	    TextView inTimeView = (TextView)attendanceView.findViewById(R.id.attendanceRowInTime);
	    TextView outTimeView = (TextView)attendanceView.findViewById(R.id.attendanceRowOutTime);
	    TextView durationView = (TextView)attendanceView.findViewById(R.id.attendanceRowDuration);	    
	    
	    inTimeView.setText(inTime);
	    outTimeView.setText(outTime);
	    durationView.setText(duration);
	    return attendanceView;
	  }
}
