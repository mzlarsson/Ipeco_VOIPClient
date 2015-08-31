package se.chalmers.fleetspeak.Network.UDP;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.sound.AudioInputProcessor;

/**
 * Created by Nieo on 25/08/15.
 */
public class RTPHandler implements Runnable, PacketReceiver, BufferedAudioStream{

    private final Executor executor;
    private boolean isRunning;

    private short sequenceNumber;
    private UDPConnector udpConnector;
    private AudioInputProcessor audioInputProcessor;

    private JitterBuffer buffer;

    public RTPHandler(UDPConnector udpConnector) {
        buffer = new JitterBuffer(120);
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

        udpConnector.setReceiver(this);
    }


    @Override
    public void run() {
        Thread.currentThread().setName("RTPHandlerThread");
        Log.i("RTPHandler", "send audio thread is running " + Thread.currentThread().getName());
        isRunning = true;
        byte[] data;
        while (isRunning){
            try {
                data = audioInputProcessor.readBuffer();
                udpConnector.sendPacket(new RTPPacket(sequenceNumber++,System.currentTimeMillis(),data).toByteArraySimple());
                Log.d("RTPHandler", +data.length + " " + (sequenceNumber-1) +  " send " + printPacket(data));
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
        RTPPacket packet = new RTPPacket(bytes);
        buffer.write(packet);
        Log.d("RTPHandler",packet.getPayload().length + " " + packet.seqNumber +  " recv " + printPacket(packet.getPayload()));
    }

    public BufferedAudioStream getAudioStream(){
        return this;
    }

    String printPacket(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length;i++){
            sb.append(" " + bytes[i]);
        }
        return sb.toString();
    }

    /**
     * Reads the next available byte-array of audio data from the stream,
     * if none is available it returns null instead.
     *
     * @return A byte-array of audio data if available, null if not.
     */
    @Override
    public byte[] read() {
        return buffer.read().getPayload();
    }
}
