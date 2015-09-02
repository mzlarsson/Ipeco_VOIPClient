package se.chalmers.fleetspeak.util;

import java.io.Serializable;

/**
 * A class holding a command for communication between server and client
 * @author Patrik
 * @version 1.0
 *
 */
public class Command implements Serializable{

	private static final long serialVersionUID = 1L;

	private String command;
	private Object key;
	private Object value;

	public Command(String command, Object key, Object value) {
		this.command = command;
		this.key = key;
		this.value = value;
	}

	public String getCommand() {
		return command;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString(){
		String skey = key == null?"null":key.toString();
		String svalue = value == null?"null":value.toString();
		return command + ":" + skey + ":" + svalue;
	}
}