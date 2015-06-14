/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.AgentGesture;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class ImitationAgent extends BasketballAgent{
    
    private boolean isRecordingGesture = false;
    private ArrayList<String> tempGestureFrames = new ArrayList<String>();
    private String tempGestureType = "";
    private int tempBallManip = 0;
    private ArrayList<AgentGesture> observedGestures = new ArrayList<AgentGesture>();
    private boolean hasReceivedBall = false;
    private Player player;
    
    public ImitationAgent(int i, BasketballPlayerModel am, RigidBodyControl p, float r, float height){
        super(i, am, p, r, height);
        abo.setSpeed("Turn", 2f);
    }
    
    public void setPlayer(Player p){
        player = p;
    }
    
    @Override
    public void setBehavior(){
        
        if(this.isInPossession()){
            
            if(!hasReceivedBall){
                hasReceivedBall = true;
            }
            else if(planner.isTargetReached(1)){
                planner.setTargetPosition(this.getPosition().add(Vector3f.UNIT_Z.mult(10)));
            }
                    
        }
        else{
            
        }
        
        if(this.isInPossession() && this.getCurrentGesture() == null){
            this.doPlayback("Pass");
        }                     
    }
    
    @Override
    public void updateMovements(){
        
        AgentGesture ag = this.getCurrentGesture();
        
        if(!observedGestures.isEmpty() && ag != null){
            ag.playGesture();

            //do any ball manipulations
            if(this.isInPossession() && ag.getCurrentFrame() >= ag.getBallManipulationFrame()){
                if(ag.getGestureType().contains("Pass")){
                    ball.passBall(this.getFacingDirection());
                    this.removePossession();
                    hasReceivedBall = false;
                }
            }
        }
        else{
            if(!this.planner.isTargetReached(1)){
                agentModel.removeUserControl();
                abo.moveTowardsTarget(planner.getTargetPosition(), false, false);
            }
            else{
                
                this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
                
                if(!hasReceivedBall){
                    abo.turnBodyToTarget(player.getPosition());
//                    abo.doTurningAnimation();
                }
            }
        }
    }
    
    public AgentGesture getCurrentGesture(){
        for(int i = 0; i < observedGestures.size(); i++){
            if(observedGestures.get(i).getCurrentFrame() >= 0){
                return observedGestures.get(i);
            }
        }
        return null;
    }
    
    //plays back one of the related gestures
    public void doPlayback(String s){
        
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        
        for(int i = 0; i < observedGestures.size(); i++){
            
            observedGestures.get(i).stopPlayback();
            
            if(observedGestures.get(i).getGestureType().contains(s)){
                candidates.add(i);
            }
        }
        
        if(candidates.size() > 0){
            int randInd = (int)(Math.random() * candidates.size());
            observedGestures.get(randInd).startPlayback();
        }

    }
        
    public void triggerRecording(ArrayList<String> record, String type, int ballManipFrame){
        
        if(!isRecordingGesture && tempGestureFrames.isEmpty()){
                tempGestureFrames = (ArrayList<String>)record.clone();
                tempGestureType = type;
                tempBallManip = ballManipFrame;
                isRecordingGesture = true;
        }
    }
    
    public void recordGesture(String frameData){
        
        if(isRecordingGesture){
            tempGestureFrames.add(frameData);

            if(tempGestureFrames.size() >= 500){
                this.finishGestureRecording();
            }
        }
    }
    
    
    private void finishGestureRecording(){
        AgentGesture gest = new AgentGesture(tempGestureFrames, characterModel);
        gest.setGestureType(tempGestureType);
        gest.setManipulationFrame(tempBallManip);
        observedGestures.add(gest);
        isRecordingGesture = false;
        tempGestureFrames.clear();
        tempGestureType = "";
        tempBallManip = 0;
    
    }
    
    
}
