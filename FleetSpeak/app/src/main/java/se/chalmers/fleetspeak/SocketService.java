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

import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.sound.SoundController;

public class SocketService extends Service {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Timer timer = new Timer();
    private Messenger messenger;

    private ArrayList<Command> commandQueue = new ArrayList<Command>();

    private int id = -1;

    //Commands
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int SETMESSENGER = 4;
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



                                } catch (IOException e) {
                                    Log.i("Connector.connect", "Connection failed " + e.getMessage());
                                }



                        break;
                    case DISCONNECT:
                        Log.i(LOGNAME, "Disconnecting");
                        trySend(new Command("disconnect", id, null));
                        break;
                    case SETNAME:

                        Log.i(LOGNAME, "Trying  to sending setName command");
                        trySend(new Command("setName", id, msg.obj));

                        break;
                    case SETMESSENGER:
                        messenger = (Messenger) msg.obj;
                        break;
                    case MOVEUSER:
                        trySend(new Command("moveUser", id, msg.arg1));
                        break;
                    case GETUSERS:
                        Log.i(LOGNAME, "trying to send getRooms command");
                        trySend(new Command("getRooms", id, null));
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

    private synchronized void trySend(Command c){

        if(id > 0){
            try {

                objectOutputStream.writeObject(c);
                objectOutputStream.flush();
                Log.i(LOGNAME, "Sent command: " + c.getCommand());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            commandQueue.add(c);
        }
    }
    private synchronized void sendCommandQueue(){

            for (int i = 0; i < commandQueue.size(); i++) {

                Command correctIDcommand = new Command(commandQueue.get(i).getCommand(), id, commandQueue.get(i).getValue());

                try {
                    objectOutputStream.writeObject(correctIDcommand);
                    objectOutputStream.flush();
                    commandQueue.remove(i);
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
                        //objectOutputStream.writeObject(new Command("setRtpPort", id, SoundController.getPort()));
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
