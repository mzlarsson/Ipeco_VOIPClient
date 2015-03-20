package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */
public class ChatFragment extends Fragment {
    private PopupWindow micAndVolumePanel;
    private ArrayList<User> users;
    private ArrayAdapter<User> adapter;
    // TODO fix set talkActive to false and call it in oncreateView
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.chat_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));


        ListView userListView = (ListView) view.findViewById(R.id.userList);
        users = new ArrayList<>(getMain().getUsers(getMain().getCurrentRoom()));
        adapter = new ChatRoomListAdapter(getMain(), users);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = adapter.getItem(i);
                muteUser(user);

            }
        });
        ImageButton button = (ImageButton) view.findViewById(R.id.pushToTalkButton);


        button.setBackgroundResource(getMain().isTalkActive() ? R.drawable.ic_mic_blue : R.drawable.ic_mic_grey);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().pushToTalk();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }
    public void seCarMode(boolean b){
        adapter.notifyDataSetChanged();
        if(this.menu != null){
            ((MenuItem) menu.findItem(R.id.volume_mic_control)).setVisible(b);
        }

    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.chatroommenu, menu);
        ImageButton locButton = (ImageButton) menu.findItem(R.id.volume_mic_control).getActionView();
        setUpVolumeAndMicControl(getMain(), locButton);

        ImageButton button = (ImageButton) this.getView().findViewById(R.id.pushToTalkButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().pushToTalk();
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            getMain().setFragment(FragmentHandler.FragmentName.JOIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update(){
        users.clear();
        users.addAll(getMain().getUsers(getMain().getCurrentRoom()));
        adapter.notifyDataSetChanged();
        ImageButton button = (ImageButton) this.getView().findViewById(R.id.pushToTalkButton);
        button.setBackgroundResource(getMain().isTalkActive() ? R.drawable.ic_mic_blue : R.drawable.ic_mic_grey);
    }



public class ChatRoomListAdapter extends ArrayAdapter<User> {
        public ChatRoomListAdapter(Context context, ArrayList<User> values) {
            super(context, R.layout.list_item_users, values);
            Log.d("ChatRoom Userlistsize: ", String.valueOf(values.size()));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(Utils.getCarMode()?R.layout.list_item_users_while_driving:
                    R.layout.list_item_users, parent, false);

            User user = getItem(position);
            String userName = user.getName();

            TextView textView = (TextView) view.findViewById(R.id.userName);
            ImageView imageView = (ImageView) view.findViewById(R.id.userTalkImage);

            textView.setText(userName); //Sets the names in the list

            imageView.setImageResource(user.getMuted()?R.drawable.ic_mute:R.drawable.ic_user); //Sets icon in the list

            return view;
        }
    }
    private void muteUser(User user){
        boolean isMuted = user.getMuted();
        getMain().muteUser(user, !isMuted);
        adapter.notifyDataSetChanged();
    }
    /**
     * A method to creates the dropdown panel with volume and mic control which is activated by a ImageButton
     * @param context - the context of the dropdown panel
     * @param imageButton - the ImageButton that activates the dropdown panel
     */
    private void setUpVolumeAndMicControl(Context context, ImageButton imageButton ){
        //Creates the view of the PopUpWindow
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.drop_down_seek_bar, null);
        // Set up the volume seekbar to control the volume
        setUpVolumeSeekbar((SeekBar)contentView.findViewById(R.id.volume_seekbar));
        // Creates the PopUpWindow and sets the size and view
        micAndVolumePanel = new PopupWindow(context, null,
                android.R.attr.actionDropDownStyle);
        micAndVolumePanel.setFocusable(true);
        micAndVolumePanel.setContentView(contentView);
        setPopupSize(micAndVolumePanel);

        // Set up the activation of the panel to a image button
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the place where the PopUpWindow appears
                micAndVolumePanel.showAsDropDown(view, 0, 0);
            }
        });
        // Set the image view of the imageButton
        imageButton.setBackgroundResource(R.drawable.ic_control_mic_volume);

    }

    /**
     * A method to set up a seekbar to control the volume
     * @param seekbar
     */
    private void setUpVolumeSeekbar(SeekBar seekbar){
        //TODO

       }
    /**
     * Set the size of the popupWindow
     * @param popupWindow - the popupWindow that will have the size set
     */
    private void setPopupSize(PopupWindow popupWindow) {

        // Set the height of the popupWindow to be the measured height of its contentView with background padding
        View contentView = popupWindow.getContentView();
        int unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(unspecified, unspecified);
        int height = contentView.getMeasuredHeight();
        Drawable background = popupWindow.getBackground();
        if (background != null) {
            Rect rect = new Rect();
            background.getPadding(rect);
            height += rect.top + rect.bottom;
        }

        // Set the width of the popupWindow to be the width of the android display
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getMain().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }
    private MainActivity getMain(){
        return (MainActivity) this.getActivity();
    }


}
