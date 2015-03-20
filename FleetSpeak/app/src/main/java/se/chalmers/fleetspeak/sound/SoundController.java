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
    private String ip;
    private int port;
	
    /**
     * Starts a new send only stream that connected to ip:port
     */
    public SoundController(Context context, String ip, int port){
        Log.d("SoundController", "Creating SoundContoller ip " + ip + ":" + port);
        this.ip = ip;
        this.port = port;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
        Log.d("SoundController", audioManager.getProperty(audioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER) + "Frames per buffer. "  + audioManager.getProperty(audioManager.PROPERTY_OUTPUT_SAMPLE_RATE) + " sample rate" );
        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);

        Log.d("SoundController", "Connecting upstream to " + ip + ":" + port);


        try {
            upStream = new AudioStream(InetAddress.getByName(fetchIP()));
            upStream.setMode(RtpStream.MODE_SEND_ONLY);
            upStream.setCodec(AudioCodec.PCMU);
            upStream.associate(InetAddress.getByName(ip), port);
          //  upStream.join(audioGroup);
            Log.i("SoundController", "Connected to " + ip + ":" + port);
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host: " + ip);
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
        }

		
        downStreams = new HashMap<Integer, AudioStream>();

        Log.d("SoundController", "SoundController created. audioGroup mode: " + audioGroup.getMode() + " ,audioManager mode: " + audioManager.getMode());

        Log.d("SoundController", "SoundController created");
       }


    /**
     * Adds a stream to the audioGroup
     * @param userid for identifying source of stream
     */
    public int addStream(int userid){
        AudioStream stream;

        Log.d("SoundController", "adding a stream to user with id:" + userid);

        try {
            String ip = fetchIP();
            stream = new AudioStream(InetAddress.getByName(ip));

            stream.setMode(RtpStream.MODE_RECEIVE_ONLY);
            stream.setCodec(AudioCodec.PCMU);
            Log.d("SoundController", stream.getCodec().toString() + " " );
            stream.associate(InetAddress.getByName(this.ip), port + 1);
            stream.join(audioGroup);
            downStreams.put(userid, stream);

            Log.d("SoundController", "Expecting audio from user " + userid + " on port " + stream.getLocalPort());
            Log.d("SoundController", "Number of downstreams " + downStreams.size());
            return stream.getLocalPort();
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host ");
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Removes the an user-stream from this client's stream
     * @param userid source of the stream
     */
    public void removeUserFromDownStream(int userid){
        downStreams.get(userid).join(null);
        downStreams.remove(userid);
        Log.d("SoundController", "Removed stream from user with userid "+ userid);
    }

    /**
     * Fetches the IPv4 address of the client device
     * @return A String containing the IPV4 address
     */
    private String fetchIP(){
        try {
            NetworkInterface network;
            InetAddress ip;

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                network = en.nextElement();
                for (Enumeration<InetAddress> enumIp = network.getInetAddresses(); enumIp.hasMoreElements();) {
                    ip = enumIp.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        Log.d("fetchip", "An IPv4 found: " + ip.getHostAddress());
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
        Log.i("SoundController", "Closing down... ");
        downStreams.clear();
        audioGroup.clear();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        Log.i("SoundController", "Closed");
    }

    public void muteUser(int userid){
       Log.e("SoundController ", "function not implemented");
       // AudioStream stream = downStreams.get(userid);
       // stream.join((stream.isBusy() ? null : audioGroup));
    }
    public void pushToTalk(){
        if(upStream.isBusy()){
            Log.d("SoundController", "Mic off");
            upStream.join(null);
            //for(Integer i : downStreams.keySet())
            //    downStreams.get(i).join(audioGroup);

        }else{
            //for(Integer i : downStreams.keySet())
            //
            //    downStreams.get(i).join(null);
            upStream.join(audioGroup);
            Log.d("SoundController", "Mic on");

        }
    }
    public boolean isTalking(){
        return upStream.isBusy();
    }
}
