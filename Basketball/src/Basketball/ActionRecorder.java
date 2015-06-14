/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class ActionRecorder {
    
    private BasketballAgent parentCharacter;
    private ArrayList<ActionChange> actionChangeList = new ArrayList<ActionChange>();
    private ArrayList<Action> actionList = new ArrayList<Action>();
    private ArrayList<BasketballCharacter> charactersToObserve = new ArrayList<BasketballCharacter>();
    private long lastRecordTime;
    private Action lastAction;
    
    public ActionRecorder(BasketballAgent ba){
        parentCharacter = ba;
        lastRecordTime = System.currentTimeMillis();
    }
    
    public void recordActions(){
        long l = System.currentTimeMillis();
        if(Math.abs(l - lastRecordTime) > 250){
            this.recordAction();
            lastRecordTime = l;
        } 
    }
    
    private void recordAction(){
        
        charactersToObserve = parentCharacter.getTeamMates();
        
        for(BasketballCharacter bc:charactersToObserve){
            Action action = new Action(bc, parentCharacter);
            
            if(lastAction != null){
                ActionChange change = new ActionChange(lastAction, action);
                actionChangeList.add(change);
                actionList.add(action);
            }  
            lastAction = action;
        }                
    }
    
    public ArrayList<ActionChange> getActionChanges(){
        return actionChangeList;    
    }
    
    public ArrayList<Action> getActions(){
        return actionList;
    }
    
}
