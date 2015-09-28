package se.ipeco.fleetspeak.management.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class Room {

	private ReadOnlyIntegerWrapper idProperty;
	private ReadOnlyStringWrapper nameProperty;
	private ReadOnlyIntegerWrapper nbrOfUsersProperty;
	
	private HashMap<Integer, User> users;
	
	public Room(int id, String name){
		this.idProperty = new ReadOnlyIntegerWrapper(id);
		this.nameProperty = new ReadOnlyStringWrapper(name);
		this.nbrOfUsersProperty = new ReadOnlyIntegerWrapper(0);
		this.users = new HashMap<Integer, User>();
	}
	
	public int getID(){
		return idProperty.get();
	}
	
	public ReadOnlyIntegerProperty idProperty(){
		return idProperty.getReadOnlyProperty();
	}
	
	public String getName(){
		return nameProperty.get();
	}
	
	public ReadOnlyStringProperty nameProperty(){
		return nameProperty.getReadOnlyProperty();
	}
	
	public void setName(String name){
		nameProperty.setValue(name);
	}
	
	public int getNbrOfUsers(){
		return nbrOfUsersProperty.get();
	}
	
	public ReadOnlyIntegerProperty nbrOfUsersProperty(){
		return nbrOfUsersProperty.getReadOnlyProperty();
	}
	
	public void addUser(User user){
		if(user != null){
			users.put(user.getID(), user);
			nbrOfUsersProperty.set(users.size());
		}
	}
	
	public List<User> getUsers(){
		return new ArrayList<User>(users.values());
	}
	
	public User removeUser(User user){
		if(user != null){
			return removeUser(user.getID());
		}
		
		return null;
	}
	
	public User removeUser(int userID){
		User removedUser = users.remove(userID);
		nbrOfUsersProperty.set(users.size());
		return removedUser;
	}
	
	public void printState(){
		System.out.println("\tRoom "+getName()+" [ID "+getID()+"]");
		for(User user : users.values()){
			System.out.println("\t\t"+user);
		}
	}
}
