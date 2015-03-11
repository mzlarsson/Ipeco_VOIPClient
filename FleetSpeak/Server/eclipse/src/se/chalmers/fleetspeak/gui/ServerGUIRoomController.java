package se.chalmers.fleetspeak.gui;

import java.util.HashMap;
import java.util.Map;

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
	private TreeView<DataContainer> treeRoot;
	
	private TreeItem<DataContainer> roomsRoot;
	
	private Map<Integer, Integer> clientRooms;
	private Map<Integer, String> clientNames;
	
	private Image allRoomsIcon, roomIcon;
	
	public ServerGUIRoomController(){
		EventBus.getInstance().addSubscriber(this);
		clientRooms = new HashMap<Integer, Integer>();
		clientNames = new HashMap<Integer, String>();

		try{
			this.allRoomsIcon = new Image(getClass().getClassLoader().getResourceAsStream("users_allrooms.png"));
			this.roomIcon = new Image(getClass().getClassLoader().getResourceAsStream("users_room.png"));
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	
	public void initialize(){
		roomsRoot = new TreeItem<DataContainer>(new DataContainer(-1, "Rooms"), new ImageView(allRoomsIcon));
		roomsRoot.setExpanded(true);
		treeRoot.setRoot(roomsRoot);
	}
	
	public void registerClient(int id, int roomid){
		clientNames.put(id, "[Unnamed user]");
		moveClient(id, roomid);
	}
	
	public void registerRoom(int id, String name){
		//Update GUI
		Platform.runLater(new Runnable(){
			@Override
			public void run() {				
				TreeItem<DataContainer> room = new TreeItem<DataContainer>(new DataContainer(id, name), new ImageView(roomIcon));
				roomsRoot.getChildren().add(room);
				
				TreeItem<DataContainer> emptyLabel = new TreeItem<DataContainer>(new DataContainer(-1, "[None]"));
				room.getChildren().add(emptyLabel);
			}
		});
	}
	
	private void moveClient(int clientID, int roomID){
		//Remove from old room
		removeClient(clientID);
		
		//Add to new room
		clientRooms.put(clientID, roomID);
		//Do GUI stuff
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TreeItem<DataContainer> room = findRoom(roomID);
				if(room != null){
					TreeItem<DataContainer> client = new TreeItem<DataContainer>(new DataContainer(clientID, clientNames.get(clientID)));
					room.getChildren().add(client);
					if(room.getChildren().remove(findClient(-1, room))){
						room.setExpanded(true);
					}
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
					TreeItem<DataContainer> room = findRoom(prevRoom);
					room.getChildren().remove(findClient(clientID, room));
					if(room.getChildren().size()==0){
						room.getChildren().add(new TreeItem<DataContainer>(new DataContainer(-1, "[None]")));
					}
				}
			});
		}
	}
	
	public void removeClientEntirely(int clientID){
		removeClient(clientID);
		clientNames.remove(clientID);
	}
	
	public void removeRoom(int roomID){
		//Update GUI
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TreeItem<DataContainer> room = findRoom(roomID);
				if(room != null){
					roomsRoot.getChildren().remove(room);
				}
			}
		});
	}
	
	public void renameClient(int clientID, String newName){
		Integer currentRoom = clientRooms.get(clientID);
		if(currentRoom != null){
			//Update GUI
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					TreeItem<DataContainer> clientNode = findClient(clientID, findRoom(currentRoom));
					if(clientNode != null){
						clientNode.setValue(new DataContainer(clientID, newName));
					}
				}
			});
		}
		
		clientNames.put(clientID, newName);
	}
	
	public void renameRoom(int roomID, String newName){
		//Update GUI
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TreeItem<DataContainer> roomNode = findRoom(roomID);
				if(roomNode != null){
					roomNode.setValue(new DataContainer(roomID, newName));
				}
			}
		});
	}
	
	private TreeItem<DataContainer> findRoom(int roomID){
		ObservableList<TreeItem<DataContainer>> roomItems = roomsRoot.getChildren();
		for(int i = 0; i<roomItems.size(); i++){
			if(roomItems.get(i).getValue().getID() == roomID){
				return roomItems.get(i);
			}
		}
		
		return null;
	}
	
	private TreeItem<DataContainer> findClient(int clientID, TreeItem<DataContainer> room){
		if(room != null){
			ObservableList<TreeItem<DataContainer>> clientItems = room.getChildren();
			for(int i = 0; i<clientItems.size(); i++){
				if(clientItems.get(i).getValue().getID() == clientID){
					return clientItems.get(i);
				}
			}
		}
			
		return null;
	}

	@Override
	public void eventPerformed(EventBusEvent event) {
		if(event.getReciever().equals("broadcast")){
			System.out.println("SERVERGUIROOMCONTROLLER:\n\t"+event.getCommand().getCommand());
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
			}else{
				System.out.println("Wtf...?");
			}
		}
	}
	
	
}
