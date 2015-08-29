package se.chalmers.fleetspeak.Network.TCP;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketReader implements Runnable{

    private Messenger messenger;
    private ObjectInputStream objectInputStream;
    private InputStream inputStream;
    private Executor executor;
    private Boolean reading;

    public SocketReader(InputStream inputStream, Messenger messenger){
        this.inputStream = inputStream;
        this.messenger = messenger;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    @Override
    public void run() {
        try {
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object readObject;
        reading = true;
        while(reading){
            try {
                if((readObject = objectInputStream.readObject()) != null){
                    Message msg = Message.obtain(null, MessageValues.COMMAND, readObject);
                    messenger.send(msg);
                }
                Thread.sleep(100);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                //TODO error handling
                stop();
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        reading = false;
        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}