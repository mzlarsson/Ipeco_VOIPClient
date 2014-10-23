package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 08/10/14.
 * For handling commands coming from the server.
 * Need to be added as Messenger to socketService to work.
 * Owns the RoomHandler
 */
public class CommandHandler extends Handler {

    private static CommandHandler commandHandler = new CommandHandler();
    private static RoomHandler roomHandler;
    private static User user;
    private static ArrayList<Commandable> activities = new ArrayList<Commandable>();

    private CommandHandler(){
        super();
        roomHandler = new RoomHandler();
        //tester();
    }

    /**
     * For testing without having a server connection
     */
    private void tester(){
        roomHandler.addUser(new User("Simulated User55", 55));
        roomHandler.addToNewRoom(new User("Simulated User56", 56),"Joan Rivers memory room");
        roomHandler.addToNewRoom(new User("Simulated User57", 57), "Ryska ubåtar");
        roomHandler.addUser(new User("Simulated User58", 58));
        roomHandler.addUser(new User("Simulated User59", 59));
        roomHandler.addUser(new User("Simulated User60", 60));



    }

    public static CommandHandler getInstance(){
        return commandHandler;
    }

    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        String sCommand = command.getCommand();
        Log.i("Commandhandler", "Got the command " + sCommand);
        //TODO

        String aCommand = "dataUpdate";

        if(sCommand.equals("setID")){
            user = new User((Integer)command.getKey());
            roomHandler = new RoomHandler();
            roomHandler.addUser(user);
            aCommand = "connected";
        }else if(sCommand.equals("connection failed")){
            aCommand = "connection failed";
        }else if(sCommand.equals("setName")){
            User u = roomHandler.getUser((Integer) command.getKey());
            u.setName((String)command.getValue());
        }else if(sCommand.equals("userDisconnected")){
            roomHandler.removeUser((Integer)command.getKey());
        }else if(sCommand.equals("setRtpPort")){
            //Not used atm
        }else if(sCommand.equals("moveUser")){
            roomHandler.moveUser((Integer)command.getKey(),(Integer)command.getValue());
        }else if(sCommand.equals("newUser")){
            roomHandler.addUser(new User((Integer) command.getKey()));
        }else if(sCommand.equals("addUser")){
            String[] u = ((String) command.getKey()).split(",");
            String[] r = ((String) command.getValue()).split(",");
            roomHandler.addUser(new User(u[0],Integer.parseInt(u[1])), new Room(r[0],Integer.parseInt(r[1])));
        }else if(sCommand.equals("createAndMove")){
            Log.i(this.getClass().toString(), "Crate and mååv ");
            String[] s = ((String) command.getValue()).split(",");
            roomHandler.moveUser(roomHandler.getUser((Integer) command.getKey()), new Room(s[0], Integer.parseInt(s[1])));
            if((Integer)command.getKey() == user.getId())
                aCommand = "roomCreated," + s[1];
        }else{
            aCommand = "unknown command";
        }

        listUsers();
        postUpdate(aCommand);

    }

    public static void addListener(Commandable a){
        if(!activities.contains(a))
            activities.add(a);
    }

    public static void removeListener(Commandable a){
        activities.remove(a);
    }

    private void postUpdate(String command){
        for(Commandable a: activities){
            a.onDataUpdate(command);
        }
    }

    public static User[] getUsers(int roomID){
        return roomHandler.getUsers(roomID);
    }

    public static Room[] getRooms(){
         return roomHandler.getRooms();
    }

    public static User getUser(){
        return user;
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
