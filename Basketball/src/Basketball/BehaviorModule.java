/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;
import java.util.ArrayList;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.math.Vector3f;

/**
 *
 * @author Divesh
 */
public class BehaviorModule {
    
    BasketballAgent parentCharacter;
    int behaviorState; // 1 - 100 is non-JP state
                       // 101 + is JP state
    
    public BehaviorModule(BasketballAgent c){
        
        parentCharacter = c;    
        
    }
    
    
    public void updateBehavior(){
        
        int gameState = GameManager.getGameState();
        
        if(gameState == 0){ //live game
            
            BasketballCharacter possessionCharacter = SceneCharacterManager.getCharacterInPossession();
            
            //ball is in air
            if(possessionCharacter == null){
                parentCharacter.planner.makeFreeBallDecision(behaviorState); 
            }
            else if(parentCharacter.isInPossession()){
                parentCharacter.planner.decidePossessionAction(behaviorState);
            }
            else if(possessionCharacter.getTeamID() == parentCharacter.getTeamID()){
        //        behaviorState = 1;
                parentCharacter.planner.decideNonPossessionAttackAction(behaviorState);
            }
            else{
                parentCharacter.planner.decideDefenseAction(behaviorState);
            }
        }
        else{
            
            parentCharacter.planner.clearRecords();
                        
            if(gameState == 1){ //ball is out
                parentCharacter.planner.doOutOfBoundActivity();
                parentCharacter.setBehaviorState(1);
            }            
            else if(gameState == 2){//team possession swapped
                parentCharacter.planner.swapRoleActivity();
            }
            else if(gameState == 3){
                parentCharacter.planner.doAfterScoreActivity();
     //           parentCharacter.setBehaviorState(1);
            }
//            else if(parentCharacter.isInPossession()){
//                parentCharacter.planner.decidePossessionAction(behaviorState);
//            }
            else{
            }
        }

    }
    
    public int getBehaviorState(){
        return behaviorState;
    }
    
    public void setBehaviorState(int s){
        if(s != behaviorState){
            System.out.println(parentCharacter.getID() + " set from " + behaviorState + " to " + s);
        }
        behaviorState = s;
    }
    
   
}
