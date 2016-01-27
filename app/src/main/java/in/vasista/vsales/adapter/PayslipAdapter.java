package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.hr.payslip.Payslip;
import in.vasista.vsales.R;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.order.Order;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class PayslipAdapter extends ArrayAdapter<Payslip> {
	  int resource;

	  public PayslipAdapter(Context context,
	                         int resource,
	                         List<Payslip> items) {
	    super(context, resource, items);
	    this.resource = resource;
	  } 

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout payslipView;

	   Payslip item = getItem(position);

	    String payrollPeriod = item.getPayrollPeriod();
	    String netAmount = String.format("%.2f", item.getNetAmount());


	    if (convertView == null) {
	    	payslipView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, payslipView, true);
	    } else {
	    	payslipView = (LinearLayout) convertView;                 
	    }     

	    TextView rowView = (TextView)payslipView.findViewById(R.id.payslipRowId);
	    rowView.setText(payrollPeriod + "   Net Amount: Rs" + netAmount);
	       
	    return payslipView;
	  }
}
