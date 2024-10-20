package data;

public class Perceptions {
    public int currentFloor; // The current floor in which the lift is located
    public boolean[] wantsToGoToFloor = new boolean[Global.NUM_FLOORS]; // Whether someone inside the lift wants to go to the indexed floor.
    public boolean[] wantToGoUpIn = new boolean[Global.NUM_FLOORS]; // Whether in the indexed floor there is a person who wants to go up
    public boolean[] wantToGoDownIn = new boolean[Global.NUM_FLOORS]; // Whether in the indexed floor there is a person who wants to go down
    public boolean isClosed; // Whether the lift door is closed.

    public boolean isStopped;
    
    public Perceptions(){
        currentFloor = 0;
        for (int i = 0; i < Global.NUM_FLOORS; i++) {
            wantsToGoToFloor[i] = false;
            wantToGoUpIn[i] = false;
            wantToGoDownIn[i] = false;
        }
        isClosed = true;
        isStopped = false;
    }
}
