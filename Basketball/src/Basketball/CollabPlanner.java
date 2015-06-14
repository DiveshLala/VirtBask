/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.CollabAgent;
import VISIE.characters.CollabLearner;
import VISIE.learning.ImitationLearner;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class CollabPlanner extends AgentPlanning {
    
    ImitationLearner imitationLearner;
    
    public CollabPlanner(CollabAgent ca){
        super(ca);
        
        if(ca.isLearner()){
            imitationLearner = new ImitationLearner(this);
        }
    }
    
    
    @Override
    public void decidePossessionAction(int behaviorState){

        if(behaviorState == 0){//has ball

            parentCharacter.setActionState(0); //dribbling to target 
            CollabAgent ca = (CollabAgent)parentCharacter;
            ca.recordActions("nonpos");
            if(ca.isLearner() && !ca.isDoingJP()){
                CollabLearner cl = (CollabLearner)ca;
                imitationLearner.decideNextAction(cl.getActionRecorder().getActionChanges(), "pos");
            }
            else{
                possession.makeCooperativePossessionDecision();
            }
        }
        
        else if(behaviorState == 2){//shooting
            parentCharacter.setActionState(2); //shooting
        }
    }
    
    @Override
    public void makeNonPossessionAttackDecision(){
        
        CollabAgent ca = (CollabAgent)parentCharacter;
        ca.recordActions("pos");
        
        if(ca.isLearner() && !ca.isDoingJP()){
            CollabLearner cl = (CollabLearner)ca;
            imitationLearner.decideNextAction(cl.getActionRecorder().getActionChanges(), "nonpos");
        }
        
        else{
            nonPossession.findFreeSpace();

            if(this.calculatePassRequirement()){
                ca.setJointProject("getAttention", SceneCharacterManager.getCharacterInPossession(), true);
            }
            parentCharacter.setBehaviorState(1);
  
        }
    }
     
    //checks if in position for a pass, and if so, calls for it
     private boolean calculatePassRequirement(){
         
         BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
         
         return(parentCharacter.get2DPosition().distance(possessor.get2DPosition()) < 25 &&
            parentCharacter.perception.isFacingEachOther(possessor, 70f) &&
            !parentCharacter.perception.isFacingEachOther(possessor, 40) &&
            this.distOfClosestOpponent() > 5f);
     }
     
     @Override
     public void decideNonPossessionAttackAction(int behaviorState){  
            parentCharacter.setActionState(1);   
            this.makeNonPossessionAttackDecision();
    }
     
     @Override
     public void doAfterScoreActivity(){
      
         if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){//do celebrations
             CollabAgent ca = (CollabAgent)parentCharacter;
             
             if(!ca.isDoingJP()){         
                BasketballCharacter bc = (BasketballCharacter)parentCharacter.planner.getMyClosestTeamMate();
                ca.setJointProject("celebration", bc, false);
             }
         }
         else if(parentCharacter.getTeamID() != GameManager.getAttackingTeam()){//team has to take ball to start location
             
             if(parentCharacter.isInPossession()){
                 GameManager.setPossessionSwapState();
             }
            else if(SceneCharacterManager.getCharacterInPossession() == null){//if no one is in possession, retrieve ball
                 if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
                        this.setTargetPosition(Court.getRandomHoopSidePosition());
                 }
                 else{
                     if(this.isClosestToBall(parentCharacter.getTeamMates())){
                         this.setTargetPosition(ball.getBallPosition());
                     }
                     else{
                         if(this.isTargetReached(2f)){
                            this.setTargetPosition(Court.getRandomNonHoopSidePosition());
                         }
                     }
                 }
            }
           else{  //go to random position
             if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
                 if(this.isTargetReached(2f)){
                    this.setTargetPosition(Court.getRandomHoopSidePosition());
                 }
             }
             else{
                 if(this.isTargetReached(2f)){
                    this.setTargetPosition(Court.getRandomNonHoopSidePosition());
                 }
             }
            }

         }
     }
     
    @Override
    public void swapRoleActivity(){//ball in hand, swap roles
                                 
         boolean isTargetHoopSide = Court.pointIsHoopSide(targetPosition);
         
         if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
             if(!isTargetHoopSide){
                  this.setTargetPosition(Court.getRandomHoopSidePosition());
             }
         }
         else{
            if(isTargetHoopSide){
                 this.setTargetPosition(Court.getRandomNonHoopSidePosition());
             }
         }
         
         if(parentCharacter.isInPossession()){
             parentCharacter.setBehaviorState(0);
             this.setTargetPosition(Court.getRestartLocation());
             if(this.isTargetReached(0.5f)){
                 GameManager.setAttackingTeam(parentCharacter.getTeamID());
                 GameManager.setInState();
             }
         }
         else{
             CollabAgent ca = (CollabAgent)parentCharacter;
             boolean apologyDone = ca.getLatestCompletedJointProject().equals("apology") && ca.getTimeSinceLatestJP() < 20;
             boolean isCelebrating = parentCharacter.getCurrentAnimations().toLowerCase().contains("celeb");
             
             if(ball.getPenultimateTouch() == parentCharacter.getID() && !apologyDone && !isCelebrating){
                if(!ca.isDoingJP()){
                    BasketballCharacter bc = (BasketballCharacter)parentCharacter.planner.getMyClosestTeamMate();
                    ca.setJointProject("apology", bc, false);
                }
                 
             }
             else{
                parentCharacter.setBehaviorState(1);
                if(this.isTargetReached(2f)){
                    parentCharacter.abo.turnBodyToTarget(ball.getBallPosition());
       //             parentCharacter.abo.doTurningAnimation();
                }
             }
         }
     }
    
    @Override
    public void doOutOfBoundActivity(){

        
        boolean isTargetHoopSide = Court.pointIsHoopSide(targetPosition);
        
        if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
             if(!isTargetHoopSide){
                  this.setTargetPosition(Court.getRandomHoopSidePosition());
             }
         }
         else{
            if(isTargetHoopSide){
                    this.setTargetPosition(Court.getRandomNonHoopSidePosition());
             }
        }
                    
        if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
            
             CollabAgent ca = (CollabAgent)parentCharacter;
            
             boolean encouragementDone = ca.getLatestCompletedJointProject().equals("encourage") && ca.getTimeSinceLatestJP() < 20;
             
             if(!encouragementDone){
                if(!ca.isDoingJP()){
                    BasketballCharacter bc = (BasketballCharacter)parentCharacter.planner.getMyClosestTeamMate();
                    ca.setJointProject("encourage", bc, false);
                }
             }         
        }
        else{
            if(this.isClosestToBall(parentCharacter.getTeamMates())){
                if(parentCharacter.isInPossession()){
                    this.setTargetPosition(Court.getRestartLocation());
                }
                else{
                    this.setTargetPosition(ball.getBallPosition());
                }
            }
        }
     }
    
    public CollabAgent getParentCharacter(){
        return (CollabAgent)parentCharacter;
    }
    
    public void generateSignalContext(){
                
    }
    
//    @Override
//    public void clearRecords(){ 
//        CollabAgent ca = (CollabAgent)parentCharacter;
//        ca.getActionRecorder().clearRecords();
//    }
       
}
