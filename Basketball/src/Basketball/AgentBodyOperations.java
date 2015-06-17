/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.Main;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import com.jme3.math.Vector3f;
import VISIE.characters.Character;
import VISIE.models.BPNewModel;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Quaternion;
import java.util.ArrayList;

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
     private float headTurnSpeed = 0.25f; //degrees per frame
    private float torsoTurnSpeed = 0.5f; //degrees per frame
    private float walkSpeed = 3;
    private float runSpeed = 4;
    private float shuffleSpeed = 1.5f;
    private float dribbleSpeed = 2.5f;
    private float shootPenalty = 1;
    private float stealingCapability = 0.5f;
    private int turnCode = 0;
    
    private float modelHeadRotation = 0;

   // private float mutualGazeAngle;
    
    public AgentBodyOperations(BasketballAgent ba, AnimatedModel m){
        
        parentCharacter = ba;
        agentModel = m;
        headRotationAngle = 0;
        torsoRotationAngle = 0;
    }
    
    public void setBodyParameters(float[] params){
        headTurnSpeed = headTurnSpeed * params[0];
        torsoTurnSpeed = torsoTurnSpeed * params[1];
        walkSpeed = walkSpeed * params[2];
        runSpeed = runSpeed * params[3];
        shuffleSpeed = shuffleSpeed * params[4];
        dribbleSpeed = dribbleSpeed * params[5];
    }
    
    public float getStealingCapability(){
        return stealingCapability;
    }
    
    public void setStealingCapability(float f){
        stealingCapability = f;
    }
    
    public void turnHeadToTarget(Vector3f target){
        
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
        float angleOffset = Conversions.minDistanceBetweenAngles(targetAngle, this.getFacingDirection());
        float orientation = Conversions.calculateSpinDirection(targetAngle, this.getFacingDirection());
                
        if(angleOffset < 60 && parentCharacter.getLegAnimationName().startsWith("standing")){
            modelHeadRotation = angleOffset * orientation;
            agentModel.turnHead(modelHeadRotation);
        }
        else{
            this.resetHeadPosition();
        }
    }
    
    public void resetHeadPosition(){
                        
        if(modelHeadRotation != 0){
            agentModel.resetHead();
            modelHeadRotation = 0;
        }
    }
    
    
    public void turnTorsoToTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), target);
        if(Conversions.minDistanceBetweenAngles(targetAngle, torsoRotationAngle) > 5){
            float f = Conversions.calculateSpinDirection(targetAngle, torsoRotationAngle);
            float rotation = Conversions.adjustAngleTo360(torsoRotationAngle + (torsoTurnSpeed * -f * (Main.getFrameRate() * 200)));
            this.setFacingDirection(rotation);

            if(f < 0){
                turnCode = 1;
            }
            else{
                turnCode = 2;
            }
        }
        else{
            turnCode = 0;
        }

        if(parentCharacter.getSpeed() == 0){
            this.doTurningAnimation();
        }        
    }
    
    private void doTurningAnimation(){
                                        
        if(turnCode == 1){
            if(!parentCharacter.isInPossession()){
                parentCharacter.playAnimation(1, "turnLeft", 1, LoopMode.DontLoop);
            }
            parentCharacter.playAnimation(2, "turnLeft", 1, LoopMode.DontLoop);
        }
        else if(turnCode == 2){
            if(!parentCharacter.isInPossession()){
                parentCharacter.playAnimation(1, "turnRight", 1, LoopMode.DontLoop);
            }
            parentCharacter.playAnimation(2, "turnRight", 1, LoopMode.DontLoop);
        }
        else if(parentCharacter.getLegAnimationName().startsWith("step") ||
                parentCharacter.getLegAnimationName().startsWith("walk") ||
                parentCharacter.getLegAnimationName().startsWith("run")){   //stops any stepping or walking animations                                        
            parentCharacter.playAnimation(2, "standingPose", runSpeed/2, LoopMode.Loop);
        }
    }
    
    public void setFacingDirection(float rotation){ 
        
        headRotationAngle = rotation;
        torsoRotationAngle = rotation;
        agentModel.turnBody(rotation);
        
    }
    
    public void turnBodyToTarget(Vector3f target){
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
    
    private float calculateMovementSpeed(boolean farFromTarget, boolean isDribbling, boolean speedBurst){

        if(!isDribbling){
            if(!speedBurst){
                if(farFromTarget){
                    return runSpeed;
                }
                else{
                    return walkSpeed;
                } 
            }
            else{
                return runSpeed;
            }
        }
        else{
            if(speedBurst){
                return runSpeed;
            }
            else{
                return dribbleSpeed;
            }
        }

    }
    
    public void moveTowardsTarget(Vector3f target, boolean speedBurst, boolean isDribbling){ //agent has not yet reached target
                
        if(target.setY(0).distance(Court.getPoleLocation()) < 2f && parentCharacter.get2DPosition().distance(Court.getPoleLocation()) < 2f){
            parentCharacter.setSpeed(0);
        }
        //agent is looking straight at target
        else if(parentCharacter.perception.isLookingAtTarget(target)){
            
            this.turnBodyToTarget(target);
            Vector3f dir = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(parentCharacter.getPosition(), target)); 
            float speed;
            if(target.setY(0).distance(parentCharacter.getPosition().setY(0)) < 10){
                speed = this.calculateMovementSpeed(false, false, false);
                parentCharacter.setSpeed(speed);
            }
            else{
                speed = this.calculateMovementSpeed(true, false, false);
                parentCharacter.setSpeed(speed);
            }
            parentCharacter.move(dir, speed);
            
            if(!isDribbling){
                this.doMovingAnimations();
            }
        }
        //agent is in vicinity of target
       //only use stepping in game situation for defense
        else if(GameManager.getGameState() == 0 && 
                !(SceneCharacterManager.getCharacterInPossession() == null) &&
                parentCharacter.get2DPosition().distance(target.setY(0)) < 5 &&
                parentCharacter.getTeamID() != GameManager.getAttackingTeam()){
            
            if(parentCharacter.planner.defense.getMarkCharacter() != null){
                this.turnBodyToTarget(parentCharacter.planner.defense.getMarkCharacter().getPosition());
            } 
            
            parentCharacter.setSpeed(shuffleSpeed);
            Vector3f dir = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(parentCharacter.getPosition(), target)); 
            
            if(Court.isInsideCourt(parentCharacter.get2DPosition().add(dir.mult(parentCharacter.getSpeed())))){
                parentCharacter.move(dir, parentCharacter.getSpeed());
                this.doSteppingAnimations();
            }
        }
        //agent is not facing target and is far away from it
        //or not in game state
        else{
            this.turnBodyToTarget(target);
        }
    }
    
            
    public void doMovingAnimations(){
                            
        if(parentCharacter.getSpeed() > 0){ 
           if(parentCharacter.getSpeed() == runSpeed){
                parentCharacter.playAnimation(1, "run", 1, LoopMode.Loop);
                parentCharacter.playAnimation(2, "run", 1, LoopMode.Loop);
           }
           else{
                parentCharacter.playAnimation(1, "walk", 1, LoopMode.Loop);
                parentCharacter.playAnimation(2, "walk", 1, LoopMode.Loop);  
           }
        }
    }
    
    public void doDribblingAnimations(){
        if(parentCharacter.getSpeed() > 0){ 
           if(parentCharacter.getSpeed() == runSpeed){
                parentCharacter.playAnimation(2, "run", 1, LoopMode.Loop);
           }
           else{
                parentCharacter.playAnimation(2, "walk", 1, LoopMode.Loop);  
           }
        }
        parentCharacter.getModel().doDribbling(1);
    }
    
    public void doSteppingAnimations(){
                
        float fd = parentCharacter.getFacingDirection();
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), parentCharacter.planner.getTargetPosition());
        
        if(Conversions.minDistanceBetweenAngles(fd, targetAngle) > 120){
            parentCharacter.playAnimation(2, "stepBack", 1, LoopMode.Loop);
        }
        else{
            if(Conversions.calculateSpinDirection(fd, targetAngle) > 0){
                parentCharacter.playAnimation(2, "stepLeft", 1, LoopMode.Loop);
            }
            else{
                parentCharacter.playAnimation(2, "stepRight", 1, LoopMode.Loop);
            }
        }
    }
        
    public void doBlocking(){
        
        BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
        BasketballCharacter markCharacter = (BasketballCharacter)parentCharacter.planner.defense.getMarkCharacter();
        
                
        if(markCharacter != null){
              this.turnBodyToTarget(parentCharacter.planner.defense.getMarkCharacter().getPosition());

              if(markCharacter.equals(possessor)){     
                  parentCharacter.getModel().doBlocking(0.25f);
              }
              else{
                  parentCharacter.playAnimation(1, "standingPose", 1, LoopMode.Loop);
              }
        }
        
    }
    
    public float getFacingToTargetAngle(){
        
        float fd = parentCharacter.getFacingDirection();
        float targetAngle = Conversions.originToTargetAngle(parentCharacter.getPosition(), parentCharacter.planner.getTargetPosition()); 
        return Conversions.minDistanceBetweenAngles(fd, targetAngle) * Conversions.calculateSpinDirection(fd, targetAngle);
        
    }
    
    public void blockingTransition(){
        
        BPNewModel bm = parentCharacter.getModel();
        
        if(bm.getCurrentAnimation(1).equals("blockLoop")){
           if(bm.hasAnimationFinished(1)){
               parentCharacter.playAnimation(1, "postBlock", 0.25f, LoopMode.DontLoop);
           }
           else{
      //         bm.setAnimationSpeed(1, 2);
           }
        }
        else if(bm.getCurrentAnimation(1).equals("postBlock")){
           if(bm.hasAnimationFinished(1)){
               parentCharacter.playAnimation(1, "standingPose", 1, LoopMode.DontLoop);
           }            
        }
        else{
            parentCharacter.playAnimation(1, "standingPose", 1, LoopMode.DontLoop);
        }
        
//        if(!(parentCharacter.getSpeed() > 0)){
//           parentCharacter.playAnimation(2, "standingPose", 1, LoopMode.DontLoop);
//        }
//        
    }
    
    public void setSpeed(String speedType, float multiplier){
        
        if(speedType.equals("Walk")){
            walkSpeed = walkSpeed * multiplier;
        }
        else if(speedType.equals("Turn")){
            headTurnSpeed = headTurnSpeed * multiplier;
            torsoTurnSpeed = torsoTurnSpeed * multiplier;
        }
        else if(speedType.equals("Run")){
            runSpeed = runSpeed * multiplier;
        }
    
    }
    
    public void setShootPenalty(float f){
        shootPenalty = f;
    }
    
    public float getShootPenalty(){
        return shootPenalty;
    }
    
}
