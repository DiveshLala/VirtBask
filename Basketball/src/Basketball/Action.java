/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class Action {
    
    public Vector3f position;
    public float facingDirection;
    public float relativeRotation;
    public float[] oppRelativeRots;
    public float distToGoal;
    public float distToMe;
    public float[] oppDistances;  
    
    public Action(BasketballCharacter bc, BasketballCharacter parentCharacter){
    
        ArrayList<BasketballCharacter> opps = parentCharacter.getOpponents();
        float fd = bc.getFacingDirection();
        oppRelativeRots = new float[opps.size()];
        oppDistances = new float[opps.size()];
        
        float a = Conversions.originToTargetAngle(bc.getPosition(), parentCharacter.getPosition());
        relativeRotation = Conversions.minDistanceBetweenAngles(fd, a);
        
        for(int i = 0; i < opps.size(); i++){
            float b = Conversions.originToTargetAngle(bc.getPosition(), opps.get(i).getPosition());
            oppRelativeRots[i] = Conversions.minDistanceBetweenAngles(fd, b);
        }
                     
        position = bc.get2DPosition();
        facingDirection = fd;   

        distToGoal = bc.get2DPosition().distance(Court.getGoalPosition());   
        distToMe = bc.get2DPosition().distance(parentCharacter.get2DPosition());
        
        for(int i = 0; i < opps.size(); i++){
            oppDistances[i] = bc.get2DPosition().distance(opps.get(i).get2DPosition());
        }

        
    }
    
    @Override
    public String toString(){
        return "POS:" + position + "FD: " + facingDirection + " RELROT:" + relativeRotation + " GOAL:" + distToGoal + " ME:" + distToMe;
    }
    
    public Vector3f getPosition(){
        return position;
    }
    
}
