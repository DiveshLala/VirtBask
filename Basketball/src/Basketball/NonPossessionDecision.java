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
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import com.jme3.math.Vector3f;

/**
 *
 * @author Divesh
 */
public class NonPossessionDecision {
    
    BasketballAgent parentCharacter;
    private long stationaryTime;
    private boolean isStationary;
    private float stationaryTimeLimit = 5f;
    private float timeSincePossessedBall;
    private long nonposStartTime;
    
    public NonPossessionDecision(BasketballAgent ba){
        parentCharacter = ba;
        stationaryTime = System.currentTimeMillis();
    }
    
    public void findFreeSpace(){
        
        timeSincePossessedBall = Conversions.nanoToSecond(System.nanoTime() - nonposStartTime);
        
        //if player can receive open pass    
        BasketballCharacter bcInPos = SceneCharacterManager.getCharacterInPossession();
        
        //if open for pass, stand still to face player with ball 
        if(timeSincePossessedBall > 5 && this.openForPass(bcInPos) && parentCharacter.get2DPosition().distance(bcInPos.get2DPosition()) < 15f){
            parentCharacter.setBehaviorState(1);
            parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
        }
        //if not open, calculate best position to travel to 
        else{
            this.updateTargetPosition(bcInPos);      
        }
        
    }
    
    public void resetNonPossessionTime(){
        timeSincePossessedBall = 0;
        nonposStartTime = System.nanoTime();
    }
    
    private void updateTargetPosition(BasketballCharacter bcInPos){
        
        if(parentCharacter.planner.isTargetReached(2f)){//target reached
            
            if(timeSincePossessedBall < 5){ //ensures that new target generated after ball is passed
                this.createNewTarget(bcInPos);
            }    
            else if(!isStationary){ //sets up stationary time after reaching target
                this.setStationaryTime();
                isStationary = true;
            }
            else{
                float timeStationary = Conversions.milliToSecond(System.currentTimeMillis() - stationaryTime);                   
                if(timeStationary > stationaryTimeLimit){ //find new target           
                    this.createNewTarget(bcInPos);
                    isStationary = false;
                }
            }
        } 
        else{
            parentCharacter.planner.updateCurrentTargetPosition();
            parentCharacter.setBehaviorState(1);
        }
    
    }
    
    private void createNewTarget(BasketballCharacter bcInPos){
        
        String possPos = Court.getPlayerArea(bcInPos);  
        parentCharacter.setBehaviorState(1);
        Vector3f bestPos = this.calculateBestPosition(bcInPos, possPos);
        parentCharacter.planner.setTargetPosition(bestPos);
        
    }
    
    private Vector3f calculateBestPosition(BasketballCharacter bcInPos, String possession){
        
        Vector3f bestPos = parentCharacter.getPosition();
        String currentArea = Court.getPlayerArea(parentCharacter);
        boolean stayStill = false;
        
        ArrayList<String> candidates = this.doPlayerLabelling(possession);
        ArrayList<String> emptyRegions = this.doCandidateLabeling(candidates);
        
        //if no empty areas, find best area from other candidates
        //if player is already in an empty area, stay in that area        
        if(!emptyRegions.isEmpty()){
            for(int i = 0; i < emptyRegions.size(); i++){
                if(emptyRegions.get(i).equals(currentArea)){                    
                    //if near target, make decision on mext movement
                    if(parentCharacter.planner.isTargetReached(2f)){
                        int decision = this.runOrCall(bcInPos);
                        if(decision == 1){
                            bestPos = this.getBestPositionInNonOccupied(currentArea, bcInPos);
                        }
                    }
                    stayStill = true;
                }
            }
            
           //if player decides to move, move to the closest candidate area
           //designated by empty regions
            if(!stayStill){  
                bestPos = this.getBestPositionInNonOccupied(this.getClosestAreaToPlayer(emptyRegions), bcInPos);               
            }
        }
        //if player is in same area as possession player
        else if(currentArea.equals(possession)){
            bestPos = this.getBestPositionInNonOccupied(currentArea, bcInPos);
        }
        //candidate areas are all occupied by players
        else{
            this.getBestPositionInOccupied(candidates);        
        }
        
        return bestPos;
    }
    
    private void getBestPositionInOccupied(ArrayList<String> candidates){
        System.out.println("DDDDDDDDD");

    }
    
    private ArrayList<String> doPlayerLabelling(String possession){
        
        BasketballCharacter bcInPos = SceneCharacterManager.getCharacterInPossession();         
        ArrayList<ArrayList<String>> areas = AgentPlanning.getPlayerAreas(parentCharacter, parentCharacter);
        ArrayList<String> teammates = areas.get(0);
        ArrayList<String> opponents = areas.get(1);
        ArrayList<String> candidates = Court.getAdjacentAreas(possession);
        
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
        
        return candidates;
    }
    
    private ArrayList<String> doCandidateLabeling(ArrayList<String> candidates){
        
        ArrayList<String> emptyRegions = new ArrayList<String>();
        //labels empty candidate areas
        for(int i = 0; i < candidates.size(); i++){
            if(!candidates.get(i).startsWith("O") && !candidates.get(i).startsWith("T")){
                emptyRegions.add(candidates.get(i));
            }
        }
        
        return emptyRegions;
    }
    
