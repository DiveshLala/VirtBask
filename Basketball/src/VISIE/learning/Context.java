/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import Basketball.*;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class Context {
    
    public Vector3f position;
    public float facingDirection;
    public float relativeRotation;
    public float[] oppRelativeRots;
    public float distToGoal;
    public float distToMe;
    public Vector3f myPos;
    public float[] oppDistances;  
    public Vector3f[] oppPositions;
    public float goalRelRot;
    public boolean inPossession;
    
    //first character is character being observed (focus character), second character is observer (main character)
    public Context(BasketballCharacter bc, BasketballCharacter parentCharacter){
    
        ArrayList<BasketballCharacter> opps = parentCharacter.getOpponents();
        float fd = bc.getFacingDirection();
        oppRelativeRots = new float[opps.size()];
        oppDistances = new float[opps.size()];
        oppPositions = new Vector3f[opps.size()];
        
        float a = Conversions.originToTargetAngle(bc.getPosition(), parentCharacter.getPosition());
        relativeRotation = Conversions.minDistanceBetweenAngles(fd, a);
        
        for(int i = 0; i < opps.size(); i++){
            float b = Conversions.originToTargetAngle(bc.getPosition(), opps.get(i).getPosition());
            oppRelativeRots[i] = Conversions.minDistanceBetweenAngles(fd, b);
            oppDistances[i] = bc.get2DPosition().distance(opps.get(i).get2DPosition());
            oppPositions[i] = opps.get(i).get2DPosition();
        }
                     
        position = bc.get2DPosition();
        facingDirection = fd;   

        distToGoal = bc.get2DPosition().distance(Court.getGoalPosition());   
        distToMe = bc.get2DPosition().distance(parentCharacter.get2DPosition());
                
        float b = Conversions.originToTargetAngle(bc.getPosition(), Court.getGoalPosition());
        goalRelRot = Conversions.minDistanceBetweenAngles(fd, b);
        myPos = parentCharacter.getPosition();  
        inPossession = SceneCharacterManager.getCharacterInPossession().equals(bc);
        
    }
    

    @Override
    public String toString(){
        String oppRots = "";
        String oppDist = "";
        for(int i = 0; i < oppRelativeRots.length; i++){
            oppRots += oppRelativeRots[i] + ";";
            oppDist += oppDistances[i] + ";";
        }
        return "RELROT:" + relativeRotation + " MEDIST:" + distToMe + " GOALROT:" + goalRelRot + " GOALDIST:" + distToGoal + 
               " OPPROT:" + oppRots + " OPPDIST" + oppDist;
    }
    
    public Vector3f getPosition(){
        return position;
    }
    
    public void updateContext(float val, int variable){
        if(variable == 0){
            relativeRotation = val;
        }
        else if(variable == 1){
            distToMe = val;
        }
        else if(variable == 2){
            goalRelRot = val;
        }
        else if(variable == 3){
            distToGoal = val;
        }
        else if(variable == 4){
            oppRelativeRots[0] = val;
        }
        else if(variable == 5){
            oppRelativeRots[1] = val;
        }
        else if(variable == 6){
            oppDistances[0] = val;
        }
        else if(variable == 7){
            oppDistances[1] = val;
        }             
    }
    
    public ArrayList<Float> getContextData(){
    
        ArrayList<Float> data = new ArrayList<Float>();
        
  //      data.add(context.facingDirection);
        data.add(relativeRotation);
        data.add(distToMe);
        data.add(goalRelRot);
        data.add(distToGoal);
        for(float f:oppRelativeRots){
            data.add(f);
        }
        for(float f:oppDistances){
            data.add(f);
        }

        return data;
    }
    
    public boolean isContextEqual(Context testCon){
        
        ArrayList<Float> myCon = this.getContextData();
        ArrayList<Float> theirCon = testCon.getContextData();
        for(int i = 0; i < myCon.size(); i++){
            if(Math.abs(myCon.get(i) - theirCon.get(i)) > 0.0001f){
                return false;
            }
        }
        
        return true;
    
    }
    
}
