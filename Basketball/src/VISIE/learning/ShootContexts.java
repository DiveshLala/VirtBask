/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class ShootContexts {
    
    private ArrayList<Context> shootContexts;
    private boolean isSuccessful;
    
    public ShootContexts(ArrayList<Context> c){
        shootContexts = c;
    }
    
    public void setSuccess(boolean b){
        isSuccessful = b;
    }
    
    public boolean getSuccess(){
        return isSuccessful;
    }
    
    public ArrayList<Context> getShootContexts(){
        return shootContexts;
    }
    
}
