package se.chalmers.fleetspeak.audio.codec.opus.collection;

import android.util.Log;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.EncoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusEncoderWrapper;
import se.chalmers.fleetspeak.audio.sound.SoundConstants;

//Static import


/**
 * A class for encoding PCMU data to the opus codec
 * Created by Fridgeridge on 2015-07-20.
 */
public class OpusEncoder implements EncoderInterface {

    private long opusEncoder;

    private SoundConstants s;
    public OpusEncoder(){
        s = SoundConstants.getCurrent();
        try {
            create(s.getSampleRate(),
                    s.getChannels());
        } catch (FleetspeakAudioException e) {
            e.printStackTrace();
        }
    }

    private void create(int sampleRate, int channels) throws FleetspeakAudioException {
        opusEncoder = OpusEncoderWrapper.create(sampleRate,channels);
        if(opusEncoder == 0){//TODO Not sure if pointer can assume this value while casting in the c wrapper code
            throw new FleetspeakAudioException("Encoder failed to initialize with :"+opusEncoder);
        }
    }

    public byte[] encode(byte[] pcmInData, int offset){
        byte[] opusEncoded = new byte[1000];//FIXME Subsitute with a constant value threshold
        byte[] tmp = new byte[0];
        int read = encode(pcmInData,offset,opusEncoded,0);
        if(read <= 0){
            Log.d("OPUS", "Failed to encode with :"+read);
        }else{
                tmp =  new byte[read];
            System.arraycopy(opusEncoded, 0, tmp, 0, read);
        }
        return tmp;
    }

    private int encode(byte[] pcmInData, int inOffset, byte[] opusEncoded, int outOffset){
        return OpusEncoderWrapper.encode(this.opusEncoder, pcmInData, inOffset, s.getFrameSize(), opusEncoded, outOffset, opusEncoded.length);
    }


    public void destroy(){
        OpusEncoderWrapper.destroy(this.opusEncoder);
        opusEncoder = 0;
    }



}
