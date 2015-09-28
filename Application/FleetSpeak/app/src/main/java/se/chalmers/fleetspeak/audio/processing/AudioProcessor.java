package se.chalmers.fleetspeak.audio.processing;

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

        speexEchoState = NativeAudioProcessor.createSpeexEchoState(s.getFrameSize(), s.getFrameSize() * 10);

        speexProcessorState = NativeAudioProcessor.createSpeexProcessor(s.getFrameSize(), s.getSampleRate());

    }


    public byte[] process(byte[] input){
        byte[] processed = new byte[1500];


        return processed;
    }




}
