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

public class SocketService extends Service {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Timer timer = new Timer();
    private Messenger messenger;


    private int id;

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
        Log.i(LOGNAME, "I have been bound");
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
                                    socket = new Socket(s, i);
                                    Log.i(LOGNAME, "Connection established to" + socket.toString());


                                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                    Log.i(LOGNAME, "Outputsteam ready");


                                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                                    Log.i(LOGNAME, "InputStream ready");

                                    Log.i(LOGNAME, "trying to send getRooms");
                                    objectOutputStream.writeObject(new Command("getRooms", id, null));
                                    objectOutputStream.flush();
                                    Log.i(LOGNAME, "sent getRooms");

                                } catch (IOException e) {
                                    Log.i("Connector.connect", "Connection failed " + e.getMessage());
                                }
                            }

                        });
                        thread.start();


                        break;
                    case DISCONNECT:
                        Log.i(LOGNAME, "Disconnecting");
                        try {
                            objectOutputStream.writeObject(new Command("disconnect", id, null));
                            objectOutputStream.flush();

                            socket.close();

                        } catch (IOException e) {
                            Log.i(LOGNAME, e.getMessage());
                        }
                        break;
                    case SETNAME:

                        Log.i(LOGNAME, "Trying  to sending setName command");
                        try {
                            if(socket != null && socket.isConnected()) {
                                objectOutputStream.writeObject(new Command("setName", id, "I FUCKING HATE THIS"));
                                objectOutputStream.flush();
                                Log.i(LOGNAME, "Sent setName");
                            }

                        } catch (IOException e) {
                            Log.i(LOGNAME, e.toString());
                        }
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
                        Log.i(LOGNAME, "trying to send getRooms command");
                        try{
                            if(socket != null && socket.isConnected()){
                                objectOutputStream.writeObject(new Command("getRooms", id, null));
                                objectOutputStream.flush();
                            }
                        }catch (IOException e){
                            Log.e(LOGNAME,e.toString());
                        }
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
                            if(socket != null && socket.isConnected()) {
                                objectOutputStream.writeObject(new Command("data", null, null));
                                objectOutputStream.flush();
                            }
                            Thread.sleep(10L);
                            //lookForMessage();
                        } catch (IOException e) {
                            Log.i(LOGNAME, e.toString());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }



    @Override
    public void onCreate(){
        Log.i("FUUUUUUUUUCK" , "DIE MOTHERFUCKER DIE");

         timer.scheduleAtFixedRate(new TimerTask(){ public void run() {lookForMessage();}}, 0, 100L);
    }

    private void lookForMessage() {
        if(socket != null && socket.isConnected()) {
            //Log.i(LOGNAME, "Looking for message form server");
            try {
                Command c;
                c = (Command) objectInputStream.readObject();

                if (c != null) {
                    Log.i(LOGNAME, " Something have been found: " + c.getCommand());
                    if(c.getCommand().equals("setID")){
                        id = (Integer) c.getKey();
                    }
                    messenger.send(Message.obtain(null, 0, c));
                }else {
                    Log.i(LOGNAME, "Found nothing");
                }
            } catch (IOException e) {
                endSocketConnection();
               // Log.i(LOGNAME, e.toString());
            } catch (ClassNotFoundException e) {
                Log.e(LOGNAME, e.toString());
            } catch (NullPointerException e) {
                Log.e(LOGNAME, e.toString());
            } catch (RemoteException e) {
                Log.e(LOGNAME, e.toString());
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
