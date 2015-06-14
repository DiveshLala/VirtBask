/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.CollabPlanner;
import VISIE.learning.ActionRecorder;
import VISIE.learning.Context;
import VISIE.learning.KnowledgeBase;
import VISIE.learning.ShootContexts;
import VISIE.learning.SignalContexts;
import VISIE.models.AnimatedModel;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.recording.Log;
import com.jme3.bullet.control.RigidBodyControl;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class CollabLearner extends CollabAgent {
    
    private ActionRecorder actionRecords;
    public KnowledgeBase knowledgeBase;
    
    public CollabLearner(int i, BPNewModel am, RigidBodyControl p, float r, float height){
        super(i, am, p, r, height);
        actionRecords = new ActionRecorder(this);
        knowledgeBase = new KnowledgeBase();
        isLearnerAgent = true;
    }
    
    @Override
    public void recordSignalContexts(String name, String outputAction, boolean inPossession){
        
        ArrayList<Context> cont = actionRecords.recordSignalContexts(10, inPossession);

        if(!cont.isEmpty()){
            ArrayList<SignalContexts> a = knowledgeBase.signalContextKnowledge;

            if(!a.isEmpty()){
                Context c1 = cont.get(cont.size() - 1);
                SignalContexts b = a.get(a.size() - 1);
                Context c2 = b.getSignalContexts().get(b.getSignalContexts().size() - 1);
                if(!c1.isContextEqual(c2)){
                    SignalContexts sc = new SignalContexts(name, cont, inPossession, outputAction);
                    knowledgeBase.updateSignalKnowledgeBase(sc); 
                }
            }  
            else{
                SignalContexts sc = new SignalContexts(name, cont, inPossession, outputAction);
                knowledgeBase.updateSignalKnowledgeBase(sc); 
            }
        }
    }
    
    @Override
    public void recordShootContexts(){
                
        ArrayList<Context> cont = actionRecords.recordSignalContexts(10, true);

        if(!cont.isEmpty()){
            ArrayList<ShootContexts> a  = knowledgeBase.shootContextKnowledge;

            if(!a.isEmpty()){
                Context c1 = cont.get(cont.size() - 1);
                ShootContexts b = a.get(a.size() - 1);
                Context c2 = b.getShootContexts().get(b.getShootContexts().size() - 1);
                if(!c1.isContextEqual(c2)){
                    ShootContexts sc = new ShootContexts(cont);
                    knowledgeBase.updateShootKnowledgeBase(sc);
                }
            }  
            else{
                ShootContexts sc = new ShootContexts(cont);
                knowledgeBase.updateShootKnowledgeBase(sc);  
            }
        }

    }
    
    public String getLearningData(){
        return knowledgeBase.getKnowledgeText();
    }
    
    public String getSortedContexts(){
        return knowledgeBase.getSortedContexts();
    }
    
    @Override
    public void recordActions(String state){
        actionRecords.recordActions(state);
    }
    
    public ActionRecorder getActionRecorder(){
        return actionRecords;
    }
    
    public void updateKnowledge(Context prevContext, Context newContext, String state){
        knowledgeBase.updateKnowledgeBase(prevContext, newContext, state);
    }
    
    public void clearRecords(){ 
        this.getActionRecorder().clearRecords();
    }
    
    @Override
    public void recordBatchData(){
        
        String s = this.getLearningData();
        Log.clearFile("learning.txt");
        Log.write("learning.txt", s);
        String s2 = this.getSortedContexts();
        Log.clearFile("contexts.txt");
        Log.write("contexts.txt", s2);
    
    }
    
    protected void recordExplicitSignal(Character interactionCharacter, String projectName, boolean hasPos){
                
            //add to signal database
            String outputAction = "explicit:" + interactionCharacter.getCurrentGestureName();

            if(!outputAction.equals("")){
                System.out.println(outputAction);
                this.recordSignalContexts(projectName, outputAction, hasPos);
            }
    }
}
    

