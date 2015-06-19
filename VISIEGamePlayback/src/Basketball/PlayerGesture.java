/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballPlayer;
import VISIE.characters.Player;
import VISIE.characters.Character;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.animation.LoopMode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.plugins.ogre.SceneLoader;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class PlayerGesture {
    
    Player p;
    AnimatedModel characterModel;
    String currentKinectGesture = "";
    Ball ball;
    private int passingJPID = -1;
    
    public PlayerGesture(Player player, AnimatedModel am, Ball b){
        p = player;   
        characterModel = am;
        ball = b;
    }
    
        
    private void pointToGoal(){
        
        Vector3f armBoneVec = characterModel.getWorldCoordinateOfJoint("right hand").subtract(characterModel.getWorldCoordinateOfJoint("right shoulder"));
        Vector3f vecToHoop = Court.getHoopLocation().subtract(p.getShoulderPosition(1));
        
        vecToHoop.normalizeLocal();
        armBoneVec.normalizeLocal();
        

        Vector3f fd = Conversions.degreesToNormalizedCoordinates(p.getFacingDirection());
        Vector3f x = characterModel.convertWorldToModelVector(vecToHoop, fd);
        x.setZ(-x.getZ());
        x.setX(-x.getX());
        
       characterModel.moveSingleJoint("ERSR", x);
       
       
    }
    
     private Character getGestureTarget(){
       Character targetedCharacter = null;
       float minAngle = 100000000f;
       
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray(); 
             for(int i = 0 ; i < c.size(); i++){
                 Character testChar = c.get(i);
                 if(p.getID() != testChar.getID()){
                     float targetAngle = Conversions.originToTargetAngle(p.getPosition(), testChar.getPosition());
                     float testAngle = Conversions.minDistanceBetweenAngles(p.getFacingDirection(), targetAngle);
                     if(testAngle < minAngle && testAngle < 50f){
                         targetedCharacter = testChar;
                         minAngle = testAngle;
                     }
                 }
             }   
         return targetedCharacter;    
     }
     
     private Character getPassTarget(){
        Character targetedCharacter = null; 
        float minAngle = 100000000f;
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray(); 
        
         for(int i = 0 ; i < c.size(); i++){
             BasketballCharacter testChar = (BasketballCharacter)c.get(i);
             if(p.getID() != testChar.getID() && p.getTeamID() == testChar.getTeamID()){
                 float targetAngle = Conversions.originToTargetAngle(p.getPosition(), testChar.getPosition());
                 float testAngle = Conversions.minDistanceBetweenAngles(p.getFacingDirection(), targetAngle);
                 if(testAngle < minAngle && testAngle < 50f){
                     targetedCharacter = testChar;
                     minAngle = testAngle;
                 }
             }
         }
         
         if(minAngle < 10){
             return targetedCharacter;
         }
         else{
             return null;
         }
     }
     
     
     public void setKinectGesture(String animationName){
           currentKinectGesture = animationName;
     }
     
     public boolean isDynamicGestureComplete(){
         if(currentKinectGesture.equals("Point to goal")){
             Vector3f vecToHoop = Court.getHoopLocation().subtract(p.getShoulderPosition(1));
             return checkPointCompletion(vecToHoop, 1);
         }
         
         return false;       
     }
     
     private boolean checkPointCompletion(Vector3f target, int hand){
         
         Vector3f armBoneVec;
         
         if(hand == 0){
             armBoneVec = characterModel.getWorldCoordinateOfJoint("left hand").subtract(characterModel.getWorldCoordinateOfJoint("left shoulder"));
         }
         else{
             armBoneVec = characterModel.getWorldCoordinateOfJoint("right hand").subtract(characterModel.getWorldCoordinateOfJoint("right shoulder"));
         }
         
         target.normalizeLocal();
         armBoneVec.normalizeLocal();
         
         return target.distance(armBoneVec) < 0.1f;
     }
     
     public String getKinectGesture(){
         return currentKinectGesture;
     }
     
     
}
