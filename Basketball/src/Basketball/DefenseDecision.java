/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.serializers.Vector3Serializer;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;


/**
 *
 * @author Divesh
 */
public class DefenseDecision {
    
    BasketballAgent parentCharacter;
    private BasketballCharacter markedCharacter;
    
    
    public DefenseDecision(BasketballAgent ba){
        parentCharacter = ba;
    }
    
    public void manToManMarking(){
        
        //mark possessor if close to goal
        if(SceneCharacterManager.getCharacterInPossession().get2DPosition().distance(Court.getPoleLocation()) < 20f){
            this.markPossessor();
        }
        else{
            //find opponent to mark  
            int[][] pairs = this.getNearestNeighbourPairs();
            int characterToMark = this.getCharacterToMark(pairs);

            //set position according to marking decision
            if(characterToMark != -1){
                  Vector3f newPos = this.makeMarkingDecision(characterToMark, true);
                  parentCharacter.planner.setTargetPosition(newPos);
                  markedCharacter = (BasketballCharacter)SceneCharacterManager.getCharacterByID(characterToMark);
            }
            else{
                markedCharacter = null;
            }
            
       //     this.setDefensiveHeadPosition();
            this.setDefensiveBehaviorState();
        }        
    }
    
    private void setDefensiveHeadPosition(){
        
        BasketballCharacter posCharacter = SceneCharacterManager.getCharacterInPossession();
        
        if(markedCharacter.equals(posCharacter)){
            if(parentCharacter.planner.isTargetReached(1)){
                parentCharacter.abo.turnHeadToTarget(posCharacter.get2DPosition());
            }
//            else{
//                parentCharacter.abo.resetHeadPosition();
//            }
        }
        else{//turn head to possessor if target reached
//            if(parentCharacter.planner.isTargetReached(1)){
//                parentCharacter.abo.turnHeadToTarget(posCharacter.get2DPosition());
//            }
//            else{
//                System.out.println("reset no target");
//                parentCharacter.abo.resetHeadPosition();
//            }
        }
    }
    
    
    private int getCharacterToMark(int[][] pairs){
        
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
        
        return characterToMark;
    }
        
    private int[][] getNearestNeighbourPairs(){

      ArrayList<Character> temp = (ArrayList<Character>)SceneCharacterManager.getCharacterArray().clone();
      int[][] markingPairs = new int[temp.size()/2][2];
      ArrayList<Integer> usedIDs = new ArrayList<Integer>();
      int index = 0;
      
      for(int i = 0; i < temp.size(); i++){

            BasketballCharacter x = (BasketballCharacter)temp.get(i); //player to test
            float minDist = 1000000;
            int minID = -1;
            
            if(!usedIDs.contains(x.getID())){ //if not yet used

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

               if(minID != -1){ 
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
    
    private void setDefensiveBehaviorState(){
        
        //sets behavior state if player is able to defend
        if(markedCharacter != null 
           && parentCharacter.perception.isLookingAtTarget(markedCharacter.getPosition())
           && parentCharacter.planner.getTargetPosition().setY(0).distance(parentCharacter.get2DPosition()) < 3){
            parentCharacter.setBehaviorState(3); 
        }
        else{
            parentCharacter.setBehaviorState(1);
        }    
    }
    
    public void markPossessor(){
        
        Vector3f newPos;
        int possessorID = SceneCharacterManager.getCharacterInPossession().getID();
        
        BasketballCharacter closestToPossessor = parentCharacter.planner.getClosestTeamMateToOpponent(SceneCharacterManager.getCharacterInPossession());
        
        if(!closestToPossessor.equals(parentCharacter)){ //character not closest - mark closest team mate
            newPos = this.makeMarkingDecision(possessorID, false);
            markedCharacter = parentCharacter.planner.getPlayerClosestTeammate(SceneCharacterManager.getCharacterInPossession());
        }
        else{//character closest - mark possessor and do blocking
            newPos = this.makeMarkingDecision(possessorID, true);
            markedCharacter = SceneCharacterManager.getCharacterInPossession();   
        }
        
        parentCharacter.planner.setTargetPosition(newPos);
        this.setDefensiveBehaviorState();
    }
    
    private Vector3f makeMarkingDecision(int playerID, boolean onlyMarker){
        
        Vector3f targetPos;
        Character c = SceneCharacterManager.getCharacterByID(playerID); 
        BasketballCharacter bc = (BasketballCharacter)c;
                
        if(!bc.isInPossession()){// player not in possession
            targetPos = this.getNonPossessorMarkTarget(playerID, onlyMarker);
        }
        else{
            targetPos = this.getPossessorMarkTarget(playerID, onlyMarker); 
        }
        
        return targetPos;
    
    }
    
    public Character getMarkCharacter(){
        return markedCharacter;
    }
    
    private Vector3f getPossessorMarkTarget(int playerID, boolean onlyMarker){
        
        Vector3f targetPos;
        Character c = SceneCharacterManager.getCharacterByID(playerID); 
                
        Vector3f toGoalVector = Court.getGoalPosition().subtract(c.getPosition()).setY(0);
        toGoalVector.normalizeLocal();
        float distFromPlayer = 5f;
        
        if(onlyMarker){
            if(Court.getGoalPosition().clone().setY(0).distance(c.getPosition().setY(0)) < 25){
                distFromPlayer = 3.5f;
            }
            targetPos = c.getPosition().add(toGoalVector.mult(distFromPlayer));
      //      System.out.println("non-double team " + parentCharacter.getID());
        }
        else{  
            targetPos = this.getDoubleTeamPosition(c);
      //      System.out.println("double team " + parentCharacter.getID());
        }
        
        return targetPos; 
    }
    
    private Vector3f getNonPossessorMarkTarget(int playerID, boolean onlyMarker){
        
        Vector3f targetPos;
        Character c = SceneCharacterManager.getCharacterByID(playerID); 
        BasketballCharacter bc = (BasketballCharacter)c;
                
        Vector3f toGoalVector = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(bc.getPosition(), Court.getGoalPosition()));
        float distFromPlayer = 5f;
        
        float distToGoal = bc.get2DPosition().distance(Court.getGoalPosition().clone().setY(0));

        if(distToGoal > 20){
            distFromPlayer = 15f;
        }
        else if(distToGoal < 5f){
            distFromPlayer = 3.5f;
        }
        
        targetPos = c.getPosition().add(toGoalVector.mult(distFromPlayer));
        return targetPos;
        
    }
    
    private Vector3f getDoubleTeamPosition(Character c){
        
        BasketballCharacter marked = (BasketballCharacter)c; //marked player
        BasketballCharacter closestOpponent = parentCharacter.planner.getPlayerClosestTeammate(marked); //closest opponent to marked character
        
        Vector3f markedPos = marked.get2DPosition();
        Vector3f closestOppPos = closestOpponent.get2DPosition();
        
        float f = Conversions.originToTargetAngle(markedPos, Court.getPoleLocation());
        
        boolean isAbove = Conversions.isAboveLine(marked.get2DPosition(), Court.getPoleLocation(), closestOppPos);
        float newAngle;
        
        if(isAbove){
            newAngle = Conversions.adjustAngleTo360(f + 45);
        }
        else{
            newAngle = Conversions.adjustAngleTo360(f - 45);
        }
        
        Vector3f angle = Conversions.degreesToNormalizedCoordinates(newAngle);
        Vector3f targetPos = markedPos.add(angle.mult(10f));
        
        return targetPos;
    }
    
    
}
