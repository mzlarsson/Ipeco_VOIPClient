package se.chalmers.fleetspeak.audio.sound;

import android.media.AudioTrack;
import android.os.Process;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.Network.UDP.RTPHandler;

import static se.chalmers.fleetspeak.audio.sound.SoundConstants.*;

/**
 * A class for playing PCMU sound from a buffer
 * Created by Fridgeridge on 2015-06-20.
 */
public class SoundOutputController implements Runnable {

    private AudioTrack audioTrack;
    private boolean soundIsPlaying;
    private AudioOutputProcessor audioOutputProcessor;

    private Executor executor;

    public SoundOutputController(RTPHandler rtpHandler){
        audioOutputProcessor = new AudioOutputProcessor(rtpHandler);
        audioTrack = new AudioTrack(
                STREAM_TYPE.value(),
                SAMPLE_RATE.value(),
                OUTPUT_CHANNEL_CONFIG.value(),
                AUDIO_ENCODING.value(),
                AUDIO_OUT_BUFFER_SIZE.value(),
                SESSION_ID.value()
        );

        if( audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            throw new ExceptionInInitializerError("AudioTrack couldn't initialize");
        }

        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
        init();
    }

    public synchronized void destroy(){
        soundIsPlaying = false;
        if(audioTrack != null){
            audioTrack.release();
        }
    }

    public void init(){
        audioTrack.play();
        soundIsPlaying = true;
    }

    public void writeAudio(byte[] b,int offset){
        audioTrack.write(b,offset,b.length);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        while(soundIsPlaying){
            try {
                writeAudio(audioOutputProcessor.readBuffer(), 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }



}
