/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import Basketball.CollabPlanner;
import Basketball.GameManager;
import Basketball.JointProjectManager;
import VISIE.gesturerecognition.SignalInterpreter;
import VISIE.learning.Context;
import VISIE.learning.ActionChange;
import VISIE.learning.ActionRecorder;
import VISIE.learning.KnowledgeBase;
import VISIE.learning.ShootContexts;
import VISIE.learning.SignalContexts;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.recording.Log;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author DiveshLala
 */
public class CollabAgent extends BasketballAgent{
    
    private SignalInterpreter signalInterpreter;
    private Timer signalFixedTimer = new Timer(true);
    private String currentAttention = "";
    private JointProjectManager currentJP;
    private String signalDescriptorFilePath = "config/individualKB.txt";
    private String commonGroundFilePath = "config/commonKB.txt";
    private long latestJPEndTime;
    private String latestJPToEnd = "";
//    private ActionRecorder actionRecords;
    protected boolean isLearnerAgent;
    
    
    public CollabAgent(int i, BPNewModel am, RigidBodyControl p, float r, float height){
        super(i, am, p, r, height);
        float[] bodyParams = {1,1,1,1,1,1};
        abo.setBodyParameters(bodyParams);
     //   abo.setShootPenalty(1.25f);
        planner = new CollabPlanner(this);
        latestJPEndTime = System.currentTimeMillis();
    }
    
    
    //used for learner
    public void recordActions(String s){
    
    }
    
    //used for learner
    protected void recordSignalContexts(String s1, String s2, boolean b){
    
    }
    
