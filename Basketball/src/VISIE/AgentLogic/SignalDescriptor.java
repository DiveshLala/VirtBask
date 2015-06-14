/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.AgentLogic;

import Basketball.GameManager;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.gesturerecognition.CharacterSignals;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class SignalDescriptor {
    
    //This describes all the signals recognized by a player, taken from the individualKB file
    //Also assesses whether this signal has been found
    
    private String signalType;   
    private String signalName;
    private ArrayList<String[]> properties;
    private BasketballAgent parentCharacter;
    private float currentImplicitEvidence;
    private float currentExplicitEvidence;
    private String focusObject;
    
    public SignalDescriptor(String type, String name, BasketballAgent ba, String foc){
        
        if(type.contains("implicit")){
            signalType = "implicit";
        }
        else if(type.contains("explicit")){
            signalType = "explicit";
        }
        else{
            signalType = type;
        }
        
        signalName = name;
        properties = new ArrayList<String[]>();
        parentCharacter = ba;
        focusObject = foc;
        
    }
    
    public boolean isImplicit(){
        return signalType.equals("implicit");
    }
    
    public String getFocusObject(BasketballCharacter bc){
        
        if(focusObject.equals("me")){
            return "char" + bc.getID();
        }
        else if(focusObject.equals("location")){
            return "location";
        }
        else if(focusObject.equals("ball")){
            return "ball";
        }
        return "";
    }
    
    public void setProperties(ArrayList<String[]> props){
        properties = props;
    }
    
    public void assessCurrentImplicitSignal(CharacterSignals currentSignals, BasketballCharacter signalReceiver){

        BasketballCharacter signalProducer = currentSignals.getCharacter();
        ArrayList<Float> scores = new ArrayList<Float>();
        float maxScore = 0;
        
      //  System.out.println(currentSignals.getMovementTrend() + " " + currentSignals.getOrientationTrend());
        
        for(int i = 0; i < properties.size(); i++){
            if(properties.get(i)[0].trim().startsWith("state=")){
                String state = properties.get(i)[0].replace("state=", ""); 
                if(!this.checkState(state, signalProducer)){
                    currentImplicitEvidence = 0;
                    break;
                }
            }
            else if(properties.get(i)[0].trim().startsWith("orientation=")){
                String orientation = properties.get(i)[0].replace("orientation=", ""); 
                float weighting = Float.parseFloat(properties.get(i)[1]);
                maxScore += weighting;
                if(this.checkOrientation(orientation, currentSignals.getOrientationTrend(signalReceiver))){
                    scores.add(weighting);
                }
                else{
                     scores.add(0f);                   
                }
            }
            else if(properties.get(i)[0].trim().startsWith("movement=")){
                String movement = properties.get(i)[0].replace("movement=", ""); 
                float weighting = Float.parseFloat(properties.get(i)[1]);
                maxScore += weighting;
                if(this.checkMovement(movement, currentSignals.getMovementTrend(signalReceiver))){
                    scores.add(weighting);
                }
                else{
                     scores.add(0f);                   
                }
            }
            else if(properties.get(i)[0].trim().startsWith("range=")){
                String rangeData = properties.get(i)[0].replace("range=", "").trim(); 
                String[] range = rangeData.split(":");
                float weighting = Float.parseFloat(properties.get(i)[1]);
                maxScore += weighting;
                if(this.checkDistance(Float.parseFloat(range[0]), Float.parseFloat(range[1]), signalProducer, signalReceiver)){
                    scores.add(weighting);
                }
                else{
                     scores.add(0f);                   
                }
            }
            else if(properties.get(i)[0].trim().startsWith("facing=")){
                String facing = properties.get(i)[0].replace("facing=", ""); 
                float weighting = Float.parseFloat(properties.get(i)[1]);
                maxScore += weighting;
                if(this.checkFacing(facing, signalProducer)){
                    scores.add(weighting);
                }
                else{
                     scores.add(0f);                   
                }
            }
        }
        
        float total = 0;
        
        for(int i = 0; i < scores.size(); i++){
          total += scores.get(i);  
        }
        
        if(total > maxScore * 0.5f){
            currentImplicitEvidence++;
            if(currentImplicitEvidence> 200){
                currentImplicitEvidence = 200;
            }
        }
        else{
            currentImplicitEvidence--;
            if(currentImplicitEvidence < 0){
                currentImplicitEvidence= 0;
            }
        } 
        
  //      System.out.println(signalName + " " + currentEvidence);
    }
    
    public void assessExplicitSignal(CharacterSignals currentSignals, BasketballCharacter bc){
        
        
        BasketballCharacter signalProducer = currentSignals.getCharacter();  
        
        for(int i = 0; i < properties.size(); i++){
            if(properties.get(i)[0].trim().startsWith("state=")){
                String state = properties.get(i)[0].replace("state=", ""); 
                if(!this.checkState(state, signalProducer)){
                    currentExplicitEvidence = 0;
                    break;
                }
            }
            else if(properties.get(i)[0].trim().startsWith("arms=")){
                String prop = properties.get(i)[0].replace("arms=", "");
                ArrayList<Vector3f[]> vectors = currentSignals.getExplicitnessVectors(bc);
                currentExplicitEvidence = this.calculateArmExplicitness(prop, vectors);
            }
        } 
        
    }
    
    public float getImplicitSignalEvidence(){
        return currentImplicitEvidence;
    }
    
    public float getExplicitSignalEvidence(){
        return currentExplicitEvidence;
    }
    
    public String getName(){
        return signalName;
    }
    
    @Override
    public  String toString(){
        String s = "";
        for(int i = 0; i < properties.size(); i++){
            s = s + properties.get(i)[0] + " " + properties.get(i)[1];
        }
        return s;
    }
    
    private boolean checkState(String state, BasketballCharacter signalProducer){
                
        if(state.trim().equals("ball") && signalProducer.hasPossession){
            return true;
        }
        else if(state.trim().equals("noball") && 
               (GameManager.getAttackingTeam() == parentCharacter.getTeamID()) &&
                GameManager.getGameState() == 0 &&
                SceneCharacterManager.getCharacterInPossession() != null &&
                !signalProducer.hasPossession){
                
            return true;
        }
        else if(state.trim().equals("defending") && 
                (GameManager.getAttackingTeam() != parentCharacter.getTeamID()) &&
                GameManager.getGameState() == 0){
            return true;
        }
        
        return false; 
    }
    
    private boolean checkOrientation(String orientation, String trend){ 
        return (orientation.trim().equals(trend.trim()));
    }
    
    private boolean checkMovement(String movement, String movementTrend){ 
        return (movement.trim().equals(movementTrend.trim()));
    }
    
    private boolean checkDistance(float minDist, float maxDist, BasketballCharacter producer, BasketballCharacter receiver){
        float distance = producer.get2DPosition().distance(receiver.get2DPosition());
        return (minDist < distance) && (maxDist > distance);
    }
    
    private boolean checkFacing(String facing, BasketballCharacter signalProducer){
        if(facing.trim().equals("me")){
            return parentCharacter.perception.isFacingEachOther(signalProducer, 30);
        }
        return false;
    }
    
    private float calculateArmExplicitness(String s, ArrayList<Vector3f[]> vectors){
                
        int lUp = 0;
        int rUp = 0;
        int lForward = 0;
        int rForward = 0;        
        
        for(int i = 0; i < vectors.size(); i++){
            
            Vector3f[] vecColl = vectors.get(i);

            if(vecColl != null){
                                
                if(vecColl[0].y + vecColl[1].y > 0.1f){
                    lUp++;
                }
                if(vecColl[2].y + vecColl[3].y > 0.1f){
                    rUp++;
                }
                if(vecColl[0].z < -0.4f){
                    lForward++;
                }
                if(vecColl[2].z < -0.4f){
                    rForward++;
                }
            }
        }
        
        if(s.trim().equals("oneup")){
            return (Math.max(lUp, rUp))/vectors.size() * 100;
        }
        else if(s.trim().equals("bothup")){
            return ((lUp + rUp)/2)/vectors.size() * 100;
        }
        
        return 0;
    }
    
    
}
