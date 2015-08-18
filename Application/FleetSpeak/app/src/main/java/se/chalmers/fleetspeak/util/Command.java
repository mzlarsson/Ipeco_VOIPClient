package se.chalmers.fleetspeak.util;

import java.io.Serializable;

/**
 * A class holding a command for communication between server and client
 * Created by Patrik on 2014-10-10.
 */
public class Command implements Serializable {

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
}
