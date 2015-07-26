package se.chalmers.fleetspeak.audio.codec.opus;

import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 * Created by Fridgeridge on 2015-07-25.
 */
public enum OpusConstants {
    SAMPLE_RATE(SoundConstants.SAMPLE_RATE.value()),
    MAX_ENCODING_PAYLOAD(1500), //TODO Find more fitting values
    FRAME_SIZE(1000), //TODO Find more fitting values
    CHANNELS(1);

    private final int value;

    private OpusConstants(int i){
        this.value = i;
    }

    public int value(){
        return value;
    }

}
