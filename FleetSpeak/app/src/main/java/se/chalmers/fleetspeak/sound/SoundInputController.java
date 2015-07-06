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


    public SoundInputController(){

        audioRecBuffer = ByteBuffer.allocateDirect(BYTEBUFFER_IN_SIZE.value());
        try {
            audioRecord = new AudioRecord(
                    INPUT_SOURCE.value(),
                    SAMPLING_RATE.value(),
                    INPUT_CHANNEL_CONFIG.value(),
                    AUDIO_ENCODING.value(),
                    AUDIO_IN_BUFFER_SIZE.value()
            );
        }catch (IllegalArgumentException e) {
            Log.e("SoundRecord", e.getMessage());//TODO Remove or replace this snippet
        }

        if(audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
            throw new ExceptionInInitializerError("AudioRecord couldn't initialize");

    }

    public void init(){
        audioRecord.startRecording();
        isRecording = true;
    }

    //Write
    public synchronized int fillAudioBuffer() {
        int bytesRead=0;
        if (audioRecBuffer.remaining() >= INPUT_ARRAY_SIZE.value()) {
            bytesRead = audioRecord.read(audioRecBuffer, INPUT_ARRAY_SIZE.value());
            if (bytesRead > 0) {
                audioRecBuffer.limit(bytesRead);
                audioRecBuffer.position(bytesRead);
            }
        }
            return bytesRead;
    }


    //Read
    public synchronized byte[] readBuffer() {
        audioRecBuffer.flip();
        byte[] audioArray = new byte[audioRecBuffer.remaining()];
        audioRecBuffer.get(audioArray);

            if(audioRecBuffer.hasRemaining()) {
                audioRecBuffer.compact();
            }else{
                audioRecBuffer.clear();
            }
        return audioArray;
        }


    public synchronized void destroy(){
        isRecording = false;
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


    public boolean equals(Object o){
        if(o == null || o.getClass() != this.getClass()) {
        return false;
        }
        SoundInputController sic = (SoundInputController) o;

        return this.audioRecBuffer.equals(sic.audioRecBuffer) &&
                this.audioRecord.equals(sic.audioRecord) &&
                this.isRecording == sic.isRecording;
    }


}
