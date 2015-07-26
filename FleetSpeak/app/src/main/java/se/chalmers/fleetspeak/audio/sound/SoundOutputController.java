package se.chalmers.fleetspeak.audio.sound;

import android.media.AudioTrack;
import android.os.Process;
import java.nio.ByteBuffer;

import static se.chalmers.fleetspeak.audio.sound.SoundConstants.*;

/**
 * A class for playing PCMU sound from a buffer
 * Created by Fridgeridge on 2015-06-20.
 */
public class SoundOutputController implements Runnable {

    private AudioTrack audioTrack;
    private ByteBuffer audioPlayBuffer;
    private volatile boolean soundIsPlaying;

    public SoundOutputController(){

        audioPlayBuffer = ByteBuffer.allocateDirect(BYTEBUFFER_OUT_SIZE.value());
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
        init();
    }

    //Read
    public synchronized void playFromBuffer(){
            audioPlayBuffer.flip();
            byte[] audioArray = new byte[audioPlayBuffer.remaining()];
            audioPlayBuffer.get(audioArray);
            audioTrack.write(audioArray, 0, audioArray.length);

            if(audioPlayBuffer.hasRemaining()){
                audioPlayBuffer.compact();
            }else{
                audioPlayBuffer.clear();
            }

    }


    //Write
    public synchronized void fillAudioBuffer(byte[] b){
        if(audioPlayBuffer.remaining() > b.length) {
            audioPlayBuffer.put(b);
        }
    }

    public synchronized void destroy(){
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

            if(audioPlayBuffer.hasRemaining()) {
                playFromBuffer();
            }
        }
    }


    public boolean equals(Object o) {

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SoundOutputController soc = (SoundOutputController) o;
        return this.audioPlayBuffer.equals(soc.audioPlayBuffer) &&
                this.audioTrack.equals(soc.audioTrack) &&
                this.soundIsPlaying == soc.soundIsPlaying;

    }


}
