package se.chalmers.fleetspeak.fragments.Lists;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * Created by David Gustafsson on 2015-07-20.
 */
public class RoomAdpater extends ArrayAdapter<RoomListItem> {

    public RoomAdpater(Context context, int resource) {
        super(context, resource);
    }
}
