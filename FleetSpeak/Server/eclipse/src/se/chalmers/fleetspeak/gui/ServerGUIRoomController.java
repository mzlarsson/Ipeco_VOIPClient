package se.chalmers.fleetspeak.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;

public class ServerGUIRoomController implements IEventBusSubscriber{

	@FXML
	private VBox root;
	@FXML
	private TreeView<String> treeRoot;
	
	private TreeItem<String> roomsRoot;

	private Map<Integer, String> clients;
	private Map<Integer, String> rooms;
	
	private Map<Integer, Integer> clientRooms;
	
	public ServerGUIRoomController(){
		EventBus.getInstance().addSubscriber(this);
		clients = new HashMap<Integer, String>();
		rooms = new HashMap<Integer, String>();
		clientRooms = new HashMap<Integer, Integer>();
	}
	
	public void registerClient(String name, int id){
		clients.put(id, name);
	}
	
	public void registerRoom(String name, int id){
		rooms.put(id, name);
		
		if(roomsRoot == null){
			roomsRoot = new TreeItem<String>("Rooms", null);
			roomsRoot.setExpanded(true);
			treeRoot.setRoot(roomsRoot);
		}
		
		TreeItem<String> room = new TreeItem<String>(name, null);
		roomsRoot.getChildren().add(room);
		
		TreeItem<String> emptyLabel = new TreeItem<String>("[None]");
		room.getChildren().add(emptyLabel);
	}
	
	public void moveClient(int clientID, int roomID){
		removeClient(clientID);
		
		clientRooms.put(clientID, roomID);
		TreeItem<String> room = findRoom(roomID);
		TreeItem<String> client = new TreeItem<String>(clients.get(clientID));
		room.getChildren().add(client);
		
		if(room.getChildren().remove(findClient("[None]", room))){
			room.setExpanded(true);
		}
	}
	
	public void removeClient(int clientID){
		Integer prevRoom = clientRooms.get(clientID);
		if(prevRoom != null){
			TreeItem<String> room = findRoom(prevRoom);
			room.getChildren().remove(findClient(clientID, room));
			clientRooms.remove(clientID);
		}
	}
	
	private TreeItem<String> findRoom(int roomID){
		String name = rooms.get(roomID);
		ObservableList<TreeItem<String>> roomItems = roomsRoot.getChildren();
		for(int i = 0; i<roomItems.size(); i++){
			if(roomItems.get(i).getValue().equals(name)){
				return roomItems.get(i);
			}
		}
		
		return null;
	}
	
	private TreeItem<String> findClient(int clientID, TreeItem<String> room){
		return findClient(clients.get(clientID), room);
	}
	
	private TreeItem<String> findClient(String name, TreeItem<String> room){
		ObservableList<TreeItem<String>> clientItems = room.getChildren();
		for(int i = 0; i<clientItems.size(); i++){
			if(clientItems.get(i).getValue().equals(name)){
				return clientItems.get(i);
			}
		}
		
		return null;
	}

	@Override
	public void eventPerformed(EventBusEvent event) {
		String cmd = event.getCommand().getCommand();
//		System.out.println("ServerGuiRoomController caught event: "+cmd);
		if(cmd.equals("addUser")){
			String[] data = ((String)event.getCommand().getKey()).split(",");
			registerClient(data[0], Integer.parseInt(data[1]));
		}else if(cmd.equals("move")){
			moveClient((Integer)event.getCommand().getKey(), (Integer)event.getCommand().getValue());
		}else if(cmd.equals("createAndMove")){
			String[] roomData = ((String)event.getCommand().getValue()).split(",");
			int roomID = Integer.parseInt(roomData[0]);
			registerRoom(roomData[1], roomID);
			
			moveClient((Integer)event.getCommand().getKey(), roomID);
		}else if(cmd.equals("removedClient")){
			removeClient((Integer)event.getCommand().getKey());
		}
	}
	
	
}
