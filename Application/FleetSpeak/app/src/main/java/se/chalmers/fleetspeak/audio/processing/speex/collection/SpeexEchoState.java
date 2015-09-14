package se.chalmers.fleetspeak.audio.processing.speex.collection;

import se.chalmers.fleetspeak.audio.processing.speex.jnispeex.SpeexEchoStateWrapper;
import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 * Created by Fridgeridge on 2015-09-08.
 */
public class SpeexEchoState {

    private long echoState;

    private SoundConstants s;

    public SpeexEchoState(int frameSize, int filterLength){
        s = SoundConstants.getCurrent();
        this.echoState = SpeexEchoStateWrapper.init(frameSize, filterLength);
    }

    public byte[] echoCancellation(byte[] rec, byte[] play ){
        byte[] out = new byte[s.getPCMSize()];
        SpeexEchoStateWrapper.cancellation(echoState,rec,play,out);
        return out;
    }





}
