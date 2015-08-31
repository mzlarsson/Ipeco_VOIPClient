package se.chalmers.fleetspeak.audio.sound;

import android.media.AudioRecord;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;

import static se.chalmers.fleetspeak.audio.sound.SoundConstants.AUDIO_ENCODING;
import static se.chalmers.fleetspeak.audio.sound.SoundConstants.AUDIO_IN_BUFFER_SIZE;
import static se.chalmers.fleetspeak.audio.sound.SoundConstants.INPUT_CHANNEL_CONFIG;
import static se.chalmers.fleetspeak.audio.sound.SoundConstants.INPUT_SOURCE;
import static se.chalmers.fleetspeak.audio.sound.SoundConstants.SAMPLE_RATE;

/**
 * A class for recording audio from the microphone to a bytebuffer.
 * Created by Fridgeridge on 2015-06-18.
 */
public class SoundInputController implements Runnable {

    final String LOGTAG = "SoundInputController";
    private AudioRecord audioRecord;

    private BlockingQueue<byte[]> inputBuffer;
    private boolean isRecording;

    private Executor executor;

    public SoundInputController() throws FleetspeakAudioException {
        inputBuffer = new LinkedBlockingQueue<>(10);
        try {
            audioRecord = new AudioRecord(
                    INPUT_SOURCE.value(),
                    SAMPLE_RATE.value(),
                    INPUT_CHANNEL_CONFIG.value(),
                    AUDIO_ENCODING.value(),
                    AUDIO_IN_BUFFER_SIZE.value()
            );
        } catch (IllegalArgumentException e) {
            Log.e("SoundRecord", e.getMessage());//TODO Remove or replace this snippet
        }

        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
            throw new FleetspeakAudioException("AudioRecord couldn't initialize");

        Log.i(LOGTAG, "initiated");
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    public void init() {
        audioRecord.startRecording();
        isRecording = true;
    }

    //Read
    public byte[] readBuffer() throws InterruptedException {
        return inputBuffer.take();
    }


    public void destroy() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("SoundInputControllerThread");
        Log.i(LOGTAG, "started recording " + Thread.currentThread().getName());
        init();
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        while (isRecording) {

                byte[] input = new byte[640];
                audioRecord.read(input,0,input.length);
            try {
                inputBuffer.put(input);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }




}
