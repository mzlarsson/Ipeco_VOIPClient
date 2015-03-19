package se.chalmers.fleetspeak.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
public class MicSender extends Thread {

    private static MicSender micSender;
    private static final int updateInterval = 160;

    private boolean stopped = false;
    private DatagramSocket output;
    private DatagramPacket packet;
    private byte[] buffer = new byte[172];

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    private MicSender(String ip, int port) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        try {
            output = new DatagramSocket();
            packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
        } catch (SocketException e) {
            Log.d("Mic", "Could not start mic sender socket");
        } catch (UnknownHostException e) {
            Log.d("Mic", "Could not find server");
        }

        loadRTPHeader();
    }

    public static void startPlayBack(String ip, int port){
        if(micSender != null){
            micSender.close();
        }

        micSender = new MicSender(ip, port);
        micSender.start();
    }

    public static void stopPlayback(){
        if(micSender != null) {
            micSender.close();
            micSender = null;
        }
    }

    @Override
    public void run() {
        Log.i("MicSender", "Running MicSender Thread");
        AudioRecord recorder = null;
        byte[][] buffers = new byte[256][160];
        int ix = 0;

        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
        try {
            int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_8BIT);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_8BIT, N * 10);
            recorder.startRecording();
            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
            while (!stopped) {
                Log.i("Map", "Writing new data to buffer");
                byte[] soundBuffer = buffers[ix++ % buffers.length];
                N = recorder.read(soundBuffer, 0, soundBuffer.length);
                //Copy data part
                System.arraycopy(soundBuffer, 0, this.buffer, 12, 160);
                //Fix header
                updateRTPHeader();
                packet.setData(this.buffer);
                output.send(packet);
            }
        } catch (Throwable x) {
            Log.w("MicSender", "Error reading voice audio", x);
        }
        /*
         * Frees the thread's resources after the loop completes so that it can be run again
         */ finally {
            recorder.stop();
            recorder.release();
            output.close();
        }
    }

    private void loadRTPHeader(){
        byte[] tmp = new byte[4];
        //General settings, empirically gathered
        buffer[0] = -128;
        buffer[1] = 0;
        //Sequence number bits.
        buffer[2] = 35;
        buffer[3] = 0;
        //Timestamp (updated in runtime)
        new Random().nextBytes(tmp);
        System.arraycopy(tmp, 0, buffer, 4, 4);
        //Ssrc (source identifier)
        new Random().nextBytes(tmp);
        System.arraycopy(tmp, 0, buffer, 8, 4);
    }

    private void updateRTPHeader(){
        //Count up packet num
        buffer[3]++;
        if(buffer[3]==0){
            buffer[2]++;
        }

        //Count up time stamp
        buffer[7] += updateInterval;
        if(buffer[7]>=0 && buffer[7]-updateInterval<0){
            buffer[6]++;
            if(buffer[6]==0){
                buffer[5]++;
                if(buffer[5]==0){
                    buffer[4]++;
                }
            }
        }
    }

    /**
     * Called from outside of the thread in order to stop the recording/playback loop
     */
    private void close() {
      stopped = true;
    }
}