package se.chalmers.fleetspeak.Network.TCP;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketReader implements Runnable{

    private Messenger messenger;
    private BufferedReader bufferedReader;
    private InputStream inputStream;
    private ExecutorService executor;
    private volatile boolean reading;

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
        String readLine;
        reading = true;
        Log.i("SocketReader", "start reading");

            try {
                while(reading) {
                    readLine = bufferedReader.readLine();
                    Log.d("SocketReader", "" + readLine);
                    if(readLine != null) {
                        Message msg = Message.obtain(null, MessageValues.COMMAND, readLine);
                        messenger.send(msg);
                    }else{
                        Log.e("SocketReader", "socket crashed " );
                        messenger.send(Message.obtain(null, MessageValues.DISCONNECTED));
                        reading = false;
                    }
                }
            } catch (IOException e) {
                try {
                    Log.e("SocketReader", "socket crashed " + e.getMessage());
                    messenger.send(Message.obtain(null, MessageValues.DISCONNECTED));
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

    }

    public void stop(){
        reading = false;
        Log.d("TLS", "stopped reader");

    }

}
