package se.chalmers.fleetspeak.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * 
 * Created by Fridgeridge on 2015-06-29.
 */
public enum SoundConstants {
    AUDIO_ENCODING(AudioFormat.ENCODING_PCM_16BIT),
    INPUT_CHANNEL_CONFIG(AudioFormat.CHANNEL_IN_MONO),
    INPUT_SOURCE(MediaRecorder.AudioSource.MIC),
    SAMPLING_RATE(44100),
    OUTPUT_CHANNEL_CONFIG(AudioFormat.CHANNEL_OUT_MONO),
    SESSION_ID(AudioTrack.MODE_STREAM),
    STREAM_TYPE(AudioManager.STREAM_VOICE_CALL),
    AUDIO_BUFFER_SIZE(AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) ),
    BYTEBUFFER_SIZE(AUDIO_BUFFER_SIZE.value()*4),
    INPUT_ARRAY_SIZE(2000);

    private int value;

    private SoundConstants(int i){
        this.value = i;
    }

    public int value(){
        return value;
    }

}
