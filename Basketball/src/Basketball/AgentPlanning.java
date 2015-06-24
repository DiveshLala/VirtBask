/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.characters.SkilledAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import com.jme3.math.Vector3f;

/**
 *
 * @author DiveshLala
 */
public class AgentPlanning {
    
    BasketballAgent parentCharacter;
    Vector3f targetPosition;
    Ball ball;
    DefenseDecision defense;
    public NonPossessionDecision nonPossession;
    public PossessionDecision possession;
    
    public AgentPlanning(BasketballAgent ba){
        parentCharacter = ba;
        defense = new DefenseDecision(ba);
        nonPossession = new NonPossessionDecision(ba);
        possession = new PossessionDecision(ba);
    }
    
    public void decidePossessionAction(int behaviorState){

        if(behaviorState == 0){//has ball
            parentCharacter.setActionState(0); //dribbling to target
        //    possession.makePossessionDecision();
        }
        
        else if(behaviorState == 2){//shooting
            parentCharacter.setActionState(2); //shooting
        }
        
        possession.makePossessionDecision();
    }
    
    public void decideNonPossessionAttackAction(int behaviorState){  
        
  //      if(behaviorState == 1){
            parentCharacter.setActionState(1);   
            this.makeNonPossessionAttackDecision();
     //   }           
    }
    
    public void decideDefenseAction(int behaviorState){
      
    //   if(behaviorState == 1){//no ball
           parentCharacter.setActionState(1);  
           
           if(parentCharacter.getDefenseStrategy().equals("markPossessor")){
               defense.markPossessor();
           }
           else{
                defense.manToManMarking();
           }
   //    }
       
    }
    
    public static Vector3f calculateRandomPosition(){
           Vector3f newPos = Conversions.generateRandomPosition(Court.getCourtDimensions());
           newPos.y = 4;
           return newPos;
    }
        
    //for continuous incoming target positions
    public void setTargetPosition(Vector3f target){
        Vector3f realTarget = parentCharacter.perception.adjustTargetForCollisions(target, false);
        targetPosition = realTarget.setY(0);
    }
    
    // ignore collisions and go to target
    public void setTargetPositionIgnoreCollision(Vector3f target){
        targetPosition = target.setY(0);
    }
    
    //for current target positions
    public void updateCurrentTargetPosition(){
        Vector3f realTarget = parentCharacter.perception.adjustTargetForCollisions(targetPosition, false);
        targetPosition = realTarget.setY(0);       
    }
    
    public void setCharacterAsTarget(BasketballCharacter bc, float range){
        
        Vector3f vec = bc.get2DPosition();
        if(parentCharacter.get2DPosition().distance(vec) > range){
            this.setTargetPosition(bc.get2DPosition());
        }
        else{
            this.setTargetPosition(parentCharacter.getPosition());
        }    
    }
    
    public void setGoalAsTarget(float range){
        Vector3f vec = Court.getGoalPosition();
        if(parentCharacter.get2DPosition().distance(vec) > range){
            this.setTargetPosition(Court.getGoalPosition());
        }
        else{
            this.setTargetPosition(parentCharacter.getPosition());
        }
    }
    
    public Vector3f getTargetPosition(){
     //   System.out.println("tp is " + targetPosition);
        return targetPosition;
    }
    
    public boolean isTargetReached(float threshold){
        float targetAccuracy = threshold;
        return (this.getTargetPosition().distance(parentCharacter.get2DPosition())) < targetAccuracy;
    }

    
    public void makeNonPossessionAttackDecision(){
        nonPossession.findFreeSpace();
    }
    
       
    public void makeFreeBallDecision(int behaviorState){
        
        
        //if characters are shooting or passing, do nothing
        if(parentCharacter.abo.isCoolDown()){
            parentCharacter.setBehaviorState(1);
        }
        
        //go to ball if close
        if(ball.getBallPosition().setY(0).distance(parentCharacter.get2DPosition()) < 5){
            this.setTargetPosition(ball.getBallPosition());       
        }
        else{
            Vector3f predictedBallPos = ball.getBallPosition().add(ball.getBallTravellingDirection().normalize().mult(5));
            
            //if closest, go to ball
            if(this.isClosestToLocation(parentCharacter.getTeamMates(), predictedBallPos)){
                this.setTargetPosition(predictedBallPos);
            }
            else if(ball.isBeingShot()){
                this.setTargetPosition(Court.getGoalPosition());
            }
            else{
                this.setTargetPosition(parentCharacter.getPosition());
                parentCharacter.abo.turnBodyToTarget(ball.getBallPosition());
            }
        }
        this.updateCurrentTargetPosition();
    }
    
