package se.chalmers.fleetspeak.sound;

import android.media.AudioRecord;
import android.util.Log;

import java.nio.ByteBuffer;

import static se.chalmers.fleetspeak.sound.SoundConstants.*;

/**
 * A class for recording audio from the microphone to a bytebuffer.
 * Created by Fridgeridge on 2015-06-18.
 */
public class SoundInputController implements Runnable {

    private AudioRecord audioRecord;
    private ByteBuffer audioRecBuffer;
    private volatile boolean isRecording;

    //Local variable export
    private byte[] audioArray;
    private int bytesRead;


    public SoundInputController(){

        audioRecBuffer = ByteBuffer.allocateDirect(BYTEBUFFER_SIZE.value());
        try {
            audioRecord = new AudioRecord(
                    INPUT_SOURCE.value(),
                    SAMPLING_RATE.value(),
                    INPUT_CHANNEL_CONFIG.value(),
                    AUDIO_ENCODING.value(),
                    AUDIO_BUFFER_SIZE.value()
            );
        }catch (IllegalArgumentException e) {
            Log.e("SoundRecord", e.getMessage());//TODO Remove or replace this snippet
        }

        if(audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
            throw new ExceptionInInitializerError("AudioRecord couldn't initialize");

    }


    //Write
    public synchronized void fillAudioBuffer(){
        if(audioRecBuffer.remaining()>= INPUT_ARRAY_SIZE.value()) {
            bytesRead = audioRecord.read(audioRecBuffer, INPUT_ARRAY_SIZE.value());
            audioRecBuffer.limit(bytesRead);
            audioRecBuffer.position(bytesRead);
        }
    }

    //Read
    public synchronized byte[] readBuffer(){
        audioRecBuffer.flip();
        audioArray = new byte[audioRecBuffer.remaining()];
            audioRecBuffer.get(audioArray);

        audioRecBuffer.compact();
        return audioArray;
    }

    public void init(){
        audioRecord.startRecording();
        isRecording = true;
    }



    public synchronized void kill(){
        if(audioRecord != null){
            audioRecord.release();
        }
        audioRecBuffer = null;
    }

    @Override
    public void run() {
        init();

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        while(isRecording) {
            fillAudioBuffer();

        }
    }



}
