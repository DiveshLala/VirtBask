/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballPlayer;
import VISIE.characters.Player;
import VISIE.characters.Character;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
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
    String currentDynamicGesture = "";
    Ball ball;
    
    public PlayerGesture(Player player, AnimatedModel am, Ball b){
        p = player;   
        characterModel = am;
        ball = b;
    }
    
    private void playPredefinedGestures(){ 
//         BasketballPlayerModel bm = (BasketballPlayerModel) characterModel;
//         String currentGest = characterModel.getCurrentAnimation(1);
//         
//         if(!p.isInPossession()){
//             if(currentGest.equals("shootAction") && bm.hasAnimationFinished(1)){
//                 p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
//             }
//             else if(currentGest.equals("passAction") && bm.hasAnimationFinished(1)){
//                 p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
//             }
//             else if(currentGest.equals("callForPass")){
//                 if(bm.hasAnimationFinished(1)){
//                    p.playAnimation(1, "standingPose", 1, LoopMode.Loop);
//                 }
//                 else if(bm.getCurrentAnimationTimePercentage(1) < 0.75){
//                    Character c = this.getGestureTarget();
//                    ArrayList<Character> list = new ArrayList<Character>();
//                    list.add(c);
//                    if(c instanceof VISIE.characters.BasketballAgent){
//                        BasketballAgent ba = (BasketballAgent)c;
//                        if(ba.isInPossession()){
//                            ba.receiveJPPerceptionSignal(p, jp);
//                            ba.receiveSharedProjectInfo(jp.getSharedID(), "callForPass", 2);
//                        }
//                    }                      
//                 }
//             }   
//         }
//         else if(currentGest.equals("shootAction")){
//             if(bm.isBallShot()){
//                 if(ball.shootBall()){
//                    p.removePossession();
//                 }
//             }
//         }
//         else if(currentGest.equals("passAction")){
//            Character c = this.getGestureTarget();
//            ArrayList<Character> list = new ArrayList<Character>();
//            list.add(c);
//            if(c instanceof VISIE.characters.BasketballAgent){
//                  BasketballAgent ba = (BasketballAgent)c;
//                
//                  JointProject jp = new JointProject("pass to player", 0, list, p);
//                  ba.receiveJPPerceptionSignal(p, jp);
//                  if(characterModel.getCurrentAnimationTimePercentage(1) > 0f){
//                        ba.receiveSharedProjectInfo(jp.getSharedID(), "passAction", 2);
//                  } 
//            }
//             
//             if(bm.isBallPassed()){
//                 float angleOfPass = p.getFacingDirection();
//                 ball.passBall(angleOfPass);
//                 p.removePossession();
//             }
//         }
//         else if(currentGest.equals("acceptPass")){
//             p.playAnimation(1, "postReceivePass", 1, LoopMode.DontLoop);
//         }
//         else if(currentGest.equals("initiatePass")){
//            Character c = this.getGestureTarget();
//            ArrayList<Character> list = new ArrayList<Character>();
//            list.add(c);
//            if(c instanceof VISIE.characters.BasketballAgent){
//                 BasketballAgent ba = (BasketballAgent)c;
//                
//                  JointProject jp = new JointProject("pass proposal", 0, list, p);
//                  ba.receiveJPPerceptionSignal(p, jp);
//                  if(characterModel.getCurrentAnimationTimePercentage(1) > 0.5f){
//                        ba.receiveSharedProjectInfo(jp.getSharedID(), "initiatePass", 2);               
//                  } 
//            }
//         }
//         
//         if(!currentGest.equals("standingPose")){
//             currentDynamicGesture = "";
//         }
    }
    
    private void playDynamicGestures(){
//        if(currentDynamicGesture.equals("Point to goal")){
//            this.pointToGoal();
//            
//            Character c = this.getGestureTarget();
//            ArrayList<Character> list = new ArrayList<Character>();
//            list.add(c);
//            if(c instanceof VISIE.characters.BasketballAgent){
//                 BasketballAgent ba = (BasketballAgent)c;
//                
//                 if(ba.getBehaviorState() != 2){
//                      JointProject jp = new JointProject("point to goal", 0, list, p);
//                      ba.receiveJPPerceptionSignal(p, jp);
//                    //  if(characterModel.getCurrentAnimationTimePercentage(1) > 0.5f){
//                            ba.receiveSharedProjectInfo(jp.getSharedID(), "point to goal", 2);               
//                    //  } 
//                 }
//            }
//            
//        }
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
     
     public void updateGestures(){
            this.playPredefinedGestures();
            this.playDynamicGestures();
     }
     
     public void setDynamicGesture(String animationName){
         if(animationName.equals(currentDynamicGesture) && this.isDynamicGestureComplete()){
             characterModel.removeUserControl();
             currentDynamicGesture = "";
         }
         else{
             currentDynamicGesture = animationName;
         }
     }
     
     public boolean isDynamicGestureComplete(){
         if(currentDynamicGesture.equals("Point to goal")){
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
     
     
}
