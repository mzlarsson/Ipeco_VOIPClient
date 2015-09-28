package se.chalmers.fleetspeak.audio.codec.opus.collection;

import android.util.Log;

import se.chalmers.fleetspeak.audio.FleetspeakAudioException;
import se.chalmers.fleetspeak.audio.codec.DecoderInterface;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusDecoderWrapper;
import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 *
 * Created by Fridgeridge on 2015-07-25.
 */
public class OpusDecoder implements DecoderInterface {

    private long opusDecoder;
    private SoundConstants s;
    public OpusDecoder(){
        s = SoundConstants.getCurrent();
        try{
            create(s.getSampleRate(),
                    s.getChannels());
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
        byte[] pcmDecoded = new byte[s.getPCMSize()];
        int frameSize = s.getFrameSize();
        int read = OpusDecoderWrapper.decode(this.opusDecoder, opusEncoded, offset, opusEncoded.length,
                pcmDecoded, 0,frameSize , 0);
        if(read <= 0){
            Log.d("OPUS", "Failed to encode with :" + read);
        }
        return pcmDecoded;
    }

    public void destroy(){
        OpusDecoderWrapper.destroy(opusDecoder);
        opusDecoder = 0;
    }


}
