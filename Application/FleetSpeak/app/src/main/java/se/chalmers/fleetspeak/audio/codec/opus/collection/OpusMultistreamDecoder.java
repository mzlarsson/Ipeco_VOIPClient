package se.chalmers.fleetspeak.audio.codec.opus.collection;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.opus.OpusConstants;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusMultistreamDecoderWrapper;

/**
 * Created by Fridgeridge on 2015-08-08.
 */
public class OpusMultistreamDecoder {

    private long opusMSDecoder;
    private int channels;
    private byte[] mapping;

    public OpusMultistreamDecoder(int channels, byte[] mapping){
        try {
            create(channels, mapping);
        } catch (FleetspeakAudioException e) {
            e.printStackTrace();
        }
    }

    private void create(int channels, byte[] mapping ) throws FleetspeakAudioException {
        opusMSDecoder = OpusMultistreamDecoderWrapper.create(OpusConstants.SAMPLE_RATE.value(), channels, channels, 0, mapping);
        if(opusMSDecoder == 0){
            throw new FleetspeakAudioException("MSDecoder failed to initialize with :"+opusMSDecoder);
        }
    }

    public byte[] decode(byte[] opusEncoded){
        //int read = OpusMultistreamDecoderWrapper.decode(opusMSDecoder,
        //        opusEncoded,0,opusEncoded.length)
        return null;
    }

}
