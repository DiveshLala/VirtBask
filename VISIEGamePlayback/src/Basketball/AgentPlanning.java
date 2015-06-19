/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballPlayer;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.characters.Player;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCreator;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Size;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 *
 * @author DiveshLala
 */
public class AgentPlanning {
    
    BasketballAgent parentCharacter;
    Vector3f targetPosition;
    Ball ball;
    
    public AgentPlanning(BasketballAgent ba){
        parentCharacter = ba;
    }
    
    public Character getBestPositionedCharacter(){
        ArrayList<Character> candidates = SceneCharacterManager.getCharacterArray();
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
    
    public void decidePossessionAction(int behaviorState){

        if(behaviorState == 0){//has ball
            parentCharacter.setActionState(0); //dribbling to target
            
            if(this.isTargetReached()){       // if target is reached
        //       this.makePossessionDecision();
            }
        }
        
        else if(behaviorState == 2){//shooting
            parentCharacter.setActionState(2); //shooting
        }
    }
    
    public void decideNonPossessionAttackAction(int behaviorState){  
        
        if(behaviorState == 1){
            parentCharacter.setActionState(1);   
            this.makeNonPossessionAttackDecision();
            
        }           
    }
    
    public void decideDefenseAction(int behaviorState){
      
       if(behaviorState == 1){//no ball
           parentCharacter.setActionState(1);      
           this.makeDefenseDecision();
       }
       
    }
    
    public Vector3f calculateRandomPosition(){
           Vector3f newPos = Conversions.generateRandomPosition(Court.getCourtDimensions());
           newPos.y = 4;
           return newPos;
    }
        
    public void setTargetPosition(Vector3f target){
        targetPosition = target.setY(0);
    }
    
    public Vector3f getTargetPosition(){
        return targetPosition;
    }
    
    public boolean isTargetReached(){
        float targetAccuracy = 0.5f;
        return (this.getTargetPosition().distance(parentCharacter.getPosition().setY(0))) < targetAccuracy;
    }
    
    public void makePossessionDecision(){
        if(this.getBestPositionedCharacter() == null){
             this.setTargetPosition(this.calculateRandomPosition());
        }
        else{ //pass
            System.out.println("PASS");
            parentCharacter.setBehaviorState(101);
      //      parentCharacter.setBehaviorState(2);
        }
    }
    
    public void makeNonPossessionAttackDecision(){
        this.findFreeSpace();
    }
    
    public void makeDefenseDecision(){

      //find opponent to mark  
      int[][] pairs = this.getNearestNeighbourPairs();
      int characterToMark = -1;
      
      for(int i = 0; i < pairs.length; i++){
          if(parentCharacter.getID() == pairs[i][0]){
              characterToMark = pairs[i][1];
              break;
          }
          else if(parentCharacter.getID() == pairs[i][1]){
              characterToMark = pairs[i][0];
              break;
          }
      }
      
      //set position according to marking decision
      Vector3f newPos = this.makeMarkingDecision(characterToMark);
      this.setTargetPosition(newPos);
      
    }
    
    private int[][] getNearestNeighbourPairs(){

      ArrayList<Character> temp = (ArrayList<Character>)SceneCharacterManager.getCharacterArray().clone();
      int[][] markingPairs = new int[temp.size()/2][2];
      ArrayList<Integer> usedIDs = new ArrayList<Integer>();
      int index = 0;
      
      for(int i = 0; i < temp.size(); i++){

            BasketballCharacter x = (BasketballCharacter)temp.get(i);
            float minDist = 1000000;
            int minID = -1;
            
            if(!usedIDs.contains(x.getID())){

                for(int j = 0; j < temp.size(); j++){

                    BasketballCharacter y = (BasketballCharacter)temp.get(j);

                    if(!x.playerIsTeamMate(y) && !usedIDs.contains(y.getID())){
                        float dist = x.get2DPosition().distance(y.get2DPosition());
                        if(dist < minDist){
                            minDist = dist;
                            minID = y.getID();
                        }
                    } 
                }  

               if(minID != 1){ 
                    int[] pair = {x.getID(), minID}; 
                    markingPairs[index] = pair;
                    index++;
                    usedIDs.add(x.getID());
                    usedIDs.add(minID);
               }
            }
      }
      return markingPairs; 
    }
    
    private Vector3f makeMarkingDecision(int playerID){
        
        Vector3f targetPos = new Vector3f();
        Character c = SceneCharacterManager.getCharacterByID(playerID); 
        
        int decision = 0; //0 to block goal, 1 to block pass
        float distFromPlayer = 5f;
        
        if(decision == 0){
            Vector3f toGoalVector = Court.getGoalPosition().subtract(c.getPosition()).setY(0);
            toGoalVector.normalizeLocal();
            targetPos = c.getPosition().add(toGoalVector.mult(distFromPlayer));
        }
        
        return targetPos;
    
    }
    
    
    
    
    public void makeFreeBallDecision(int behaviorState){ 
        
        //if characters are shooting or passing, do nothing
        if(!parentCharacter.isShooting() && !parentCharacter.isPassing()){
            parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);
            this.setTargetPosition(ball.getBallPosition());   
        }
        else if(parentCharacter.isShooting() && parentCharacter.getCurrentMovementProgress(1) > 0.8){
           parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);
            this.setTargetPosition(ball.getBallPosition());   
        }
        else if(parentCharacter.isPassing() && parentCharacter.getCurrentMovementProgress(1) > 0.5){
            parentCharacter.setActionState(1);
            parentCharacter.setBehaviorState(1);
            this.setTargetPosition(ball.getBallPosition());  
        }
    }
    
    public void setBall(Ball b){
        ball = b;
    }
    
    private boolean isFree(){
        return true;
    }
    
    private void findFreeSpace(){
        BasketballCharacter bcInPos = SceneCharacterManager.getCharacterInPossession();
        ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
        
        String possPos = Court.getPlayerArea(bcInPos);        
        
        ArrayList<String> teammatePos = new ArrayList<String>();
        ArrayList<String> opponentPos = new ArrayList<String>();
        
        
        //get areas of players on court
        for(int i = 0; i < players.size(); i++){
            if(parentCharacter.getID() != players.get(i).getID() && bcInPos.getID() != players.get(i).getID()){
                BasketballCharacter bc = (BasketballCharacter)players.get(i);
                if(parentCharacter.playerIsTeamMate(bc)){
                    teammatePos.add(Court.getPlayerArea(bc));   
                }
                else{
                    opponentPos.add(Court.getPlayerArea(bc));
                }
            }
        }
        
        //use information to get best area 
        this.calculateBestPosition(bcInPos, possPos, teammatePos, opponentPos);
        
    }
    
    private boolean isClosestToHoop(ArrayList<BasketballCharacter> group){
        float distToHoop = parentCharacter.get2DPosition().distance(Court.getGoalPosition());
        
        for(int i = 0; i < group.size(); i++){
            float d = group.get(i).get2DPosition().distance(Court.getGoalPosition());
            if(d < distToHoop){
                return false;
            }
        } 
        
        return true;
    }
    
    private void calculateBestPosition(BasketballCharacter bcInPos, String possession, ArrayList<String> teammates, ArrayList<String> opponents){
        
        //finds candidate areas
        ArrayList<String> candidates = Court.getAdjacentAreas(possession);
        
        ArrayList<String> emptyRegions = new ArrayList<String>();
        String currentArea = Court.getPlayerArea(parentCharacter);
        boolean stayStill = false;
        
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
            for(int i = 0; i < emptyRegions.size(); i++){
                if(emptyRegions.get(i).equals(currentArea)){
            //        System.out.println("staying still at " + currentArea);
                    Vector3f bestPos = this.getBestPositionInNonOccupied(currentArea, bcInPos);
          //       
                    this.setTargetPosition(parentCharacter.getPosition());
                    this.setTargetPosition(bestPos);
                    stayStill = true;
                    break;
                }
            }
            
           //if player decides to move, move to the closest candidate area
           //designated by empty regions
            if(!stayStill){  
                Vector3f bestPos = this.getBestPositionInNonOccupied(this.getClosestAreaToPlayer(emptyRegions), bcInPos);
               
                this.setTargetPosition(bestPos);
            //    this.setTargetPosition(Court.getCentreCoordinate(this.getClosestAreaToPlayer(emptyRegions)));
          //      System.out.println("moving to " + this.getClosestAreaToPlayer(emptyRegions));
            }
        }
        //if player is in same area as possession player
        else if(currentArea.equals(possession)){
            Vector3f bestPos = this.getBestPositionInNonOccupied(currentArea, bcInPos);
   
            //         this.setTargetPosition(parentCharacter.getPosition());
            this.setTargetPosition(bestPos);
        }
        //candidate areas are all occupied by players
        else{
            this.getBestPositionInOccupied(candidates);        
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
    
    private void getBestPositionInOccupied(ArrayList<String> candidates){
        
//        Vector3f bestPos = null;
//        
//        for(int i = 0; i < candidates.size(); i++){
//            if(candidates.get(i).startsWith("T")){
//                return parentCharacter.get2DPosition();
//            }
//        }
//        
//        return bestPos;
        
//        System.out.println(Court.getPlayerArea(parentCharacter));
//        System.out.println(candidates);
//        System.out.println();
    }
    
    private Vector3f getBestPositionInNonOccupied(String area, BasketballCharacter possessor){
        
        float distanceFromPossessor = parentCharacter.get2DPosition().distance(possessor.get2DPosition());
        float minDistFromPossessor = 7f;
        boolean isInArea = Court.getPlayerArea(parentCharacter).equals(area);
        boolean isWithinDist = distanceFromPossessor > minDistFromPossessor;
        
        
        if(isInArea && isWithinDist){
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
    
    
    private boolean isInArea(String area, ArrayList<String> opponentPos){
        
        for(int i = 0; i < opponentPos.size(); i++){
            if(opponentPos.get(i).contains(area)){
                return true;
            }
        }        
        
        return false;
    }
    
    public Character getClosestTeamMate(){
        
        ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
        float minDist = 10000000;
        Character c = parentCharacter;
        
        for(int i = 0; i < players.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)players.get(i);
            if(parentCharacter.playerIsTeamMate(bc)){
                float dist = parentCharacter.getPosition().distance(bc.getPosition());
                if(dist < minDist){
                    c = bc;
                    minDist = dist;
                }
            }
            
        }
        return c;
    
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
    
}

