/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.characters.Character;
import com.jme3.math.Vector3f;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Polygon2D;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import java.util.ArrayList;
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
        rangeOfVision = 80;
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
    
    public boolean canSeeCharacter(Character target, float angle){
        float angleBetween = Conversions.originToTargetAngle(parentCharacter.getPosition(), target.getPosition());
        float f = Conversions.minDistanceBetweenAngles(angleBetween, parentCharacter.abo.getHeadRotationAngle());
        return f < angle;                   
    }
        
    public boolean isFacingEachOther(Character c, float thresholdAngle){
        float viewAngle = c.getFacingDirection();
        return this.canSeeCharacter(c) && Conversions.minDistanceBetweenAngles(parentCharacter.getFacingDirection(), Conversions.findOppositeAngle(viewAngle)) < thresholdAngle;
    }
    
    //predicts collision will occur on way to target
    public boolean isCollisionPredicted(Vector3f target){
        Vector3f dir = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(parentCharacter.get2DPosition(), target.setY(0)));
        ArrayList<BasketballCharacter> members = parentCharacter.getAllOtherCharacters();
        boolean collisionPredicted = !CollisionMath.rayCollisionTest(parentCharacter.getPosition(), dir, parentCharacter, members, 5, 5f);
        boolean closestToTarget =  parentCharacter.planner.isClosestToLocation(parentCharacter.getAllOtherCharacters(), target);
        return collisionPredicted && !closestToTarget;
    }
    
    //generates subtarget if main target is blocked
    public Vector3f avoidCollision(Vector3f target){
            
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.get2DPosition(), target);
        float distToTarget = parentCharacter.get2DPosition().distance(target);
        float newAngle;
        Vector3f newTarget = target;
        float increment = 0;
        float mult = 1;
        
        for(int i = 1; i < 20; i++){
            
            if(i%2 == 0){
                increment += 10;
            }
            
            mult = -mult;
                        
            newAngle = Conversions.adjustAngleTo360(targetAngle + (increment * mult));  
            Vector3f newDir = Conversions.degreesToNormalizedCoordinates(newAngle);
            newTarget = parentCharacter.get2DPosition().add(newDir.mult(distToTarget));
            
            if(!parentCharacter.perception.isCollisionPredicted(newTarget) && Court.isInsideCourt(newTarget)){          
                return newTarget;
            }
        }
        
        return target;
    }
    
    public boolean characterWithinLineBounds(Vector3f pos1, Vector3f pos2, BasketballCharacter character){
        
        float[] xPoints = new float[4];
        float[] yPoints = new float[4];
        
        float angle = Conversions.originToTargetAngle(pos1, pos2);
        Vector3f passVector = Conversions.degreesToNormalizedCoordinates(angle);
        
        Vector3f lMe = new Vector3f(-passVector.z, 0, passVector.x);
        Vector3f rMe = new Vector3f(passVector.z, 0, -passVector.x);
        
        Vector3f corner1 = pos1.add(lMe.mult(3));
        Vector3f corner2 = pos1.add(rMe.mult(3));
        Vector3f corner3 = pos2.add(rMe.mult(3));
        Vector3f corner4 = pos2.add(lMe.mult(3));
        
        xPoints[0] = corner1.x;
        xPoints[1] = corner2.x;
        xPoints[2] = corner3.x;
        xPoints[3] = corner4.x;
        
        yPoints[0] = corner1.z;
        yPoints[1] = corner2.z;
        yPoints[2] = corner3.z;
        yPoints[3] = corner4.z;
         
        Polygon2D poly = new Polygon2D(xPoints, yPoints, 4);  
        return Conversions.isPointInsidePolygon(character.get2DPosition().x, character.get2DPosition().z, poly);
        
    }
    
    public boolean spaceIsOccupied(Vector3f target, float threshold){
        target.setY(0);
        ArrayList<BasketballCharacter> chars = parentCharacter.getAllOtherCharacters();
        
        for(BasketballCharacter bc:chars){
            float theirDist = bc.get2DPosition().distance(target);
            if(theirDist < threshold){
                return true;
            }        
        }
            
        return false;    
    }
    
    public Vector3f getClosestUnoccupied(Vector3f originalTarget, float range){
        
        float rand = 0;
        Vector3f newTarget;
        Vector3f dir;
      
        for(int i = 0; i < 7; i++){
           dir  = Conversions.degreesToNormalizedCoordinates(rand + (45 * i));
           newTarget = originalTarget.add(dir.mult(range));
           
           if(Court.isInsideCourt(newTarget) && !this.spaceIsOccupied(newTarget, 3)){
               return newTarget;
           }
            
        }    
        
        return originalTarget;
    }
    
    public Vector3f adjustTargetForCollisions(Vector3f target, boolean ignoreCollision){
        
        Vector3f realTarget = target;
            
        if(!ignoreCollision && this.isCollisionPredicted(target)){
    //        System.out.println(parentCharacter.getID() + " collision predicted " + realTarget);
            realTarget = this.avoidCollision(target);
   //         System.out.println(parentCharacter.getID() + " collision adjusted target " + realTarget);
        }    
        
        return realTarget;
    }
    
//    public Vector3f adjustTargetForOccupation(Vector3f target){
//        
//        Vector3f realTarget = target;
//    
//        if(parentCharacter.get2DPosition().distance(target) < 7.5f && this.spaceIsOccupied(target, 3f)){
//            System.out.println(parentCharacter.getID() + " space is occupied" + realTarget);
//            realTarget = parentCharacter.perception.getClosestUnoccupied(realTarget, 5);
//        }
//        
//        return realTarget;
//    
//    }
    
}
