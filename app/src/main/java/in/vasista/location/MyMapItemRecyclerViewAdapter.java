package in.vasista.location;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import in.vasista.location.MapItemFragment.OnListFragmentInteractionListener;
import in.vasista.milkosoft.mdkmf.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Location} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMapItemRecyclerViewAdapter extends RecyclerView.Adapter<MyMapItemRecyclerViewAdapter.ViewHolder> {

    private final List<Location> mValues;
    private final OnListFragmentInteractionListener mListener;
    SimpleDateFormat dateFormat;

    public MyMapItemRecyclerViewAdapter(List<Location> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mapitem, parent, false);
        dateFormat = new SimpleDateFormat(parent.getContext().getString(R.string.date_format), Locale.getDefault());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.id.setText(""+(position+1));
        holder.head.setText(mValues.get(position).getNoteName());
        holder.body.setText(mValues.get(position).getNoteInfo());


        holder.date.setText(dateFormat.format(mValues.get(position).getCreatedDate()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView id;
        public final TextView head,body,date;
        public Location mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            id = (TextView) view.findViewById(R.id.id);
            head = (TextView) view.findViewById(R.id.infoHeading);
            body = (TextView) view.findViewById(R.id.infoDesc);
            date = (TextView) view.findViewById(R.id.infodate);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + head.getText() + "'";
        }
    }
}
