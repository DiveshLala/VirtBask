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
import VISIE.characters.CollabAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
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
    Ball ball;
    protected int passingJPID = -1;
    
    
    public PlayerGesture(Player player, AnimatedModel am, Ball b){
        p = player;   
        characterModel = am;
        ball = b;
    }
    
    private void playPredefinedGestures(){
         
        BPNewModel bm = (BPNewModel) characterModel;
        String currentGest = characterModel.getCurrentAnimation(1);
         
         
         if(!p.isInPossession()){
             this.doNonPossessionGestures();                         
         }
         else if(currentGest.contains("shoot") && bm.isBallShot()){
             this.doShooting();
         }

         else if(currentGest.equals("pass")){
             this.doPassGesture();
         }
         else if(currentGest.equals("acceptPass")){
             p.playAnimation(1, "postReceivePass", 1, LoopMode.DontLoop);
         }
         else if(currentGest.equals("initiatePass")){
            Character c = this.getGestureTarget();
            ArrayList<Character> list = new ArrayList<Character>();
            list.add(c);
         }
       
   }
    
    private void doNonPossessionGestures(){
        
         BPNewModel bm = (BPNewModel) characterModel;
         String currentGest = characterModel.getCurrentAnimation(1);
        
        if(currentGest.contains("shoot") && bm.hasAnimationFinished(1)){
            p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
        }
        else if(currentGest.startsWith("pass") && bm.hasAnimationFinished(1)){
            p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
            passingJPID = -1;
        }
        else if(currentGest.contains("callForPass")){
            if(bm.hasAnimationFinished(1)){
               p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
            }
        }   
        else if(p.getSpeed() > 0){
            p.playAnimation(1, "walk", 1, LoopMode.Loop);
        }
        else{
            if(!currentGest.contains("shoot") && 
               !currentGest.startsWith("pass") &&
               !currentGest.startsWith("receivePass")){
               String legGesture = p.getCurrentGesture(2);
               p.playAnimation(1, legGesture, 1, LoopMode.Loop);
            }
        }
    }
    
    private void doPassGesture(){
        
        BPNewModel bm = (BPNewModel)characterModel;     
        Character c = this.getPassTarget();
        ArrayList<Character> list = new ArrayList<Character>();
        list.add(c);

         if(bm.isBallPassed()){
             if(c != null){
                 if(p.canPass((BasketballCharacter)c)){
                    ball.passBall(p, c);
                 }
                 else{
                    ball.passBall(p.getFacingDirection());
                 }
             }
             else{
                 ball.passBall(p.getFacingDirection());
             }
             p.removePossession();
         }
        
    }
    
    private void doShooting(){
        
        if(ball.isValidShoot()){
            p.removePossession();
            p.recordShootContexts();
        }            
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
     
     protected Character getPassTarget(){
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
         
         if(minAngle < 30){
             return targetedCharacter;
         }
         else{
             return null;
         }
     }
     
     public void updatePredefinedGestures(){
            this.playPredefinedGestures();
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
       
     public boolean areArmsRaised(){
         
         Vector3f lVecSum = characterModel.getBoneVectorOfModel("left shoulder", "left elbow").add(characterModel.getBoneVectorOfModel("left elbow", "left hand"));
         Vector3f rVecSum = characterModel.getBoneVectorOfModel("right shoulder", "right elbow").add(characterModel.getBoneVectorOfModel("right elbow", "right hand"));
         
         return (lVecSum.getY() > 0.1f || rVecSum.getY() > 0.1f);
     }
     
}
