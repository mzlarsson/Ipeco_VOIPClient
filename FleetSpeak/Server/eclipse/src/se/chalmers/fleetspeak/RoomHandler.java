package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
	
	private List<RoomInterface> rooms;
	
	public RoomHandler(){
		rooms = new ArrayList<RoomInterface>();
	}
	
	public RoomInterface getRoom(int i){
		RoomInterface ri;
		try{
		ri = rooms.get(i);	
		}catch(IndexOutOfBoundsException e){
		System.out.println(e.toString());	
		ri = rooms.get(0);
		}
		return ri;
	}
	
	public void addRoom(RoomInterface ri){
		try{
			rooms.add(ri);
		}catch(ClassCastException e){
			System.out.println(e.toString());
		}catch(NullPointerException e){
			System.out.println(e.toString());
		}
	}
	
	public void removeRoom(RoomInterface ri){
		try{
			rooms.remove(ri);
		}catch(ClassCastException e){
			System.out.println(e.toString());
		}catch(NullPointerException e){
			System.out.println(e.toString());
		}
	}
	
	public void userChangeRoom(RoomInterface ri, RoomInterface ri2, Client c){
		ri.removeUser(c);
		ri2.addUser(c);
	}
	
	
}
