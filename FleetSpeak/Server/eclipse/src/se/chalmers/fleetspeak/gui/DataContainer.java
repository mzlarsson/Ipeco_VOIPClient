package se.chalmers.fleetspeak.gui;

public class DataContainer {

	private int id;
	private String name;
	
	public DataContainer(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
}
