/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.Main;
import VISIE.characters.BasketballAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import com.jme3.math.Vector3f;
import VISIE.characters.Character;

/**
 *
 * @author DiveshLala
 */
public class AgentBodyOperations {
    
    BasketballAgent parentCharacter;
    AnimatedModel agentModel;
    float headRotationAngle;
    float torsoRotationAngle;
//    private float maxSpeed;
//    private float turningSpeed;
    private float rangeOfVision;
//    private float headMaxTurningAngle;
     private float headTurnSpeed; //degrees per frame
    private float torsoTurnSpeed; //degrees per frame
   // private float mutualGazeAngle;
    
    public AgentBodyOperations(BasketballAgent ba, AnimatedModel m){
        
        parentCharacter = ba;
        agentModel = m;
        
        headTurnSpeed = 1.5f; 
        torsoTurnSpeed = 1f;
        headRotationAngle = 0;
        torsoRotationAngle = 0;
    }
    
    public void turnHeadToTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
        if(Math.abs(targetAngle - headRotationAngle) > 5){
            float f = Conversions.calculateSpinDirection(targetAngle, headRotationAngle);
            headRotationAngle = Conversions.adjustAngleTo360(headRotationAngle + (headTurnSpeed * -f * (Main.getFrameRate() * 200)));
            this.setFacingDirection(torsoRotationAngle, headRotationAngle);
        }
    }
    
    public void setHeadAngle(Vector3f vec){
        headRotationAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), vec);
    }
    
    public void turnTorsoToTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
            if(Math.abs(targetAngle - torsoRotationAngle) > 5){
            float f = Conversions.calculateSpinDirection(targetAngle, torsoRotationAngle);
            torsoRotationAngle = Conversions.adjustAngleTo360(torsoRotationAngle + (torsoTurnSpeed * -f * (Main.getFrameRate() * 200)));
            this.setFacingDirection(torsoRotationAngle, headRotationAngle);
        }
    }
    
    public void setFacingDirection(float torsoRotation, float headRotation){ 
        headRotationAngle = headRotation;
        torsoRotationAngle = torsoRotation;
        agentModel.turnBody(headRotationAngle - torsoRotationAngle, torsoRotationAngle);
    }
    
    public void turnBodyToTarget(Vector3f target){
        this.turnHeadToTarget(target);
        this.turnTorsoToTarget(target);
    }
    
    public boolean isReadyForPass(Character c){
        return (Math.abs(torsoRotationAngle - Conversions.originToTargetAngle(parentCharacter.getPosition(), c.getPosition())) < 5);
    }
        
    public float getFacingDirection(){
        return torsoRotationAngle;
    }
    
    public float getHeadRotationAngle(){
        return headRotationAngle;
    }
    
    public void moveTowardsTarget(Vector3f target, float speed){
        this.turnBodyToTarget(target);      
        if(parentCharacter.perception.isLookingAtTarget(target)){
            parentCharacter.setSpeed(speed);
            Vector3f dir = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(parentCharacter.getPosition(), target)); 
            parentCharacter.walk(dir, speed);
        }
        else{
            parentCharacter.setSpeed(0);
        }
    }
}
