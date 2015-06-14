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
public class CharacterSignals {
    
    //current signal data for one particular character
    //associated with one SignalData entry for each other teammate
    
    private BasketballCharacter produceSignalCharacter;
    private ArrayList<SignalData> signalDataRecords;
    private BallSignalData ballSignalData;
    
    public CharacterSignals(BasketballCharacter c, ArrayList<BasketballCharacter> teamMates){
        produceSignalCharacter = c;
        signalDataRecords = new ArrayList<SignalData>();
        
        for(int i = 0; i < teamMates.size(); i++){
            if(teamMates.get(i).getID() != produceSignalCharacter.getID()){
                signalDataRecords.add(new SignalData(teamMates.get(i)));
            }
        }
        
        ballSignalData = new BallSignalData(c);
    }
    
    public BasketballCharacter getCharacter(){
        return produceSignalCharacter;
    }
    
//    public ArrayList<Float> getRotations(){
//        return orientationSignals;
//    }
    
    public void updateRotations(BasketballCharacter receiveCharacter, float value){
                
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(receiveCharacter)){
                signalDataRecords.get(i).updateRotations(value);
                break;
            }
        } 
    }
    
    public void updateMovements(BasketballCharacter receiveCharacter, float value){
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(receiveCharacter)){
                signalDataRecords.get(i).updateMovements(value);
                break;
            }
        } 
    }
        
    public void updateSpeeds(BasketballCharacter receiveCharacter, float value){
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(receiveCharacter)){
                signalDataRecords.get(i).updateSpeeds(value);
                break;
            }
        } 
    }
    
    public void updateExplicitness(Vector3f[] values){
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            signalDataRecords.get(i).updateExplicitness(values);
        }
    }
    
    public String getOrientationTrend(BasketballCharacter bc){
        
    //    System.out.println(produceSignalCharacter.getID() + " " + bc.getID());
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(bc)){
                return signalDataRecords.get(i).getOrientationTrend();
            }
        } 
       
        return "";
    }
    
    public String getMovementTrend(BasketballCharacter bc){
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(bc)){
                return signalDataRecords.get(i).getMovementTrend();

            }
        }
        return "";

    }
    
    public ArrayList<Vector3f[]> getExplicitnessVectors(BasketballCharacter bc){
        
        for(int i = 0; i < signalDataRecords.size(); i++){
            if(signalDataRecords.get(i).getReceivingCharacter().equals(bc)){
                return signalDataRecords.get(i).getExplicitnessVectors();
            }
        }
        
        return null;
    
    }
    
    public void updateBallSignalData(float mov, float rot, float sp){
        ballSignalData.updateMovements(mov);
        ballSignalData.updateRotations(rot);
        ballSignalData.updateSpeeds(sp);
    }
    
    public void getBallSignalTrend(){
    //    System.out.println(produceSignalCharacter.getID() + " " + ballSignalData.getMovementTrend() + " " + ballSignalData.getOrientationTrend());    
    }

        
}
