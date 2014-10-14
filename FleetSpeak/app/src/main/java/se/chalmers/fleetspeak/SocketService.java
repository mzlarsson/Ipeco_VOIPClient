package se.chalmers.fleetspeak;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Timer timer = new Timer();
    private Messenger messenger;
    private boolean isConnected = false;

    //Commands
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int SETMESSENGER = 4;
    public static final int CREATEROOM = 5;
    public static final int MOVEUSER = 6;
    public static final int GETROOMS = 7;
    public static final int GETUSERSINROOM = 8;
    public static final int MUTEUSER = 9;
    public static final int SENDTESTDATA = 44; // only for testing will send a string from the server when used

    private String LOGNAME = "SocketService";

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }




    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                Log.i(LOGNAME, "Command received. id: " + msg.what);
                switch (msg.what) {
                    case CONNECT:
                        final String s = (String) msg.obj;
                        final int i = msg.arg1;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(LOGNAME, "Trying to connect to " + s);
                                try {
                                    socket = new Socket(s, 8867);
                                    Log.i(LOGNAME, "Connection established to" + socket.toString());


                                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                    Log.i(LOGNAME, "Outputsteam ready");


                                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                                    Log.i(LOGNAME, "InputStream ready");
                                    isConnected = true;
                                } catch (IOException e) {
                                    Log.i("Connector.connect", "Connection failed " + e.getMessage());
                                }
                            }
                        });
                        thread.start();


                        break;
                    case DISCONNECT:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case SETNAME:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case SETMESSENGER:
                        messenger = (Messenger) msg.obj;
                        break;
                    case CREATEROOM:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case MOVEUSER:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case GETROOMS:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case GETUSERSINROOM:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case MUTEUSER:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case SENDTESTDATA:
                        try {
                            objectOutputStream.writeObject(new Command("data", null, null));
                            objectOutputStream.flush();
                            //lookForMessage();
                        } catch (IOException e) {
                            Log.i(LOGNAME, e.toString());
                        }
                        break;
                    default:
                        Log.i(LOGNAME, "Something unexpected happened with that command");
                }
            }
        }
    }



    @Override
    public void onCreate(){
         timer.scheduleAtFixedRate(new TimerTask(){ public void run() {lookForMessage();}}, 0, 5000L);
    }

    private void lookForMessage() {

        if(socket != null && !socket.isClosed() ) {
            Log.i(LOGNAME, "Looking for message form server");
            try {
                Command c;
                c = (Command) objectInputStream.readObject();

                if (c != null) {
                    Log.i(LOGNAME, " Something have been found: " + c.getCommand());
                    messenger.send(Message.obtain(null, 0, c));
                }

            } catch (IOException e) {
                endSocketConnection();
                Log.i(LOGNAME, e.toString());
            } catch (ClassNotFoundException e) {
                Log.i(LOGNAME, e.getMessage());
            } catch (NullPointerException e) {
                Log.i(LOGNAME, e.getMessage());
            } catch (RemoteException e) {
                Log.i(LOGNAME, e.getMessage());
            }

        }
    }

    public void endSocketConnection(){
     try{
       socket.close();
       objectInputStream.close();
       objectOutputStream.close();
     }catch(IOException e){
        Log.i(LOGNAME,"Connection ended unexeptedly");
     }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(timer != null){timer.cancel();}
        Log.i(LOGNAME, "Service Stopped.");

    }




}
