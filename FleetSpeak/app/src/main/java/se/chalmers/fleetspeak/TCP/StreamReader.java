package se.chalmers.fleetspeak.TCP;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Nieo on 21/04/15.
 * Creates an inputstream on a socket and start running it in its own thread.
 * All objects are sent to the handler passed on in the constructor.
 * Dies when the socket looses connection
 */
public class StreamReader implements Runnable{

    private InputStream myStream;
    private ObjectInputStream myObjectStream;
    private Thread myThread;
    private Messenger myMessenger;
    private Boolean reading = false;
    private Connector.ErrorHandler myErrorHandler;

    public StreamReader(InputStream stream, Messenger messenger, Connector.ErrorHandler errorHandler){
        myStream = stream;
        myMessenger = messenger;
        myErrorHandler = errorHandler;
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        try {
            myObjectStream = new ObjectInputStream(myStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reading = true;
        while(reading){
            try {
                Object o;
                if((o = myObjectStream.readObject()) != null){
                    Log.i("Reader", "Found stuff");
                    Message msg = Message.obtain(null, 0,o);
                    myMessenger.send(msg);
                }
                Thread.sleep(100);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                reading = false;
                myErrorHandler.fix();
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
            myObjectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