    private Vector3f getBestPositionInNonOccupied(String area, BasketballCharacter possessor){
        
        float distanceFromPossessor = parentCharacter.get2DPosition().distance(possessor.get2DPosition());
        float minDistFromPossessor = 7f;
        float maxDistFromPossessor = 15f;
        int candidateNumber = 5;
        boolean isInArea = Court.getPlayerArea(parentCharacter).equals(area);
        boolean isWithinDist = distanceFromPossessor > minDistFromPossessor;
        
        
        if(isInArea && isWithinDist){
            return parentCharacter.get2DPosition();
        }
        else{
            //create random candidates in area
            ArrayList<Vector3f> candidates = new ArrayList<Vector3f>();
            for(int i=0; i < candidateNumber; i++){
                candidates.add(Court.getRandomCoordinateInArea(area));
            }
            
            //choose best that satisfies requirements
            for(Vector3f vec:candidates){
                if(possessor.get2DPosition().distance(vec) > minDistFromPossessor &&
                   possessor.get2DPosition().distance(vec) < maxDistFromPossessor){
                    return vec;
                }
            }
            
            //if none, move in direction of centre coordinate
            Vector3f dir = Court.getCentreCoordinate(area).subtract(possessor.get2DPosition()).normalize();
            return possessor.get2DPosition().add(dir.mult(minDistFromPossessor));
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
        
    //options 
    //        - stay and face possessor
    //        - shuffle in area
    // return run = 1, call(stay) = 0
    private int runOrCall(BasketballCharacter bcInPos){
        
        if(this.openForPass(bcInPos)){
            return 0;
        }
        return 1;
    }
    
    private boolean openForPass(BasketballCharacter bcInPos){
        float possessorPlayerAngle = Conversions.originToTargetAngle(bcInPos.getPosition(), parentCharacter.getPosition());
        ArrayList<BasketballCharacter> opp = parentCharacter.getOpponents();
        float posToGoal = bcInPos.getPosition().distance(Court.getGoalPosition());
        float meToGoal = parentCharacter.getPosition().distance(Court.getGoalPosition());
        boolean notObstructed = CollisionMath.rayCollisionTest(bcInPos.getPosition(), Conversions.degreesToNormalizedCoordinates(possessorPlayerAngle), parentCharacter, opp, 3, 20);
        boolean isBetterPosition = posToGoal - meToGoal > 5f;
        
        return notObstructed && isBetterPosition;
    }
    
    public void gotoDoubleTeamFreeSpace(){
                
        if(System.currentTimeMillis() - stationaryTime > 10000){
            System.out.println("going to get ball...");
            BasketballCharacter bc = SceneCharacterManager.getCharacterInPossession();
            Vector3f facingVec = Conversions.degreesToNormalizedCoordinates(bc.getFacingDirection());
            parentCharacter.planner.setTargetPosition(bc.get2DPosition().add(facingVec.mult(15f)));
            
            if(parentCharacter.get2DPosition().distance(parentCharacter.planner.getTargetPosition()) < 2f){
                parentCharacter.abo.turnBodyToTarget(bc.get2DPosition());
//                parentCharacter.abo.doTurningAnimation();
                this.setStationaryTime();
            }
            parentCharacter.setBehaviorState(1);   
        }
        else if(System.currentTimeMillis() - stationaryTime > 6000){  
            System.out.println("getting attention...");
            parentCharacter.setBehaviorState(6);
        }
       
        else{
            ArrayList<BasketballCharacter> opps = parentCharacter.getOpponents();
            BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
            Vector3f targetVec = adjustTargetPos();

            if(targetVec.distance(parentCharacter.get2DPosition()) > 10f){
                parentCharacter.planner.setTargetPosition(targetVec);
                stationaryTime = System.currentTimeMillis();
                parentCharacter.setBehaviorState(1);
//                System.out.println("m");
            }
            else if(targetVec.distance(parentCharacter.get2DPosition()) > 3f 
                && parentCharacter.getNearestCharacterDist(opps) < 10f){
                parentCharacter.planner.setTargetPosition(targetVec);
                stationaryTime = System.currentTimeMillis();
                parentCharacter.setBehaviorState(1);
//                System.out.println("m2");
            }
            else{
                parentCharacter.abo.turnBodyToTarget(possessor.get2DPosition());
//                parentCharacter.abo.doTurningAnimation();
//                System.out.println("t");            
            }        
        }
    }
    
    public void setStationaryTime(){
        stationaryTime = System.currentTimeMillis();
    }
    
    private Vector3f adjustTargetPos(){
        
        BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
        Vector3f targetVec;
        float posToGoal = Conversions.originToTargetAngle(possessor.get2DPosition(), Court.getGoalPosition());

        float a = Conversions.adjustAngleTo360(posToGoal + 60);
        Vector3f aVec =  possessor.get2DPosition().add(Conversions.degreesToNormalizedCoordinates(a).mult(25f));

        float b = Conversions.adjustAngleTo360(posToGoal - 60);
        Vector3f bVec =  possessor.get2DPosition().add(Conversions.degreesToNormalizedCoordinates(b).mult(25f));

        if(!Court.isInsideCourt(aVec)){
            targetVec = bVec;
        }
        else if(!Court.isInsideCourt(bVec)){
            targetVec = aVec;
        }
        else{
            if(aVec.distance(parentCharacter.get2DPosition()) < bVec.distance(parentCharacter.get2DPosition())){
                targetVec = aVec;
            }
            else{
                targetVec = bVec;
            }

        }
        
        return targetVec;
    }
    
    
}
