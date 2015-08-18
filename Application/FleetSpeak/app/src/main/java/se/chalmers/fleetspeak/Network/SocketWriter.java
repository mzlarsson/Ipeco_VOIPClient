package se.chalmers.fleetspeak.Network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketWriter implements Runnable{

    private String LOGTAG = "SocketWriter";

    private ObjectOutputStream objectOutputStream;
    private OutputStream outputStream;
    private Handler handler;

    private Executor executor;

    public SocketWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);

    }


    @Override
    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.prepare();
        handler = new Handler(){
            public void handleMessage(Message msg){
                Command c =(Command) msg.obj;
                Log.d(LOGTAG, c.getCommand() + " " + c.getKey());
                switch (msg.what){
                    case 0:
                        close();
                        break;
                    case 1:
                        try {
                            objectOutputStream.writeObject(msg.obj);
                        } catch (IOException e) {
                            Log.e(LOGTAG, e.getMessage());
                        }
                        break;
                    default:
                        Log.e(LOGTAG, "Unknown message type " + msg.what);
                        break;
                }
            }
        };
        synchronized (this) {
            notify();
        }
        Looper.loop();
    }
    public Handler getHandler(){
        return handler;
    }

    private void close(){
        Looper.myLooper().quit();
        try{
            objectOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
