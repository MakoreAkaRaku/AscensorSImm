package control;

import data.Global;
import data.Perceptions;
import view.View;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;


public class Lift implements Runnable {
    private Perceptions perc;
    private Clip bgAudio,openDoorAudio;
    private View view;

    private boolean goingUp;

    public Lift(Perceptions perc,Global data){
        this.perc = perc;
        goingUp = true;
    }

    public void act(){
        // 1 - Door is closed, someone wants to go to that floor
        if(perc.isClosed && perc.wantsToGoToFloor[perc.currentFloor]) {
            openDoor();
            return;
        }
        // 2 - Door is closed, someone wants to go up in the current floor
        if(perc.isClosed && goingUp && perc.wantToGoUpIn[perc.currentFloor]){
            openDoor();
            return;
        }
        // 3 - Door is closed, someone wants to go down in the current floor
        if(perc.isClosed && !goingUp && perc.wantToGoDownIn[perc.currentFloor]){
            openDoor();
            return;
        }

        // 4, 5 - Door is open
        if(!perc.isClosed){
            waitAndClose();
            return;
        }
        // From here we don't check if the door is closed because if it's open it will not get to this part of the code

        // Go up a floor
        if(goingUp){
            // 6 - Someone inside the elevator wants to go up
            if(!perc.wantsToGoToFloor[perc.currentFloor] && inWantsToGoUp()){
                goUp();
                return;
            }
            // No one wants to enter the elevator
            if(!perc.wantToGoDownIn[perc.currentFloor] && !perc.wantToGoUpIn[perc.currentFloor]){
                if(upWantsToGoUp()){
                    // 7
                    goUp();
                    return;
                } else if(downWantsToGoUp()){
                    // 8
                    goDown();
                    return;
                }
            }
            // Someone wants to enter the elevator to go down
            if(perc.wantToGoDownIn[perc.currentFloor]){
                // 9
                openDoor();
                return;
            }
        }

        // Go down a floor
        if(!goingUp){
            if(!perc.wantsToGoToFloor[perc.currentFloor] && inWantsToGoDown()){
                // 10
                goDown();
                return;
            }
            if(!perc.wantToGoDownIn[perc.currentFloor] && !perc.wantToGoUpIn[perc.currentFloor]){
                if(downWantsToGoDown()) {
                    // 11
                    goDown();
                    return;
                } else if(upWantsToGoDown()){
                    // 12
                    goUp();
                    return;
                }
            }
            if(perc.wantToGoUpIn[perc.currentFloor]){
                // 13
                openDoor();
                return;
            }
        }
        
        // Go down a floor (but we were going up)
        if(goingUp){
            if(!perc.wantsToGoToFloor[perc.currentFloor] && inWantsToGoDown()){
                // 14
                goDown();
                return;
            }
            if(!perc.wantToGoDownIn[perc.currentFloor] && !perc.wantToGoUpIn[perc.currentFloor]){
                if(downWantsToGoDown()) {
                    // 15
                    goDown();
                    return;
                } else if(upWantsToGoDown()){
                    // 16
                    goUp();
                    return;
                }
            }
        }

        // Go up a floor (but we were going down)
        if(!goingUp){
            if(!perc.wantsToGoToFloor[perc.currentFloor] && inWantsToGoUp()){
                // 17
                goUp();
                return;
            }
            if(!perc.wantToGoDownIn[perc.currentFloor] && !perc.wantToGoUpIn[perc.currentFloor]){
                if(upWantsToGoUp()){
                    // 18
                    goUp();
                } else if(downWantsToGoUp()){
                    // 19
                    goDown();
                }
            }
        }
    }

    /**
     * Checks whether people inside the lift want to go up.
     * @return
     */
    private boolean inWantsToGoUp(){
        for (int i = perc.currentFloor+1; i < Global.NUM_FLOORS; i++) {
            if(perc.wantsToGoToFloor[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the lower floors wants to go DOWN
     * @return
     */
    private boolean inWantsToGoDown(){
        for (int i = perc.currentFloor-1; i >= 0; i--) {
            if(perc.wantsToGoToFloor[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the upper floors wants to go UP
     * @return
     */
    private boolean upWantsToGoUp(){
        for (int i = perc.currentFloor+1; i < Global.NUM_FLOORS; i++) {
            if(perc.wantToGoUpIn[i]) return true;
        }
        return false;
    }
    
    /**
     * Checks whether someone in the upper floors wants to go UP
     * @return
     */
    private boolean upWantsToGoDown(){
        for (int i = perc.currentFloor+1; i < Global.NUM_FLOORS; i++) {
            if(perc.wantToGoDownIn[i]) return true;
        }
        return false;
    }
    
    /**
     * Checks whether someone in the lower floors wants to go UP
     * @return
     */
    private boolean downWantsToGoUp(){
        for (int i = perc.currentFloor-1; i >= 0; i--) {
            if(perc.wantToGoUpIn[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the lower floors wants to go DOWN
     * @return
     */
    private boolean downWantsToGoDown(){
        for (int i = perc.currentFloor-1; i >= 0; i--) {
            if(perc.wantToGoDownIn[i]) return true;
        }
        return false;
    }

    public void goUp(){
        if(perc.isClosed && perc.currentFloor < Global.NUM_FLOORS-1){
            perc.currentFloor++;
            goingUp = true;
            view.goUpFloor();
        }
    }

    public void goDown(){
        if(perc.isClosed && perc.currentFloor > 0){
            perc.currentFloor--;
            goingUp = false;
            view.goDownFloor();
        }
    }

    public void closeDoor(){
        perc.isClosed = true;
        view.closeDoor();
    }

    public void openDoor(){
        // Simulation of the external behaviour
        perc.wantsToGoToFloor[perc.currentFloor] = false;
        if(goingUp && perc.wantToGoUpIn[perc.currentFloor])
            perc.wantToGoUpIn[perc.currentFloor] = false;
        if(!goingUp && perc.wantToGoDownIn[perc.currentFloor])
            perc.wantToGoDownIn[perc.currentFloor] = false;
        if(goingUp && perc.wantToGoDownIn[perc.currentFloor] && !inWantsToGoUp())
            perc.wantToGoDownIn[perc.currentFloor] = false;
        if(!goingUp && perc.wantToGoUpIn[perc.currentFloor] && !inWantsToGoDown())
            perc.wantToGoUpIn[perc.currentFloor] = false;
        // Lift behaviour
        perc.isClosed = false;
        view.openDoor();
    }

    public void waitAndClose(){
        try {
            Thread.sleep(Global.DELTA_TIME);
            closeDoor();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            if(!perc.isStopped)
                act();
        }
    }
    
    public void linkView(View view){
        this.view = view;
    }

    @Override
    public String toString(){
        String s = "";
        for (int i = Global.NUM_FLOORS-1; i >= 0; i--) {
            s += "_";
            if(perc.wantToGoDownIn[i] || perc.wantToGoUpIn[i]) s += " * ";
            else s += "   ";
            if(perc.currentFloor == i){
                if(perc.isClosed) s += "[]";
                else s += "[ ]";
            }
            if(perc.wantsToGoToFloor[i]) s += " <-- ";
            s += "\n";
        }
        return s;
    }
}
