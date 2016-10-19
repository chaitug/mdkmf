package in.vasista.vsales.adapter;


import in.vasista.hr.payslip.PayslipItem;
import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.indent.IndentItem;
import in.vasista.vsales.order.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PayslipItemAdapter extends ArrayAdapter<PayslipItem>{
	public static final String module = PayslipItemAdapter.class.getName();
	
	  int resource;
	  boolean isEditable = false;
	  List<PayslipItem> myItems;
	  public PayslipItemAdapter(Context context,
	                         int resource,
	                         List<PayslipItem> items) {
	    super(context, resource, items);
	    this.myItems = items;
	    this.resource = resource;
	  }

	  public boolean isEditable() {
		  return isEditable;
	  }

		public void setEditable(boolean isEditable) {
			this.isEditable = isEditable;
		}
		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout payslipItemView;

	    PayslipItem item = getItem(position);
	    String payheadType = item.getPayheadType();
	    String amount = String.format("%.2f", item.getPayheadAmount());

	    if (convertView == null) {
	    	payslipItemView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, payslipItemView, true);
	    } else {
	    	payslipItemView = (LinearLayout) convertView;
	    }

	    TextView payheadTypeView = (TextView)payslipItemView.findViewById(R.id.payheadName);
	    TextView amountView = (TextView)payslipItemView.findViewById(R.id.payheadNetAmount);

	    payheadTypeView.setText(payheadType);	    
	    amountView.setText(amount);
    
	    return payslipItemView;
	  }
}

