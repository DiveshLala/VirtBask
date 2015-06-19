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
    JointProjectManagerModule jpm;
    
    public BehaviorModule(BasketballAgent c){
        
        parentCharacter = c;    
        
    }
    
    
    public void updateBehavior(){
                
        if(isEngagedInJointProject()){
            //already engaged in joint project
        }  
        else if(parentCharacter.isInPossession()){
     //       this.setBehaviorState(101);
            parentCharacter.planner.decidePossessionAction(behaviorState);
        }
        else{
            BasketballCharacter possessionCharacter = SceneCharacterManager.getCharacterInPossession();
            
            //ball is in air
            if(possessionCharacter == null){
                parentCharacter.planner.makeFreeBallDecision(behaviorState);   
            }
            
            else if(possessionCharacter.getTeamID() == parentCharacter.getTeamID()){
                parentCharacter.planner.decideNonPossessionAttackAction(behaviorState);
            }
            else{
                parentCharacter.planner.decideDefenseAction(behaviorState);
            }
           // behaviorState = 1;
        }
    }
    
    public boolean isEngagedInJointProject(){
        return behaviorState > 100;
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
