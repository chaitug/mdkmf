package in.vasista.helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.vasista.location.MapItemFragment;
import in.vasista.location.MapsActivity;
import in.vasista.milkosoft.mdkmf.R;

/**
 * Created by Upendra on 29/2/16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

private static MapsActivity mapsActivity;

    public static DatePickerFragment newInstance(MapsActivity maps) {

        mapsActivity = maps;
        Bundle args = new Bundle();

        DatePickerFragment fragment = new DatePickerFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        Date d = new Date();
        dialog.getDatePicker().setMaxDate(d.getTime());

        try {
            // month return one month prior
            d = sdf.parse(day+"/"+month+"/"+year);
            dialog.getDatePicker().setMinDate(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }



        // Create a new instance of DatePickerDialog and return it
        return dialog;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

           mapsActivity.markLocations(calendar.getTime());
            MapItemFragment m= (MapItemFragment)getFragmentManager().findFragmentById(R.id.maplist_fragment);
            m.markLocations(calendar.getTime());
    }
}
