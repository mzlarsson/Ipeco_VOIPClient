package se.chalmers.fleetspeak.network.UDP;

/**
 * Created by Nieo on 25/08/15.
 */
public interface PacketReceiver {
    void handlePacket(byte[] bytes);
}
