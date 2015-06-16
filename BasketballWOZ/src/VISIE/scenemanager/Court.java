/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;

import VISIE.mathfunctions.Conversions;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author DiveshLala
 */
public class Court {
    
    private static Rectangle2D.Float courtDimensions;
    private static Vector3f goalPosition;
    private static Vector3f hoopLocation;
    private static Vector3f restartLocation;
    
    public static void initialiseCourtDimensions(Vector3f courtTopLeft, BoundingBox b){
    
        courtDimensions = new Rectangle2D.Float(courtTopLeft.x, courtTopLeft.z, b.getXExtent() * 2, b.getZExtent() * 2);
    }
    
    public static boolean isInsideCourt(Vector3f pos){
        return courtDimensions.contains(pos.x, pos.z);
    }
    
        
    public static Rectangle2D getCourtDimensions(){
        return courtDimensions;
    }
    
    public static Vector3f getRandomHoopSidePosition(){
       Rectangle2D newRect =  new Rectangle2D.Double(courtDimensions.getMinX(), courtDimensions.getMinY(), courtDimensions.width/2, courtDimensions.height);
       return Conversions.generateRandomPosition(newRect);
    }
    
    public static Vector3f getRandomNonHoopSidePosition(){
       Rectangle2D newRect =  new Rectangle2D.Double(courtDimensions.getCenterX(), courtDimensions.getMinY(), courtDimensions.width/2, courtDimensions.height);
       return Conversions.generateRandomPosition(newRect);
        
    }
    
    public static Vector3f calculateGoalPosition(){
        goalPosition = new Vector3f((float)courtDimensions.getMinX() + 3f, 0, (float)courtDimensions.getCenterY());
        hoopLocation = goalPosition.add(Vector3f.UNIT_Y.mult(11.25f)).add(Vector3f.UNIT_X.mult(3));
        restartLocation = new Vector3f(courtDimensions.x + courtDimensions.width - 10, 0, courtDimensions.y + courtDimensions.height/2);
        return goalPosition; 
    }  
    
    public static Vector3f getHoopLocation(){
        //manual calc
        return hoopLocation;
    }
    
    public static Vector3f getRestartLocation(){
        return restartLocation;
    }
    
    
}
