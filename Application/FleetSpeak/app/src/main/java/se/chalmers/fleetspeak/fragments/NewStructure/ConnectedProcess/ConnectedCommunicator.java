package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;

/**
 * Created by David Gustafsson on 2015-07-22.
 */
public interface ConnectedCommunicator {

    public List<Room> getRooms();
    public void reconnect();
    public ArrayList<User> getUsersForRoom(int RoomID);
    public boolean getTruckState();
    public String getUsername();
    public void createAndMoveRoom(String newRoomName);
    public int getCurrentRoomId();
    public List<User> getCurrentRoomsUsers();

    void sendUserClicked(User user);
    void roomClicked(Room room);
    void onBackNo();
    void onBackYes();

}
