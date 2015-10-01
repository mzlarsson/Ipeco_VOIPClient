package se.chalmers.fleetspeak.Network.UDP;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.sound.AudioInputProcessor;
import se.chalmers.fleetspeak.audio.sound.AudioType;

/**
 * Created by Nieo on 25/08/15.
 */
public class RTPHandler implements Runnable, PacketReceiver, BufferedAudioStream{

    private int frameSizeMs = 20;

    private final Executor executor;
    private boolean isRunning;

    private short sequenceNumber;
    private long timestamp;
    private UDPConnector udpConnector;
    private AudioInputProcessor audioInputProcessor;

    private JitterBuffer buffer;
    private AudioType currAudioType = AudioType.OPUS_WB;    //TODO We probably want to be able to change this dynamically.

    public RTPHandler(UDPConnector udpConnector) {
        buffer = new JitterBuffer(100);
        this.udpConnector = udpConnector;
        udpConnector.setPacketSize(currAudioType.getMaxLength()+RTPPacket.HEADER_SIZE);
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
                udpConnector.sendPacket(new RTPPacket(currAudioType, sequenceNumber++, getNextTimestamp(), data).toByteArrayDetailed());
                //buffer.write(new RTPPacket(currAudioType, sequenceNumber++, getNextTimestamp(),data));
                //Log.d("RTPHandler", +data.length + " " + (sequenceNumber-1) +  " send " + data.length +" and time "+System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long getNextTimestamp() {
        long curr = System.currentTimeMillis();
        if(Math.abs(curr-timestamp)>(2*frameSizeMs)) {
            timestamp = curr;
        } else {
            timestamp += frameSizeMs;
        }
        return timestamp;
    }

    public void terminate() {
        isRunning = false;
        if(udpConnector != null)
            udpConnector.terminate();
        if(audioInputProcessor != null)
            audioInputProcessor.terminate();
    }

    @Override
    public void handlePacket(byte[] bytes) {
        RTPPacket packet = null;
        try {
            packet = new RTPPacket(bytes);
        } catch (IllegalArgumentException ex) {
            Log.w("rtp", "Read packet was not on a valid RTP form: " + ex.getMessage());
        }
        if(packet != null && packet.payloadType == currAudioType) {
            buffer.write(packet);
            //Log.d("RTPHandler", packet.getPayload().length + " " + packet.seqNumber + " recv " + printPacket(packet.getPayload()));
        }
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
        RTPPacket r = buffer.read();
        return r != null ? r.getPayload(): null;
    }
}
