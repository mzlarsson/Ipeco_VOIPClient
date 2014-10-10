package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;




/**
 * Created by Nieo on 08/10/14.
 */
public class CommandHandler extends Handler {

    EditText et;

    public CommandHandler(EditText e){
        super();
        et =e;
    }


    public void handleMessage(Message msg) {
        String s = (String) msg.obj;
        Log.i("Commandhandler", "Got the message " + s);
        //TODO
    }


}
