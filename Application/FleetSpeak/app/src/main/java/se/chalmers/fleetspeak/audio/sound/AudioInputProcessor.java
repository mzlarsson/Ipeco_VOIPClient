package se.chalmers.fleetspeak.audio.sound;


import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.EncoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusDecoder;
import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusEncoder;


/**
 * Created by Fridgeridge on 2015-08-28.
 */
public class AudioInputProcessor implements Runnable {

    final String LOGTAG = "AudioInputProcessor";

    private final Executor executor;
    boolean isProcessing;
    private BlockingQueue<byte[]> processBuffer;


    EncoderInterface opusEncoder;
    private SoundInputController soundInputController;


    public AudioInputProcessor() throws FleetspeakAudioException {
        opusEncoder = new OpusEncoder();
        soundInputController = new SoundInputController();
        processBuffer = new LinkedBlockingQueue<>(10);//TODO Fix as a importable constant
        Log.i(LOGTAG, "initiated");
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    public byte[] readBuffer() throws InterruptedException {
        return processBuffer.take();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        Thread.currentThread().setName("AudioInputProcessorThread");
        Log.i(LOGTAG, "working started " + Thread.currentThread().getName());
        isProcessing = true;

        byte[] sound;
        while (isProcessing) {

            try {
                sound = soundInputController.readBuffer();
                if(sound !=null) {
                    byte[] encoded = opusEncoder.encode(sound, 0);
                    processBuffer.put(encoded);
                }else{
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public void terminate() {
        isProcessing = false;
        if(soundInputController != null)
            soundInputController.destroy();
    }
}
