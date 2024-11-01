package practica1;

import control.AudioManager;
import control.Lift;
import data.Global;
import data.Perceptions;
import view.View;

/*
 *
 * @author Marc Roman, Sergio Vega
 */
public class Practica1 {
    public static Global data;
    public static View view;
    private static Perceptions perc;
    private static Lift lift;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        data = new Global();
        perc = new Perceptions();
        lift = new Lift(perc,data);


        view = new View(perc, lift, data, new AudioManager(data));
        lift.linkView(view);
        view.setVisible(true);
        view.setResizable(true);
        
        Thread t = new Thread(lift);
        t.start();
        
        t.join();
    }
}
