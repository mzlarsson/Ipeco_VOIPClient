package se.chalmers.fleetspeak.audio;

/**
 * A class for exceptions caused by the audio side.
 * Created by Fridgeridge on 2015-07-25.
 */
public class FleetspeakAudioException extends Exception {

    public FleetspeakAudioException(String s){
        super(s);
    }

    public FleetspeakAudioException(String s , Throwable t){
        super(s,t);
    }

}
