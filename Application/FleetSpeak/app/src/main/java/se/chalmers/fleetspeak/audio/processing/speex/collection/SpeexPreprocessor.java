package se.chalmers.fleetspeak.audio.processing.speex.collection;

import se.chalmers.fleetspeak.audio.processing.speex.jnispeex.SpeexPreprocessorWrapper;

/**
 * Created by Fridgeridge on 2015-09-08.
 */
public class SpeexPreprocessor {

    private long preprocessorState;

    public SpeexPreprocessor(int frameSize, int sampleRate){
        preprocessorState = SpeexPreprocessorWrapper.init(frameSize, sampleRate);
    }

    public void run(byte[] src){
        SpeexPreprocessorWrapper.run(preprocessorState,src);
    }




    
}
