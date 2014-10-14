package se.chalmers.fleetspeak.eventbus;

import se.chalmers.fleetspeak.util.Command;

/**
 * A class that creates an Event that is used by the EventBus and it's
 * subscribers.
 * @author Patrik
 * @version 1.0
 * 
 */
public class EventBusEvent {
	private String receiver;
	private Command command;
	private Object actor;

	/**
	 * Constructor to create an event.
	 * 
	 * @param receiver
	 *            a string that contains the receiver of the type of the event. The
	 *            name must be a viable event name.
	 * @param command
	 *            the command connected to the event if necessary. Null otherwise.
	 * @param actor
	 *            the object that is the source of the event if necessary. Null
	 *            otherwise.
	 */
	public EventBusEvent(String receiver, Command command, Object actor) {
		this.receiver = receiver;
		this.command = command;
		this.actor = actor;
	}

	/**
	 * Getter for the name of the event
	 * 
	 * @return the name of the event.
	 */
	public String getReciever() {
		return receiver;
	}

	/**
	 * Getter for the command of the event
	 * 
	 * @return the recipient
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * Getter for the actor of the event
	 * 
	 * @return the actor
	 */
	public Object getActor() {
		return actor;
	}

}
