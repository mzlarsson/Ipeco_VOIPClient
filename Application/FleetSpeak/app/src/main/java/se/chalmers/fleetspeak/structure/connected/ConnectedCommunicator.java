package se.chalmers.fleetspeak.structure.connected;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;

/**
 * Created by David Gustafsson on 2015-07-22.
 */
public interface ConnectedCommunicator {

    List<Room> getRooms();
    void reconnect();
    ArrayList<User> getUsersForRoom(int RoomID);
    boolean getTruckState();
    String getUsername();
    void createAndMoveRoom(String newRoomName);
    int getCurrentRoomId();
    List<User> getCurrentRoomsUsers();

    void sendUserClicked(User user);
    void roomClicked(Room room);
    void onBackNo();
    void onBackYes();
    Resources getResources();


}
