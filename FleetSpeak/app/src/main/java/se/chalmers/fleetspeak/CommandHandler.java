package se.chalmers.fleetspeak;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 08/10/14.
 * For handling commands coming from the server.
 * Need to be added as Messenger to socketService to work.
 * Owns the RoomHandler
 */
public class CommandHandler extends Handler {

    private RoomHandler roomHandler;
    private SoundController soundController;

    private Context context;

    private String remoteIP;

    public CommandHandler(RoomHandler roomHandler, Context context){
        super();
        this.roomHandler = roomHandler;

        this.context = context;
    }

     /**
     * Handles incomming messages form SocketService and changes the model
     * @param msg
     */

    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        String sCommand = command.getCommand();
        Log.i("Commandhandler", "Got the command " + sCommand);
        //TODO way to send info to activity

        //TODO implement  spec
       switch (sCommand.toLowerCase()){
           case "setid":
               roomHandler.setUserid((Integer) command.getValue());
               break;
           case "addeduser":
               roomHandler.addUser(new User((Integer)command.getKey()), (Integer) command.getValue());
               break;
           case "changedusername":
               roomHandler.getUser((Integer) command.getKey()).setName((String) command.getValue());
               break;
           case "changedroomname":
               roomHandler.changeRoomName((Integer) command.getKey(), (String) command.getValue());
           case "moveduser":
               roomHandler.moveUser((Integer) command.getKey(), (Integer) command.getValue());
               break;
           case "createdroom":
               roomHandler.addRoom((Integer) command.getKey(), (String) command.getValue());
               break;
           case "removeduser":
               roomHandler.removeUser((Integer) command.getKey());
               break;
           case "removedroom":
               roomHandler.removeRoom((Integer) command.getKey());
               break;
           case "requestsoundport":
               int port = soundController.addStream((Integer) command.getKey());
               try {
                   new Messenger(msg.getTarget()).send(Message.obtain(null, MessageValues.SETSOUNDPORT, port));
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
               break;
           case "usesoundport":
               soundController = new SoundController(context, remoteIP, (Integer) command.getKey());
               break;
       }

        Log.d(this.getClass().toString(), roomHandler.toString());


    }
}
