package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */
public class ChatFragment extends Fragment {
    private PopupWindow micAndVolumePanel;
    private ArrayList<User> userArrayList = new ArrayList<User>();
    private ArrayAdapter<User> adapter;
    private boolean isTalkActive;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.chat_fragment, container, false);

        ListView userListView = (ListView) view.findViewById(R.id.userList);
        adapter = new ChatRoomListAdapter(this.getActivity(), userArrayList);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = adapter.getItem(i);
                muteUser(user);

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
        setUpVolumeAndMicControl(this.getActivity(), locButton);

        ImageButton button = (ImageButton) this.getActivity().findViewById(R.id.pushToTalkButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToTalk();
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * A method that toggles between enables or disables the michophone and shows the current mode in the view
     */
    public void pushToTalk() {
        Log.i("ChatroomActivity", "pushToTalkCalled");
        isTalkActive = !isTalkActive;
        ImageButton button = (ImageButton) this.getActivity().findViewById(R.id.pushToTalkButton);
        button.setBackgroundResource(isTalkActive?R.drawable.ic_mic_blue:R.drawable.ic_mic_grey);
        SoundController.mute(!isTalkActive);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            ((MainActivity) this.getActivity()).setFragment(FragmentHandler.FragmentName.JOIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void setRoomID(int currentRoomID){
//        User[] users = CommandHandler.getUsers(currentRoomID);
 //       userArrayList.clear();
 //       for(User u: users){
 //           userArrayList.add(u);
 //       }
 //       adapter.notifyDataSetChanged();

    }
    public class ChatRoomListAdapter extends ArrayAdapter<User> {
        public ChatRoomListAdapter(Context context, ArrayList<User> values) {
            super(context, R.layout.list_item_users, values);
            Log.d("ChatRoom Userlist size: ", String.valueOf(values.size()));
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
        //FIXME
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
        // Check for -1 return since the get-methods return -1 when volume control is not accessible
        if(SoundController.getMaxVolume() != -1) {
            seekbar.setMax(SoundController.getMaxVolume());
            Log.i("VolumeAudio: ", "max volume =" + SoundController.getMaxVolume() );
        }
        if(SoundController.getCurrentVolume() != -1){
            Log.i("VolumeAudio: ", "current volume="+ SoundController.getCurrentVolume());
            seekbar.setProgress(SoundController.getCurrentVolume());
        }
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                SoundController.setVoloume(progress);
                Log.i("Volume value:", " " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }


}
