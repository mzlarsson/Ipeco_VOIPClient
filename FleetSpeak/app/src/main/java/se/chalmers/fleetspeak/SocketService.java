package se.chalmers.fleetspeak;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.fleetspeak.util.Command;

/**
 * Service for handling tcp connection to server
 * Use messages and ServerHandler to send commands to the server
 * Sends all incoming commands to CommandHandler
 */
public class SocketService extends Service {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Timer timer = new Timer();
    private Messenger messenger = new Messenger(CommandHandler.getInstance());

    //Commands
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int MOVENEWROOM = 4;
    public static final int MOVE = 5;
    public static final int SETSOUNDPORT = 6;

    private String LOGNAME = "SocketService";

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOGNAME, "I have been bound");
        return mMessenger.getBinder();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Handles all incoming commands
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                Log.i(LOGNAME, "Command received. id: " + msg.what);

                switch (msg.what) {
                    case CONNECT:
                        final String s = (String) msg.obj;
                        final int i = msg.arg1;

                        Log.i(LOGNAME, "Trying to connect to " + s);
                        try {
                            socket = new Socket(s, i);
                            Log.i(LOGNAME, "Connection established to" + socket.toString());
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            Log.i(LOGNAME, "Outputsteam ready");
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            Log.i(LOGNAME, "InputStream ready");
                            timer.scheduleAtFixedRate(new TimerTask() {
                                public void run() {lookForMessage();}
                            }, 0, 100L);
                        } catch (IOException e) {
                            Log.i("Connector.connect", "Connection failed " + e.getMessage());
                            try {
                                endSocketConnection();
                                messenger.send(Message.obtain(null, 0,new Command("connection failed", null, null)));
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }
                        break;
                    case DISCONNECT:
                        send(new Command("disconnect", null, null));
                        endSocketConnection();
                        break;
                    case SETNAME:
                        send(new Command("setName", msg.obj, null));
                        break;
                    case MOVENEWROOM:
                        send(new Command("moveNewRoom", msg.obj, null));
                        break;
                    case MOVE:
                        send(new Command("moveUser", msg.obj, null));
                        break;
                    case SETSOUNDPORT:
                        send(new Command("setSoundPort", msg.obj, msg.arg1));
                    default:
                        break;
                }
            }
        }
    }

    private void send(Command c){
        Log.d(LOGNAME, "Trying to send: "+c.getCommand()+", "+c.getKey()+","+c.getValue());
        try {
            objectOutputStream.writeObject(c);
            objectOutputStream.flush();
            Log.i(LOGNAME, "Sent command: " + c.getCommand());
        } catch (IOException e) {
            Log.e(LOGNAME, e.toString());
        }

    }
    /**
     * Checks if there is something on the input stream
     * Pass on everything found to CommandHandler
     */

    private void lookForMessage() {
        if(objectInputStream != null) {
            try {
                Command c;
                c = (Command) objectInputStream.readObject();

                if (c != null) {
                    Log.i(LOGNAME, " Something have been found: " + c.getCommand());
                    messenger.send(Message.obtain(null, 0, c));
                }else {
                    Log.i(LOGNAME, "Found nothing");
                }
            } catch (IOException e) {
                endSocketConnection();
            } catch (ClassNotFoundException e) {
                Log.e(LOGNAME, e.toString());
            } catch (NullPointerException e) {
                Log.e(LOGNAME, e.toString());
            } catch (RemoteException e) {
                Log.e(LOGNAME, e.toString());
            }
        }
    }

    private void endSocketConnection(){
        try{
            timer.purge();
            if(objectOutputStream != null)
                objectOutputStream.close();

            if(objectInputStream != null)
                objectInputStream.close();
            if(socket != null)
                socket.close();
            objectOutputStream = null;
            objectInputStream = null;
            socket = null;


         Command c = new Command("Disconnected", null,null);
         messenger.send(Message.obtain(null, 0, c));
         Log.i(LOGNAME, "Succesfully ended connection");
     }catch(IOException e){
        Log.i(LOGNAME,"Connection ended unexeptedly");
     } catch (RemoteException e) {
         e.printStackTrace();
     }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(timer != null){timer.cancel();}
        Log.i(LOGNAME, "Service Stopped.");
        endSocketConnection();

    }




}
