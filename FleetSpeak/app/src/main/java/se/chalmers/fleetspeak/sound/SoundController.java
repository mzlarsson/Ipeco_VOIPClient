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
import java.util.HashMap;

/**
 * Created by Nieo on 22/02/15.
 * For controling an Audiogroup
 */
public class SoundController {
    private AudioManager audioManager;
    private AudioGroup audioGroup;
    private AudioStream upStream;
    private HashMap<Integer, AudioStream> downStreams;
	
    /**
     * Starts a new send only stream that connected to ip:port
     */
    public SoundController(Context context, String ip, int port){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.d("SoundControler", "getMode " + audioManager.getMode());
        audioManager.setMode(3);
        audioManager.setSpeakerphoneOn(true);
        Log.d("SoundControler", "getMode " + audioManager.getMode());
        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);

        Log.d("SoundController", "Addres" + ip + ":" + port);


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
		
        downStreams = new HashMap<Integer, AudioStream>();
    }

    /**
     * Adds a stream to the audioGroup
     */
    public int addStream(int userid){
        AudioStream stream = null;

        Log.d("Add stream", "Audiogroup " + audioGroup);

        try {
            String ip = fetchIP();
            Log.d("Add steam", "ip:"+ip);
            stream = new AudioStream(InetAddress.getByName(ip));
            stream.setMode(RtpStream.MODE_RECEIVE_ONLY);
            stream.setCodec(AudioCodec.PCMU);

            Log.d("Add stream", "The stream " + stream.getLocalAddress().toString() + ":" + stream.getLocalPort());
            for(AudioStream a: audioGroup.getStreams())
            Log.d("Add stream", "group" + a.getLocalAddress().toString() + ":" +  a.getLocalPort());
            stream.join(null);
            downStreams.put(userid, stream);
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host ");
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
        }
        if(stream != null)
            return stream.getLocalPort();
        return 0;
    }

    /**
     * Removes the an user-stream from this client's stream
     * @param userid The user ID identifying the downStream
     */
    public void removeUserFromDownStream(int userid){
        downStreams.get(userid).join(null);
        downStreams.remove(userid);
    }

    /**
     * Fetches the IPv4 address of the client device
     * @return A String containing the IPV4 address
     */
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

    public void close() {
        downStreams.clear();
        audioGroup.clear();
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }
}
