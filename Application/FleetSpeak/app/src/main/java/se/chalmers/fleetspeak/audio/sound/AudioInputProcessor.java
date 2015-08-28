package se.chalmers.fleetspeak.audio.sound;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusEncoder;

import static se.chalmers.fleetspeak.audio.sound.SoundConstants.*;


/**
 * Created by Fridgeridge on 2015-08-28.
 */
public class AudioInputProcessor implements Runnable {

    boolean isProcessing;
    private BlockingQueue<byte[]> processBuffer;


    OpusEncoder opusEncoder;
    private SoundInputController soundInputController;


    public AudioInputProcessor() throws FleetspeakAudioException {
        opusEncoder = new OpusEncoder();
        soundInputController = new SoundInputController();
        processBuffer = new LinkedBlockingQueue<>(10);//TODO Fix as a importable constant

    }

    public byte[] readBuffer() throws InterruptedException {
        return processBuffer.take();
    }

    @Override
    public void run() {
        isProcessing = true;

        byte[] sound;
        while (isProcessing) {

            try {
                sound = soundInputController.readBuffer();
                byte[] encoded = opusEncoder.encode(sound, 0);
                processBuffer.put(encoded);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

}