    public void setBall(Ball b){
        ball = b;
    }
    
    private boolean isFree(){
        return true;
    }
    
    protected boolean isClosestToHoop(ArrayList<BasketballCharacter> group){
        float distToHoop = parentCharacter.get2DPosition().distance(Court.getGoalPosition());
        
        for(int i = 0; i < group.size(); i++){
            float d = group.get(i).get2DPosition().distance(Court.getGoalPosition());
            if(d < distToHoop){
                return false;
            }
        } 
        
        return true;
    }
    
    protected boolean isCloserThanPlayer(BasketballCharacter player, Vector3f location){
        float distToLoc = parentCharacter.get2DPosition().distance(location);        
        return player.get2DPosition().distance(location) > distToLoc;
    }
    
    private boolean isInArea(String area, ArrayList<String> opponentPos){
        
        for(int i = 0; i < opponentPos.size(); i++){
            if(opponentPos.get(i).contains(area)){
                return true;
            }
        }        
        
        return false;
    }
    
    public Character getMyClosestTeamMate(){
        
        ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
        float minDist = 10000000;
        Character c = parentCharacter;
        
        for(int i = 0; i < players.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)players.get(i);
            if(parentCharacter.playerIsTeamMate(bc) && bc.getID() != parentCharacter.getID()){
                float dist = parentCharacter.getPosition().distance(bc.getPosition());
                if(dist < minDist){
                    c = bc;
                    minDist = dist;
                }
            }
            
        }
        return c;
    }
    
    public BasketballCharacter getPlayerClosestTeammate(BasketballCharacter player){
        
        ArrayList<BasketballCharacter> teammates = player.getTeamMates();
        float minDist = 10000000;
        Character c = player;
        
        for(BasketballCharacter bc:teammates){
            float dist = bc.get2DPosition().distance(player.get2DPosition());
            if(dist < minDist){
                minDist = dist;
                c = bc;
            }
        }
        
        return (BasketballCharacter)c;
        
    }
    
    public BasketballCharacter getClosestTeamMateToOpponent(BasketballCharacter opponent){
        
        ArrayList<BasketballCharacter> teammates = parentCharacter.getTeamMates();
        Character c = parentCharacter;
        float minDist = parentCharacter.get2DPosition().distance(opponent.get2DPosition());
        
        for(BasketballCharacter bc:teammates){
            float dist = bc.get2DPosition().distance(opponent.get2DPosition());
            if(dist < minDist){
                minDist = dist;
                c = bc;
            }
        }
        
        return (BasketballCharacter)c;
    }
    
    public Character getClosestOpponent(){
        
        ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
        float minDist = 10000000;
        Character c = parentCharacter;
        
        for(int i = 0; i < players.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)players.get(i);
            if(!parentCharacter.playerIsTeamMate(bc)){
                float dist = parentCharacter.getPosition().distance(bc.getPosition());
                if(dist < minDist){
                    c = bc;
                    minDist = dist;
                }
            }
            
        }
        return c;
    }
    
    public float distOfClosestOpponent(){
        
        ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
        float minDist = 10000000;
        Character c = parentCharacter;
        
        for(int i = 0; i < players.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)players.get(i);
            if(!parentCharacter.playerIsTeamMate(bc)){
                float dist = parentCharacter.getPosition().distance(bc.getPosition());
                if(dist < minDist){
                    c = bc;
                    minDist = dist;
                }
            }
            
        }
        return minDist;    
    }
    
    public BasketballCharacter getPassingTarget(){
        
        ArrayList<BasketballCharacter> teamMates = parentCharacter.getTeamMates();
        float minDist = 10000000;
        BasketballCharacter target = null;
        
        for(BasketballCharacter player : teamMates){
            if(parentCharacter.perception.isWithinGaze(player.getPosition(), 10f)){
                float dist = parentCharacter.getPosition().distance(player.getPosition());
                if(dist < minDist){
                    target = player;
                    minDist = dist;
                }
            }
        }
        
        return target;
    }    
    
     public Character getBestPositionedCharacter(){
        ArrayList<BasketballCharacter> candidates = parentCharacter.getTeamMates();
        ArrayList<Character> perceivedList = new ArrayList<Character>();
        for(int i = 0; i < candidates.size(); i++){
            if(parentCharacter.perception.canSeeCharacter(candidates.get(i)) && parentCharacter.getID() != candidates.get(i).getID()){
                perceivedList.add(candidates.get(i));
            }
        }
        
        if(perceivedList.size() > 0){
            return parentCharacter.getNearestCharacter(perceivedList);
        }
        else{
            return null;
        }  
    }
     
     public static ArrayList<ArrayList<String>> getPlayerAreas(BasketballCharacter parent, BasketballCharacter possessor){
         ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
         ArrayList<String> teammatePos = new ArrayList<String>();
         ArrayList<String> opponentPos = new ArrayList<String>();
     
         for(int i = 0; i < players.size(); i++){
            if(parent.getID() != players.get(i).getID() && possessor.getID() != players.get(i).getID()){
                BasketballCharacter bc = (BasketballCharacter)players.get(i);
                if(parent.playerIsTeamMate(bc)){
                    teammatePos.add(Court.getPlayerArea(bc));   
                }
                else{
                    opponentPos.add(Court.getPlayerArea(bc));
                }
            }
        }
         ArrayList<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
         
         l.add(teammatePos);
         l.add(opponentPos);
         
         return l;
     
     }
     
     public static boolean isBetweenGoal(BasketballCharacter parent, BasketballCharacter test, float limit){
         Vector3f goalPos = Court.getHoopLocation();
         float maxX = Math.max(goalPos.x, parent.getPosition().x) + limit;
         float minX = Math.min(goalPos.x, parent.getPosition().x) - limit;
         float maxZ = Math.max(goalPos.z, parent.getPosition().z) + limit;
         float minZ = Math.min(goalPos.z, parent.getPosition().z) - limit;
         
         return (test.getPosition().x > minX && test.getPosition().x < maxX)
             && (test.getPosition().z > minZ && test.getPosition().z < maxZ);
     }
     
     public void resetPossessionTime(){
         possession.resetPossessionTime();
     }
     
     public void doOutOfBoundActivity(){
         
        boolean isTargetHoopSide = Court.pointIsHoopSide(targetPosition);
        boolean targetInsideCourt = Court.isInsideCourt(targetPosition);
        
        if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
             if(!isTargetHoopSide || !targetInsideCourt){
                  this.setTargetPosition(Court.getRandomHoopSidePosition());
             }
         }
         else{
            if(isTargetHoopSide || !targetInsideCourt){
                 this.setTargetPosition(Court.getRandomNonHoopSidePosition());
             }
        }
                    
        if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
            if(this.isTargetReached(2f)){
                this.setTargetPosition(Court.getRandomHoopSidePosition());
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
            else{
                if(this.isTargetReached(2f)){
                    this.setTargetPosition(Court.getRandomNonHoopSidePosition());
                }
            }
        }
        
        this.updateCurrentTargetPosition();
     }
     
     private void adjustCourtTargets(){
         
         boolean isTargetHoopSide = Court.pointIsHoopSide(targetPosition);
         
         if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
             if(!isTargetHoopSide || !Court.isInsideCourt(targetPosition)){
                  this.setTargetPosition(Court.getRandomHoopSidePosition());
             }
         }
         else{
            if(isTargetHoopSide || !Court.isInsideCourt(targetPosition)){
                 this.setTargetPosition(Court.getRandomNonHoopSidePosition());
             }
         }
     }
         
         
         
     
     public void swapRoleActivity(){
         
         this.adjustCourtTargets();
                           
         if(parentCharacter.isInPossession()){
             parentCharacter.setBehaviorState(0);
             this.setTargetPosition(Court.getRestartLocation());
             if(this.isTargetReached(0.5f)){
                 GameManager.setAttackingTeam(parentCharacter.getTeamID());
                 GameManager.setInState();
             }
         }
         else{
             parentCharacter.setBehaviorState(1);
             if(this.isTargetReached(2f)){
                 parentCharacter.setSpeed(0);
                 parentCharacter.abo.turnBodyToTarget(ball.getBallPosition());
             }
         }
         this.updateCurrentTargetPosition();
     }
     
     private void retrieveDeadBall(){
          if(this.isClosestToBall(parentCharacter.getTeamMates())){
               if(ball.getBallPosition().y < Court.getHoopLocation().getY()){
                  this.setTargetPosition(ball.getBallPosition());
               }
               else{
                   this.setTargetPosition(parentCharacter.get2DPosition());
               }
           }
           else{
               if(this.isTargetReached(2f) || !Court.isInsideCourt(targetPosition)){
                  this.setTargetPosition(Court.getRandomNonHoopSidePosition());
               }
           }
          parentCharacter.setBehaviorState(1);
     }
     
     private void prepareForNextRound(){
         
        if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){
           if(this.isTargetReached(2f) || !Court.isInsideCourt(targetPosition)){
              this.setTargetPosition(Court.getRandomHoopSidePosition());
           }
       }
       else{
           if(this.isTargetReached(2f) || !Court.isInsideCourt(targetPosition)){
              this.setTargetPosition(Court.getRandomNonHoopSidePosition());
           }
       }
         
     }
     
     public void doAfterScoreActivity(){
                  
         if(parentCharacter.isInPossession()){
             GameManager.setPossessionSwapState();
         }
         else if(SceneCharacterManager.getCharacterInPossession() == null){
             parentCharacter.setBehaviorState(1);
             if(parentCharacter.getTeamID() == GameManager.getAttackingTeam()){ //team which just scored
                  this.adjustCourtTargets();
             }
             else{                                                             //team in next possession
                 this.retrieveDeadBall();
             }
         }
         else{
             this.prepareForNextRound();
         }   
         this.updateCurrentTargetPosition();
     }
     
     public boolean isClosestToBall(ArrayList<BasketballCharacter> group){
        float distToBall = parentCharacter.get2DPosition().distance(ball.getBallPosition());
        
        for(int i = 0; i < group.size(); i++){
            float d = group.get(i).get2DPosition().distance(ball.getBallPosition());
            if(d < distToBall){
                return false;
            }
        }   
        return true;
    }
     
     public boolean isClosestToLocation(ArrayList<BasketballCharacter> group, Vector3f location){
        float distToLocation = parentCharacter.get2DPosition().distance(location.setY(0));
        
        for(int i = 0; i < group.size(); i++){
            float d = group.get(i).get2DPosition().distance(location.setY(0));
            if(d < distToLocation){
                return false;
            }
        }   
        return true;
     
     }
     
     public boolean targetIsBall(){
 //        System.out.println(targetPosition + " " + ball.getBallPosition());
  //       return targetPosition == ball.getBallPosition().setY(0);
         return targetPosition.distance(ball.getBallPosition().setY(0)) < 0.5f;
     }
     
     public Vector3f positionFurthestFromOpponents(ArrayList<Vector3f> vecs){
        
        float maxDist = 0;
        Vector3f bestVec = null;
        
        for(Vector3f vec:vecs){ 
            float tot = 0;
            for(BasketballCharacter bc:parentCharacter.getOpponents()){
                tot += vec.distance(bc.get2DPosition());
            }
            if(tot > maxDist){
                maxDist = tot;
                bestVec = vec;
            }
        }
        
        return bestVec;
         
     }
     
    public void clearRecords(){}
     
     
     
     
     
     
}

