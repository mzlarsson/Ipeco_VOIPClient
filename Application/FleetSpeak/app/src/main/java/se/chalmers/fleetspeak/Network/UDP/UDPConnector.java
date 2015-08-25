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

    private DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    private Boolean running;

    private Executor executor;
    private PacketReceiver receiver;

    public UDPConnector(String ip, int port, PacketReceiver receiver){

        this.receiver = receiver;
        try {
            InetAddress address = InetAddress.getByName(ip);
            sendPacket = new DatagramPacket(new byte[172],172,address, port);
            socket = new DatagramSocket();
            socket.connect(address,port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        receivePacket = new DatagramPacket(new byte[172],172);

        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }


    @Override
    public void run() {
        running = true;
        while(running){
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(receiver != null)
                receiver.handlePacket(receivePacket.getData());
        }

    }

    public void sendPacket(byte[] data){
        if(socket.isConnected()) {
            sendPacket.setData(data);
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("UDP", "udp socket is not connected");
        }
    }

    public void setReceiver(PacketReceiver receiver) {
        this.receiver = receiver;
    }

    public void terminate(){
        running = false;
        socket.close();
    }
}
