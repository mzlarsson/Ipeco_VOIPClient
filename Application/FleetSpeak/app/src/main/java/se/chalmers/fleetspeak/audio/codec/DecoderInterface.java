package se.chalmers.fleetspeak.audio.codec;

/**
 * Created by Fridgeridge on 2015-07-20.
 */
public interface DecoderInterface {

    public byte[] decode(byte[] encoded, int offset);

}
