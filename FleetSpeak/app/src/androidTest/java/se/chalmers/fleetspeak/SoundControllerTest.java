package se.chalmers.fleetspeak;

import android.content.Context;
import android.media.AudioManager;
import android.test.AndroidTestCase;

import java.net.DatagramSocket;
import java.net.SocketException;

import se.chalmers.fleetspeak.sound.SoundController;

/**
 * Created by Nieo on 09/03/15.
 */
public class SoundControllerTest extends AndroidTestCase {



    public SoundControllerTest(){
        super();
    }

    SoundController soundController;
    Context context;
    AudioManager audioManager;
    DatagramSocket datagramSocket;
    DatagramSocket senderSocket;

    protected void setUp(){
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context = getContext();
        try {
            datagramSocket = new DatagramSocket(8989);
            senderSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        soundController = new SoundController(context, "127.0.0.1", 8989);
    }


    public void testInit(){

        assertNotNull(soundController);
        assertEquals(AudioManager.MODE_IN_COMMUNICATION, audioManager.getMode());
        int port = soundController.addStream(2);
        assertNotNull(port);

        soundController.removeUserFromDownStream(2);

    }


    protected void tearDown(){
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        soundController.close();
    }

}
