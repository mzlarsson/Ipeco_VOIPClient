package se.chalmers.fleetspeak.sound;

import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Matz on 2014-10-10.
 */
public class SoundController {
    private static AudioManager audioManager;
    private static AudioGroup audioGroup;
    private static AudioStream audioStream;
    private static boolean loaded = false;

    private static String clientIP = "192.168.1.5";

    public static void create(Context context, String serverIP, int serverPort){
        fetchIP();

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            audioStream = new AudioStream(InetAddress.getByName(clientIP));
        } catch(UnknownHostException uhe){
            Log.d("Sound", "Unknown host: "+clientIP);
        }catch (SocketException e) {
            Log.d("Sound", "Socket Error");
        }
        audioStream.setMode(RtpStream.MODE_NORMAL);
        audioStream.setCodec(AudioCodec.PCMU);
        try{
            audioStream.associate(InetAddress.getByName(serverIP), serverPort);
        } catch(UnknownHostException uhe){
            Log.d("Sound", "Unknown host: " + serverIP + ":" + serverPort);
        }

        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_NORMAL);
        audioStream.join(audioGroup);

        loaded = true;
    }

    /**
     * Sets the sound volume to a integer
     * @param i - the integer
     */
    public static void setVoloume(int i){
        if(audioManager != null){
            Log.i("AudioManager", ": set Volume to" + i);
            if(i == 0){
                audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
            }
            else {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, i, 0);
                Log.i("AudioManager: ", "current Volume = " + audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
            }
        }
    }

    /**
     * Get the current volume as a integer, if the current volume cannot be accessed return -1
     * @return int - the current volume
     */
    public static int getCurrentVolume(){
        if(audioManager!= null){
            return  audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        }
        else{
            return  -1;
        }
    }

    /**
     * Get the max value of the volume as a integer, if the max volume cannot be accessed return -1
     * @return int - the max volume value
     */
    public static int getMaxVolume(){
        if(audioManager != null){
            return  audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        }else {
            return -1;
        }
    }
    public static void mute(boolean b){
        audioManager.setMicrophoneMute(b);
    }

    private static void fetchIP(){
        try {
            NetworkInterface network = null;
            InetAddress ip = null;

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                network = en.nextElement();
                for (Enumeration<InetAddress> enumIp = network.getInetAddresses(); enumIp.hasMoreElements();) {
                    ip = enumIp.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        Log.d("Sound", "An IPv4 found: "+ip.getHostAddress());
                        clientIP = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("SocketException ", ex.toString());
        }
        Log.d("Sound", "Fetched IP: "+clientIP);
    }

    public static boolean hasValue(){
        return (loaded && audioGroup!=null && audioStream!=null);
    }

    public static int getPort(){
        Log.d("Sound", "Fetching port..."+audioStream.getLocalPort());
        return audioStream.getLocalPort();
    }

    public static void close(){
        if(hasValue())
            audioGroup.clear();
        audioGroup = null;
        audioStream = null;
        loaded = false;
        Log.d("Sound", "Group left...");
    }
}
