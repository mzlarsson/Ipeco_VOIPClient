package se.chalmers.fleetspeak;

import android.os.Message;

/**
 * Created by Nieo on 10/10/14.
 * Help class for messages
 * try {
 *      mService.send(Message.obtain(SERVERHANDLER.[command]()));
 *  } catch (RemoteException e) {
 *  }

 */
public class ServerHandler {


    private ServerHandler(){}


    public static Message connect(String ip, int port) {
        return Message.obtain(null,SocketService.CONNECT,port,0,ip);

    }


    public static Message disconnect() {
        return Message.obtain(null,SocketService.DISCONNECT,null);
    }


    public static Message setName(String name) {
        return  Message.obtain(null, SocketService.SETNAME, 0,0,name);
    }


    public static Message move(int roomID) {
        return Message.obtain(null,SocketService.MOVEUSER,roomID);
    }

    public static Message getUsers() {
        return Message.obtain(null,SocketService.GETUSERS);
    }


    public static Message muteUser(int userID) {
        //Not implemented
        return Message.obtain(null,SocketService.MUTEUSER,userID);
    }

    public static Message createAndMove(String name){
        return Message.obtain(null,SocketService.CREATEANDMOVE,name);
    }
}
