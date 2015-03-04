package se.chalmers.fleetspeak.sound;

import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Nieo on 22/02/15.
 * For controling an Audiogroup
 */
public class SoundController {
    private AudioManager audioManager;
    private AudioGroup audioGroup;
    private AudioStream upStream;

    /**
     * Starts a new send only stream that connected to ip:port
     */
    public SoundController(Context context, String ip, int port){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);

        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_NORMAL);

        try {
            upStream = new AudioStream(InetAddress.getByName(fetchIP()));
            upStream.setMode(RtpStream.MODE_SEND_ONLY);
            upStream.setCodec(AudioCodec.PCMU);
            upStream.associate(InetAddress.getByName(ip), port);
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host: " + ip);
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
        }
        upStream.join(audioGroup);

    }

    /**
     * Adds a stream to the audioGroup
     */
    public void addStream(String ip, int port){
        AudioStream stream;

        try {
            stream = new AudioStream(InetAddress.getByName(fetchIP()));
            stream.setMode(RtpStream.MODE_RECEIVE_ONLY);
            stream.setCodec(AudioCodec.PCMU);
            stream.associate(InetAddress.getByName(ip), port);
            stream.join(audioGroup);
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host: " + ip);
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
        }


    }

    public void removeStream(int port){
        AudioStream[] streams = audioGroup.getStreams();
        for(AudioStream a : streams){
            if(a.getRemotePort() == port)
                a.join(null);
               // a.release();
        }
    }

    private String fetchIP(){
        try {
            NetworkInterface network = null;
            InetAddress ip = null;

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                network = en.nextElement();
                for (Enumeration<InetAddress> enumIp = network.getInetAddresses(); enumIp.hasMoreElements();) {
                    ip = enumIp.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        Log.d("Sound", "An IPv4 found: " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("SocketException ", ex.toString());
        }
        return null;
    }
}
