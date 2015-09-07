package se.chalmers.fleetspeak.audio.sound;

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
    LOW() {
        @Override
        public int getSampleRate(){
            return 8000;
        }

        @Override
        public double getFrameSizeMS(){
            return 20;
        }
    },

    MID() {
        @Override
        public int getSampleRate(){
            return 16000;
        }

        @Override
        public double getFrameSizeMS(){
            return 20;
        }
    },

    HIGH() {
        @Override
        public int getSampleRate(){
            return 24000;
        }

        @Override
        public double getFrameSizeMS(){
            return 20;
        }
    },

    CD() {
        @Override
        public int getSampleRate(){
            return 48000;
        }

        @Override
        public double getFrameSizeMS(){
            return 20;
        }
    };

    public static SoundConstants getCurrent() {
        return LOW;
    }

    public int getEncoding() {
        return AudioFormat.ENCODING_PCM_16BIT;
    }
    public int getInputChannelConfig() {
        return AudioFormat.CHANNEL_IN_MONO;
    }
    public int getOutputChannelConfig(){
        return AudioFormat.CHANNEL_OUT_MONO;
    }
    public int getInputSource() {
        return MediaRecorder.AudioSource.MIC;
    }
    public int getSampleRate(){
        return 16000;
    }
    public int getChannels(){
        return 1;
    }
    public int getSessionID(){
        return AudioTrack.MODE_STREAM;
    }
    public int getStreamType(){
        return AudioManager.STREAM_VOICE_CALL;
    }
    public int getInputBufferSize(){
        return AudioRecord.getMinBufferSize(getSampleRate(), getInputChannelConfig(), getEncoding())*3;
    }
    public int getOutputBufferSize(){
        return AudioRecord.getMinBufferSize(getSampleRate(), getInputChannelConfig(), getEncoding())*3;
    }
    public double getFrameSizeMS(){
        return 20;
    }
    public int getFrameSize(){
        return (int)((getSampleRate()*getFrameSizeMS())/1000);
    }
    public int getPCMSize(){
        return getFrameSize()*getChannels()*2;
    }
}
