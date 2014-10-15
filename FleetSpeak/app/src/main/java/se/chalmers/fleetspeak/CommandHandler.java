package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 08/10/14.
 */
public class CommandHandler extends Handler {

    private RoomHandler roomHandler;

    private static CommandHandler commandHandler = new CommandHandler();

    private CommandHandler(){
        super();
        roomHandler = new RoomHandler();
    }

    public static CommandHandler getInstance(){
        return commandHandler;
    }

    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        String sCommand = command.getCommand();
        Log.i("Commandhandler", "Got the message " + sCommand);
        //TODO
        if(sCommand.equals("setID")){
            roomHandler.addUser(new User((Integer)command.getKey()));
        }else if(sCommand.equals("setName")){
            User u = roomHandler.getUser((Integer) command.getKey());
            u.setName((String)command.getValue());
        }else if(sCommand.equals("userDisconnected")){
            roomHandler.removeUser((Integer)command.getKey());
        }else if(sCommand.equals("setRtpPort")){
            //TODO
        }else if(sCommand.equals("createAndMove")){
            //TODO
        }else if(sCommand.equals("move")){
            roomHandler.moveUser((Integer)command.getKey(),(Integer)command.getValue());
        }else if(sCommand.equals("newUser")){
            roomHandler.addUser(new User((Integer)command.getKey()));
        }else if(sCommand.equals("addUser")){
            roomHandler.addUser(new User( (String) command.getValue(),(Integer) command.getKey())) ;
        }

        listUsers();

    }


    private void listUsers(){
        for(Room r : roomHandler.getRooms()){
            Log.i("LISTUSER", "RoomID " + r.getId());
            for(User u : roomHandler.getUsers(r)){
                Log.i("LISTUSER", u.toString());
            }
        }
    }

}
