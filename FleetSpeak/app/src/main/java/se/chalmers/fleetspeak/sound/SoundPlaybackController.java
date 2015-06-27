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
public class SoundPlaybackController implements Runnable {

    private AudioTrack audioTrack;
    private ByteBuffer audioPlayBuffer;
    private boolean soundIsPlaying;



    public SoundPlaybackController(){

        audioPlayBuffer = ByteBuffer.allocateDirect(512*4);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_VOICE_CALL,
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

    public void playAudio(byte[] b){
        audioTrack.write(b,0,b.length);
    }

    public void playFromBuffer(){
        if(audioPlayBuffer.hasArray()){
            playAudio(audioPlayBuffer.array());//TODO Really ugly way of handling this
            audioPlayBuffer.reset();
        }
    }

    public synchronized void fillAudioBuffer(ByteBuffer b){
        audioPlayBuffer.put(b);
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

        while(!soundIsPlaying){
            playFromBuffer();
        }
    }
}
