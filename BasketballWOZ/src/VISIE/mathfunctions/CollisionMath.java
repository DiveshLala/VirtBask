/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.mathfunctions;
import VISIE.scenemanager.SceneCreator;
import VISIE.scenemanager.Scene;
import VISIE.characters.Character;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.collision.CollisionResult;
/**
 *
 * @author huang
 */
public class CollisionMath {
    
    public static boolean circleCollisionTest(Vector3f c1, Vector3f c2, float r1, float r2){
        float dx = c2.getX() - c1.getX();
        float dz = c2.getZ() - c1.getZ();
        float radii = r1 + r2;
        return ((dx*dx) + (dz*dz) < radii*radii);
      }

    //Character r1 is the caller of the method
    //cannot use for player as there is no way of knowing target position
    public static boolean collisionPrediction(Character r1, Vector3f targetPosition, Character r2, int forecast, float personalSpace){
        //returns true if collision predicted
      float r1Speed = r1.getSpeed();
      float r2Speed = r2.getSpeed();
      float ratio = 0;
      float increment;
      float rotAng1;
      float rotAng2;
      Vector3f pos1;
      Vector3f pos2;
      float rad1;
      float rad2;

      if(r1Speed >= r2Speed){
            if(!(r2Speed == 0 && r1Speed == 0))
                ratio = r2Speed/r1Speed;
            increment = r1.getRadius() + personalSpace;
            rotAng1 = Conversions.originToTargetAngle(r1.getPosition(), targetPosition);
            if(r2.getActionState() == 2)
                rotAng2 = r2.getFacingDirection() + 20;
            else if(r2.getActionState() == 3)
                rotAng2 = r2.getFacingDirection() - 20;
            else
                rotAng2 = r2.getFacingDirection();
            pos1 = r1.getPosition();
            pos2 = r2.getPosition();
            rad1 = r1.getRadius() + personalSpace;
            rad2 = r2.getRadius();
      }
      else{
          if(!(r2Speed == 0 && r1Speed == 0))
                ratio = r1Speed/r2Speed;
            increment = r2.getRadius();
            if(r2.getActionState() == 2)
                rotAng1 = r2.getFacingDirection() + 20;
            else if(r2.getActionState() == 3)
                rotAng1 = r2.getFacingDirection() - 20;
            else
                rotAng1 = r2.getFacingDirection();
            rotAng2 = Conversions.originToTargetAngle(r1.getPosition(), targetPosition);
            pos1 = r2.getPosition();
            pos2 = r1.getPosition();
            rad1 = r2.getRadius();
            rad2 = r1.getRadius() + personalSpace;
      }
//      Vector3f dirVec1 = new Vector3f((float)(Math.sin(Math.toRadians(rotAng1))), 0, (float)Math.cos(Math.toRadians(rotAng1)));
//      Vector3f dirVec2 = new Vector3f((float)(Math.sin(Math.toRadians(rotAng2))), 0, (float)Math.cos(Math.toRadians(rotAng2)));
        Vector3f dirVec1 = new Vector3f(Conversions.degreesToNormalizedCoordinates(rotAng1));
        Vector3f dirVec2 = new Vector3f(Conversions.degreesToNormalizedCoordinates(rotAng2));
      boolean willHit = false;
      for(int i = 1; i <= forecast; i++){
            Vector3f predictPos1 = pos1.add(dirVec1.mult(i * increment * ratio));
            Vector3f predictPos2 = pos2.add(dirVec2.mult(i * increment * ratio));
            if(CollisionMath.circleCollisionTest(predictPos1, predictPos2, rad1, rad2)){
                willHit = true;
                break;
          }
      }
      return willHit;
    }

