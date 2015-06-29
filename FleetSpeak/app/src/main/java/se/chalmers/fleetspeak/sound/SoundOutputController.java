package se.chalmers.fleetspeak.sound;

import android.media.AudioTrack;
import android.os.Process;
import java.nio.ByteBuffer;

import static se.chalmers.fleetspeak.sound.SoundConstants.*;

/**
 * A class for playing PCMU sound from a buffer
 * Created by Fridgeridge on 2015-06-20.
 */
public class SoundOutputController implements Runnable {

    private AudioTrack audioTrack;
    private ByteBuffer audioPlayBuffer;
    private volatile boolean soundIsPlaying;

    //Local variable export
    private byte[] audioArray;


    public SoundOutputController(){

        audioPlayBuffer = ByteBuffer.allocateDirect(BYTEBUFFER_SIZE.value());
        audioTrack = new AudioTrack(
                STREAM_TYPE.value(),
                SAMPLING_RATE.value(),
                OUTPUT_CHANNEL_CONFIG.value(),
                AUDIO_ENCODING.value(),
                AUDIO_BUFFER_SIZE.value(),
                SESSION_ID.value()
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
            audioArray = new byte[audioPlayBuffer.remaining()];
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
