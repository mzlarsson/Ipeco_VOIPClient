package se.chalmers.fleetspeak.TCP;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Nieo on 21/04/15.
 * Sends object to its given socket
 */
public class StreamWriter implements Runnable {

    private ObjectOutputStream myObjectStream;
    private Thread myThread;
    private Handler myHandler;
    private OutputStream myStream;

    public StreamWriter(OutputStream stream){
        myStream = stream;
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        try {
            myObjectStream = new ObjectOutputStream(myStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.prepare();
        myHandler =  new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        try {
                            myObjectStream.writeObject(msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        kill();
                }
            }
        };
        synchronized (this) {
            notify();
        }
        Looper.loop();
    }

    public Handler getMyHandler() {
        return myHandler;
    }

    public void kill(){
        Looper.myLooper().quit();
        try {
            myObjectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
