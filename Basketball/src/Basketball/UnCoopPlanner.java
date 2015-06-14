/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.CollabAgent;
import VISIE.characters.SkilledAgent;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;

/**
 *
 * @author DiveshLala
 */
public class UnCoopPlanner extends AgentPlanning {
    
    public UnCoopPlanner(BasketballAgent ba){
        super(ba);
    }
    
    @Override
    public void decidePossessionAction(int behaviorState){

        if(behaviorState == 0){//has ball
            parentCharacter.setActionState(0); //dribbling to target 
            possession.makeGreedyPossessionDecision();
        }
        
        else if(behaviorState == 2){//shooting
            parentCharacter.setActionState(2); //shooting
        }
    }
    
    @Override
    public void makeFreeBallDecision(int behaviorState){
        
        //if characters are shooting or passing, do nothing
        if(!parentCharacter.isShooting() && !parentCharacter.isPassing()){
            parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);
        }
        else if(parentCharacter.isShooting() && parentCharacter.getCurrentMovementProgress(1) > 0.8){
            parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);  
        }
        else if(parentCharacter.isPassing() && parentCharacter.getCurrentMovementProgress(1) > 0.5){
            parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);
        }
        
        if(ball.getBallPosition().setY(0).distance(parentCharacter.get2DPosition()) < 5){
            this.setTargetPosition(ball.getBallPosition());       
        }
        
        else{
            Vector3f predictedBallPos = ball.getBallPosition().add(ball.getBallTravellingDirection().normalize().mult(5));
            this.setTargetPosition(predictedBallPos);
        }

    }
    
    @Override
    public void doAfterScoreActivity(){
         
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
             GameManager.setPossessionSwapState();
         }
         else if(SceneCharacterManager.getCharacterInPossession() == null){
             if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
                 this.setTargetPosition(Court.getRandomHoopSidePosition());
             }
             else{
                 this.setTargetPosition(ball.getBallPosition());
             }
         }
         else{
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
    @Override
    public void decideDefenseAction(int behaviorState){
           parentCharacter.setActionState(1);  
     //      defense.makeGreedyDefenseDecision();
    }

    
}
