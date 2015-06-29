package se.chalmers.fleetspeak.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;

import java.nio.ByteBuffer;

/**
 * A class for playing PCMU sound from a buffer
 * Created by Fridgeridge on 2015-06-20.
 */
public class SoundOutputController implements Runnable {

    private AudioTrack audioTrack;
    private ByteBuffer audioPlayBuffer;
    private volatile boolean soundIsPlaying;



    public SoundOutputController(){

        audioPlayBuffer = ByteBuffer.allocateDirect(AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM
        );

        if( audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            throw new ExceptionInInitializerError("AudioTrack couldn't initialize");
        }
        init();
    }

    //Read
    public synchronized void playFromBuffer(){
        if(audioPlayBuffer.hasRemaining()) {
            audioPlayBuffer.flip();
            byte[] audioArray = new byte[audioPlayBuffer.remaining()];
            audioPlayBuffer.get(audioArray);
            audioPlayBuffer.compact();
            audioTrack.write(audioArray, 0, audioArray.length);
        }
    }


    //Write
    public synchronized void fillAudioBuffer(byte[] b){
        if(audioPlayBuffer.remaining() > b.length) {
            audioPlayBuffer.put(b);
        }
    }

    public synchronized void kill(){
        soundIsPlaying = false;
        if(audioTrack != null){
            audioTrack.release();
        }
        audioPlayBuffer = null;
    }

    public void init(){
        audioTrack.play();
        soundIsPlaying = true;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        while(soundIsPlaying){
            playFromBuffer();
        }
    }
}
