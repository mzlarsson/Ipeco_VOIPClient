package se.chalmers.fleetspeak.Network.UDP;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.Model;
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 25/08/15.
 */
public class STUNInitiator implements PacketReceiver, Runnable{

    private Boolean receicedOk;

    private Executor executor;

    private UDPConnector udpConnector;

    private byte[] controlcode;

    private Messenger messenger;

    public STUNInitiator(String ip, int port, byte controlcode, Handler handler) {
        this.controlcode = new byte[]{controlcode};
        this.udpConnector = new UDPConnector(ip, port, this);
        this.messenger = new Messenger(handler);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }


    @Override
    public void handlePacket(byte[] bytes) {
        if(bytes[0] == controlcode[0])
            receicedOk = true;
    }

    @Override
    public void run() {
        receicedOk = false;
        while(!receicedOk)
            udpConnector.sendPacket(controlcode);
        try {
            messenger.send(Message.obtain(null, MessageValues.UDPCONNECTOR, udpConnector));
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("STUN", "failed to pass stunned socket to handler");
        }
    }








}
