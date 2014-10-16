package se.chalmers.fleetspeak;

import android.os.Message;

/**
 * Created by Nieo on 10/10/14.
 * Help class for messages
 * try {
 *  Message msg = Message.obtain(SERVERHANDLER.[command]());
 *  mService.send(msg);
 *  } catch (RemoteException e) {
 *  }

 */
public class ServerHandler {


    private ServerHandler(){}


    public static Message connect(String ip, int port) {
        return Message.obtain(null,SocketService.CONNECT,port,0,ip);

    }


    public static Message disconnect(int userID) {
        return Message.obtain(null,SocketService.DISCONNECT,userID);
    }


    public static Message setName(String name, int userID) {
        return  Message.obtain(null, SocketService.SETNAME, userID,0,name);
    }


    public static Message move(int roomID, int userID) {
        return Message.obtain(null,SocketService.MOVEUSER,roomID,0,userID);
    }

    public static Message getUsers() {
        return Message.obtain(null,SocketService.GETUSERS);
    }


    public static Message muteUser(int userID) {
        //Not implemented
        return Message.obtain(null,SocketService.MUTEUSER,userID);
    }
}
