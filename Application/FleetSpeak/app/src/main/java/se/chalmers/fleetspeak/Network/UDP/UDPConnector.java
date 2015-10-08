package se.chalmers.fleetspeak.Network.UDP;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Nieo on 25/08/15.
 */
public class UDPConnector implements Runnable{

    protected DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    private volatile boolean running;

    private Executor executor;
    private PacketReceiver receiver;

    public UDPConnector(String ip, int port, PacketReceiver receiver){

        this.receiver = receiver;
        try {
            InetAddress address = InetAddress.getByName(ip);
            sendPacket = new DatagramPacket(new byte[332],332,address, port);
            socket = new DatagramSocket();
            socket.connect(address,port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        receivePacket = new DatagramPacket(new byte[52],52);

        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }


    @Override
    public void run() {
        Thread.currentThread().setName("UDPConnectorThread");
        Log.i("UDP" , "is running on " + Thread.currentThread().getName());
        running = true;
        while(running){
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                if(!running) {
                    Log.i("UDP", "udp socket is closed");
                }else{
                    Log.e("UDP", "" + e.getMessage());
                }
            }
            if(receiver != null)
                receiver.handlePacket(receivePacket.getData());
        }
        Log.i(Thread.currentThread().getName(),"Closing thread");
    }

    public void sendPacket(byte[] data){
        if(data.length == 1)
            Log.i("UDP", "sent " +data[0]);
        if(!socket.isClosed()) {
            sendPacket.setData(data);
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("UDP", "udp socket is closed");
            terminate();
        }
    }

    /**
     * Set the size of the packets the UDPHandler will read from the incoming udp-traffic.
     * @param packetSize The size of the read-array in number of bytes.
     */
    public void setPacketSize(int packetSize) {
        receivePacket.setData(new byte[packetSize]);
    }

    public void setReceiver(PacketReceiver receiver) {
        this.receiver = receiver;
    }

    public void terminate(){
        running = false;
        if(socket != null)
            socket.close();
    }
}
