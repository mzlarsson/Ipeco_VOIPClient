package se.chalmers.fleetspeak.audio.sound;

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
    SAMPLE_RATE(16000), //Recommended sample rate for VoIP TODO Implement support for more sample rates.
    OUTPUT_CHANNEL_CONFIG(AudioFormat.CHANNEL_OUT_MONO),
    SESSION_ID(AudioTrack.MODE_STREAM),
    STREAM_TYPE(AudioManager.STREAM_VOICE_CALL),
    AUDIO_IN_BUFFER_SIZE(AudioRecord.getMinBufferSize(SAMPLE_RATE.value(), AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING.value())*3),
    AUDIO_OUT_BUFFER_SIZE(AudioRecord.getMinBufferSize(SAMPLE_RATE.value(), AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING.value())*3),
    BYTEBUFFER_IN_SIZE(AUDIO_IN_BUFFER_SIZE.value()),
    BYTEBUFFER_OUT_SIZE(AUDIO_OUT_BUFFER_SIZE.value()),
    FRAME_SIZE_MS(60),
    INPUT_FRAME_SIZE((AudioRecord.getMinBufferSize(SAMPLE_RATE.value(),INPUT_CHANNEL_CONFIG.value(),AUDIO_ENCODING.value()))*2);//Higher value (To a limit...) degrades sound quality and latency but eases on the performance.

    private final int value;

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
