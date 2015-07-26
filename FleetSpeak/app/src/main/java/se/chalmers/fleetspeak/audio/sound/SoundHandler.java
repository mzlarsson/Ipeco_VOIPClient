package se.chalmers.fleetspeak.audio.sound;

import se.chalmers.fleetspeak.audio.codec.opus.collection.OpusEncoder;
import se.chalmers.fleetspeak.audio.codec.opus.jniopus.OpusEncoderWrapper;

/**
 * Made for testing purposes only
 * Created by Fridgeridge on 2015-06-22.
 */
public class SoundHandler implements Runnable {


    static{
        System.loadLibrary("Opus");
    }

    private SoundOutputController soundOutputController;
    private SoundInputController soundInputController;

    private OpusEncoder oe;

    Thread playThread, recThread;

    private boolean soundIsRunning;

    public SoundHandler(){
        SoundConstants.printValues();

        oe= new OpusEncoder();
        soundOutputController = new SoundOutputController();
        soundInputController = new SoundInputController();
        recThread = new Thread(soundInputController,"SoundInputController");
        playThread = new Thread(soundOutputController,"SoundRecordController");
        startControllers();
        soundIsRunning = true;
    }

    public void startControllers(){
        if(recThread != null || playThread !=null){
            recThread.start();
            playThread.start();
        }
    }

    public synchronized void transferAudio(){
        soundOutputController.fillAudioBuffer(soundInputController.readBuffer());
    }

    public void killControllers(){
        soundOutputController.destroy();
        soundInputController.destroy();
    }

    public void kill(){
        killControllers();
        soundIsRunning = false;
    }

    @Override
    public void run() {
        while(soundIsRunning) {
         //   transferAudio();
            byte[] ass = soundInputController.readBuffer();
            if(ass.length > 0 && ass[2]!=0) {
                byte[] bb = oe.encode(ass, 0);
                boolean a = ass.equals(bb);
            }
        }
        }
}
