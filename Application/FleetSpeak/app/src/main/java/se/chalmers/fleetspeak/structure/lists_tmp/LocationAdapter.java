package se.chalmers.fleetspeak.structure.lists_tmp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by David Gustafsson on 2015-08-17.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;
        TextView nv;

        LocationViewHolder(View viewItem){
            super(viewItem);
        }
}
}
