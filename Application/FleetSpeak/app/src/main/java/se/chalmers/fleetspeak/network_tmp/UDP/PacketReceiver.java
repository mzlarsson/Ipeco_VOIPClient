package se.chalmers.fleetspeak.network_tmp.UDP;

/**
 * Created by Nieo on 25/08/15.
 */
public interface PacketReceiver {
    void handlePacket(byte[] bytes);
}
