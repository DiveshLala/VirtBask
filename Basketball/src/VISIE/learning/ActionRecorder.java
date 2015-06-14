/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import Basketball.*;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.CollabAgent;
import VISIE.characters.CollabLearner;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Divesh
 */
public class ActionRecorder {
    
    private BasketballAgent parentCharacter;
    private ArrayList<ActionChange> actionChangeList = new ArrayList<ActionChange>();
    private ArrayList<Context> contextList = new ArrayList<Context>();
    private ArrayList<BasketballCharacter> charactersToObserve = new ArrayList<BasketballCharacter>();
    private long lastRecordTime;
    private Context lastAction;
    
    public ActionRecorder(BasketballAgent ba){
        parentCharacter = ba;
        lastRecordTime = System.currentTimeMillis();
    }
    
    public void recordActions(String state){
        long l = System.currentTimeMillis();
        if(Math.abs(l - lastRecordTime) > 250){
            int numPast = 5;
            this.recordAction();
            
            if(actionChangeList.size() > numPast && contextList.size() > numPast){
                CollabLearner cl = (CollabLearner)parentCharacter;
                Context oldContext = contextList.get(contextList.size() - 1 - numPast);
                Context newContext = contextList.get(contextList.size() - 1);
                cl.updateKnowledge(oldContext, newContext, state);
            }
            
            lastRecordTime = l;
        } 
    }
    
    private void recordAction(){
        
        charactersToObserve = parentCharacter.getTeamMates();
        
        for(BasketballCharacter bc:charactersToObserve){
            Context context = new Context(bc, parentCharacter);
            
            if(lastAction != null){
                ActionChange change = new ActionChange(lastAction, context);
                actionChangeList.add(change);
                contextList.add(context);
            }  
            lastAction = context;
        } 
        
        //removes old entries
        if(contextList.size() > 200){
            actionChangeList.remove(0);
            contextList.remove(0);
        }   
    }
    
    public ArrayList<Context> recordSignalContexts(int numToRecord, boolean hasPossession){
        
      ArrayList<Context> signalRecord = new ArrayList<Context>();
        
      for(int i = contextList.size() - 1; i > 0; i--){
          if(signalRecord.size() > numToRecord){
              break;
          }
          else{
            if(hasPossession == contextList.get(i).inPossession){
                signalRecord.add(contextList.get(i));
            }
            else{
                break;
            }
          }
      }
      
      Collections.reverse(signalRecord); 
      return signalRecord;
      
    }
        
    public ArrayList<ActionChange> getActionChanges(){
        return actionChangeList;    
    }
    
    public ArrayList<Context> getActions(){
        return contextList;
    }
    
    public void clearRecords(){
        actionChangeList.clear();
        contextList.clear();
    }
    
}
