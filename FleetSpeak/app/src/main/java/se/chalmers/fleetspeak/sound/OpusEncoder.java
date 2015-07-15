package se.chalmers.fleetspeak.sound;

/**
 * Created by Fridgeridge on 2015-06-30.
 */
public class OpusEncoder {

    private int opus;

    static{
        System.loadLibrary("libOpus");
    }


    public OpusEncoder(){

    }

    public native static int opus_encoder_create();

    public native static void opus_encoder_destroy();

    public native int getSize(int i);

    public native short encode(float f);

    public native void destroy();




}
