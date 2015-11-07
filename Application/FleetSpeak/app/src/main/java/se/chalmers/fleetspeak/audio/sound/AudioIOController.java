package se.chalmers.fleetspeak.audio.sound;

import java.util.concurrent.Executor;

import se.chalmers.fleetspeak.network.UDP.RTPHandler;
import se.chalmers.fleetspeak.audio.FleetspeakAudioException;

/**
 * Created by Fridgeridge on 2015-09-11.
 */
public class AudioIOController implements Runnable {

    private Executor executor;
    private boolean ioAlive;



    private SoundInputController soundInputController;
    private SoundOutputController  soundOutputController;

    private SoundConstants s;

    public AudioIOController(RTPHandler rtpHandler){
        s = SoundConstants.getCurrent();
        try {
            soundInputController = new SoundInputController();
            soundOutputController = new SoundOutputController(rtpHandler);
        } catch (FleetspeakAudioException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the echobuffer which keeps the output data from the SoundOutputController
     * @return The requested ByteBuffer
     */
//    private ByteBuffer requestEchoBuffer(){

//    }


    @Override
    public void run() {


        while(ioAlive){

         //move data from


        }
    }
}
