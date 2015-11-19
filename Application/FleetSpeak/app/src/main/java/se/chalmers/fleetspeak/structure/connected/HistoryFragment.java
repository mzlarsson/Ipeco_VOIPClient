package se.chalmers.fleetspeak.structure.connected;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.structure.lists.RoomList;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;

/**
 * Created by Matz Larsson on 2015-11-17.
 */

public class HistoryFragment extends AppConnectFragment {

    private RoomList roomList;
    private TextView infoView;
    private RoomList.OnRoomClickedListener onRoomClickedListener;

    private boolean carmode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        carmode = TruckModeHandlerFactory.getCurrentHandler().truckModeActive();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        roomList = new RoomList();
        roomList.setRoomKeeper(new RoomList.RoomKeeper() {
            @Override
            public List<Room> getRooms() {
                return ModelFactory.getCurrentModel().getHistory();
            }
        });
        roomList.setOnRoomClickedListener(onRoomClickedListener);

        infoView = (TextView)view.findViewById(R.id.info_history_fragment);
        updateInfoLabel();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room_history, roomList);
        ft.commit();

        return view;
    }

    private void updateInfoLabel(){
        if(infoView != null) {
            boolean hasItems = (roomList != null && roomList.getRoomCount() > 0);
            infoView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
            infoView.setText(getResources().getText(R.string.no_rooms_found));
            infoView.setTextSize(getResources().getDimension(carmode ? R.dimen.carsize : R.dimen.normalsize));
        }else{
            Log.d("Nano", "Got an uninitiated info view");
        }
    }

    public void truckModeChanged(boolean b) {
        if(roomList != null) {
            roomList.changedTruckState(b);
        }

        carmode = b;
        updateInfoLabel();
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        this.onRoomClickedListener = listener;
        if(roomList != null) {
            roomList.setOnRoomClickedListener(listener);
        }
    }

    public void refresh() {
        Model m = ModelFactory.getCurrentModel();
        List<Room> rooms = m.getHistory();
        if (roomList != null) {
            if(rooms != null) {
                roomList.refreshData(rooms);
            }
        }

        updateInfoLabel();
    }
}