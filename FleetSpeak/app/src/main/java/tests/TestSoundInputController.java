package tests;

import android.test.AndroidTestCase;
import android.util.Log;

import se.chalmers.fleetspeak.sound.SoundInputController;

/**
 * Created by Fridgeridge on 2015-07-02.
 */
public class TestSoundInputController extends AndroidTestCase {

    private SoundInputController soundInputController;

    public void setUp() throws Exception {
        this.soundInputController = new SoundInputController();
        super.setUp();
    }

    public void testConstructorNotNull(){
        Log.d("TestSoundInput","Starting testConstructorNotNull");
        assertNotNull("Testing constructor",soundInputController);
    }

    public void testReadBuffer(){
        Log.d("TestSoundInput","Starting testReadBuffer");
        int runs = 500;
        long sum = 0;
        Log.d("TestSoundInput","runs = " + runs);

        for(int i = 0; i < runs; i++){
            long start =System.nanoTime();
            soundInputController.readBuffer();
            long stop = System.nanoTime();
            Log.d("TestSoundInput","Time taken :"+(stop-start));
            sum += stop-start;
        }
        Log.d("TestSoundInput","Average time for testReadBuffer is: "+ (sum/runs)+" nanoseconds");
    }

    public void testFillAudioBuffer(){
        Log.d("TestSoundInput","Starting testFillAudioBuffer");
        int runs = 500;
        long sum = 0;
        Log.d("TestSoundInput","runs = " + runs);

        for(int i = 0; i < runs; i++) {
            long start =System.nanoTime();
            soundInputController.fillAudioBuffer();
            long stop = System.nanoTime();
            Log.d("TestSoundInput","Time taken :"+(stop-start));
            sum += stop-start;
        }

        Log.d("TestSoundInput","Average time for fillAudioBuffer is: "+ (sum/runs)+" nanoseconds");
    }


    public void tearDown() throws Exception {
        super.tearDown();
    }


}
