package se.chalmers.fleetspeak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TwiZ on 2014-10-07.
 * TODO: Make this an inner class in activity_chatroom?
 *  */
public class ChatRoomListAdapter extends ArrayAdapter<String> {

    public ChatRoomListAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.list_item_users, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.list_item_users,parent, false);

        String user = getItem(position);


        TextView textView = (TextView) view.findViewById(R.id.userName);
        ImageView imageView = (ImageView) view.findViewById(R.id.userTalkImage);

        textView.setText(user);

        imageView.setImageResource(R.drawable.ic_mute_status);

        return view;
    }
}
