package control;

import data.Global;
import data.Perceptions;
import view.View;


public class Lift implements Runnable {
    private Perceptions perc;
    private View view;

    private boolean estaPujant;

    public Lift(Perceptions perc,Global data){
        this.perc = perc;
        estaPujant = true;
    }

    public void act(){

        //if door is closed and
        if (perc.portaTancada){
            // 1 - Someone wants to go to that floor
            if (perc.volAnarAlPis[perc.pisActual]) {
                obrePorta();
                return;
            }
            // 2 - Someone wants to go up in the current floor and is lifting up
            if(estaPujant && perc.volPujarA[perc.pisActual]){
                obrePorta();
                return;
            }
            else if (estaPujant && !desdeDaltVolAnarAdalt() && perc.volBaixarA[perc.pisActual]){
                obrePorta();
                return;
            }
            // 3 Someone wants to go down in the current floor and is lifting down
            if(!estaPujant && perc.volBaixarA[perc.pisActual]){
                obrePorta();
                return;
            }
            else if (!estaPujant && !desdeBaixVolAnarAbaix() && perc.volPujarA[perc.pisActual]){
                obrePorta();
                return;
            }
        }

        // 4, 5 - Door is open
        if(!perc.portaTancada){
            esperaITanca();
            return;
        }
        // From here we don't check if the door is closed because if it's open it will not get to this part of the code

        // Go up a floor
        if(estaPujant){
            // 6 - Someone inside the elevator wants to go up
            if(!perc.volAnarAlPis[perc.pisActual] && desdeDinsVolAnarAdalt()){
                puja();
                return;
            }
            // No one wants to enter the elevator
            if(!perc.volPujarA[perc.pisActual]){
                if(desdeDaltVolAnarAdalt() || desdeDaltVolAnarAbaix()){
                    // 7
                    puja();
                    return;
                } else if(desdeBaixVolAnarAdalt()){
                    // 8
                    baixa();
                    return;
                }
            }
            // Someone wants to enter the elevator to go down
            if(perc.volBaixarA[perc.pisActual]){
                // 9
                obrePorta();
                return;
            }
        }

        // Go down a floor
        if(!estaPujant){
            if(!perc.volAnarAlPis[perc.pisActual] && desdeDinsVolAnarAbaix()){
                // 10
                baixa();
                return;
            }
            if(!perc.volBaixarA[perc.pisActual]){
                if(desdeBaixVolAnarAbaix() || desdeBaixVolAnarAdalt()) {
                    // 11
                    baixa();
                    return;
                } else if(desdeDaltVolAnarAbaix()){
                    // 12
                    puja();
                    return;
                }
            }
            if(perc.volPujarA[perc.pisActual]){
                // 13
                obrePorta();
                return;
            }
        }
        
        // Go down a floor (but we were going up)
        if(estaPujant){
            if(!perc.volAnarAlPis[perc.pisActual] && desdeDinsVolAnarAbaix()){
                // 14
                baixa();
                return;
            }
            if(!perc.volBaixarA[perc.pisActual] && !perc.volPujarA[perc.pisActual]){
                if(desdeBaixVolAnarAbaix()) {
                    // 15
                    baixa();
                    return;
                } else if(desdeDaltVolAnarAbaix()){
                    // 16
                    puja();
                    return;
                }
            }
        }

        // Go up a floor (but we were going down)
        if(!estaPujant){
            if(!perc.volAnarAlPis[perc.pisActual] && desdeDinsVolAnarAdalt()){
                // 17
                puja();
                return;
            }
            if(!perc.volBaixarA[perc.pisActual] && !perc.volPujarA[perc.pisActual]){
                if(desdeDaltVolAnarAdalt()){
                    // 18
                    puja();
                } else if(desdeBaixVolAnarAdalt()){
                    // 19
                    baixa();
                }
            }
        }
    }

