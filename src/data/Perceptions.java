package data;

public class Perceptions {
    public int pisActual; // The current floor in which the lift is located
    public boolean[] volAnarAlPis = new boolean[Global.NUM_PISOS]; // Whether someone inside the lift wants to go to the indexed floor.
    public boolean[] volPujarA = new boolean[Global.NUM_PISOS]; // Whether in the indexed floor there is a person who wants to go up
    public boolean[] volBaixarA = new boolean[Global.NUM_PISOS]; // Whether in the indexed floor there is a person who wants to go down
    public boolean portaTancada; // Whether the lift door is closed.

    public boolean estaAturat;
    
    public Perceptions(){
        pisActual = 0;
        for (int i = 0; i < Global.NUM_PISOS; i++) {
            volAnarAlPis[i] = false;
            volPujarA[i] = false;
            volBaixarA[i] = false;
        }
        portaTancada = true;
        estaAturat = false;
    }
}
