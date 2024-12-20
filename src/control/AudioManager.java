package control;

import data.Global;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioManager {

    private static final int FRAME_POSITION_BG_MUSIC_START = 3;
    public static final float MAX_SLIDER_VALUE = 50f;
    private static final int FRAME_POSITION_OPEN_DOOR_START = 70000;
    private static Clip bgAudio, openDoorAudio;
    private static BooleanControl bgAudioMuted, openDoorMuted;

    private static FloatControl bgAudioVolume, openDoorVolume;

    public AudioManager(Global data) {
        try {
            bgAudio = AudioSystem.getClip();
            openDoorAudio = AudioSystem.getClip();
            bgAudio.open(data.getBgAudioStream());
            openDoorAudio.open(data.getOpenDoorAudioStream());
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bgAudio.setLoopPoints(3, bgAudio.getFrameLength() - 1);
        bgAudio.loop(Clip.LOOP_CONTINUOUSLY);
        bgAudio.start();
        bgAudioMuted = (BooleanControl) bgAudio.getControl(BooleanControl.Type.MUTE);
        openDoorMuted = (BooleanControl) openDoorAudio.getControl(BooleanControl.Type.MUTE);
        bgAudioVolume = (FloatControl) bgAudio.getControl(FloatControl.Type.MASTER_GAIN);
        openDoorVolume = (FloatControl) openDoorAudio.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public void setAudioVolume(int sliderValue) {
        final float newVolume = (bgAudioVolume.getMaximum()-bgAudioVolume.getMinimum() )*(sliderValue/ MAX_SLIDER_VALUE) + bgAudioVolume.getMinimum();
        bgAudioVolume.setValue(newVolume);
        openDoorVolume.setValue(newVolume);
    }

    public void toggleAudio() {
        final boolean mute = !bgAudioMuted.getValue();
        if (mute) {
            bgAudio.stop();
            openDoorAudio.stop();
        } else {
            bgAudio.setFramePosition(FRAME_POSITION_BG_MUSIC_START);
            bgAudio.start();
        }
        bgAudioMuted.setValue(mute);
        openDoorMuted.setValue(mute);
    }

    public void playDoorOpened() {
        if (openDoorAudio.isRunning())
            openDoorAudio.stop();
        openDoorAudio.setFramePosition(FRAME_POSITION_OPEN_DOOR_START);
        openDoorAudio.start();
    }

    public int getAudioVolume() {
        final int volume =  Math.round((bgAudioVolume.getValue() - bgAudioVolume.getMinimum() )/((bgAudioVolume.getMaximum()-bgAudioVolume.getMinimum() ))*MAX_SLIDER_VALUE);
        return volume;
    }
}
