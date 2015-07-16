package se.chalmers.fleetspeak.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

/**
 *
 * Created by Fridgeridge on 2015-06-29.
 */
public enum SoundConstants {
    AUDIO_ENCODING(AudioFormat.ENCODING_PCM_16BIT),
    INPUT_CHANNEL_CONFIG(AudioFormat.CHANNEL_IN_MONO),
    INPUT_SOURCE(MediaRecorder.AudioSource.MIC),
    SAMPLING_RATE(16000), //Recommended sample rate for VoIP
    OUTPUT_CHANNEL_CONFIG(AudioFormat.CHANNEL_OUT_MONO),
    SESSION_ID(AudioTrack.MODE_STREAM),
    STREAM_TYPE(AudioManager.STREAM_VOICE_CALL),
    AUDIO_IN_BUFFER_SIZE(AudioRecord.getMinBufferSize(SAMPLING_RATE.value(), AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING.value())*10 ),
    AUDIO_OUT_BUFFER_SIZE(AudioRecord.getMinBufferSize(SAMPLING_RATE.value(), AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING.value())*10 ),
    BYTEBUFFER_IN_SIZE(AUDIO_IN_BUFFER_SIZE.value()),
    BYTEBUFFER_OUT_SIZE(AUDIO_OUT_BUFFER_SIZE.value()),
    INPUT_ARRAY_SIZE(5000);//Higher value (To a limit...) degrades sound quality but eases on the performance.

    private int value;

    private SoundConstants(int i){
        this.value = i;
    }

    public int value(){
        return value;
    }

    public static void printValues(){
        SoundConstants[] all = SoundConstants.class.getEnumConstants();
        for(SoundConstants s: all){
            Log.d("SoundConstants",s.name()+": " + s.value);
        }


    }

}
