package se.chalmers.fleetspeak.audio.sound;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.Network.UDP.BufferedAudioStream;
import se.chalmers.fleetspeak.Network.UDP.RTPHandler;
import se.chalmers.fleetspeak.audio.codec.DecoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusDecoder;

/**
 * Created by Fridgeridge on 2015-08-29.
 */
public class AudioOutputProcessor implements Runnable {

    private final Executor executor;
    private OpusDecoder opusDecoder;
    private RTPHandler rtpHandler;
    private volatile boolean isProcessing;
    private LinkedBlockingQueue<byte[]> outputBuffer;
    private BufferedAudioStream outputStream;


    public AudioOutputProcessor(RTPHandler rtpHandler) {
        this.rtpHandler = rtpHandler;
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
        outputStream = rtpHandler.getAudioStream();
        byte[] encoded;
        while (isProcessing) {
            encoded = outputStream.read();
            try {
                if(encoded != null) {
                    outputBuffer.put(opusDecoder.decode(encoded,0));
                }else{
                    Thread.sleep(19);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    public void terminate() {
        isProcessing = false;

        if(outputBuffer != null) {
            outputBuffer.clear();
            outputBuffer = null;
        }
        if(rtpHandler != null) {
            rtpHandler.terminate();
            rtpHandler = null;
        }

        if(opusDecoder!=null) {
            opusDecoder.destroy();
            opusDecoder = null;
        }


    }

}
