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
        // 1 - Door is closed, someone wants to go to that floor
        if(perc.portaTancada && perc.volAnarAlPis[perc.pisActual]) {
            obrePorta();
            return;
        }
        // 2 - Door is closed, someone wants to go up in the current floor
        if(perc.portaTancada && estaPujant && perc.volPujarA[perc.pisActual]){
            obrePorta();
            return;
        }
        // 3 - Door is closed, someone wants to go down in the current floor
        if(perc.portaTancada && !estaPujant && perc.volBaixarA[perc.pisActual]){
            obrePorta();
            return;
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
                if(desdeDaltVolAnarAdalt()){
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
                if(desdeBaixVolAnarAbaix()) {
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
     * Checks whether people inside the lift want to go up.
     * @return
     */
    private boolean desdeDinsVolAnarAdalt(){
        for (int i = perc.pisActual +1; i < Global.NUM_PISOS; i++) {
            if(perc.volAnarAlPis[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the lower floors wants to go DOWN
     * @return
     */
    private boolean desdeDinsVolAnarAbaix(){
        for (int i = perc.pisActual -1; i >= 0; i--) {
            if(perc.volAnarAlPis[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the upper floors wants to go UP
     * @return
     */
    private boolean desdeDaltVolAnarAdalt(){
        for (int i = perc.pisActual +1; i < Global.NUM_PISOS; i++) {
            if(perc.volPujarA[i]) return true;
        }
        return false;
    }
    
    /**
     * Checks whether someone in the upper floors wants to go DOWN
     * @return
     */
    private boolean desdeDaltVolAnarAbaix(){
        for (int i = perc.pisActual +1; i < Global.NUM_PISOS; i++) {
            if(perc.volBaixarA[i]) return true;
        }
        return false;
    }
    
    /**
     * Checks whether someone in the lower floors wants to go UP
     * @return
     */
    private boolean desdeBaixVolAnarAdalt(){
        for (int i = perc.pisActual -1; i >= 0; i--) {
            if(perc.volPujarA[i]) return true;
        }
        return false;
    }

    /**
     * Checks whether someone in the lower floors wants to go DOWN
     * @return
     */
    private boolean desdeBaixVolAnarAbaix(){
        for (int i = perc.pisActual -1; i >= 0; i--) {
            if(perc.volBaixarA[i]) return true;
        }
        return false;
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
