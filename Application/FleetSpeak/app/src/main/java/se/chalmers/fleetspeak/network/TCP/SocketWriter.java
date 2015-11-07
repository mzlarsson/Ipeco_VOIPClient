package se.chalmers.fleetspeak.network.TCP;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketWriter implements Runnable{

    private String LOGTAG = "SocketWriter";

    private PrintWriter printWriter;
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
        printWriter = new PrintWriter(outputStream);
        Looper.prepare();
        writerHandler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Log.d("TLS", "sending disconnect");
                        printWriter.print("{\"command\":\"disconnect\"}");
                        printWriter.flush();
                        close();
                        break;
                    case 1:
                        Log.d(LOGTAG, "writing to Stream");
                        printWriter.println(msg.obj);
                        printWriter.flush();
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
        printWriter.close();

    }
}
