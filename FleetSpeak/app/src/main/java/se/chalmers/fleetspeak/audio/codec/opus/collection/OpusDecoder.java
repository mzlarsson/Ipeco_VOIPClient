package se.chalmers.fleetspeak.audio.codec.opus.collection;

import android.util.Log;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.DecoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.OpusConstants;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusDecoderWrapper;
import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 *
 * Created by Fridgeridge on 2015-07-25.
 */
public class OpusDecoder implements DecoderInterface {
    private long opusDecoder;


    public OpusDecoder(){
        try{
            create(OpusConstants.SAMPLE_RATE.value(),
                    OpusConstants.CHANNELS.value());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void create(int sampleRate, int channels) throws Exception {
        opusDecoder = OpusDecoderWrapper.create(sampleRate,channels);
        if(opusDecoder == 0){//TODO Not sure if pointer can assume this value while casting in the c wrapper code
            throw new FleetspeakAudioException("Decoder failed to initialize with :"+opusDecoder);
        }
    }

    public byte[] decode(byte[] opusEncoded, int offset){
        byte[] pcmDecoded = new byte[SoundConstants.INPUT_FRAME_SIZE.value()];
        int frameSize = OpusConstants.FRAME_SIZE.value();
        int read = OpusDecoderWrapper.decode(this.opusDecoder, opusEncoded, offset, opusEncoded.length,
                pcmDecoded, 0,frameSize , 0);

        if(read <= 0){
            Log.d("OPUS", "Failed to encode with :" + read);
        }else{
            System.arraycopy(pcmDecoded, 0, pcmDecoded, 0, read);
        }
        return pcmDecoded;
    }

    public void destroy(){
        OpusDecoderWrapper.destroy(opusDecoder);
        opusDecoder = 0;
    }


}
