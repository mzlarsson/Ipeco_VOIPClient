package se.chalmers.fleetspeak.audio.codec.opus.collection;

import android.util.Log;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.EncoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.OpusConstants;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusEncoderWrapper;

//Static import

import static se.chalmers.fleetspeak.audio.sound.SoundConstants.*;

/**
 * A class for encoding PCMU data to the opus codec
 * Created by Fridgeridge on 2015-07-20.
 */
public class OpusEncoder implements EncoderInterface {

    private long opusEncoder;

    public OpusEncoder(){
        try {
            create(OpusConstants.SAMPLE_RATE.value(),
                    OpusConstants.CHANNELS.value());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create(int sampleRate, int channels) throws Exception {
        opusEncoder = OpusEncoderWrapper.create(sampleRate,channels);
        if(opusEncoder == 0){//TODO Not sure if pointer can assume this value while casting in the c wrapper code
            throw new FleetspeakAudioException("Encoder failed to initialize with :"+opusEncoder);
        }
    }

    public byte[] encode(byte[] pcmInData, int offset){
        byte[] opusEncoded = new byte[ 3*1276];

        int read = OpusEncoderWrapper.encode(this.opusEncoder, pcmInData, 0, 1000, opusEncoded, 0, 3*1276);

        if(read <= 0){
            Log.d("OPUS", "Failed to encode with :"+read);
        }

        return opusEncoded;
    }

    public void destroy(){
        OpusEncoderWrapper.destroy(this.opusEncoder);
        opusEncoder = 0;
    }



}
