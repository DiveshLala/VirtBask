/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.CollabAgent;
import VISIE.characters.KinectPlayer;
import VISIE.characters.Player;
import VISIE.gesturerecognition.SignalInterpreter;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class JointProjectManager {
    
    
    private String projectName;
    private BasketballCharacter interactionCharacter;
    private CollabAgent parentCharacter;
    private boolean isActive;
    private long jpStartTime;
    private boolean explicitSignal;
    
    public JointProjectManager(String name, CollabAgent parent, BasketballCharacter character, boolean b){
        
        projectName = name;
        parentCharacter = parent;
        interactionCharacter = character;
        isActive = true;
        jpStartTime = System.currentTimeMillis();
        explicitSignal = b;  
        
        recordExplicitSignal();
    }
    
    //used for learner
    protected void recordExplicitSignal(){
        
    }
        
    public void doActivities(SignalInterpreter signalInterpreter){
        
  //      System.out.println(projectName);
        
        String focus = signalInterpreter.getCharacterFocus(interactionCharacter);
        
        //do this to check for explicit signal together with implicit
        if(!signalInterpreter.getExplicitSignalObserved(interactionCharacter).isEmpty()
            && !explicitSignal){
            explicitSignal = true;
            this.recordExplicitSignal();
        }
                
        
        if(projectName.equals("receivePass")){
            
            this.receivePass(focus);
        }
        
        else if(projectName.equals("passBall")){
                        
                if(parentCharacter.canDoClearPass(interactionCharacter, 4.5f)){
                    parentCharacter.setSpeed(0);
                    parentCharacter.playAnimation(1, "initiatePass", 1, LoopMode.DontLoop);
                    parentCharacter.doTurnAndPass(interactionCharacter);
                }
                else if(!parentCharacter.getCurrentAnimations().startsWith("pass")){
                    
                    Vector3f bestVec = this.getOpenSpace(true, 30);

                    if(bestVec != null){
                       parentCharacter.planner.setTargetPositionIgnoreCollision(bestVec.setY(0));
                       parentCharacter.abo.moveTowardsTarget(parentCharacter.planner.getTargetPosition(), true, true);
                       parentCharacter.abo.doDribblingAnimations();
                    }                   
            }
             
            //ball no longer in hands
            if(SceneCharacterManager.getCharacterInPossession() == null || !SceneCharacterManager.getCharacterInPossession().equals(parentCharacter)){
                this.endProject();
            }
            
            //gaze changed (only if explicit signal not seen)
            if(!focus.startsWith("char") || Integer.parseInt(focus.replace("char", "")) != parentCharacter.getID()){
                if(!explicitSignal){  
                    System.out.println("lost focus");
                    this.endProject();
                }
            }
            
            //too much time
            if(this.getElapsedTime() > 5f){
                this.endProject();
            }
        }
        else if(projectName.equals("getAttention")){
            
            this.getAttention();
             
        }
        else if(projectName.equals("celebration")){
            parentCharacter.setSpeed(0);
            parentCharacter.abo.turnBodyToTarget(interactionCharacter.getPosition());
                        
            if(parentCharacter.perception.isFacingEachOther(interactionCharacter, 45f)){
                parentCharacter.getModel().forceAnimation(1, "celebration", 0.3f, LoopMode.Loop);
            }
            
            //play has restarted
            if(GameManager.getGameState() == 0){
                this.endProject();
            }            
            
            //too much time
            if(this.getElapsedTime() > 6){
                this.endProject();
            }
            
            //finish after post celebration
            if(parentCharacter.getCurrentGestureName().startsWith("celebration") && 
               parentCharacter.getCurrentMovementProgress(1) > 0.95f){
                this.endProject();
            }
        }
        else if(projectName.equals("apology")){
            parentCharacter.setSpeed(0);
            parentCharacter.abo.turnBodyToTarget(interactionCharacter.getPosition());
            
            if(parentCharacter.perception.canSeeCharacter(interactionCharacter, 50) 
                &&parentCharacter.perception.isFacingEachOther(interactionCharacter, 50)){
        //        parentCharacter.playAnimation(1, "apologize", 0.5f, LoopMode.DontLoop);
            }
            
            //play has restarted
            if(GameManager.getGameState() == 0){
                this.endProject();
            }            
            
            //too much time
            if(this.getElapsedTime() > 5){
                this.endProject();
            }
            
            //finish after post celebration
            if(parentCharacter.getCurrentAnimations().split(",")[0].equals("apology") && 
               parentCharacter.getCurrentMovementProgress(1) > 0.95f){
                this.endProject();
            }
        }
        
        else if(projectName.equals("encourage")){ //encourage uses the apology signal but faster animation
            parentCharacter.setSpeed(0);
            parentCharacter.abo.turnBodyToTarget(interactionCharacter.getPosition());
            
            if(parentCharacter.perception.canSeeCharacter(interactionCharacter, 50) &&
                parentCharacter.perception.isFacingEachOther(interactionCharacter, 50)){
           //     parentCharacter.playAnimation(1, "apologize", 0.75f, LoopMode.DontLoop);
            }
            
            //play has restarted
            if(GameManager.getGameState() == 0){
                this.endProject();
            }            
            
            //too much time
            if(this.getElapsedTime() > 5){
                this.endProject();
            }
            
            //finish after post reassurance
            if(parentCharacter.getCurrentAnimations().split(",")[0].equals("apology") && 
               parentCharacter.getCurrentMovementProgress(1) > 0.95f){
                this.endProject();
            }
        }
    }
    
    private void endProject(){
        System.out.println("project ended");
        parentCharacter.setJPEndData(System.currentTimeMillis(), projectName);
        isActive = false;
        projectName = "";

    }
    
    private void receivePass(String focus){
        
        if(interactionCharacter.canDoClearPass(parentCharacter, 4.5f)){ //if can pass signal for pass

                parentCharacter.setSpeed(0);  
                parentCharacter.abo.turnBodyToTarget(interactionCharacter.get2DPosition());
                
                if(parentCharacter.perception.isWithinGaze(interactionCharacter.get2DPosition(), 10)){
                    parentCharacter.playAnimation(1, "receivePass", 1, LoopMode.DontLoop);
                }
//                else{
//                    parentCharacter.abo.turnBodyToTarget(interactionCharacter.get2DPosition());
//                }
                
                //focus changed
                if(!focus.startsWith("char") || Integer.parseInt(focus.replace("char", "")) != parentCharacter.getID()){
                    System.out.println("lost focus");
                    this.endProject();
                }

                //ball in different place
                if(SceneCharacterManager.getCharacterInPossession() == null || !SceneCharacterManager.getCharacterInPossession().equals(interactionCharacter)){
                    this.endProject();
                    parentCharacter.playAnimation(1, "postReceivePass", 1, LoopMode.DontLoop);
                }            
            }
            else{ //if cant pass, move into passing position
                //simulate pass by moving 45 degrees
            
                Vector3f bestVec = this.getOpenSpace(false, 45);
                
                if(bestVec != null){
                    parentCharacter.planner.setTargetPositionIgnoreCollision(bestVec.setY(0));
                    parentCharacter.abo.moveTowardsTarget(parentCharacter.planner.getTargetPosition(), true, false);
                }  
                
            }
    }
    
    private void getAttention(){
                                
            if(!parentCharacter.planner.isTargetReached(0.5f)){
                parentCharacter.abo.moveTowardsTarget(parentCharacter.planner.getTargetPosition(), true, false);
            }
            else{
                parentCharacter.abo.turnBodyToTarget(interactionCharacter.get2DPosition());
            }
            
            if(parentCharacter.perception.canSeeCharacter(interactionCharacter, 20)){
                int num = (int)(Math.random() * 3 + 1);
                String gestVersion = "";
                if(num > 1){
                    gestVersion = String.valueOf(num);
                }
                parentCharacter.playAnimation(1, "callForPass" + gestVersion, 1, LoopMode.DontLoop);
            }
                
            //ball out of possessors hands
            if(SceneCharacterManager.getCharacterInPossession() == null || !SceneCharacterManager.getCharacterInPossession().equals(interactionCharacter)){
                this.endProject();
            }
            
            //possessor moves closer - change to receive pass project
            if(parentCharacter.perception.isFacingEachOther(interactionCharacter, 20)){
                this.endProject();
                parentCharacter.setJointProject("receivePass", interactionCharacter, false);
            }
            
            //possessor moves away
            if(parentCharacter.get2DPosition().distance(interactionCharacter.get2DPosition()) > 25 ||
            !parentCharacter.perception.isFacingEachOther(interactionCharacter, 70f)){
                this.endProject();
            }
            
            if(this.getElapsedTime() > 2.5f){
                this.endProject();
            }            
    }
    
    public boolean isActive(){
        return isActive;
    }
    
    private float getElapsedTime(){
        return (System.currentTimeMillis() - jpStartTime) / 1000;
    }
    
    public String getName(){
        return projectName;
    }
    
    private boolean clearPassSimulation(Vector3f simPos){
        
        for(int i = 0; i < parentCharacter.getOpponents().size(); i++){
            BasketballCharacter opp = parentCharacter.getOpponents().get(i);
            boolean isOpponentThere = parentCharacter.perception.characterWithinLineBounds(simPos, interactionCharacter.get2DPosition(), opp);
            if(isOpponentThere){
                return false;
            }
        } 
        
        return true;
        
    }
    
    private Vector3f getOpenSpace(boolean isPasser, float degrees){
        
        BasketballCharacter passer, receiver;
            
        if(isPasser){
            passer = parentCharacter;
            receiver = interactionCharacter;
        }
        else{
            receiver = parentCharacter;
            passer = interactionCharacter;
        }

        float dist = receiver.get2DPosition().distance(passer.get2DPosition());
        float orientationAngle = Conversions.originToTargetAngle(passer.get2DPosition(), receiver.get2DPosition());               

        float lAngle = Conversions.adjustAngleTo360(orientationAngle + degrees);
        float rAngle = Conversions.adjustAngleTo360(orientationAngle - degrees);                

        Vector3f lDir = passer.getPosition().add(Conversions.degreesToNormalizedCoordinates(lAngle).mult(dist));
        Vector3f rDir = passer.getPosition().add(Conversions.degreesToNormalizedCoordinates(rAngle).mult(dist));

        ArrayList<Vector3f> testVecs = new ArrayList<Vector3f>();

        if((Court.isInsideCourt(lDir)) &&  this.clearPassSimulation(lDir)){
            testVecs.add(lDir);
        }
        if((Court.isInsideCourt(rDir)) && this.clearPassSimulation(rDir)){
            testVecs.add(rDir);
        }
        
        Vector3f bestVec = parentCharacter.planner.positionFurthestFromOpponents(testVecs);
        
        //if nothing is clear, get best point
        if(testVecs.isEmpty()){
            testVecs.add(lDir);
            testVecs.add(rDir);
            bestVec = parentCharacter.getPosition();
        }
        
        return bestVec;

    }
}
