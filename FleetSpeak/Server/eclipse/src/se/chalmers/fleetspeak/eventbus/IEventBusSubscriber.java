package se.chalmers.fleetspeak.eventbus;


/**
 * Interface for subscribers to the eventbus.
 * @author Patrik
 * @version 1.0
 * 
 */
public interface IEventBusSubscriber {
	/**
	 * Method called when an event has been forwarded to the subscriber by the
	 * EventBus.
	 * 
	 * @param event
	 *            the event that has been performed.
	 */
	public void eventPerformed(EventBusEvent event);

}
