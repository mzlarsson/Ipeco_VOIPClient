package se.chalmers.fleetspeak.TCP;

/**
 * Created by Nieo on 24/04/15.
 */
public interface IConnector {
    void connect(String ip, int port);
    void disconnect();
    void sendMessage(Object command);
}