    //used for learner
    protected void recordShootContexts(){}
    

    
    public void setSignalInterpreter(){
        signalInterpreter = new SignalInterpreter(this,signalDescriptorFilePath);
        signalFixedTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                updateSignalInterpreter();
           //     System.out.println("sssfd");
            }
        }, 1, 10);
    }
    
    @Override
    public void updateAgentAttention(){
                
        int behaviorState = behaviorModule.getBehaviorState();
                
        if(behaviorState == 0){ //dribbling
            
            currentAttention = "location"; 
            this.doPosJPActivities();
        }        
        else if(behaviorState == 1 &&
            !(latestJPToEnd.equals("passBall") && this.getTimeSinceLatestJP() < 3)){ //non dribbling
            
            this.doNonPosJPActivities();
        }
        else if(behaviorState == 2){ //shooting 
            currentAttention = "goal";
        }
        else if(behaviorState == 3){ //defending
            currentAttention = "opponent";
        }
        else if(behaviorState == 4){ //passing
            currentAttention = "team mate";
        }
        else if(behaviorState == 5){
            currentAttention = "team mate";
        }
    }
    
    
    private void doPosJPActivities(){
        
            ArrayList<BasketballCharacter> teammates = this.getTeamMates();
            
            for(int i = 0; i < teammates.size(); i++){
                                
                String focus = signalInterpreter.getCharacterFocus(teammates.get(i));
                
                if(!signalInterpreter.getExplicitSignalObserved(teammates.get(i)).isEmpty() &&
                !(this.getTimeSinceLatestJP() < 5f) &&
                (currentJP == null || !currentJP.isActive())){
                    System.out.println("JP from attention");
                    this.setJointProject("passBall", teammates.get(i), true);
         //           currentthis.setJointProject("passBall", teammates.get(i), true);JP = new JointProjectManager("passBall", this, teammates.get(i), true);
                    currentAttention = "team mate";
                    break;
                }
                else if(focus.startsWith("char") &&
                !(this.getTimeSinceLatestJP() < 5f) &&
                        (currentJP == null || !currentJP.isActive())){
                    System.out.println("implicit jp recognized");
                    int focusCharacterID = Integer.parseInt(focus.replace("char", ""));

                    //attention of character is me
                    if(focusCharacterID == this.getID()){
                        this.setJointProject("passBall", teammates.get(i), false);
           //             currentJP = new JointProjectManager("passBall", this, teammates.get(i), false);
                        recordSignalContexts("receivePass", "implicit", false);
                        currentAttention = "team mate";
                        break;
                    }               
                }
                else{
                 //   System.out.println("srfsdffds");
                }
            }    
    }
    
    private void doNonPosJPActivities(){
        
            BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();

            if(possessor != null && this.getTeamID() == GameManager.getAttackingTeam()){
                if(!planner.isTargetReached(0.5f) && !signalInterpreter.getCharacterFocus(possessor).startsWith("char")){
                    
                    if(!signalInterpreter.getExplicitSignalObserved(possessor).isEmpty()){
                        this.setJointProject("receivePass", possessor, true);     
            //            currentJP = new JointProjectManager("receivePass", this, possessor, true);
                    }
                    else{                      
                        currentAttention = "location";               
                    }
                }
                else{
                    currentAttention = "possessor";
                                   
                    if(signalInterpreter.getCharacterFocus(possessor).startsWith("char")){
                        int focusCharacterID = Integer.parseInt(signalInterpreter.getCharacterFocus(possessor).replace("char", ""));
                        
                        //attention of character is me
                        if(focusCharacterID == this.getID()){
                                                        
                            if(currentJP == null || !currentJP.isActive()){
                                System.out.println("implicit jp recognized");
                                this.setJointProject("receivePass", possessor, true);
                //                currentJP = new JointProjectManager("receivePass", this, possessor, false);
                                recordSignalContexts("passBall", "implicit", true);
                            }                            
                        }               
                    }
                    else if(!signalInterpreter.getExplicitSignalObserved(possessor).isEmpty()){
                        System.out.println("explicit JP recognized");
                        this.setJointProject("receivePass", possessor, true);
                 //       currentJP = new JointProjectManager("receivePass", this, possessor, true);
                    }
                }
            }
            else if(possessor != null && this.getTeamID() != GameManager.getAttackingTeam()){
                currentAttention = "opponent";
            }
            else{
                currentAttention = "ball";
            }    
    }
    
    public void setCurrentFocus(String s){
        currentAttention = s;
    }
    
    @Override
    public void setBehavior(){
        if(!this.isDoingJP()){
            behaviorModule.updateBehavior();
        }
    }
    
    public String getCurrentFocus(){
        return currentAttention;
    }
    
    public void updateSignalInterpreter(){
        signalInterpreter.updateSignals();
        signalInterpreter.interpretSignals();
    }
    
    @Override
    public void updateMovements(){
        
        if(currentJP != null && currentJP.isActive()){
            currentJP.doActivities(signalInterpreter);       
        }
        else{
            super.updateMovements();
        }
    }
        
    public void setJointProject(String jpName, BasketballCharacter receiver, boolean isExplicit){
        currentJP = new JointProjectManager(jpName, this, receiver, isExplicit);
    }
    
    public float getTimeSinceLatestJP(){
       return (System.currentTimeMillis() - latestJPEndTime)/1000f;
    }
    
    public void setJPEndData(long l, String name){
        latestJPEndTime = l;
        latestJPToEnd = name;
    }
    
    public String getLatestCompletedJointProject(){
        return latestJPToEnd;
    }
    
    public boolean isDoingJP(){
        return currentJP != null && currentJP.isActive();
    }
        
//    @Override
//    public void checkBallStealing(Ball b, Vector3f vec){
//        
//        super.updateBallPossession(b, vec);
//    }
    
    public boolean isLearner(){
        return isLearnerAgent;
    }
    
    public Vector3f doStalemateActivity(){
        this.setBehaviorState(5);
        return super.doStalemateActivity();
    }
    
    @Override
    public void initializePositions(BasketballCharacter posChar){
        
        super.initializePositions(posChar);
        this.setSignalInterpreter();
    
    }
    
//    @Override
//    public void recordBatchData(){
//        
//        String s = this.getLearningData();
//        Log.clearFile("learning.txt");
//        Log.write("learning.txt", s);
//        String s2 = this.getSortedContexts();
//        Log.clearFile("contexts.txt");
//        Log.write("contexts.txt", s2);
//    
//    }
    
}
    

