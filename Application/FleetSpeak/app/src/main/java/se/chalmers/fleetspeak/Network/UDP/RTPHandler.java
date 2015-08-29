package se.chalmers.fleetspeak.Network.UDP;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.sound.AudioInputProcessor;

/**
 * Created by Nieo on 25/08/15.
 */
public class RTPHandler implements Runnable, PacketReceiver{

    private final Executor executor;
    private boolean isRunning;

    private short sequenceNumber;
    private UDPConnector udpConnector;
    private AudioInputProcessor audioInputProcessor;

    private JitterBuffer buffer;

    public RTPHandler(UDPConnector udpConnector) {
        this.udpConnector = udpConnector;
        sequenceNumber = 0;
        try {
            this.audioInputProcessor = new AudioInputProcessor();
        } catch (FleetspeakAudioException e) {
            Log.e("Audio", "got a fucking audioException");
            e.printStackTrace();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);

        buffer = new JitterBuffer(60);
        udpConnector.setReceiver(this);
    }


    @Override
    public void run() {
        Log.i("RTPHandler", "send audio thread is running");
        isRunning = true;
        byte[] data;
        while (isRunning){
            try {
                data = audioInputProcessor.readBuffer();
                udpConnector.sendPacket(new RTPPacket(sequenceNumber++,System.currentTimeMillis(),data).toByteArraySimple());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }

    public void terminate() {
        isRunning = false;
        udpConnector.terminate();
        audioInputProcessor.terminate();
    }

    @Override
    public void handlePacket(byte[] bytes) {
        buffer.write(new RTPPacket(bytes));
    }

    public BufferedAudioStream getAudioStream(){
        return buffer;
    }
}
