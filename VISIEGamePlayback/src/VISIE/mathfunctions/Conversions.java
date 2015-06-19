/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.mathfunctions;
import VISIE.scenemanager.Court;
import com.jme3.math.Vector3f;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author huang
 */
public class Conversions{

    //angle from origin to target measured in radians
    public static float originToTargetAngle(Vector3f origin, Vector3f target){
      float currentZ = origin.getZ();
      float currentX = origin.getX();
      float vecX = target.getZ() - currentZ;
      float vecY = target.getX()- currentX;
      float angle = ((float)Math.toDegrees(Math.atan(vecY/vecX)));
      if(target.getX() < currentX){
            if(target.getZ() >= currentZ)
                angle = angle + 360;
            else
                angle = angle + 180;
      }
      else if(target.getZ() < currentZ)
          angle = angle + 180;
     return angle;
    }

    //converts angle from degrees to a normalized vector
    public static Vector3f degreesToNormalizedCoordinates(float angle){
        return new Vector3f((float)(Math.sin(Math.toRadians(angle))), 0, (float)Math.cos(Math.toRadians(angle)));
    }

    public static float adjustAngleTo360(float angle){
        if(angle >= 0 && angle <= 360){
            return angle;
        }
        else{
            if(angle < 0)
                return 360 + angle;
            else
                return angle - 360;
        }
    }

    //calculates nearest way to spin to achieve angle (in degrees)
    public static float calculateSpinDirection(float targetAngle, float currentAngle){
        float a = adjustAngleTo360(targetAngle);
        float b = adjustAngleTo360(currentAngle);
        float clockwise;
        float anticlockwise;
        if(a > b){
            clockwise = b + (360 - a);
            anticlockwise = (360 - b) - (360 - a);
            }
        else{
            clockwise = b - a;
            anticlockwise = (360 - b) + a;
        }
        if(clockwise <= anticlockwise)
            return 1;
        else
            return -1;
  }

    //finds smallest distance between two angles in degrees
    public static float minDistanceBetweenAngles(float a1, float a2){
        float clockwise;
        float anticlockwise;
        if(a1 > a2){
                clockwise = a1 - a2;
                anticlockwise = a2 + (360 - a1);
            }
        else{
            clockwise = a1+ (360 - a2);
            anticlockwise = a2 - a1;
        }
        return Math.min(clockwise, anticlockwise);
  }
    
    //generates random position inside bounds
   public static Vector3f generateRandomPosition(float xMax, float xMin, float zMax, float zMin){
      return new Vector3f((float)(Math.random() * (xMax - xMin)) + xMin, 2f, (float)(Math.random() * (zMax - zMin)) + zMin);   
   }
   
   public static Vector3f generateRandomPosition(ArrayList<Float> bounds){
       //Array list is in order xmin, xmax, zmin, zmax
       float xMax = bounds.get(1);
       float xMin = bounds.get(0);
       float zMax = bounds.get(3);
       float zMin = bounds.get(2);
      return new Vector3f((float)(Math.random() * (xMax - xMin)) + xMin, 2f, (float)(Math.random() * (zMax - zMin)) + zMin);   
   }    
   
   public static Vector3f generateRandomPosition(Rectangle2D rect){
       
       //Array list is in order xmin, xmax, zmin, zmax
       float xMax = (float)rect.getMaxX();
       float xMin = (float)rect.getMinX();
       float zMax = (float)rect.getMaxY();
       float zMin = (float)rect.getMinY();
      return new Vector3f((float)(Math.random() * (xMax - xMin)) + xMin, 2f, (float)(Math.random() * (zMax - zMin)) + zMin);   
   }    
   
   public static float findOppositeAngle(float f){
       if(f < 180){
           return f + 180;
       }             
       else{
           return f - 180;
       }

   }
   
   public static Vector3f generateRandomPosition(Vector3f pos, float minDist, float maxDist){
   
        Vector3f randPos = pos.setY(0);
        
        while(!(randPos.distance(pos) > minDist && randPos.distance(pos) < maxDist)){
            randPos = generateRandomPosition(Court.getCourtDimensions()).setY(0);
        }
        
        return randPos;
   }
   
   public static void distancePointToLine(Vector3f lineOrig, Vector3f lineEnd, Vector3f point){
       
   //    float slope = line
   
   }
}
