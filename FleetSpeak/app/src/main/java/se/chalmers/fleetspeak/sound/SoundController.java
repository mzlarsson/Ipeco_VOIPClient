package se.chalmers.fleetspeak.sound;

import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Matz on 2014-10-10.
 */
public class SoundController {

    private AudioGroup audioGroup;

    private static String clientIP = "192.168.43.26";

    private SoundController(AudioGroup audioGroup){
        this.audioGroup = audioGroup;
    }

    public static SoundController create(Context context, String serverIP, int serverPort){
        fetchIP(context);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);

        AudioStream inRtpStream = null;
        try {
            inRtpStream = new AudioStream(InetAddress.getByName(clientIP));
        } catch(UnknownHostException uhe){
            Log.d("Sound", "Unknown host: "+clientIP);
        }catch (SocketException e) {
            Log.d("Sound", "Socket Error");
        }
        inRtpStream.setMode(RtpStream.MODE_SEND_ONLY);
        inRtpStream.setCodec(AudioCodec.PCMU);
        try{
            inRtpStream.associate(InetAddress.getByName(serverIP), serverPort);
        } catch(UnknownHostException uhe){
            Log.d("Sound", "Unknown host: " + serverIP + ":" + serverPort);
        }

        AudioGroup audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
        inRtpStream.join(audioGroup);
        Log.d("Sound"," Group joined"+inRtpStream.getLocalPort());

        return new SoundController(audioGroup);
    }

    private static void fetchIP(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        clientIP = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        Log.d("Sound", "Fetched IP: "+clientIP);
    }

    public void close(){
        this.audioGroup.clear();
        Log.d("Sound", "Group left...");
    }
}
