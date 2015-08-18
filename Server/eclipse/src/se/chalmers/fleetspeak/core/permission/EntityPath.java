package se.chalmers.fleetspeak.core.permission;

/**
 * The path to an entity such as room or user.
 * Sort of like a file-path of the form:
 * \tbuilding\\room\\client
 * 
 * @author Patrik Haar
 */
public class EntityPath {

	private Object building = null;
	private Object client = null;
	private Object room = null;
	
	/**
	 * Get the entity of focus, meaning the one with the deepest path.
	 * @return The entity of focus
	 */
	public Object getEntity() {
		return (client!=null ? client : (room!=null ? room : (building!=null ? building : null)));
	}
	
	/**
	 * @return the building
	 */
	public Object getBuilding() {
		return building;
	}

	/**
	 * @param building the building to set
	 */
	public void setBuilding(Object building) {
		this.building = building;
	}

	/**
	 * @return the room
	 */
	public Object getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(Object room) {
		this.room = room;
	}

	/**
	 * @return the client
	 */
	public Object getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Object client) {
		this.client = client;
	}

	@Override
	public String toString() {
		String path = "no path set";
		if (building != null) {
			path = building.toString();
		}
		if (room != null) {
			path += "\\" + room.toString();
		}
		if (client != null) {
			path = "\\" + client.toString();
		}
		return path;
	}
}
