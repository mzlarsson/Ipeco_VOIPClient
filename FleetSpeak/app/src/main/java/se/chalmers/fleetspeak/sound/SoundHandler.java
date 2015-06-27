package se.chalmers.fleetspeak.sound;

/**
 * Made for testing purposes only
 * Created by Fridgeridge on 2015-06-22.
 */
public class SoundHandler implements Runnable {

    private SoundPlaybackController soundPlaybackController;
    private SoundRecordController soundRecordController;
    Thread playThread, recThread;

    private boolean soundIsRunning;

    public SoundHandler(){
        soundPlaybackController = new SoundPlaybackController();
        soundRecordController = new SoundRecordController();
        recThread = new Thread(soundRecordController);
        playThread = new Thread(soundPlaybackController);
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
        soundPlaybackController.fillAudioBuffer(soundRecordController.getByteBuffer());
    }

    public void killControllers(){
        soundPlaybackController.kill();
        soundRecordController.kill();
    }

    public void kill(){
        killControllers();
        soundIsRunning = false;
    }

    @Override
    public void run() {
        while(!soundIsRunning) {
            //transferAudio();
        }
        }
}
