package se.chalmers.fleetspeak.audio.sound;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.Network.UDP.BufferedAudioStream;
import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusDecoder;

/**
 * Created by Fridgeridge on 2015-08-29.
 */
public class AudioOutputProcessor implements Runnable {

    private final Executor executor;
    private OpusDecoder opusDecoder;
    private AudioInputProcessor aip;
    private boolean isProcessing;
    private LinkedBlockingQueue<byte[]> outputBuffer;
    private BufferedAudioStream outputStream;


    public AudioOutputProcessor(AudioInputProcessor aip) {
        this.aip = aip;
        outputBuffer = new LinkedBlockingQueue(10);//FIXME Constants yada-yada
        opusDecoder = new OpusDecoder();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    public byte[] readBuffer() throws InterruptedException {
        return outputBuffer.take();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("AudioOutputProcessorThread");
        Log.i("AOP", "staring processing audio " + Thread.currentThread().getName());
        isProcessing = true;
        byte[] encoded;
        while (isProcessing) {
            try {
                encoded = aip.readBuffer();
                if(encoded != null) {
                    outputBuffer.put(encoded);
//                    outputBuffer.put(opusDecoder.decode(encoded, 0));
                }else{
                    //TODO cant process nulls takes way to much proccessing power
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    public void terminate() {
        isProcessing = false;
    }

}