    /**
     * Checks whether people inside the lift want to go in the specified direction.
     * @param startFloor The current floor where the check should begin.
     * @param step The step direction (1 for up, -1 for down).
     * @param requests The array of requests (volAnarAlPis, volPujarA, volBaixarA).
     * @return true if there is a request in the specified direction.
     */
    private boolean checkRequests(int startFloor, int step, boolean[] requests) {
        for (int i = startFloor + step; (step == 1) ? i < Global.NUM_PISOS : i >= 0; i += step) {
            if (requests[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether people inside the lift want to go up from the current floor.
     * @return true if someone wants to go up from the current floor.
     */
    private boolean desdeDinsVolAnarAdalt() {
        return checkRequests(perc.pisActual, 1, perc.volAnarAlPis);
    }

    /**
     * Checks whether people inside the lift want to go down from the current floor.
     * @return true if someone wants to go down from the current floor.
     */
    private boolean desdeDinsVolAnarAbaix() {
        return checkRequests(perc.pisActual, -1, perc.volAnarAlPis);
    }

    /**
     * Checks whether someone on the upper floors wants to go up.
     * @return true if someone on a higher floor wants to go up.
     */
    private boolean desdeDaltVolAnarAdalt() {
        return checkRequests(perc.pisActual, 1, perc.volPujarA);
    }

    /**
     * Checks whether someone on the upper floors wants to go down.
     * @return true if someone on a higher floor wants to go down.
     */
    private boolean desdeDaltVolAnarAbaix() {
        return checkRequests(perc.pisActual, 1, perc.volBaixarA);
    }

    /**
     * Checks whether someone on the lower floors wants to go up.
     * @return true if someone on a lower floor wants to go up.
     */
    private boolean desdeBaixVolAnarAdalt() {
        return checkRequests(perc.pisActual, -1, perc.volPujarA);
    }

    /**
     * Checks whether someone on the lower floors wants to go down.
     * @return true if someone on a lower floor wants to go down.
     */
    private boolean desdeBaixVolAnarAbaix() {
        return checkRequests(perc.pisActual, -1, perc.volBaixarA);
    }


    public void puja(){
        if(perc.portaTancada && perc.pisActual < Global.NUM_PISOS -1){
            perc.pisActual++;
            estaPujant = true;
            view.goUpFloor();
        }
    }

    public void baixa(){
        if(perc.portaTancada && perc.pisActual > 0){
            perc.pisActual--;
            estaPujant = false;
            view.goDownFloor();
        }
    }

    public void tancaPorta(){
        perc.portaTancada = true;
        view.closeDoor();
    }

    public void obrePorta(){
        // Simulation of the external behaviour
        perc.volAnarAlPis[perc.pisActual] = false;
        if(estaPujant && perc.volPujarA[perc.pisActual])
            perc.volPujarA[perc.pisActual] = false;
        if(!estaPujant && perc.volBaixarA[perc.pisActual])
            perc.volBaixarA[perc.pisActual] = false;
        if(estaPujant && perc.volBaixarA[perc.pisActual] && !desdeDinsVolAnarAdalt())
            perc.volBaixarA[perc.pisActual] = false;
        if(!estaPujant && perc.volPujarA[perc.pisActual] && !desdeDinsVolAnarAbaix())
            perc.volPujarA[perc.pisActual] = false;
        // Lift behaviour
        perc.portaTancada = false;
        view.openDoor();
    }

    public void esperaITanca(){
        try {
            Thread.sleep(Global.DELTA_TIME);
            tancaPorta();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            if(!perc.estaAturat)
                act();
        }
    }
    
    public void enllacaVista(View view){
        this.view = view;
    }

    @Override
    public String toString(){
        String s = "";
        for (int i = Global.NUM_PISOS -1; i >= 0; i--) {
            s += "_";
            if(perc.volBaixarA[i] || perc.volPujarA[i]) s += " * ";
            else s += "   ";
            if(perc.pisActual == i){
                if(perc.portaTancada) s += "[]";
                else s += "[ ]";
            }
            if(perc.volAnarAlPis[i]) s += " <-- ";
            s += "\n";
        }
        return s;
    }
}
