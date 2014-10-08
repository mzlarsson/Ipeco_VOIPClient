package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;



/**
 * Created by Nieo on 08/10/14.
 */
public class CommandHandler extends Handler {

    private EditText et;

    public CommandHandler(EditText e){
        super();
        et =e;
    }


    public void handleMessage(Message msg) {
        et.setText(msg.getData().getString("1"), TextView.BufferType.EDITABLE);
    }


}
