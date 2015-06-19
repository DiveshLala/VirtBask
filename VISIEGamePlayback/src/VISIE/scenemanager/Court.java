/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;

import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class Court {
    
    private static Rectangle2D.Float courtDimensions;
    private static Vector3f goalPosition;
    private static Vector3f hoopLocation;
    private static ArrayList<Rectangle2D> courtSections;
    
    public static void initialiseCourtDimensions(Vector3f courtTopLeft, BoundingBox b){
        courtDimensions = new Rectangle2D.Float(courtTopLeft.x, courtTopLeft.z, b.getXExtent() * 2, b.getZExtent() * 2);
        
        courtSections = new ArrayList<Rectangle2D>();
        
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                courtSections.add(new Rectangle2D.Float(courtDimensions.x + (i*courtDimensions.width/3), courtDimensions.y + (j*courtDimensions.height/3), courtDimensions.height/3, courtDimensions.width/3));
            }
            
        }
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
        return goalPosition; 
    }  
    
    public static Vector3f getHoopLocation(){
        //manual calc
        return hoopLocation;
    }
    
    public static Vector3f getGoalPosition(){
        return goalPosition;
    }
    
    public static ArrayList<Rectangle2D> getCourtSections(){
        return courtSections;
    }
    
    public static String getPlayerArea(BasketballCharacter bc){
        int section = getCourtSection(bc.get2DPosition());
        return getHorizontalCourtArea(section) + " " + getVerticalCourtArea(section);
    }
    
    public static int getCourtSection(Vector3f pos){
        
        for(int i = 0; i < courtSections.size(); i++){
            if(courtSections.get(i).contains(pos.x, pos.z)){
                return i;
            }        
        }
        
        return -1;
    }
    
    
    //returns type of area
    //areas are: front, mid, back court (horizontal to goal)
    public static String getHorizontalCourtArea(int section){
        
        
        if(section < 0){
            return "out";
        }
        else if(section <= 2){
            return "front";
        }
        else if(section <= 5){
            return "mid";
        }
        else{
            return "back";
        }
        
    }
    
    
    //returns type of area
    // areas are: left, centre, right court (vertical to goal)
    public static String getVerticalCourtArea(int section){
        
        switch(section){
            case 0: return "right";
            case 1: return "centre";
            case 2: return "left";
            case 3: return "right";
            case 4: return "centre";
            case 5: return "left";
            case 6: return "right";
            case 7: return "centre";
            case 8: return "left";
            default: return "out";
        }
    }
    
    public static Vector3f getCentreCoordinate(String areaDescription){
        
        Vector3f coordinate;
        int index = getAreaIndex(areaDescription);
        
        coordinate = new Vector3f((float)courtSections.get(index).getCenterX(), 0, (float)courtSections.get(index).getCenterY());
        return coordinate;
    }
    
    public static Vector3f getRandomCoordinateInArea(String areaDescription){
        
        int index = getAreaIndex(areaDescription);
        Rectangle2D area = courtSections.get(index);
        
        return Conversions.generateRandomPosition(area).setY(0);
    }
    
    public static int getAreaIndex(String areaDescription){
        
        if(areaDescription.equals("back left")){
            return 8;
        }   
        else if(areaDescription.equals("back centre")){
            return 7;
        }  
        else if(areaDescription.equals("back right")){
            return 6;
        } 
        else if(areaDescription.equals("mid left")){
            return 5;
        } 
        else if(areaDescription.equals("mid centre")){
            return 4;
        } 
        else if(areaDescription.equals("mid right")){
            return 3;
        } 
        else if(areaDescription.equals("front left")){
            return 2;
        } 
        else if(areaDescription.equals("front centre")){
            return 1;
        } 
        else if(areaDescription.equals("front right")){
            return 0;
        } 
        else{
            return -1;
        }
    } 
    
    public static ArrayList<String> getAdjacentAreas(String inputArea){
        ArrayList<String> candidates = new ArrayList<String>();
        
        if(inputArea.equals("back left")){
            candidates.add("mid left");
            candidates.add("mid centre");
        }
        else if(inputArea.equals("back centre")){
            candidates.add("mid left");
            candidates.add("mid centre");
            candidates.add("mid right");
        
        }
        else if(inputArea.equals("back right")){
            candidates.add("mid centre");
            candidates.add("mid right");
        }
        else if(inputArea.equals("mid left")){
            candidates.add("front left");
            candidates.add("front centre");
            candidates.add("mid centre");
        
        }
        else if(inputArea.equals("mid centre")){
            candidates.add("mid left");
            candidates.add("mid right");
            candidates.add("front left");
            candidates.add("front centre");
            candidates.add("front right");
        }
        else if(inputArea.equals("mid right")){
            candidates.add("front right");
            candidates.add("front centre");
            candidates.add("mid centre");
        }
        else if(inputArea.equals("front left")){
            candidates.add("mid left");
            candidates.add("front centre");
            candidates.add("mid centre");
        }
        else if(inputArea.equals("front centre")){
            candidates.add("front right");
            candidates.add("front left");
            candidates.add("mid centre");
            candidates.add("mid right");
            candidates.add("mid left");
        }
        else if(inputArea.equals("front right")){
            candidates.add("mid right");
            candidates.add("front centre");
            candidates.add("mid centre");
        }
        return candidates;
    }

}
