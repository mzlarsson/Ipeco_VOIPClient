package se.chalmers.fleetspeak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;




public class StartActivity extends ActionBarActivity {

    Connector c = null;

    private EditText ip;
    private EditText port;
    private boolean isConnected = false;

    Messenger mService = null;
    final Messenger mMessenger = new Messenger(new CommandHandler(port));

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);


        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ip = (EditText) findViewById(R.id.ipField);
        port = (EditText) findViewById(R.id.portField);
        startService(new Intent(StartActivity.this, SocketService.class));
        bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onConnectButtonClick(View view) {

        String a = String.valueOf(ip.getText());
        String b = String.valueOf(port.getText());
        Toast.makeText(this, a + ":" + b, Toast.LENGTH_SHORT).show();
        if (!isConnected) {
            try {
                Message msg = Message.obtain(null, SocketService.CONNECT, a);
                msg.replyTo = mMessenger;
                mService.send(msg);
                isConnected = true;
            } catch (RemoteException e) {
            }
        }else{
            Log.i("Hej","hej");
            try {
                Message msg = Message.obtain(null, SocketService.SENDDATA);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
        }
       /* if(c == null ||  !c.isConnected()){
            c = new Connector(a, Integer.parseInt(b));
            c.connect();
        }else{
            c.sendCommand("/nick xxx");
            c.sendCommand("/disconnect");
            c.sendCommand("/mute");
            c.sendCommand("/unmute");
           //c.getData("data");
           // port.setText(s, TextView.BufferType.EDITABLE);
        }*/
    }



}
