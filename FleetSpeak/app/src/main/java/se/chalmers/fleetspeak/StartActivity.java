package se.chalmers.fleetspeak;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class StartActivity extends ActionBarActivity {

    Connector c = null;

    private EditText ip;
    private EditText port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);ip = (EditText) findViewById(R.id.ipField);
        port = (EditText) findViewById(R.id.portField);

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
        Toast.makeText(this, a + b, Toast.LENGTH_SHORT).show();
        if(c == null){
            c = new Connector(a, Integer.parseInt(b));
            c.connect();
        }else{
            c.sendCommand(a);
        }
    }

}
