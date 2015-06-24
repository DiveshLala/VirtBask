/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import Basketball.GameManager;
import Basketball.UnCoopPlanner;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author DiveshLala
 */
public class SkilledAgent extends BasketballAgent{
    
    public SkilledAgent(int i, BasketballPlayerModel am, RigidBodyControl p, float r, float height){
        super(i, am, p, r, height);
        float[] bodyParams = {1.5f,1.5f,1.5f,1.5f,1.5f,1.75f};
        abo.setBodyParameters(bodyParams);
        abo.setShootPenalty(0.25f);
        planner = new UnCoopPlanner(this);
    }
    
    @Override
    public void updateMovements(){ 
          
        int behaviorState = behaviorModule.getBehaviorState();   
        
        BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
            //animations are according to agent state
                        
            if(behaviorState == 0){             //dribbling
                
                if(!planner.isTargetReached(0.5f)){ //target not reached    
                                        
                    if(characterModel.getCurrentAnimation(1).contains("block")){
                        this.abo.blockingTransition();
                    }
                    else if(GameManager.getGameState() != 0){
               //         abo.moveTowardsTarget(planner.getTargetPosition());
                    }
                    else{
                 //       abo.dribbleTowardsTarget(planner.getTargetPosition(), false, false);
                //        abo.doDribbling();
                    }
                }
                else{                           //stop
                    agentModel.noArmGesture();
                    agentModel.standStill();
                    this.setSpeed(0);
                }
            }
            
            else if(behaviorState == 1){  //no ball running
                
                if(!planner.isTargetReached(0.5f)){ //target reached
//                    abo.moveTowardsTarget(planner.getTargetPosition()); 
                }
                else{                           //stop
                    agentModel.standStill();
                    
                    //transitions block to standingPose
                    if(characterModel.getCurrentAnimation(1).equals("blockLoop")){
                        this.abo.blockingTransition();
                    }
                    else{
                        agentModel.noArmGesture();
                    }
                    this.setSpeed(0);
                    
                    BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
                    
                    if(possessor != null){
                        
                        if(GameManager.getGameState() == 0){
                            
                            if(this.getTeamID() != GameManager.getAttackingTeam()){
                                abo.turnBodyToTarget(planner.getClosestOpponent().getPosition());
                            }
                            else{
                                abo.turnBodyToTarget(Court.getHoopLocation());
                            }
//                            abo.doTurningAnimation();
                        }
                        else if(GameManager.getGameState() == 2){
                            abo.turnBodyToTarget(ball.getBallPosition());
                        }
                    }
                }
            }
            
            else if(behaviorState == 2){            //shooting
                if(perception.isLookingAtTarget(Court.getHoopLocation())){
                    model.playArmAnimation("shootAction", 2f, LoopMode.DontLoop);
                    model.playLegAnimation("standingPose", 1, LoopMode.Loop);
                }
                else{
                    abo.turnBodyToTarget(Court.getHoopLocation());
                }
            }
            
            else if(behaviorState == 3){        //defending
                if(!planner.isTargetReached(0.5f)){ //target reached
//                    abo.moveTowardsTarget(planner.getTargetPosition());                            
                }
                else{
                    agentModel.standStill();
                }
                    abo.doBlocking();
            }
            else if(behaviorState == 4){ // passing
                Character c = planner.getMyClosestTeamMate();
                this.doTurnAndPass(c);
            }
            else if(behaviorState == 5){ // look for player and then pass
                
                Character c = planner.getMyClosestTeamMate();
                if(!perception.isWithinGaze(c.getPosition(), 10f)){         
                    model.playArmAnimation("initiatePass", 1, LoopMode.DontLoop);
                    model.playLegAnimation("standingPose", 1, LoopMode.Loop);
                    abo.turnBodyToTarget(c.getPosition());
//                    abo.doTurningAnimation();
                }
                else{
                    this.doTurnAndPass(c);
                }
            }
     //   }
    }
    
//    public void updateBallPossession(Ball b, Vector3f vec){
//        super.updateBallPossession(b, vec);
//    }
    
    @Override
    public Vector3f doStalemateActivity(){
        planner.possession.doGreedyStalemate();
        return Vector3f.ZERO;
    }
    
}
