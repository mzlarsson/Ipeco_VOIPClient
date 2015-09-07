package se.chalmers.fleetspeak.audio.sound;

import android.media.AudioRecord;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;

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

    private SoundConstants s;

    public SoundInputController() throws FleetspeakAudioException {
        s = SoundConstants.getCurrent();
        inputBuffer = new LinkedBlockingQueue<>(10);
        try {
            audioRecord = new AudioRecord(
                    s.getInputSource(),
                    s.getSampleRate(),
                    s.getInputChannelConfig(),
                    s.getEncoding(),
                    s.getInputBufferSize()
            );
        } catch (IllegalArgumentException e) {
            Log.e("SoundRecord", e.getMessage());//TODO Remove or replace this snippet
        }

        if (audioRecord == null || audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
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
                byte[] input = new byte[s.getPCMSize()];
                audioRecord.read(input,0,input.length);
            try {
                inputBuffer.put(input);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }




}
