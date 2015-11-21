package se.chalmers.fleetspeak.gui.connected;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import se.chalmers.fleetspeak.R;

/**
 * Created by David Gustafsson on 2015-11-07.
 */
public class CreateRoomDialog extends Dialog {
   public CreateRoomDialog(Context context, final CRDListener listener, final String hint){
       super(context);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       this.setContentView(R.layout.dialog);
       final EditText edit = (EditText)findViewById(R.id.dia_edit);
       edit.setHint(hint);

       Button ok = (Button)findViewById(R.id.btn_ok);
       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if ((edit.getText().toString()).length() == 0) {
                   if (listener != null) {
                       listener.okClick(hint);
                   }
               } else {
                   if (listener != null) {
                       listener.okClick(edit.getText().toString());
                   }
               }
               cancel();
           }
       });
       Button cancel = (Button)findViewById(R.id.btn_cancel);
       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               cancel();
           }
       });

   }
    public interface CRDListener {
        void okClick(String name);
    }
}
