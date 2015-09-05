package se.chalmers.fleetspeak.Network.TCP;

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
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketWriter implements Runnable{

    private String LOGTAG = "SocketWriter";

    private ObjectOutputStream objectOutputStream;
    private OutputStream outputStream;
    private Handler writerHandler;
    private Handler errorHandler;

    private Executor executor;

    public SocketWriter(OutputStream outputStream, Handler errorHandler) {
        this.errorHandler = errorHandler;
        this.outputStream = outputStream;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);

    }


    @Override
    public void run() {
        Thread.currentThread().setName("SocketWriterThread");
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.prepare();
        writerHandler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        close();
                        break;
                    case 1:
                        Command c =(Command) msg.obj;
                        Log.d(LOGTAG, c.getCommand() + " " + c.getKey());
                        try {
                            objectOutputStream.writeObject(msg.obj);
                        } catch (IOException e) {
                            Log.e(LOGTAG, "IOException " + e.getMessage());
                            errorHandler.sendEmptyMessage(MessageValues.DISCONNECTED);
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
    public Handler getWriterHandler(){
        return writerHandler;
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
