package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 07/03/15.
 * Handles connection with the server. Got 2 threads that handle outgoing and incomming traffic.
 */
public class Connector {

    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int MOVENEWROOM = 4;
    public static final int MOVE = 5;
    public static final int SETSOUNDPORT = 6;

    public static final int CONNECTED = 10;
    public static final int DISCONNECTED = 11;




    private SocketWriter socketWriter;
    private SocketReader socketListener;
    private Thread outThread;
    private Thread inThread;

    private Socket socket;
    private Messenger threadMessenger;

    /**
     * @param h Handler to recieve objects from server
     */
    public Connector(Handler h){
        socketWriter = new SocketWriter();
        outThread = new Thread(socketWriter);
        outThread.start();
        socketListener = new SocketReader(new Messenger(h));
        inThread = new Thread(socketListener);


    }

    /**
     * Connect to server with ip:port
     * @param callbackHandler Handler for callback message
     * @param ip
     * @param port
     */
    public void connect(Handler callbackHandler, String ip, int port){
        sendMessage(callbackHandler, CONNECT, ip, port);
    }

    /**
     * Disconnects from the server
     * @param callbackHandler Handler for callback message
     */
    public void discconect(Handler callbackHandler ){
        sendMessage(callbackHandler, DISCONNECT, null, 0);
    }

    /**
     * Sets username to name
     * @param callbackHandler Handler for callback message
     * @param name
     */
    public void setName(Handler callbackHandler, String name){
        sendMessage(callbackHandler, SETNAME, name, 0);
    }

    /**
     * move user to a room
     * @param callbackHandler Handler for callback message
     * @param roomid id of room to move to
     */
    public void move(Handler callbackHandler, int roomid){
        sendMessage(callbackHandler, MOVE, roomid, 0);
    }

    /**
     * make a new room and move there
     * @param callbackHandler Handler for callback message
     * @param roomname name of new room
     */
    public void moveNewRoom(Handler callbackHandler, String roomname){
        sendMessage(callbackHandler, MOVENEWROOM, roomname, 0);
    }

    /**
     * Tell server what port you are using to receive soundpackets from a specific user
     * @param callbackHandler Handler for callback message
     * @param remoteUserid id of user
     * @param port local port used to receive packets
     */
    public void setSoundPort(Handler callbackHandler, int remoteUserid, int port){
        sendMessage(callbackHandler, SETSOUNDPORT, remoteUserid, port);
    }

    /**
     * sends a message to the socketwriter
     * @param callbackhandler handler for callbackamessage
     * @param command command to send
     * @param key command key
     * @param value command value
     */
    private void sendMessage(Handler callbackhandler, int command, Object key, int value){
        try{
            threadMessenger.send(Message.obtain(callbackhandler, command, value, 0, key));
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * Starts the listenerthread
     */
    private void startSocketListener(){
        inThread.start();
    }


    /**
     * closes the socket
     */
    private void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Handles incomming messages and sends commands to the server
     */
    private class SocketWriter implements Runnable{

        Handler handler = null;
        ObjectOutputStream oos;

        @Override
        public void run() {
            Looper.prepare();

            handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg != null) {
                        Log.i("Connector", "Command received. id: " + msg.what);

                        switch (msg.what) {
                            case CONNECT:
                                Log.i("Connector", "Connecting...");
                                try{
                                    socket = new Socket((String)msg.obj, msg.arg1);
                                    startSocketListener();
                                    oos = new ObjectOutputStream(socket.getOutputStream());

                                }catch(IOException e){
                                    Log.i("EROORRORORR", "IOException" );
                                    e.printStackTrace();
                                }
                                Log.i("Connector", "Connected to" + msg.obj + ":" + msg.arg1);
                                try {
                                    new Messenger(msg.getTarget()).send(Message.obtain(null, CONNECTED));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DISCONNECT:
                                sendCommand("closeSocket", null ,null);
                                closeSocket();
                                break;
                            case SETNAME:
                                sendCommand("setname", msg.obj, null);
                                break;
                            case MOVENEWROOM:
                                sendCommand("movenewroom", msg.obj, null);
                                break;
                            case MOVE:
                                sendCommand("move", msg.obj, null);
                                break;
                            case SETSOUNDPORT:
                                sendCommand("setsoundport", msg.obj, msg.arg1);
                                break;
                            default:
                                break;
                        }
                    }
                }
            };
            threadMessenger = new Messenger(handler);
            Looper.loop();
        }

        void sendCommand(String command, Object key, Object value){
            try {
                oos.writeObject(new Command(command, key, value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *Reads objects from the server and sends them to a Handler
     */

    private class SocketReader implements Runnable{
        Messenger messenger;
        ObjectInputStream ois;
        public SocketReader(Messenger m){
            messenger = m;
        }
        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(socket != null){
                try {
                    Object o;
                    if((o = ois.readObject()) != null){
                        messenger.send(Message.obtain(null, 0, o));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("SocketListener", "EOF exception");
                    closeSocket();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
