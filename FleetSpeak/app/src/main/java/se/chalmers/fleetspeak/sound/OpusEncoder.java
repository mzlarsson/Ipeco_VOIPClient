package se.chalmers.fleetspeak.sound;

/**
 * Created by Fridgeridge on 2015-06-30.
 */
public class OpusEncoder {

public native int getSize(int i);


public native short encode(float f);

public native void destroy();




}
