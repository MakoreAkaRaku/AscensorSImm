package data;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Global {
    public final static String RESOURCEAUDIOPATH = "resources/audio/";
    public final static String RESOURCEAIMGPATH = "resources/img/";
    public final String AUDIO_RES_NAME[] = {"elevator music.wav", "opening door.wav"};
    public final String IMG_RES_NAME[] = {"open.png", "closed.png"};
    private static ImageIcon[] elevatorImages;
    private static AudioInputStream bgMusic, openDoorSnd;
    public static final int NUM_FLOORS = 4;
    public static final int DELTA_TIME = 1_000;
    public static final boolean DEBUG = false;

    public Global() {
        elevatorImages = new ImageIcon[IMG_RES_NAME.length];
        File f = new File(RESOURCEAUDIOPATH + AUDIO_RES_NAME[0]);
        try {
            bgMusic = AudioSystem.getAudioInputStream(f);
            f = new File(RESOURCEAUDIOPATH + AUDIO_RES_NAME[1]);
            openDoorSnd = AudioSystem.getAudioInputStream(f);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < IMG_RES_NAME.length; i++) {
            Image img = Toolkit.getDefaultToolkit().createImage(RESOURCEAIMGPATH + IMG_RES_NAME[i]);
            img  = img.getScaledInstance(100,-1,Image.SCALE_FAST);
            //Rendimiento Entorno Actuadores y Sensores taller 2 el robot sense pasadissos.
            elevatorImages[i] = new ImageIcon(img);
        }
    }

    public AudioInputStream getBgAudioStream()
    {
        return bgMusic;
    }

    public AudioInputStream getOpenDoorAudioStream(){
        return openDoorSnd;
    }

    public ImageIcon getElevatorImgIcon(int idx)
    {
        if (idx >= elevatorImages.length)
            return null;
        return elevatorImages[idx];
    }
};
