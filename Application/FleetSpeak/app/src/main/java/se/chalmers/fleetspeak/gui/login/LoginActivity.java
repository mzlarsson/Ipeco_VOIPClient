package se.chalmers.fleetspeak.gui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.network.TCP.SocketFactory;
import se.chalmers.fleetspeak.gui.connected.ConnectionActivity;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;
import se.chalmers.fleetspeak.truck.TruckModeHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;

public class LoginActivity extends ActionBarActivity implements TruckStateListener, StartCommunicator {
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private boolean carmode = false;
    private String username;
    private String password;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();

        if(this.getIntent().hasExtra("error")){
                error = this.getIntent().getStringExtra("error");
        }else{
                error = null;
        }
        String defaultUsername = this.getApplicationContext().getString(R.string.username_text);
        username = prefs.getString("username", defaultUsername);


        password = prefs.getString("password", "");
        TruckModeHandler handler = TruckModeHandlerFactory.getHandler(this);
        handler.addListener(this);
        carmode = handler.truckModeActive();
        setStartFragment(carmode);
    }

    private void setStartFragment(boolean b){
        Fragment fragment;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(b){
            fragment = fragmentManager.findFragmentByTag("CarStartLogin");
            if( fragment == null);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("password", password);
            if(error!= null) {
                bundle.putString("error", error);
            }
            fragment = new CarStartLogin();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, fragment, "CarStartLogin");
        }else{
            fragment = fragmentManager.findFragmentByTag("Start");
            if( fragment == null);
            Bundle bundle = new Bundle();
            if(error!= null) {
                bundle.putString("error", error);
            }
            bundle.putString("username", username);
            bundle.putString("password", password);
            fragment = new StartLogin();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, fragment, "Start");
        }
        Log.d("NanoDebug", "Well now we do the commit the redneck way");
        fragmentTransaction.commitAllowingStateLoss();
    }
    public void saveUserSettings(){
        Fragment fragment = fragmentManager.findFragmentByTag("Start");
        if(fragment != null && fragment.isVisible()){
            EditText usernameField = (EditText)findViewById(R.id.usernameField);
            prefEdit.putString("username", usernameField.getText().toString());

            EditText passwordField = (EditText)findViewById(R.id.passwordField);
            prefEdit.putString("password", passwordField.getText().toString());
        }else{
            fragment = fragmentManager.findFragmentByTag("Start");
            if(fragment == null && fragment.isVisible()){
                TextView usernameField = (TextView)findViewById(R.id.usernameField);
                prefEdit.putString("username", usernameField.getText().toString());
                TextView passwordField = (TextView) findViewById(R.id.passwordField);
                prefEdit.putString("password", passwordField.getText().toString());
            }
        }
        prefEdit.commit();
    }
    public void startConnectionProcess(){
        SocketFactory.setContext(this);
        Intent newIntent = new Intent(this, ConnectionActivity.class);
        username = username.replaceAll("\\s+","");
        //FIXME remove strip when we actually use the password
        password = password.replaceAll("\\s+","");
        newIntent.putExtra("username", username);
        newIntent.putExtra("password", password);
        newIntent.putExtra("carState", carmode);
        Log.d("test", "asdf2 |" + password + "|");
        startActivity(newIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_truck_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.changeTruck) {
            truckModeChanged(!carmode);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void truckModeChanged(boolean mode) {
        carmode = mode;
        setStartFragment(carmode);

    }

    @Override
    public void changePassword(String b) {
        password = b;
        CarStartLogin fragment = (CarStartLogin) fragmentManager.findFragmentByTag("CarStartLogin");
        if(fragment != null){
            fragment.changePassword(b);
        }
    }

    @Override
    public void changeUsername(String a) {
        username = a;
        CarStartLogin fragment = (CarStartLogin) fragmentManager.findFragmentByTag("CarStartLogin");
        if(fragment != null){
            fragment.changeUsername(a);
        }
    }
    @Override
    public void onBackPressed(){
        Log.d("LoginActivity", " back pressed");
        this.finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Remove truck mode notifications
        TruckModeHandler handler = TruckModeHandlerFactory.getCurrentHandler();
        handler.removeListener(this);
    }
}