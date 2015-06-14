/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.gesturerecognition;

import Basketball.Ball;
import VISIE.AgentLogic.SignalDescriptor;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.sun.crypto.provider.DESCipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class SignalInterpreter {
    
    //Updates all the signals seen from each character
    //Interprets them to find current focus
    

    //body information on teammates
    private ArrayList<CharacterSignals> teamMateSignals;
    private BasketballAgent parentCharacter;
    
    //focus for each teammate
    private String[] currentFocuses;
    
    //explicitness for each teammate
    private String[] explicitSignalsObserved;
       
    //representation of known signals
    private ArrayList<SignalDescriptor> signalDescriptions;
    
    
    public SignalInterpreter(BasketballAgent ba, String filePath){
        
        parentCharacter = ba;
        teamMateSignals = new ArrayList<CharacterSignals>();
        signalDescriptions = new ArrayList<SignalDescriptor>();
        
        ArrayList<BasketballCharacter> t = ba.getTeamMates();  
        
        for(int i = 0; i < t.size(); i++){
            CharacterSignals signals = new CharacterSignals(t.get(i), t.get(i).getTeamMates());
            teamMateSignals.add(signals);
        }
        
        currentFocuses = new String[teamMateSignals.size()];
        explicitSignalsObserved = new String[teamMateSignals.size()];
        
        this.initializeSignalDescriptions(filePath);
    }
    
    public void updateSignals(){
        this.updateOrientations(); 
        this.updateDistances();
        this.updateSpeeds();
        this.updateExplicitness();
        this.updateBallSignalData();
        
    }
    
    private void updateBallSignalData(){
        
        for(int i = 0; i < teamMateSignals.size(); i++){
            
            BasketballCharacter c = teamMateSignals.get(i).getCharacter();
            Ball ball = SceneObjectManager.getBall();
            Vector3f ballPos = ball.getBallPosition().setY(0);
            
            if(parentCharacter.perception.canSeeCharacter(c) &&
               SceneCharacterManager.getCharacterInPossession() == null){
                    float mov = ballPos.distance(c.get2DPosition());
                    float f = Conversions.originToTargetAngle(c.get2DPosition(), ballPos);
                    float rot = Conversions.minDistanceBetweenAngles(f, c.getFacingDirection());
                    float speed = c.getSpeed();
                    teamMateSignals.get(i).updateBallSignalData(mov, rot, speed);
             }
            else{
                teamMateSignals.get(i).updateBallSignalData(-1, -1, -1);
            }
        }    
    }
    
    private void updateOrientations(){
        
        for(int i = 0; i < teamMateSignals.size(); i++){
            
            BasketballCharacter c = teamMateSignals.get(i).getCharacter();
            ArrayList<BasketballCharacter> teamMates = c.getTeamMates();
            
            if(parentCharacter.perception.canSeeCharacter(c)){            
                for(int j = 0; j < teamMates.size(); j++){            
                    float f = Conversions.originToTargetAngle(c.get2DPosition(), teamMates.get(j).get2DPosition());
                    float diff = Conversions.minDistanceBetweenAngles(f, c.getFacingDirection());
                    teamMateSignals.get(i).updateRotations(teamMates.get(j), diff);  
                }
            }
            else{
                for(int j = 0; j < teamMates.size(); j++){ 
                    teamMateSignals.get(i).updateRotations(teamMates.get(j), -1); 
                }
            }
       
        }
    }
    
    private void updateDistances(){
        
        for(int i = 0; i < teamMateSignals.size(); i++){
            
            BasketballCharacter c = teamMateSignals.get(i).getCharacter();
            ArrayList<BasketballCharacter> teamMates = c.getTeamMates();
            
            if(parentCharacter.perception.canSeeCharacter(c)){
                for(int j = 0; j < teamMates.size(); j++){            
                    float d = teamMates.get(j).get2DPosition().distance(c.get2DPosition());
                    teamMateSignals.get(i).updateMovements(teamMates.get(j), d);  
                }

            }
            else{
                for(int j = 0; j < teamMates.size(); j++){ 
                    teamMateSignals.get(i).updateRotations(teamMates.get(j), -1); 
                }                                
            }

        }
    }
    
    private void updateSpeeds(){
        
        for(int i = 0; i < teamMateSignals.size(); i++){
            
            BasketballCharacter c = teamMateSignals.get(i).getCharacter();
            ArrayList<BasketballCharacter> teamMates = c.getTeamMates();
            
            if(parentCharacter.perception.canSeeCharacter(c)){
                for(int j = 0; j < teamMates.size(); j++){
                    teamMateSignals.get(i).updateSpeeds(teamMates.get(j), c.getSpeed()); 
                }
            }
            else{
                for(int j = 0; j < teamMates.size(); j++){
                    teamMateSignals.get(i).updateSpeeds(teamMates.get(j), -1); 
                }                              
            }
        }
    }
    
    private void updateExplicitness(){
        
        for(int i = 0; i < teamMateSignals.size(); i++){
                      
            BasketballCharacter c = teamMateSignals.get(i).getCharacter();
            
            if(parentCharacter.perception.canSeeCharacter(c, 70)){
                Vector3f[] values = c.getArmVectors();
                teamMateSignals.get(i).updateExplicitness(values); 
            }
            else{
                teamMateSignals.get(i).updateExplicitness(null);                            
            }
        }    
    }
    
    public void interpretSignals(){
        

        
        //interprets signals from all teammates we can see
        for(int i = 0; i < teamMateSignals.size(); i++){
            if(parentCharacter.perception.canSeeCharacter(teamMateSignals.get(i).getCharacter())){
                this.interpretExplicitSignals(teamMateSignals.get(i), i);
                this.interpretImplicitSignals(teamMateSignals.get(i), i);
            }
        }
//        
//        if(parentCharacter.getID() == 3){
//            System.out.println(teamMateSignals.get(0).getOrientationTrend(parentCharacter) + " " + teamMateSignals.get(0).getMovementTrend(parentCharacter));
//        }
    }
    
    private void interpretImplicitSignals(CharacterSignals teamMateSignal, int index){
        String focus = "";
        float curHigh = 0;
        
        ArrayList<BasketballCharacter> receivers = teamMateSignal.getCharacter().getTeamMates();
        
        for(int i = 0; i < receivers.size(); i++){

            for(int j = 0; j < signalDescriptions.size(); j++){
                SignalDescriptor desc = signalDescriptions.get(j);
                
                if(desc.isImplicit()){

                    desc.assessCurrentImplicitSignal(teamMateSignal, receivers.get(i));

                    if(desc.getImplicitSignalEvidence() > curHigh){
                        curHigh = desc.getImplicitSignalEvidence();
                        focus = desc.getFocusObject(receivers.get(i));
                    }
                }
            } 
        }      
        
        teamMateSignal.getBallSignalTrend();
     //   System.out.println(parentCharacter.getID() + " " + curHigh);
        
        if(curHigh > 100f){
            currentFocuses[index] = focus;       
        }
        else{
            currentFocuses[index] = "";
        }
    }
    
    private void interpretExplicitSignals(CharacterSignals teamMateSignal, int index){
        
        float curHigh = 0;
        String focus = "";                       
                
        for(int j = 0; j < signalDescriptions.size(); j++){
            SignalDescriptor desc = signalDescriptions.get(j);

            if(!desc.isImplicit()){
                desc.assessExplicitSignal(teamMateSignal, parentCharacter);
                if(desc.getExplicitSignalEvidence() > curHigh){
                    curHigh = desc.getExplicitSignalEvidence();
                    focus = desc.getName();
                }
            }
        }
               
        //limit of signalData
        if(curHigh > 50){
            explicitSignalsObserved[index] = focus;
        }
        else{
            explicitSignalsObserved[index] = "";
        }         
    }
    
    private void initializeSignalDescriptions(String filePath){
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("name")){
                    String name = line.replace("name:", "");
                    String attributes = br.readLine();
                    attributes = attributes.trim();
                    String[] props = attributes.split(",");
                    String weightings = br.readLine();
                    String[] weights = weightings.split(",");
                    String focusData = br.readLine();
                    String focus = focusData.replace("focus:", "");
                    SignalDescriptor descriptor = new SignalDescriptor(props[0], name, parentCharacter, focus);
                    ArrayList<String[]> data = new ArrayList<String[]>();
                    for(int i = 1; i < props.length; i++){
                        String[] s = {props[i], weights[i]};
                        data.add(s);
                    }
                    descriptor.setProperties(data);
                    signalDescriptions.add(descriptor);
                }
            }
        }
        catch(IOException e){
        }
    
    }
    
    public String getCharacterFocus(BasketballCharacter bc){
    
        for(int i = 0; i < teamMateSignals.size(); i++){
        
            if(teamMateSignals.get(i).getCharacter().equals(bc) && currentFocuses[i] != null){
                return currentFocuses[i];                
            }        
        }
        
        return "";
    }
    
    public String getExplicitSignalObserved(BasketballCharacter bc){
    
        for(int i = 0; i < teamMateSignals.size(); i++){      
            if(teamMateSignals.get(i).getCharacter().equals(bc) && explicitSignalsObserved[i] != null){
                    return explicitSignalsObserved[i];                
                }        
        }
        
        return "";
    }
    
    
}
