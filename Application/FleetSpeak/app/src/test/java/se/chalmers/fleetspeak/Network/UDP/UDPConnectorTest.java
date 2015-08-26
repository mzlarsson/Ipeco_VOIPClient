package se.chalmers.fleetspeak.Network.UDP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Nieo on 26/08/15.
 */
public class UDPConnectorTest {


    DatagramPacket recPacket, sendPacket;
    DatagramSocket socket;

    UDPConnector udpConnector;
    Receiver receiver;

    Object waitobject = new Object();


    @Before
    public void setUp() throws Exception {
        receiver = new Receiver();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        udpConnector = new UDPConnector("127.0.0.1", socket.getLocalPort(), receiver);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSendPacket() throws Exception {
        recPacket = new DatagramPacket(new byte[1], 1);
        Executors.newSingleThreadExecutor().execute(new SendPacket());
        try {
            socket.receive(recPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, recPacket.getData()[0]);
    }

    class SendPacket implements Runnable{

        @Override
        public void run() {
            udpConnector.sendPacket(new byte[]{1});
        }
    }
    @Test
    public void testReceivePacket() throws Exception{
        sendPacket = new DatagramPacket(new byte[1], 1, InetAddress.getByName("127.0.0.1"), udpConnector.socket.getLocalPort());
        socket.send(sendPacket);
        boolean gotPacket= false;
        try {
            synchronized (waitobject) {
                waitobject.wait();
            }
            gotPacket = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        assertTrue(gotPacket);

    }



    class Receiver implements PacketReceiver {

        @Override
        public void handlePacket(byte[] bytes) {
            synchronized (waitobject) {
                waitobject.notify();
            }
        }
    }
}