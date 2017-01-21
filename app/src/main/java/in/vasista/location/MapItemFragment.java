package in.vasista.location;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import in.vasista.helper.RecyclerViewItemDecorator;
import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.db.LocationsDataSource;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MapItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    List<Location> locations;
    MyMapItemRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private static MapsActivity mapsActivity;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MapItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MapItemFragment newInstance(int columnCount) {
        MapItemFragment fragment = new MapItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(false);
            recyclerView.addItemDecoration(new RecyclerViewItemDecorator(this.getActivity(), null));
            if (mColumnCount <= 1) {

                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
            } else {

                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            markLocations(null);

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Location item);
    }

    public final void markLocations(Date date){

        ArrayList<Integer> indexes = new ArrayList<>();

        LocationsDataSource datasource;
        datasource = new LocationsDataSource(this.getActivity());
        datasource.open();
        Log.v("date", Calendar.getInstance().getTime()+"");
        Date today = Calendar.getInstance().getTime();
        //.
        locations = datasource.getSyncedLocations();


        for (Location location:locations){

            if (date != null) {

                this.getActivity().findViewById(R.id.clearFilter).setVisibility(View.VISIBLE);
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(location.getCreatedDate());
                cal2.setTime(date);

               Log.v("current date",location.getCreatedDate()+"");



                boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                        &&  cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);


                if (!sameDay){
                    continue;
                }

            }
            // New indexes
            indexes.add(locations.indexOf(location));


        }

        // Update new locations
        ArrayList<Location> newLocations = new ArrayList<>();
        for (int index:indexes){
            newLocations.add(locations.get(index));
        }

        locations = newLocations;
        Collections.reverse(locations);
        adapter = new MyMapItemRecyclerViewAdapter(locations, mListener);
        recyclerView.setAdapter(adapter);
        //mapsActivity.showDatePickerDialog();





    }



}
