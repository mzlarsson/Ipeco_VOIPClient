package se.ipeco.speedtester;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.ipeco.speedtester.R;

public class MainActivity extends Activity {

    private static String defaultProvider = LocationManager.GPS_PROVIDER;

    private Context context;
    private ImageView providerStatusView;
    private TextView speedLabel;
    private TextView providerNameDisplayer;
    private LinearLayout providerExampleWrapper;

    private LocationProvider provider;
    private LocationListener locationListener;

    private List<Toast> errorToasts = new ArrayList<Toast>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        providerStatusView = (ImageView)view.findViewById(R.id.providerStatus);
        speedLabel = (TextView)view.findViewById(R.id.speedLabel);
        providerNameDisplayer = (TextView)view.findViewById(R.id.providerNameDisplayer);
        providerExampleWrapper = (LinearLayout)view.findViewById(R.id.providerExampleWrapper);
        initProviderSwitches();

        setContentView(view);
        setupTracker(defaultProvider);
    }

    private List<String> getProviders(){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getProviders(false);
    }

    private void initProviderSwitches(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 0);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupTracker(((TextView) v).getText().toString());
            }
        };

        for (String providerName : getProviders()){
            Button providerView = new Button(this);
            providerView.setText(providerName);
            providerView.setLayoutParams(params);
            providerView.setOnClickListener(listener);
            providerExampleWrapper.addView(providerView);
        }
    }

    private void initListener(){
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            private boolean firstData = true;

            public void onLocationChanged(Location location) {
                speedLabel.setText(location.getSpeed()+"");

                if(firstData){
                    showInfo("Retrieved first bunch of data.");
                    firstData = false;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(context, "Status changed: "+status+" [Extras: "+extras.toString()+"]", Toast.LENGTH_LONG).show();
            }

            public void onProviderEnabled(String enabledProvider) {
                if(enabledProvider.equals(provider.getName())) {
                    providerStatusView.setImageDrawable(getResources().getDrawable(firstData?R.drawable.pending:R.drawable.active));
                }
            }

            public void onProviderDisabled(String disabledProvider) {
                if(disabledProvider.equals(provider.getName())) {
                    providerStatusView.setImageDrawable(getResources().getDrawable(R.drawable.inactive));
                }
            }
        };
    }

    private synchronized void showError(String error){
        cancelErrors();
        Toast errorToast = Toast.makeText(context, error, Toast.LENGTH_SHORT);
        errorToast.show();
        errorToasts.add(errorToast);
    }

    private void cancelErrors(){
        for(Toast t : errorToasts){
            t.cancel();
            errorToasts.remove(t);
        }
    }

    private void showInfo(String info){
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    }

    private Button findProviderView(String name){
        for(int i = 0; i<providerExampleWrapper.getChildCount(); i++){
            Button providerView = (Button)providerExampleWrapper.getChildAt(i);
            if(providerView.getText().toString().equals(provider.getName())){
                return providerView;
            }
        }

        return null;
    }

    private void setupTracker(final String providerName){
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Init listener if not initiated
        if(locationListener == null){
            initListener();
        }else{
            try {
                locationManager.removeUpdates(locationListener);
            }catch(SecurityException se){
                showError(getResources().getString(R.string.serious_error));
            }
        }

        if(provider == null || !provider.getName().equals(providerName)) {
            //Delete options
            if(provider != null){
                Button providerView = findProviderView(provider.getName());
                if(providerView != null){
                    providerView.setVisibility(Button.VISIBLE);
                }
            }

            //Setup all stuff
            try {
                providerNameDisplayer.setText(providerName);
                locationManager.requestLocationUpdates(providerName, 100, 3, locationListener);

                //Init error messages
                provider = locationManager.getProvider(providerName);
                if(!locationManager.isProviderEnabled(provider.getName())){
                    showError(getResources().getString(R.string.status_disabled));
                }else if (!provider.supportsSpeed()) {
                    showError(getResources().getString(R.string.status_nospeed));
                }else{
                    cancelErrors();
                }

                //Init provider status
                if (locationManager.isProviderEnabled(provider.getName()) && provider.supportsSpeed()) {
                    locationListener.onProviderEnabled(provider.getName());
                } else {
                    locationListener.onProviderDisabled(provider.getName());
                }

                //Hide provider view
                Button providerView = findProviderView(providerName);
                if(providerView != null){
                    providerView.setVisibility(Button.GONE);
                }
            } catch (SecurityException se) {
                showError(getResources().getString(R.string.serious_error));
            }
        }
    }
}
