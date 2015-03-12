package se.chalmers.fleetspeak.sound;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

    private ArrayList<AudioTrack> audioTracks;
    private AudioTrack mainAudioTrack;

    private String ip;
    private int port;
    DatagramSocket ds;

	
    /**
     * Starts a new send only stream that connected to ip:port
     */
    public SoundController(Context context, String ip, int port){
        Log.d("SoundController", "Creating SoundContoller ip " + ip + ":" + port);
        this.ip = ip;
        this.port = port;
        mainAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_8BIT,160,AudioTrack.MODE_STREAM);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);

        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_NORMAL);

        Log.d("SoundController", "Connecting upstream to " + ip + ":" + port);


        try {
            upStream = new AudioStream(InetAddress.getByName(fetchIP()));
            upStream.setMode(RtpStream.MODE_SEND_ONLY);
            upStream.setCodec(AudioCodec.PCMU);
            upStream.associate(InetAddress.getByName(ip), port);
            upStream.join(audioGroup);
            Log.i("SoundController", "Connected to " + ip + ":" + port);
            Log.d("SoundController", audioGroup.getStreams()[0].getCodec().toString() + "upstream");
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host: " + ip);
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
        }

		
        downStreams = new HashMap<Integer, AudioStream>();

        Log.d("SoundController", "SoundController created. audioGroup mode: " + audioGroup.getMode() + " ,audioManager mode: " + audioManager.getMode());

        audioTracks = new ArrayList<>();
        Log.d("SoundController", "SoundController created");
        mainAudioTrack.play();
    }

    public int addStream(int i){
        try {
            ds = new DatagramSocket(40000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket p = new DatagramPacket(new byte[200], 200);
                long time;
                while(true){
                    try {
                        time = System.currentTimeMillis();
                        ds.receive(p);
                        byte[] bytes = p.getData();
                        mainAudioTrack.write(bytes,20,120);
                        Log.d("Datagram", "diff "+ (System.currentTimeMillis() - time));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        return 40000;
    }
    /**
     * Adds a stream to the audioGroup
     * @param userid for identifying source of stream
     */
    public int adadStream(int userid){
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

            setAudioTracks(userid);
            Log.d("SoundController", "Expecting audio from user " + userid + " on port " + stream.getLocalPort());

            return stream.getLocalPort();
        } catch (UnknownHostException e) {
            Log.d(this.getClass().toString(), "Unknown host ");
        } catch (SocketException e) {
            Log.d(this.getClass().toString(), "Socket Error");
            e.printStackTrace();
        }

        return 0;
    }
    public void setAudioTracks(int userID){

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
        AudioStream stream = downStreams.get(userid);
        stream.join((stream.isBusy() ? null : audioGroup));
    }
    public void pushToTalk(){
        upStream.join((upStream.isBusy() ? null: audioGroup));
    }
    public boolean isTalking(){
        return upStream.isBusy();
    }
}
