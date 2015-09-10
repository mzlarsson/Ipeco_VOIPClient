package se.ipeco.fleetspeak.management.core;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class User {

	private ReadOnlyIntegerWrapper idProperty;
	private ReadOnlyStringWrapper usernameProperty;
	
	public User(int id, String username){
		this.idProperty = new ReadOnlyIntegerWrapper(id);
		this.usernameProperty = new ReadOnlyStringWrapper(username);
	}
	
	public int getID(){
		return idProperty.get();
	}
	
	public ReadOnlyIntegerProperty idProperty(){
		return idProperty.getReadOnlyProperty();
	}
	
	public String getUsername(){
		return usernameProperty.get();
	}
	
	public ReadOnlyStringProperty usernameProperty(){
		return usernameProperty.getReadOnlyProperty();
	}
	
	@Override
	public String toString(){
		return "User "+getUsername()+" ["+getID()+"]";
	}
}
