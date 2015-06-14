/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import Basketball.*;
import VISIE.Games.Game;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.CollabAgent;
import VISIE.characters.CollabLearner;
import VISIE.learning.Context;
import VISIE.learning.ActionChange;
import VISIE.mathfunctions.Conversions;
import VISIE.recording.Log;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.Scene;
import VISIE.scenemanager.SceneCreator;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class ImitationLearner {
    
    CollabPlanner parentPlanner;
    CollabLearner parentCharacter;
    ContextActionPair currentImitatedCAP;
    private boolean rawImitationFlag;
    private ArrayList<ContextActionPair> capHistory;
    
    
    public ImitationLearner(CollabPlanner cp){
        parentPlanner = cp;
        parentCharacter = (CollabLearner)parentPlanner.getParentCharacter();
        capHistory = new ArrayList<ContextActionPair>();
    }
    
    public void decideNextAction(ArrayList<ActionChange> changeList, String state){
        
        ArrayList<ContextActionPair> npKnowledge = parentCharacter.knowledgeBase.nonPosKnowledge; 
        ArrayList<ContextActionPair> posKnowledge = parentCharacter.knowledgeBase.posKnowledge; 
        ArrayList<SignalContexts> signalKnowledge = parentCharacter.knowledgeBase.signalContextKnowledge;
        ArrayList<ShootContexts> shootKnowledge = parentCharacter.knowledgeBase.shootContextKnowledge;
        
    //    System.out.println(npKnowledge.size() + " " + posKnowledge.size() + " " + signalKnowledge.size() + " " + shootKnowledge.size());
        
//        this.removeContextDisplay();
//        this.displayContexts(npKnowledge, posKnowledge);
        
        if(state.equals("pos") && this.checkShootContexts(shootKnowledge)){ //check for shooting 
            parentCharacter.setBehaviorState(2);
            Log.write("times.txt", "2," + Game.getElapsedTime() + "");
        }
        
          else if(!this.checkSignalContexts(signalKnowledge, state)){//checks if previous signals can be performed

            if(state.equals("nonpos")){
                if(npKnowledge.isEmpty()){
                    this.doRawImitation(changeList);
                }
                else{
                    this.doLearnedAction(npKnowledge, changeList);
                }
            }
            else if(state.equals("pos")){

                if(posKnowledge.isEmpty()){
                    this.doRawImitation(changeList);
                }
                else{
                    this.doLearnedAction(posKnowledge, changeList);
                }

            }
        }
        
        this.updateCAPHistory();
    }
    
    private void updateCAPHistory(){
        
//        if(capHistory.isEmpty()){
//            capHistory.add(currentImitatedCAP);
//        }
//        else if(currentImit){
//       //     capHistory.add(currentImitatedCAP);
//        }
                
    }
            
    private boolean checkSignalContexts(ArrayList<SignalContexts> signalKnowledge, String state){
        
        CollabAgent ca = (CollabAgent)parentCharacter;
        BasketballCharacter bc = parentCharacter.getNearestTeammate();
        Context myCon = new Context(parentCharacter, bc);
        SignalContexts closestContext = null;
        float threshold = 10;              
        double closestDist = 10000;
                
        if(state.equals("pos")){
            for(SignalContexts sc:signalKnowledge){
                if(sc.isSignalWithBall() && !sc.getSignalContexts().isEmpty()){ 
                    Context cont = sc.getSignalContexts().get(sc.getSignalContexts().size() - 1);

                    double comp = this.compareContexts(myCon, cont);
                    if(comp <= threshold && comp < closestDist){
                        closestDist = comp;
                        closestContext = sc;
                    }
                }
            }
        }
        else if(state.equals("nonpos")){
            for(SignalContexts sc:signalKnowledge){
                if(!sc.isSignalWithBall() && !sc.getSignalContexts().isEmpty()){
                    
                    Context cont = sc.getSignalContexts().get(sc.getSignalContexts().size() - 1);
                    
                    double comp = this.compareContexts(myCon, cont);
                    if(comp <= threshold && comp < closestDist){
                        closestDist = comp;
                        closestContext = sc;
                    }
                }
            }
        
        }
        
        if(closestContext != null){
            //IMPLICIT SIGNAL IS SAME JP NAME
            //ANY EXPLICIT SIGNAL HAS TO MAP GESTURE TO JP
            if(closestContext.getOutput().equals("implicit") && ca.getTimeSinceLatestJP() > 1.5f){
                System.out.println("JP implicit initialized " + closestContext.getSignalName());
                parentCharacter.setJointProject(closestContext.getSignalName(), bc, false);
                Log.write("times.txt", "3," + Game.getElapsedTime() + "," + closestContext);
                return true;
            }
            else if(closestContext.getOutput().contains("explicit") && ca.getTimeSinceLatestJP() > 1.5f){
                System.out.println("JP explicit initialized " + closestContext.getSignalName());                
                String gesture = closestContext.getOutput().split(":")[1];
                String jpName = parentCharacter.knowledgeBase.gestureJPMapper(gesture);
                
                if(!jpName.isEmpty()){
                    Log.write("times.txt", "4," + Game.getElapsedTime() + "," + closestContext);
                    parentCharacter.setJointProject(jpName, bc, false);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean checkShootContexts(ArrayList<ShootContexts> shootKnowledge){
        
        BasketballCharacter bc = parentCharacter.getNearestTeammate();
        Context myCon = new Context(parentCharacter, bc);
        double closest = 10000;
        float threshold = 10;
        
        for(ShootContexts sc:shootKnowledge){
            double d = this.compareContexts(sc.getShootContexts().get(sc.getShootContexts().size() - 1), myCon);
            if(d < closest){
                closest = d;
            }
        }
    //    System.out.println(closest);
        return closest < threshold;
    }
    
    private void doRawImitation(ArrayList<ActionChange> changeList){
        
    //    System.out.println("imitation");
        
        int mostRecent = 5;
        
        if(changeList.size() > mostRecent){
            
            float posTrend = 0;
            float fdTrend = 0;
            float rrTrend = 0;
            float goalRotTrend = 0;
            float oppRotTrend = 0;
            float goalTrend = 0;
            float meTrend = 0;
            float oppDistTrend = 0;
            
            Vector3f teamMatePos = parentCharacter.getNearestTeammate().getPosition();
            Vector3f opponentPos = parentCharacter.getOpponents().get(0).getPosition();
            Vector3f goalPos = Court.getGoalPosition();
            
            for(int i = changeList.size() - mostRecent; i < changeList.size(); i++){
                
                ActionChange ac = changeList.get(i);
                posTrend += ac.posChange;
                fdTrend += ac.fdChange;
                rrTrend += ac.relRotChange;
                goalTrend += ac.dtoGChange;
                meTrend += ac.dtoMeChange;
                for(float f:ac.oppDistChange){
                    oppDistTrend += f;
                }
                oppDistTrend = oppDistTrend/ac.oppDistChange.length;
                
                goalRotTrend += ac.goalRotChange;
                rrTrend += ac.relRotChange;
                for(float f:ac.oppRRChange){
                    oppRotTrend += f;
                }
                oppRotTrend = oppRotTrend/ac.oppRRChange.length;
            }
            
            if(posTrend > 1){//move somewhere
                                
                //find ratio of where moved (to goal, to player, opponents, etc.)
                float tot = Math.abs(goalTrend) + Math.abs(oppDistTrend) + Math.abs(meTrend);
                float goalRat = Math.abs(goalTrend/tot) * 100;
                float oppRat = Math.abs(oppDistTrend/tot) * 100;
                float meRat = Math.abs(meTrend/tot) * 100;
                float maxVal = Math.max(goalRat, Math.max(oppRat, meRat));
                
                //infer focus of attention of possessor
                if(goalRat == maxVal && goalTrend < 0){
                    if(parentCharacter.get2DPosition().distance(goalPos) > 5){
                        float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), goalPos);
                        parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                    }
                    else{
                        parentPlanner.setTargetPosition(parentCharacter.getPosition());
                    }
                }
                else if(oppRat == maxVal && oppDistTrend < 0){
                    float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), opponentPos);
                    parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                }
                else if(meRat == maxVal && meTrend < 0){
                    float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), teamMatePos);
                    parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                }
                else{
                    parentPlanner.setTargetPosition(parentCharacter.getPosition());
                }              
            }  
            else{
                float minVal = Math.min(rrTrend, Math.min(oppRotTrend, goalRotTrend));
                
                if(rrTrend == minVal && rrTrend < 0){
                    parentCharacter.abo.turnBodyToTarget(teamMatePos);
//                    parentCharacter.abo.doTurningAnimation();
                } 
                else if(oppRotTrend == minVal && oppRotTrend < 0){
                    parentCharacter.abo.turnBodyToTarget(teamMatePos);
    //                parentCharacter.abo.doTurningAnimation();
                }
                else if(goalTrend == minVal && goalTrend < 0){
                    parentCharacter.abo.turnBodyToTarget(goalPos);
    //                parentCharacter.abo.doTurningAnimation();
                }
            }
        }
        
        rawImitationFlag = true;
        
    }
    
    private void doLearnedAction(ArrayList<ContextActionPair> kb, ArrayList<ActionChange> changeList){
          
        BasketballCharacter bc = parentCharacter.getNearestTeammate();
        Context myCon = new Context(parentCharacter, bc);
        
        double val = 1000000;
        float threshold = 30;
        float discrimValue = 1f; //use this for leeway to switch CAPS
        double currentDist = threshold + 2f;
           
        ContextActionPair closestCap = null;
        
        if(currentImitatedCAP != null){
           currentDist =  this.compareContexts(currentImitatedCAP.getContext(), myCon);
           closestCap = currentImitatedCAP;
        }
        
        //if current imitation is a learned CAP, then make it more difficult to leave
        //if current imitation is null make it easier to get in and then hard to leave (because it becomes a CAP)
        //this stops consecutive switching
        
        for(ContextActionPair cap:kb){
            double d = this.compareContexts(myCon, cap.getContext());
            if(d < val && d < currentDist - discrimValue){
                val = d;
                closestCap = cap;
            }
        }
        
        int target;
        
        if(closestCap != null){
            target = closestCap.getTarget();
        }
        else{
            target = -1;
        }
        
        rawImitationFlag = false;
        
//        if(val > threshold){
//       //     System.out.println("not close");
//            this.doRawImitation(changeList);
//        }
        if(target == -1){  //no matching probabilities for anything - do imitation
    //        System.out.println("no matching ");
            this.doRawImitation(changeList);
        }
        else if(closestCap.isMovementAmbiguous()){
//            System.out.println("ambiguous " + closestCap.getAction().endPos);
            this.doAmbiguousMovement(closestCap, changeList);
        }
        else{
//            System.out.println("do action" + target);
            this.doAction(target, changeList);
        }
        
        if(!rawImitationFlag){
            this.updateImitatedContexts(closestCap, myCon);            
        }
        else{
            currentImitatedCAP = null;
        }
        
    }
    
    private void updateImitatedContexts(ContextActionPair closestCap, Context myCon){
                
        if(currentImitatedCAP == null || !currentImitatedCAP.equals(closestCap)){
            System.out.println("previous imitation " + currentImitatedCAP);
            if(currentImitatedCAP != null){
                System.out.println(this.compareContexts(myCon, currentImitatedCAP.getContext()) + " " + this.compareContexts(myCon, closestCap.getContext()));
            }
            currentImitatedCAP = closestCap;
            closestCap.increaseImitation();
            Log.write("times.txt", "1," + Game.getElapsedTime() + "");
        }
    }
    
    private double compareContexts(Context con1, Context con2){
            
        float tot = 0;
        float a = 0.05f;
        
        tot += Math.pow(con1.distToGoal - con2.distToGoal, 2);
        tot += Math.pow(con1.distToMe - con2.distToMe, 2);
        tot += Math.pow(a * (Conversions.minDistanceBetweenAngles(con1.facingDirection, con2.facingDirection)), 2);
        tot += Math.pow(a * (con1.goalRelRot - con2.goalRelRot), 2);
        tot += Math.pow(a * (con1.relativeRotation - con2.relativeRotation), 2);
        for(int i =0; i<con1.oppDistances.length; i++){
            tot += Math.pow(con1.oppDistances[i] - con2.oppDistances[i], 2);
            tot += Math.pow(a * (con1.oppRelativeRots[i] - con2.oppRelativeRots[i]), 2);
        }
        
        return Math.sqrt(tot);
    }
    
    private void doAction(int target, ArrayList<ActionChange> changeList){
        
        Vector3f goalPos = Court.getGoalPosition();
        Vector3f oppPos = parentCharacter.getOpponents().get(0).getPosition();
        Vector3f mePos = parentCharacter.getNearestTeammate().getPosition();
        
        if(target == 0){
            parentPlanner.setGoalAsTarget(5);
        }
        else if(target == 1){
            parentPlanner.setTargetPosition(oppPos);
        }
        else if(target == 2){
            parentPlanner.setCharacterAsTarget(parentCharacter.getNearestTeammate(), 5);
        }
        else if(target == 3){
            parentPlanner.setTargetPosition(parentCharacter.getPosition());
            parentCharacter.abo.turnBodyToTarget(goalPos);
//            parentCharacter.abo.doTurningAnimation();
        }
        else if(target == 4){
            parentPlanner.setTargetPosition(parentCharacter.getPosition());
            parentCharacter.abo.turnBodyToTarget(oppPos); 
  //          parentCharacter.abo.doTurningAnimation();
        }
        else if(target == 5){
            parentPlanner.setTargetPosition(parentCharacter.getPosition());
            parentCharacter.abo.turnBodyToTarget(mePos); 
   //         parentCharacter.abo.doTurningAnimation();
        }
        else{
            this.doRawImitation(changeList);
        }
    }
    
    private void doAmbiguousMovement(ContextActionPair cap, ArrayList<ActionChange> changeList){
        
        Vector3f vec = cap.getAction().endPos;
        
        if(parentCharacter.get2DPosition().distance(vec) > 2f){
            parentCharacter.planner.setTargetPosition(vec);
        }
        else{
            parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
            this.doRawImitation(changeList);
        }
    
    }
    
    private void displayContexts(ArrayList<ContextActionPair> np, ArrayList<ContextActionPair> pos){
       for(ContextActionPair cap:np){
           Vector3f bob = cap.getContext().getPosition();
           SceneCreator.addSphereMarker(new Vector3f(bob.x, 15, bob.z), "cap", 0.5f, "blue");
       } 
       for(ContextActionPair cap:pos){
           Vector3f bob = cap.getContext().getPosition();
           SceneCreator.addSphereMarker(new Vector3f(bob.x, 15, bob.z), "cap", 0.5f, "red");
       } 
       
       System.out.println();
    }
    
    private void removeContextDisplay(){
        
        SceneCreator.removeSphereMarker("cap");
    
    }
    
    
    
}
