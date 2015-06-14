/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.gesturerecognition;

import VISIE.characters.BasketballCharacter;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class BallSignalData extends SignalData{
    
    //stores signal data from character towards ball
    
    public BallSignalData(BasketballCharacter c){
            
        super(c);
    }
    
    
    @Override
    public void updateRotations(float f){
        
        if(orientationSignals.size() < orientationLimit){
            orientationSignals.add(f);
        }
        else{
            orientationSignals.remove(0);
            orientationSignals.add(f);
        }
    }
    @Override
    public void updateMovements(float f){
        
        if(movementSignals.size() < movementLimit){
            movementSignals.add(f);
        }
        else{
            movementSignals.remove(0);
            movementSignals.add(f);
        }
    }
    
    @Override
    public String getMovementTrend(){
        
        int approaching = 0;
        int leaving = 0;
        int invisible = 0;
        int stationary = 0;
 
        if(movementSignals.size() == movementLimit){
            
            for(int i = 1; i < movementSignals.size(); i++){
                
                float diff = movementSignals.get(i) - movementSignals.get(i - 1); 
                
                if(movementSignals.get(i) == -1){
                    invisible++;
                }
                else if(diff < 0 && Math.abs(diff) > 0.001f && speedSignals.get(i) > 0
                        && orientationSignals.get(i) < 30){
                    approaching++;
                }
                else if(diff > 0.001f && speedSignals.get(i) > 0
                        && orientationSignals.get(i) > 30){
                    leaving++;
                }
                else{
                    stationary++;
                }
            }
            
//            System.out.println(approaching + " " + leaving + " " + stationary);
                        
            if(invisible > movementLimit * 0.8f){
                return "unseen";
            }
            else if(stationary > movementLimit * 0.9f){
                return "static";
            }
            else{
                float f = Math.max(approaching, leaving);
                
                if(f == approaching){
                    return "approaching";
                }
                else{
                    return "leaving";
                }
            }
        }
        
        return "";
    }
    
    
}
