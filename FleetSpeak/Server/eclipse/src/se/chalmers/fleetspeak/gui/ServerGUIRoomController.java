package se.chalmers.fleetspeak.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	
	private ImageView allRoomsIcon, roomIcon;
	
	public ServerGUIRoomController(){
		EventBus.getInstance().addSubscriber(this);
		clients = new HashMap<Integer, String>();
		rooms = new HashMap<Integer, String>();
		clientRooms = new HashMap<Integer, Integer>();

		try{
			this.allRoomsIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("users_allrooms.png")));
			this.roomIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("users_room.png")));
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	
	public void initialize(){
		roomsRoot = new TreeItem<String>("Rooms", allRoomsIcon);
		roomsRoot.setExpanded(true);
		treeRoot.setRoot(roomsRoot);
	}
	
	public void registerClient(int id, int roomid){
		clients.put(id, "[Unnamed user]");
		moveClient(id, roomid);
	}
	
	public void registerRoom(int id, String name){
		rooms.put(id, name);
		
		//Update GUI
		Platform.runLater(new Runnable(){
			@Override
			public void run() {				
				TreeItem<String> room = new TreeItem<String>(name, roomIcon);
				roomsRoot.getChildren().add(room);
				
				TreeItem<String> emptyLabel = new TreeItem<String>("[None]");
				room.getChildren().add(emptyLabel);
			}
		});
	}
	
	public void moveClient(int clientID, int roomID){
		//Remove from old room
		removeClient(clientID);
		
		//Add to new room
		clientRooms.put(clientID, roomID);
		//Do GUI stuff
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				final TreeItem<String> room = findRoom(roomID);
				final TreeItem<String> client = new TreeItem<String>(clients.get(clientID));
				if(room != null){
					room.getChildren().add(client);
					if(room.getChildren().remove(findClient("[None]", room))){
						room.setExpanded(true);
					}
				}else{
					System.out.println("RAGE! Could not find room to update");
				}
			}
		});
	}

	public void removeClient(int clientID){
		Integer prevRoom = clientRooms.get(clientID);
		if(prevRoom != null){
			clientRooms.remove(clientID);
			
			//Update GUI
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					TreeItem<String> room = findRoom(prevRoom);
					room.getChildren().remove(findClient(clientID, room));
				}
			});
		}
	}
	
	public void removeClientEntirely(int clientID){
		removeClient(clientID);
		clients.remove(clientID);
	}
	
	public void removeRoom(int roomID){
		for(Entry<Integer, Integer> relation : clientRooms.entrySet()){
			if(relation.getValue() == roomID){
				clients.remove(relation.getKey());
				clientRooms.remove(relation.getKey());
			}
		}
		
		rooms.remove(roomID);
	}
	
	public void renameClient(int clientID, String newName){
		Integer currentRoom = clientRooms.get(clientID);
		if(currentRoom != null){
			//Update GUI
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					TreeItem<String> clientNode = findClient(clientID, findRoom(currentRoom));
					if(clientNode != null){
						clientNode.setValue(newName);
					}
				}
			});
		}
		
		clients.put(clientID, newName);
	}
	
	public void renameRoom(int roomID, String newName){
		//Update GUI
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TreeItem<String> roomNode = findRoom(roomID);
				if(roomNode != null){
					roomNode.setValue(newName);
				}
			}
		});
		
		rooms.put(roomID, newName);
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
		if(event.getReciever().equals("broadcast")){
			String cmd = event.getCommand().getCommand();
			if(cmd.equals("addedUser")){
				registerClient((Integer)event.getCommand().getKey(), (Integer)event.getCommand().getValue());
			}else if(cmd.equals("changedUsername")){
				renameClient((Integer)event.getCommand().getKey(), (String)event.getCommand().getValue());
			}else if(cmd.equals("changedRoomName")){
				renameRoom((Integer)event.getCommand().getKey(), (String)event.getCommand().getValue());
			}else if(cmd.equals("movedUser")){
				moveClient((Integer)event.getCommand().getKey(), (Integer)event.getCommand().getValue());
			}else if(cmd.equals("createdRoom")){
				registerRoom((Integer)event.getCommand().getKey(), (String)event.getCommand().getValue());
			}else if(cmd.equals("removedUser")){
				removeClient((Integer)event.getCommand().getKey());
			}else if(cmd.equals("removedRoom")){
				removeRoom((Integer)event.getCommand().getKey());
			}
		}
	}
	
	
}