    public static boolean collisionPrediction(float r1Speed, float radius, float testAngle, Vector3f cur,  Character r2, int forecast){
      float r2Speed = r2.getSpeed();
      float ratio = 0;
      float increment;
      float rotAng1;
      float rotAng2;
      Vector3f pos1;
      Vector3f pos2;
      float rad1;
      float rad2;
      if(r1Speed >= r2Speed){
            if(!(r2Speed == 0 && r1Speed == 0))
                ratio = r2Speed/r1Speed;
            increment = radius;
            rotAng1 = testAngle;
            if(r2.getActionState() == 2)
                rotAng2 = r2.getFacingDirection() + 20;
            else if(r2.getActionState() == 3)
                rotAng2 = r2.getFacingDirection() - 20;
            else
                rotAng2 = r2.getFacingDirection();
            pos1 = cur;
            pos2 = r2.getPosition();
            rad1 = radius;
            rad2 = r2.getRadius();
      }
      else{
            if(!(r2Speed == 0 && r1Speed == 0))
                ratio = r1Speed/r2Speed;
            increment = r2.getRadius();
            if(r2.getActionState() == 2)
                rotAng1 = r2.getFacingDirection() + 20;
            else if(r2.getActionState() == 3)
                rotAng1 = r2.getFacingDirection() - 20;
            else
                rotAng1 = r2.getFacingDirection();
            rotAng2 = testAngle;
            pos1 = r2.getPosition();
            pos2 = cur;
            rad1 = r2.getRadius();
            rad2 = radius;
      }
        Vector3f dirVec1 = new Vector3f(Conversions.degreesToNormalizedCoordinates(rotAng1));
        Vector3f dirVec2 = new Vector3f(Conversions.degreesToNormalizedCoordinates(rotAng2));
      boolean willHit = false;
      for(int i = 1; i <= forecast; i++){
            Vector3f predictPos1 = pos1.add(dirVec1.mult(i*increment));
            Vector3f predictPos2 = pos2.add(dirVec2.mult(i*increment*ratio));
            if(CollisionMath.circleCollisionTest(predictPos1, predictPos2, rad1, rad2)){
                willHit = true;
                break;
          }
      }
      return willHit;
    }

    public static boolean isInFOV(Character r1, float headAngle, Character r2){
      float leftLimitAngle = (headAngle + 80)%360;
      float rightLimitAngle = (headAngle - 80);
      if(rightLimitAngle < 0)
          rightLimitAngle += 360;
      float characterAngle = Conversions.originToTargetAngle(r1.getPosition(), r2.getPosition());

      if(leftLimitAngle > rightLimitAngle){
        if(characterAngle <= leftLimitAngle && characterAngle >= rightLimitAngle)
           return true;
      }
      else{
        if(!(characterAngle > leftLimitAngle && characterAngle < rightLimitAngle)){
            return true;
          }
      }
      return false;
  }

    public static boolean isInFOV(Character r1, float headAngle, Vector3f target){
      float leftLimitAngle = (headAngle + 80)%360;
      float rightLimitAngle = (headAngle - 80);
      if(rightLimitAngle < 0)
          rightLimitAngle += 360;
      float characterAngle = Conversions.originToTargetAngle(r1.getPosition(), target);

      if(leftLimitAngle > rightLimitAngle){
        if(characterAngle <= leftLimitAngle && characterAngle >= rightLimitAngle)
           return true;
      }
      else{
        if(!(characterAngle > leftLimitAngle && characterAngle < rightLimitAngle)){
            return true;
          }
      }
      return false;
  }
    
    public static boolean willHitBuildings(Vector3f startPoint, Vector3f direction, float minDistance){
        CollisionResults r = new CollisionResults();
        Ray collRay = new Ray(startPoint, direction);
        Node shootables = Scene.getSceneNode();
        shootables.collideWith(collRay, r);  

        if(r.size() > 0){
            if(r.getClosestCollision().getDistance() < minDistance){
                return true;
            }  
        }
        return false;
    }
    
    public static boolean willHitBuildings(Vector3f startPoint, float initAngle, float minDistance, float leeway){
    //  ENSURES THAT RAYS WILL HIT A TARGET THAT IS LOW
        startPoint = startPoint.subtract(Vector3f.UNIT_Y.mult(2f));
        
        Vector3f direction = Conversions.degreesToNormalizedCoordinates(initAngle);
        Vector3f leftInc = Conversions.degreesToNormalizedCoordinates(initAngle + 90);
        Vector3f rightInc = Conversions.degreesToNormalizedCoordinates(initAngle - 90);
        Ray leftCollRay = new Ray(startPoint.add(leftInc.mult(leeway)), direction);
        Ray middleRay = new Ray(startPoint, direction);
        Ray rightCollRay = new Ray(startPoint.add(rightInc.mult(leeway)), direction);
        CollisionResults leftResults = new CollisionResults();
        CollisionResults rightResults = new CollisionResults();
        CollisionResults middleResults = new CollisionResults();
        Node shootables = SceneCreator.getEnvironmentNode();

        shootables.collideWith(leftCollRay, leftResults);  
        shootables.collideWith(rightCollRay, rightResults);
        shootables.collideWith(middleRay, middleResults);

        if(middleResults.size() > 0 && middleResults.getClosestCollision().getDistance() < minDistance){
            return true;
        }
        else{   
            return (leftResults.size() > 0 && leftResults.getClosestCollision().getDistance() < minDistance)
                   ||(rightResults.size() > 0 && rightResults.getClosestCollision().getDistance() < minDistance);          
        }
    }
    
    public static float get2DDistance(Vector3f vec1, Vector3f vec2){
        vec1.setY(0);
        vec2.setY(0);
        return vec1.distance(vec2);
    }
}
