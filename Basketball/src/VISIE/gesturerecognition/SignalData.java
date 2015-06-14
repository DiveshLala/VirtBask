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
public class SignalData {
    
    //Stores actual signal information coming from character
    //One signalData related to each character
    
    private BasketballCharacter receiveSignalCharacter;
    protected ArrayList<Float> orientationSignals;
    protected ArrayList<Float> movementSignals;
    protected ArrayList<Float> speedSignals;
    private ArrayList<Vector3f[]> signalExplicitness;
    protected int orientationLimit = 50;
    protected int movementLimit = 25;
    private int speedLimit = 25;
    private int explicitLimit = 25;
    
    public SignalData(BasketballCharacter c){
        
        receiveSignalCharacter = c;
        orientationSignals = new ArrayList<Float>();
        movementSignals = new ArrayList<Float>();
        speedSignals = new ArrayList<Float>();
        signalExplicitness = new ArrayList<Vector3f[]>();
    
    }
    
    public void updateRotations(float f){
        
        if(orientationSignals.size() < orientationLimit){
            orientationSignals.add(f);
        }
        else{
            orientationSignals.remove(0);
            orientationSignals.add(f);
        }
    }
    
    public void updateMovements(float f){
        
        if(movementSignals.size() < movementLimit){
            movementSignals.add(f);
        }
        else{
            movementSignals.remove(0);
            movementSignals.add(f);
        }
    }
    
    public void updateSpeeds(float f){
        
        if(speedSignals.size() < speedLimit){
            speedSignals.add(f);
        }
        else{
            speedSignals.remove(0);
            speedSignals.add(f);
        }
    }
    
    public void updateExplicitness(Vector3f[] values){
        
        if(signalExplicitness.size() < explicitLimit){
            signalExplicitness.add(values);
        }
        else{
            signalExplicitness.remove(0);
            signalExplicitness.add(values);
        }
    }
    
    public BasketballCharacter getReceivingCharacter(){
        return receiveSignalCharacter;
    }
    
    public ArrayList<Float> getOrientationSignals(){
        return orientationSignals;
    }
    
    public ArrayList<Float> getMovementSignals(){
        return movementSignals;
    }
        
    public ArrayList<Float> getSpeedSignals(){
        return speedSignals;
    }
    
    public String getOrientationTrend(){
        
        int towards = 0;
        int away = 0;
        int invisible = 0;
        int stationary = 0;
         
        if(orientationSignals.size() == orientationLimit){
                        
            for(int i = 1; i < orientationSignals.size(); i++){
                float diff = orientationSignals.get(i) - orientationSignals.get(i - 1);                
                
                if(orientationSignals.get(i) == -1){
                    invisible++;
                }
                else if(diff < 0 && Math.abs(diff) > 0.005f){
                    towards++;
                }
                else if(diff > 0.005f){
                    away++;
                }
                else{
                    stationary++;
                }
            }
                        
            if(invisible > orientationLimit * 0.5f){
                return "unseen";
            }
            else{
            
                float f = Math.max(towards, Math.max(away, stationary));
                            
                if(f == towards){
                    return "towards";
                }
                else if(f == away){
                    return "away";
                }
                else{
                    return "static";
                }
            }
        }
        
        return "";
    }
    
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
    
    public ArrayList<Vector3f[]> getExplicitnessVectors(){
        return signalExplicitness;
    }
    
}
