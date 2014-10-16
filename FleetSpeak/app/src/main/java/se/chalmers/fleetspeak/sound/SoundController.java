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

    private AudioGroup audioGroup;
    private AudioStream audioStream;

    private static SoundController currentSoundController;

    private static String clientIP = "192.168.1.5";

    private SoundController(AudioStream audioStream, AudioGroup audioGroup){
        this.audioStream = audioStream;
        this.audioGroup = audioGroup;
    }

    public static SoundController create(Context context, String serverIP, int serverPort){
        fetchIP();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AudioStream audioStream = null;
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

        AudioGroup audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_NORMAL);
        audioStream.join(audioGroup);
        Log.d("Sound", " Group joined" + audioStream.getLocalPort());

        currentSoundController = new SoundController(audioStream, audioGroup);
        return currentSoundController;
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
        return currentSoundController != null;
    }

    public static int getPort(){
        Log.d("Sound", "Fetching port..."+currentSoundController.audioStream.getLocalPort());
        return currentSoundController.audioStream.getLocalPort();
    }

    public void close(){
        this.audioGroup.clear();
        Log.d("Sound", "Group left...");
    }
}
