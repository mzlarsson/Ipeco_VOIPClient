package se.chalmers.fleetspeak;

/**
 * An interface for the handling of the user and room hierarchy.
 * Created by Patrik on 2014-10-07.
 */
public interface IUserHandler {

    /**
     * Adds the given User to the given Room,
     * used for a new user joining a new room.
     * @param user The user to join the room.
     * @param room The room to be joined.
     */
    public void addUser(User user, Room room);

    /**
     * Adds the given User to the room with the given ID,
     * used for a new user joining an existing room room.
     * @param user The user to join the room.
     * @param roomID The ID of the room to be joined.
     */
    public void addUser(User user, int roomID);

    /**
     * Removes the user with the given ID from its current room.
     * @param userID The ID of the user to be removed.
     */
    public void removeUser(int userID);

    /**
     * Moves the user with the given ID to the room with the given ID.
     * @param userID The ID of the user to be moved.
     * @param targetRoomID The ID of the destination room.
     */
    public void moveUser(int userID, int targetRoomID);

    /**
     * Gets all the available rooms.
     * @return An array with all rooms.
     */
    public Room[] getRooms();

    /**
     * Gets the users in the given room.
     * @param room The room containing the users.
     * @return An array with the users.
     */
    public User[] getUsers(Room room);
}
