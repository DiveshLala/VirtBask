/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.characters.Character;
import com.jme3.math.Vector3f;
import VISIE.JointProjectXMLProcessor;
/**
 *
 * @author DiveshLala
 */
public class AgentPerception {
    
    private BasketballAgent parentCharacter;
    private float mutualGazeAngle;
    private float rangeOfVision;
    
    public AgentPerception(BasketballAgent ba){
        parentCharacter = ba;
        mutualGazeAngle = 30f;
        rangeOfVision = 55;
    }
    
    
    //returns mutual gaze
    public void doGaze(Character c){
        if(!this.isLookingAtTarget(c.getPosition())){
            parentCharacter.abo.turnHeadToTarget(c.getPosition());
        }
    }
    
    public boolean isMutualGaze(Character c){
        return this.isLookingAtTarget(c.getPosition()) && c.isLookingAtTarget(parentCharacter.getPosition());
    }
    
    public boolean isLookingAtTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, parentCharacter.abo.getHeadRotationAngle()) < mutualGazeAngle;
    }
    
    public boolean isWithinGaze(Vector3f target, float angle){
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, parentCharacter.abo.getHeadRotationAngle()) < angle;
    }
    
    public boolean canSeeCharacter(Character target){
        float angleBetween = Conversions.originToTargetAngle(parentCharacter.getPosition(), target.getPosition());
        float f = Conversions.minDistanceBetweenAngles(angleBetween, parentCharacter.abo.getHeadRotationAngle());
        return f < rangeOfVision;                   
    }
    
    public boolean isPerceptionLimitReached(String jpName, String movementName){
        float[] data = JointProjectXMLProcessor.getMovementLimit(jpName, movementName, 0); 
        return (parentCharacter.getCurrentMovementProgress((int)data[1])) >= data[0];
    }
    
    public boolean isFacingEachOther(Character c, float thresholdAngle){
        float viewAngle = c.getFacingDirection();
        return Math.abs(parentCharacter.getFacingDirection() - Conversions.findOppositeAngle(viewAngle)) < thresholdAngle;
    }
    
}
