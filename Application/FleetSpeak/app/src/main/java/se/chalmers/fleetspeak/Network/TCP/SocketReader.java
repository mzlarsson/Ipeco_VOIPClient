package se.chalmers.fleetspeak.Network.TCP;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketReader implements Runnable{

    private Messenger messenger;
    private BufferedReader bufferedReader;
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
        Thread.currentThread().setName("SocketReaderThread");
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String readObject;
        reading = true;
        Log.i("SocketReader", "start reading");
        while(reading){
            try {
                readObject = bufferedReader.readLine();
                Log.d("SocketReader", "" + readObject);
                Message msg = Message.obtain(null, MessageValues.COMMAND, readObject);
                messenger.send(msg);

                Thread.sleep(100);
            } catch (IOException e) {
                try {
                    Log.e("SocketReader", "socket crashed");
                    messenger.send(Message.obtain(null, MessageValues.DISCONNECTED));
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                stop();
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
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
