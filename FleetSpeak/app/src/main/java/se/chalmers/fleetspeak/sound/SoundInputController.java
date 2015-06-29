package se.chalmers.fleetspeak.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * A class for recording audio from the microphone to a bytebuffer.
 * Created by Fridgeridge on 2015-06-18.
 */
public class SoundInputController implements Runnable {

    private AudioRecord audioRecord;
    private ByteBuffer audioRecBuffer;
    private volatile boolean isRecording;

    public SoundInputController(){

        audioRecBuffer = ByteBuffer.allocateDirect(AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)*4);
        try {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)    //TODO Unsure if this is a correct buffer size //FIXME Export to some constants class
            );
        }catch (IllegalArgumentException e) {
            Log.e("SoundRecord", e.getMessage());//TODO Remove or replace this snippet
        }

        if(audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
            throw new ExceptionInInitializerError("AudioRecord couldn't initialize");

    }


    //Write
    public synchronized void fillAudioBuffer(){
        if(audioRecBuffer.remaining()>= 512) {
            int read = audioRecord.read(audioRecBuffer, 512);
            audioRecBuffer.limit(read);
            audioRecBuffer.position(read);
        }
    }

    //Read
    public synchronized byte[] readBuffer(){
        audioRecBuffer.flip();
        byte[] audioArray = new byte[audioRecBuffer.remaining()];
        try {//FIXME Remove try/catch
            audioRecBuffer.get(audioArray);
        }catch(BufferUnderflowException e){
                e.printStackTrace();
            }
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
