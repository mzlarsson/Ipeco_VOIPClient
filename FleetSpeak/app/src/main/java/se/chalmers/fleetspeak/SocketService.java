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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private PrintWriter printWriter;
    private Timer timer = new Timer();
    private Messenger messenger;
    private boolean isConnected = false;

    //Commands
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int SETNAME = 3;
    public static final int SETMESSENGER = 4;
    public static final int SENDDATA = 44; // only for testing will send a string from the server when used

    private String LOGNAME = "SocketService";

    final Messenger mMessenger = new Messenger(new IncomingHandler());


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }




    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(LOGNAME, "Command received. id: " + msg.what);
            switch (msg.what) {
                case CONNECT:
                    Log.d("humbug", "Connecting via TCP");
                    final String s = (String) msg.obj;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(s, 8867);
                                Log.i(LOGNAME, "Connection established to" + socket.toString());


                                printWriter = new PrintWriter(socket.getOutputStream(), true);
                                Log.i(LOGNAME, "Outputsteam ready");


                                objectInputStream = new ObjectInputStream(socket.getInputStream());
                                Log.i(LOGNAME, "InputStream ready");
                                isConnected = true;
                            }catch(IOException e){
                                Log.i("Connector.connect", "Connection failed " + e.getMessage() );
                            }
                        }
                    });
                    thread.start();


                    break;
                case DISCONNECT:
                    //TODO
                    break;
                case SETNAME:
                    //TODO
                    break;
                case SETMESSENGER:
                    messenger = (Messenger) msg.obj;
                    break;
                case SENDDATA:
                    printWriter.println("data");
                    lookForMessage();
                    break;
            }
        }
    }



    @Override
    public void onCreate(){
         timer.scheduleAtFixedRate(new TimerTask(){ public void run() {lookForMessage();}}, 0, 5000L);
    }

    private void lookForMessage() {
        if(isConnected) {
            Log.i(LOGNAME, "Looking for message form server");
            try {
                Object o;
                o = objectInputStream.readObject();

                if (o != null) {
                    Log.i(LOGNAME, " Something have been found: " + o.toString());
                    messenger.send(Message.obtain(null, 0, o));
                }

            } catch (IOException e) {
                Log.i(LOGNAME, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.i(LOGNAME, e.getMessage());
            } catch (NullPointerException e) {
                Log.i(LOGNAME, e.getMessage());
            } catch (RemoteException e) {
                Log.i(LOGNAME, e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy(){
        if(timer != null){timer.cancel();}
        Log.i(LOGNAME, "Service Stopped.");
    }




}
