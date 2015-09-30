package se.chalmers.fleetspeak.audio.processing;

import android.util.Log;

import se.chalmers.fleetspeak.audio.sound.SoundConstants;

/**
 * Created by Fridgeridge on 2015-09-10.
 */
public class AudioProcessor {




    private long opusEncoder, speexEchoState, speexProcessorState;

    private SoundConstants s;

    public AudioProcessor(){
        s = SoundConstants.getCurrent();

        opusEncoder = NativeAudioProcessor.createOpusEncoder(s.getChannels(), s.getSampleRate());
        speexEchoState = NativeAudioProcessor.createSpeexEchoState(s.getFrameSize(), s.getFrameSize()*10);
        speexProcessorState = NativeAudioProcessor.createSpeexProcessor(s.getFrameSize(), s.getSampleRate());

        //NativeAudioProcessor.setup(opusEncoder,speexEchoState,speexProcessorState);

    }


    public byte[] process(byte[] input, int inputOffset, byte[] play,int playOffset){
        byte[] processed = new byte[1000];
        byte[] tmp = new byte[0];

        int read = NativeAudioProcessor.processAll(
                s.getFrameSize(),
                input, inputOffset,
                play, playOffset,
                processed, processed.length
        );

        if( read <=0){
            Log.d("AudioProcess", "Failed to encode with :" + read);
        }else{
            tmp =  new byte[read];
            System.arraycopy(processed, 0, tmp, 0, read);
        }

        return tmp;
    }


    public void destroy(){
        NativeAudioProcessor.destroy(opusEncoder,speexEchoState,speexProcessorState);
        opusEncoder = 0;
        speexEchoState = 0;
        speexProcessorState = 0;
    }



}
