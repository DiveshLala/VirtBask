/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.Character;
import VISIE.characters.SkilledAgent;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class PossessionDecision {
    
    BasketballAgent parentCharacter;
    float timeSinceReceivedBall;
    long posStartTime;
    int timeAtTargetPosition;
    float decisionTimeLimit = 20;
    boolean isStationary;
    float stationaryTimeLimit = 5;
    long stationaryTime;
    
    public PossessionDecision(BasketballAgent ba){
        parentCharacter = ba;
        stationaryTime = System.currentTimeMillis();
    }
    
    public void makePossessionDecision(){
        
        timeSinceReceivedBall = Conversions.nanoToSecond(System.nanoTime() - posStartTime);
        
        if(timeSinceReceivedBall > decisionTimeLimit){ //time limit
            
            if(this.canShoot(25f, 5f)){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //CHECK IF CAN SHOOT
            }
            else if(!this.passCandidates().isEmpty()){
                parentCharacter.setBehaviorState(4); //CHECK IF CAN PASS
            }
            else if(parentCharacter.get2DPosition().distance(Court.getGoalPosition()) < 25){//IF CLOSE ENOUGH SHOOT
                parentCharacter.setBehaviorState(2);
            }
            else{   //MOVE TOWARDS GOAL
                parentCharacter.planner.setTargetPosition(Court.getGoalPosition());
            }
        }
        else if(this.canShoot(25f, 10f)){//shoot
            parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
            parentCharacter.setBehaviorState(2);
        }
        else{ //run
            this.updatePossessionTarget();
        }         
    }
    
    
    private void updatePossessionTarget(){
        
            if(parentCharacter.planner.isTargetReached(2f)){//target is reached
                if(!isStationary){ //after receives ball
                    this.setStationaryTime();
                    isStationary = true;
                }
                else{
                    float timeStationary = Conversions.milliToSecond(System.currentTimeMillis() - stationaryTime);
                    System.out.println("deciding pos ...");                     
                    if(timeStationary > stationaryTimeLimit || timeSinceReceivedBall < 0.2f){ //find new target
                        parentCharacter.setBehaviorState(0);
                        ArrayList<ArrayList<String>> areas = AgentPlanning.getPlayerAreas(parentCharacter, parentCharacter);
                        Vector3f bestPos = this.calculateBestPosition(parentCharacter, Court.getPlayerArea(parentCharacter), areas.get(0), areas.get(1));
                        parentCharacter.planner.setTargetPosition(bestPos);
                        System.out.println("new pos target " + bestPos + parentCharacter.getPosition()); 
                        isStationary = false;
                        parentCharacter.setBehaviorState(0);
                    }
                    else{
                        parentCharacter.setBehaviorState(5);
                    }
                }
            } 
            else{
                parentCharacter.planner.updateCurrentTargetPosition();
            }
    }
       
    private boolean canShoot(float shootRange, float opponentRange){
        float distToHoop = parentCharacter.get2DPosition().distance(Court.getHoopLocation().setY(0));
        if(distToHoop < shootRange && distToHoop > 7.5f){
            ArrayList<BasketballCharacter> opponents = parentCharacter.getOpponents();

            for(int i = 0; i < opponents.size(); i++){
                if(((opponents.get(i).get2DPosition().distance(parentCharacter.get2DPosition()) < opponentRange)
                     && AgentPlanning.isBetweenGoal(parentCharacter, opponents.get(i), 3.5f))
                     || (parentCharacter.get2DPosition().distance(opponents.get(i).get2DPosition()) < 10f)
                        && parentCharacter.perception.canSeeCharacter(opponents.get(i))){
                    return false;
                }
            }
            return true;
        }
        return false;    
    
    }
    
    private ArrayList<BasketballCharacter> passCandidates(){
        
        ArrayList<BasketballCharacter> teammates = parentCharacter.getTeamMates();
        ArrayList<BasketballCharacter> passCandidates = new ArrayList<BasketballCharacter>();
        
        for(int i = 0; i < teammates.size(); i++){
            
            float distToCharacter = parentCharacter.get2DPosition().distance(teammates.get(i).get2DPosition());
            
            if(parentCharacter.perception.canSeeCharacter(teammates.get(i))
            && distToCharacter < 25f){
                                
                if(!canBallBeLost(teammates.get(i))){
                    passCandidates.add(teammates.get(i));
                }
            }
        }
        
        return passCandidates;
    }
    
    private boolean canBallBeLost(BasketballCharacter teammate){
        
        Vector3f a = parentCharacter.get2DPosition();
        Vector3f b = teammate.get2DPosition();
        
        ArrayList<BasketballCharacter> opponents = parentCharacter.getOpponents();

        boolean opponentsBlocking = false;
        boolean interceptChance = false;

        for(int j = 0; j < opponents.size(); j++){

            if(!Conversions.isNotInBetween(a, b, opponents.get(j).get2DPosition())){
                opponentsBlocking = true;
            }

            if(Conversions.distancePointToLine(a, b,opponents.get(j).get2DPosition()) < 3f){
                interceptChance = true;
            }
        }
        
        return opponentsBlocking || interceptChance;
    
    }

    
     private Vector3f calculateBestPosition(BasketballCharacter bcInPos, String possession, ArrayList<String> teammates, ArrayList<String> opponents){
        
        //finds candidate areas
        ArrayList<String> candidates = Court.getAdjacentAreas(possession);
        
        ArrayList<String> emptyRegions = new ArrayList<String>();
        
        //labels candidate areas if they contain players
        for(int i = candidates.size() - 1; i >= 0; i--){
            for(int j = 0; j < teammates.size(); j++){
                if(candidates.get(i).equals(teammates.get(j))){
                    candidates.set(i, "T: " + candidates.get(i));
                }
            }
            for(int k = 0; k < opponents.size(); k++){
                if(candidates.get(i).equals(opponents.get(k))){
                    candidates.set(i, "O: " + candidates.get(i));
                }
            }
        }  
        
        //labels empty candidate areas
        for(int i = 0; i < candidates.size(); i++){
            if(!candidates.get(i).startsWith("O") && !candidates.get(i).startsWith("T")){
                emptyRegions.add(candidates.get(i));
            }
        }
        
        //if no empty areas, find best area from other candidates
        //if player is already in an empty area, stay in that area        
        if(!emptyRegions.isEmpty()){ 
                Vector3f bestPos = this.getBestPositionInNonOccupied(this.getClosestAreaToPlayer(emptyRegions), bcInPos);         
           //     parentCharacter.planner.setTargetPosition(bestPos);
                return bestPos;
        }
        //candidate areas are all occupied by players - wait
        else{
            System.out.println("stalemate");
            return this.doStalemateActivity();
   //        
//            parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
//            parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
        }
    }
     
     private Vector3f doStalemateActivity(){
         return parentCharacter.doStalemateActivity();
     }
     
     public void doGreedyStalemate(){
         
            if(this.canShoot(25f, 5f)){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //DESPERATE SHOOT
            }
            else if(parentCharacter.get2DPosition().distance(Court.getMidCourtLocation()) < 5f){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //DESPERATE SHOOT
            }
            else{
                parentCharacter.planner.setTargetPosition(Court.getMidCourtLocation());
            }
     }
     
     
     private Vector3f getBestPositionInNonOccupied(String area, BasketballCharacter possessor){
        
        float minDistFromPossessor = 7f;
        boolean isInArea = Court.getPlayerArea(parentCharacter).equals(area);
        
        
        if(isInArea){
            return parentCharacter.get2DPosition();
        }
        else{
            Vector3f newPos = possessor.get2DPosition();
            while(!(possessor.get2DPosition().distance(newPos) > minDistFromPossessor)){
                newPos = Court.getRandomCoordinateInArea(area);
            }
            
            return newPos;
        }
    }
     
     private String getClosestAreaToPlayer(ArrayList<String> areas){
        float minDist = 1000000;
        String region = Court.getPlayerArea(parentCharacter);

        for(int i = 0; i < areas.size(); i++){
            float dist = parentCharacter.getPosition().distance(Court.getCentreCoordinate(areas.get(i)));
            if(dist < minDist){
                region = areas.get(i);
                minDist = dist;
            }            
        }
        return region;   
    }
     
     private boolean straightToCentre(){
                 
         ArrayList<BasketballCharacter> opponents = parentCharacter.getOpponents();
         float angleToCentre = Conversions.originToTargetAngle(parentCharacter.getPosition(), Court.getMidCourtLocation());
         Ray midRay = new Ray();
         midRay.setOrigin(parentCharacter.get2DPosition().setY(1));
         midRay.setDirection(Conversions.degreesToNormalizedCoordinates(angleToCentre));
         
         return CollisionMath.rayCollisionTest(midRay.origin, midRay.direction, parentCharacter, opponents, 3, 20);
     }
     
     public void resetPossessionTime(){
         timeSinceReceivedBall = 0;
         ArrayList<ArrayList<String>> areas = AgentPlanning.getPlayerAreas(parentCharacter, parentCharacter);
         Vector3f bestPos = this.calculateBestPosition(parentCharacter, Court.getPlayerArea(parentCharacter), areas.get(0), areas.get(1));
         parentCharacter.planner.setTargetPosition(bestPos);
         posStartTime = System.nanoTime();
     } 
     
     
    public void makeGreedyPossessionDecision(){
        
        timeSinceReceivedBall = Conversions.nanoToSecond(System.nanoTime() - posStartTime);
        
        if(timeSinceReceivedBall > decisionTimeLimit){ //time limit
            if(this.canShoot(25f, 5f)){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //DESPERATE SHOOT
            }
            else if(parentCharacter.get2DPosition().distance(Court.getMidCourtLocation()) < 5f){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //DESPERATE SHOOT
            }
            else{
                parentCharacter.setBehaviorState(5);
           //     parentCharacter.planner.setTargetPosition(Court.getMidCourtLocation());
            }
        }
        else if(this.canShoot(25f, 7.5f)){//shoot
            parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
            parentCharacter.setBehaviorState(2);
        }
        else{ //run
            //check if can run towards hoop
            if(this.straightToCentre()){
                parentCharacter.planner.setTargetPosition(Court.getMidCourtLocation());
            }
            else{
                parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
            }
            //player is in area of target
            
            if(parentCharacter.planner.isTargetReached(2f)){  
                ArrayList<ArrayList<String>> areas = AgentPlanning.getPlayerAreas(parentCharacter, parentCharacter);
                Vector3f bestPos = this.calculateBestPosition(parentCharacter, Court.getPlayerArea(parentCharacter), areas.get(0), areas.get(1));
                parentCharacter.planner.setTargetPosition(bestPos);
            }
            else{//player is traveling to target position
            
            }
        }  
    }
    
    public void makeCooperativePossessionDecision(){
        
        timeSinceReceivedBall = Conversions.nanoToSecond(System.nanoTime() - posStartTime);
        
        if(timeSinceReceivedBall > decisionTimeLimit){ //time limit
            if(this.canShoot(25f, 5f)){
                parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
                parentCharacter.setBehaviorState(2); //DESPERATE SHOOT
            }
            else{
                this.doStalemateActivity();
            }
        }
        else if(this.canShoot(25f, 7.5f)){//shoot
            parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
            parentCharacter.setBehaviorState(2);
        }
        else{ //run
            //check if can run towards hoop
            
            if(parentCharacter.planner.isTargetReached(2f)){
                if(!isStationary){ //after receives ball
                    this.setStationaryTime();
                    isStationary = true;
                }
                else{
                    float timeStationary = Conversions.milliToSecond(System.currentTimeMillis() - stationaryTime);
           //         System.out.println("ts " + timeStationary);
                    if(timeStationary > stationaryTimeLimit || timeSinceReceivedBall < 0.2f){ //find new target
                        ArrayList<ArrayList<String>> areas = AgentPlanning.getPlayerAreas(parentCharacter, parentCharacter);
                        Vector3f bestPos = this.calculateBestPosition(parentCharacter, Court.getPlayerArea(parentCharacter), areas.get(0), areas.get(1));
                        parentCharacter.planner.setTargetPosition(bestPos);
                        System.out.println("new target " + bestPos + parentCharacter.getPosition()); 
                        isStationary = false;
                    }
                    else{
         //               parentCharacter.setBehaviorState(5); 
                    }
                }

            }   
            else{
                //  check better areas while travelling to target
            }
        }  
    }
    
    public void recordPlayerPossessionData(){
    
    }
    
    //makes decision for double teaming
    public void makeRationalDoubleTeamDecision(){

        if(this.canShoot(25f, 5f)){
            parentCharacter.abo.turnBodyToTarget(Court.getGoalPosition());
            parentCharacter.setBehaviorState(2); //CHECK IF CAN SHOOT
            stationaryTime = System.currentTimeMillis();
        }
        else if(parentCharacter.get2DPosition().distance(Court.getGoalPosition()) < 10){
            
            if(parentCharacter.get2DPosition().distance(Court.getMidCourtLocation()) > 2f){
                parentCharacter.planner.setTargetPosition(Court.getMidCourtLocation());
                stationaryTime = System.currentTimeMillis();
                parentCharacter.setBehaviorState(0);
            }
            else{
                parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
                parentCharacter.abo.turnBodyToTarget(parentCharacter.getNearestTeammate().getPosition());
//                parentCharacter.abo.doTurningAnimation();  
                
                if(parentCharacter.canDoClearPass(parentCharacter.getNearestTeammate(), 4.5f)){//pass
                    parentCharacter.setBehaviorState(5);
                }
            }
        }
        else{
        
            ArrayList<BasketballCharacter> opps = parentCharacter.getOpponents();
            BasketballCharacter teammate = parentCharacter.getNearestTeammate();
            int within5 = 0;
            int within7 = 0;

            for(BasketballCharacter opp:opps){
                
                boolean isCloser = opp.get2DPosition().distance(Court.getGoalPosition()) < parentCharacter.get2DPosition().distance(Court.getGoalPosition()) - 1;
                
                if(opp.get2DPosition().distance(parentCharacter.get2DPosition()) < 5f
                  && isCloser){
                    within5++;   
                }
                else if(opp.get2DPosition().distance(parentCharacter.get2DPosition()) < 7f
                   && isCloser){
                    within7++;
                }
            }
            
            if(within5 > 0 && (within5 + within7) > 1 ){
                                
                if(teammate.get2DPosition().distance(parentCharacter.get2DPosition()) > 5f){
                    parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
                    parentCharacter.abo.turnBodyToTarget(parentCharacter.getNearestTeammate().getPosition());
//                    parentCharacter.abo.doTurningAnimation();

                    if(System.currentTimeMillis() - stationaryTime > 10000){
             //           System.out.println("waiting too long...");
                        parentCharacter.setBehaviorState(8);
                    }
                    else if(parentCharacter.canDoClearPass(teammate, 4.5f)){//pass
              //         System.out.println("setting pass " + (System.currentTimeMillis() - stationaryTime));
                        parentCharacter.setBehaviorState(5);
                    }
                    else{
                        parentCharacter.setBehaviorState(0);
                    }
                }
                else{
            //        System.out.println("too close");
                    parentCharacter.setBehaviorState(8);
                }
            }
            else{
                this.setStationaryTime();
                parentCharacter.planner.setGoalAsTarget(5);
                parentCharacter.setBehaviorState(0);
            }
        }
    }
    
    public void setStationaryTime(){
        stationaryTime = System.currentTimeMillis();
    }
    
    
    
}
