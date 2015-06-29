package se.chalmers.fleetspeak.sound;

/**
 * Made for testing purposes only
 * Created by Fridgeridge on 2015-06-22.
 */
public class SoundHandler implements Runnable {




    private SoundOutputController soundOutputController;
    private SoundInputController soundInputController;
    Thread playThread, recThread;

    private boolean soundIsRunning;

    public SoundHandler(){
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
        soundOutputController.kill();
        soundInputController.kill();
    }

    public void kill(){
        killControllers();
        soundIsRunning = false;
    }

    @Override
    public void run() {
        while(soundIsRunning) {
            transferAudio();
        }
        }
}
