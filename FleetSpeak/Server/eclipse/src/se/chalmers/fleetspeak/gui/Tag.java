package se.chalmers.fleetspeak.gui;

public class Tag {

	private int index;
	private String name;
	private boolean start;
	
	public Tag(int index, String name){
		this.index = index;
		this.start = !name.contains("/");
		this.name = (start?name:name.substring(1, name.length()));
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public boolean isStart() {
		return start;
	}
}
