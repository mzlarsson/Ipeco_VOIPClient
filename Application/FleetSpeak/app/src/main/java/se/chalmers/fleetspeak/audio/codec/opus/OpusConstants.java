package se.chalmers.fleetspeak.audio.codec.opus;

import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 * Created by Fridgeridge on 2015-07-25.
 */
public enum OpusConstants {
    SAMPLE_RATE(SoundConstants.SAMPLE_RATE.value()),
    MAX_ENCODING_PAYLOAD(1500), //TODO Find more fitting values
    CHANNELS(1),
    FRAME_SIZE_MS(SoundConstants.FRAME_SIZE_MS.value()),
    FRAME_SIZE(SAMPLE_RATE.value()*2*FRAME_SIZE_MS.value()/1000); //TODO Find more fitting values

    private final int value;

    private OpusConstants(int i){
        this.value = i;
    }

    public int value(){
        return value;
    }

}
