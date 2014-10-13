package se.chalmers.fleetspeak;

import java.io.Serializable;

/**
 * A class that creates an Event that is used by the EventBus and it's
 * subscribers.
 * @author Patrik
 * @version 1.0
 * 
 */
public class Command implements Serializable{
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