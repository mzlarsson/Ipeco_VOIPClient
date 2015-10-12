package se.chalmers.fleetspeak.audio.sound;

import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.network_tmp.UDP.RTPHandler;

/**
 * A class for playing PCMU sound from a buffer
 * Created by Fridgeridge on 2015-06-20.
 */
public class SoundOutputController implements Runnable {

    private AudioTrack audioTrack;



    private volatile boolean soundIsPlaying;
    private AudioOutputProcessor audioOutputProcessor;
    private ByteBuffer echoBuffer;

    private Executor executor;
    private SoundConstants s;
    public SoundOutputController(RTPHandler rtpHandler){
        s = SoundConstants.getCurrent();
        audioOutputProcessor = new AudioOutputProcessor(rtpHandler);
        audioTrack = new AudioTrack(
                s.getStreamType(),
                s.getSampleRate(),
                s.getOutputChannelConfig(),
                s.getEncoding(),
                s.getOutputBufferSize(),
                s.getSessionID()
        );

        if( audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            throw new ExceptionInInitializerError("AudioTrack couldn't initialize");
        }
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    public synchronized void destroy(){
        soundIsPlaying = false;
        if(audioTrack != null){
            audioTrack.flush();
            audioTrack.release();
        }
        if(audioOutputProcessor != null)
            audioOutputProcessor.terminate();
    }


    public void writeAudio(byte[] b,int offset){
        audioTrack.write(b,offset,b.length);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("SoundOutputControllerThread");
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        audioTrack.play();
        soundIsPlaying = true;
        Log.i("SOC", "sound is playing on " + Thread.currentThread().getName());
        while(soundIsPlaying){

            try {
                writeAudio(audioOutputProcessor.readBuffer(), 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Log.i(Thread.currentThread().getName(),"Closing thread");
    }



}
