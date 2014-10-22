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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.fleetspeak.sound.SoundController;
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

    private ArrayList<Command> commandQueue = new ArrayList<Command>();


    // This should probably not be here but it was so much easier to save in here
    private int id = -1;

    //Commands
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int CREATEANDMOVE = 4;
    public static final int MOVEUSER = 5;
    public static final int GETUSERS = 6;
    public static final int MUTEUSER = 7;
    public static final int SENDTESTDATA = 44; // only for testing will send a string from the server when used

    private String LOGNAME = "SocketService";

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOGNAME, "I have been bound");
        return mMessenger.getBinder();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
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
                        commandQueue.clear();
                        if(socket != null)
                            endSocketConnection();
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



                                } catch (IOException e) {
                                    Log.i("Connector.connect", "Connection failed " + e.getMessage());
                                    try {
                                        messenger.send(Message.obtain(null, 0,new Command("connection failed", null, null)));
                                    } catch (RemoteException e1) {
                                        e1.printStackTrace();
                                    }
                                }



                        break;
                    case DISCONNECT:
                        Log.i(LOGNAME, "Disconnecting");
                        trySend(new Command("disconnect", id, null));
                        endSocketConnection();
                        SoundController.close();
                        break;
                    case SETNAME:

                        Log.i(LOGNAME, "Trying  to sending setName command");
                        trySend(new Command("setName", id, msg.obj));

                        break;
                    case MOVEUSER:
                        trySend(new Command("moveUser", id, msg.obj));
                        break;
                    case GETUSERS:
                        Log.i(LOGNAME, "trying to send getRooms command");
                        trySend(new Command("getUsers", id, null));
                        break;
                    case MUTEUSER:
                        //TODO
                        Log.i(LOGNAME, "Command not implemented");
                        break;
                    case SENDTESTDATA:
                        trySend(new Command("data", id, null));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void trySend(Command c){

        if(id > 0){
            Log.d("Volt", "Trying to send: "+c.getCommand()+", "+c.getKey()+","+c.getValue());
            try {

                objectOutputStream.writeObject(c);
                objectOutputStream.flush();
                Log.i(LOGNAME, "Sent command: " + c.getCommand());
            } catch (IOException e) {
                Log.e(LOGNAME, e.toString());
            }

        }else{
            Log.i(LOGNAME, "Command added to commandQueue " + c.getCommand());
            commandQueue.add(c);
        }
    }
    private void sendCommandQueue(){

            while (0 < commandQueue.size()) {
                Log.d("Volt", "Sending in commandQueue: "+commandQueue.get(0).getCommand()+", "+id+","+commandQueue.get(0).getValue());

                Command correctIDcommand = new Command(commandQueue.get(0).getCommand(), id, commandQueue.get(0).getValue());

                try {
                    objectOutputStream.writeObject(correctIDcommand);
                    objectOutputStream.flush();
                    Log.i(LOGNAME, "Sent command form queue: " + correctIDcommand.getCommand());
                    commandQueue.remove(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }


    @Override
    public void onCreate(){
        Log.i("FUUUUUUUUUCK" , "DIE MOTHERFUCKER DIE");

         timer.scheduleAtFixedRate(new TimerTask(){ public void run() {lookForMessage();}}, 0, 100L);
    }

    /**
     * Checks if there is something on the input stream
     * Pass on everything found to CommandHandler
     */

    private void lookForMessage() {
        if(objectInputStream != null) {
            //Log.i(LOGNAME, "Looking for message form server");
            try {
                Command c;
                c = (Command) objectInputStream.readObject();

                if (c != null) {
                    Log.i(LOGNAME, " Something have been found: " + c.getCommand());
                    if(c.getCommand().equals("setID")){
                        id = (Integer) c.getKey();
                        Log.i(LOGNAME, "ID is set now");
                        sendCommandQueue();
                        
                        while(!SoundController.hasValue()){
                            try{Thread.sleep(10);}catch(InterruptedException ie){}
                        }
                        objectOutputStream.writeObject(new Command("setRtpPort", id, SoundController.getPort()));
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

    private void endSocketConnection(){
     try{
       socket.close();
       objectInputStream.close();
       objectOutputStream.close();
       id = -1;
     }catch(IOException e){
        Log.i(LOGNAME,"Connection ended unexeptedly");
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
