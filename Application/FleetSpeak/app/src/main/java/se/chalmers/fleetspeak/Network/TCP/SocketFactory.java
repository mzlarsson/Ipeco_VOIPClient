package se.chalmers.fleetspeak.Network.TCP;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import se.chalmers.fleetspeak.R;

/**
 * Created by Nieo on 17/08/15.
 */
public class SocketFactory {

 private static Context appContext;

    //TODO not sure how to do this.... Need to run this from activity atm
    public static void setContext(Context c){
        appContext = c;
    }


    public static Socket getSSLSocket(String ip, int port){
        SSLSocketFactory socketFactory = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = appContext.getResources().openRawResource(R.raw.keystore);
            ks.load(in, "fleetspeak".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            Log.i("TLS", tmf.getAlgorithm());
            tmf.init(ks);


            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            Log.i("TLS", sslContext.getProtocol());
            sslContext.init(null, tmf.getTrustManagers(), null);
            socketFactory = sslContext.getSocketFactory();
            return socketFactory.createSocket(ip, port );
        }catch(KeyStoreException e){
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Socket getSocket(String ip, int port){
        try {
            return new Socket(ip,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
