package se.chalmers.fleetspeak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by TwiZ on 2014-10-08.
 * TODO: Make this an inner class in activity_Bookmarks?
 *  */
public class BookmarkListAdapter extends ArrayAdapter<String> {

    public BookmarkListAdapter(Context context, String[] values) {
        super(context, R.layout.list_item_servers, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.list_item_servers,parent, false);

        String server = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.serverName);
        ImageView imageView = (ImageView) view.findViewById(R.id.serverImage);

        textView.setText(server);
        imageView.setImageResource(R.drawable.ic_server);

        return view;
    }
}
