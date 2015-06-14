/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import VISIE.mathfunctions.Conversions;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class ContextActionPair {
    
    private Context context;
    private ActionChange actionChange;
 //   private ArrayList<ArrayList<Float>> similarContexts;
    private boolean movementAmbiguous;
    private ArrayList<Float> movementMeans; //means of all the movements
    private ArrayList<Float> contextMeans; //means of all the contexts
    private ArrayList<float[]> minMaxes;
    private ArrayList<Float> contextSDs;    //std devs of all the contexts
    
    private int totalContexts;
    private int timesImitated;
    private boolean subsumeFlag = false;
    
    public ContextActionPair(Context a, ActionChange ac){
        context = a;
        actionChange = ac;
        movementMeans = new ArrayList<Float>();
        contextMeans = new ArrayList<Float>();
        contextSDs = new ArrayList<Float>();
        minMaxes = new ArrayList<float[]>();
        
        for(int i = 0; i < 10; i++){
            movementMeans.add(0f);
        }
    //    similarContexts = new ArrayList<ArrayList<Float>>();
        this.calculateTrend();
        this.initializeContextStats();
        totalContexts++;
    }
    
    public Context getContext(){
        return context;
    }
    
    public ActionChange getAction(){
        return actionChange;
    }
    
    private void calculateTrend(){
       
       movementMeans.set(0, actionChange.posChange);
       
       if(movementMeans.get(0) > 0.1f){ // if trend is movement
        
            float goalTrend = actionChange.dtoGChange;
            float meTrend = actionChange.dtoMeChange;
            float oppDistTrend = 0;

            for(int i = 0; i < actionChange.oppDistChange.length; i++){
                oppDistTrend += actionChange.oppDistChange[i];
            }
            oppDistTrend = oppDistTrend/actionChange.oppDistChange.length;

            //find ratio of where moved (to goal, to player, opponents, etc.)
            float tot = Math.abs(goalTrend) + Math.abs(oppDistTrend) + Math.abs(meTrend);
            movementMeans.set(1, (goalTrend/tot) * 100);     // goal dist
            movementMeans.set(2, (oppDistTrend/tot) * 100);  // opp dist
            movementMeans.set(3, (meTrend/tot) * 100);       // me dist
      //      similarContexts.add(movementMeans);
            
            //find ratios of intention
            float a =0, b = 0, c = 0;
            
            if(goalTrend < 0){
                a = Math.abs(goalTrend/actionChange.posChange);
            }
            if(meTrend < 0){
                c = Math.abs(meTrend/actionChange.posChange);
            }
            if(oppDistTrend < 0){
                b = Math.abs(oppDistTrend/actionChange.posChange);
            }
            
            if(a > 0.5 || b > 0.5 || c > 0.5){
                movementAmbiguous = false;
            }
            else{
                movementAmbiguous = true;
            }
            
      //      System.out.println(movementAmbiguous);
              
       }
       else if(actionChange.fdChange > 10f){  //if trend is staying still and rotating
           
            float goalRotTrend = actionChange.goalRotChange;
            float relRotTrend = actionChange.relRotChange;
            float oppRotTrend = 0;

            for(int i = 0; i < actionChange.oppRRChange.length; i++){
                oppRotTrend = actionChange.oppRRChange[i];
            }
            oppRotTrend = oppRotTrend/actionChange.oppRRChange.length;

//            //find ratio of where rotated (to goal, to player, opponents, etc.)
            float tot = Math.abs(goalRotTrend) + Math.abs(relRotTrend) + Math.abs(oppRotTrend);
            movementMeans.set(4, movementMeans.get(4) + (goalRotTrend/tot) * 100);     // goal dist
            movementMeans.set(5, movementMeans.get(5) + (oppRotTrend/tot) * 100);  // opp dist
            movementMeans.set(6, movementMeans.get(6) + (relRotTrend/tot) * 100);       // me dist
     //       similarContexts.add(movementMeans);
            
    //        System.out.println("new  rot " + this.toString());
       }
       
    }
    
    public Vector3f getMovementVector(){
        return actionChange.posVectorChange;
    }
    
    public ArrayList<Float> getChangeTrend(){    
        return movementMeans;
    }
    
    public ArrayList<Float> getNormalizedTrend(){
        
        ArrayList<Float> normalized = new ArrayList<Float>();
        
        float tot = Math.abs(movementMeans.get(1)) + Math.abs(movementMeans.get(2)) + Math.abs(movementMeans.get(3));
        normalized.add((movementMeans.get(1)/tot) * 100);
        normalized.add((movementMeans.get(2)/tot) * 100);
        normalized.add((movementMeans.get(3)/tot) * 100);
        
        tot = Math.abs(movementMeans.get(4)) + Math.abs(movementMeans.get(5)) + Math.abs(movementMeans.get(6));
        normalized.add((movementMeans.get(4)/tot) * 100);
        normalized.add((movementMeans.get(5)/tot) * 100);
        normalized.add((movementMeans.get(6)/tot) * 100);
        
        
        for(int i = 0; i < normalized.size(); i++){
            if(Float.isNaN(normalized.get(i))){
                normalized.set(i, 0f);
            }       
        }
        
        return normalized;
    
    }
    
    @Override
    public String toString(){
        return  context + "\n" + 
                "Change = " + "goal dist: " + movementMeans.get(1) + " opp dist: " + movementMeans.get(2) + " me dist: " + movementMeans.get(3) + "\n" + 
                "goal rot: " + movementMeans.get(4) + " opp rot: " + movementMeans.get(5) + " me rot: " + movementMeans.get(6); 
    }
    
    public void updateTrends(ArrayList<Float> newMovements){
        
   //     System.out.println(movementMeans);
   //     System.out.println(newMovements);
           
        for(int i = 0; i < movementMeans.size(); i++){
            float oldMean = movementMeans.get(i);
            float newValue = newMovements.get(i);
            movementMeans.set(i, Conversions.updatedMean(oldMean, newValue, totalContexts));
        } 
        
    //    System.out.println(movementMeans);
    //    System.out.println();
    }
    
    public int getTarget(){
        
        ArrayList<Float> normTrend = this.getNormalizedTrend();
        float maxProb = 0;
        int ind = -1;
        
        if(this.getChangeTrend().get(0) > 1f){ //action is a movement
            for(int i = 0; i < 3; i++){
                if(Math.abs(normTrend.get(i)) > maxProb){
                    maxProb = Math.abs(normTrend.get(i));
                    ind = i;
                }
            }
        }
        else{                                       //action is a rotation
            for(int i = 3; i < 6; i++){
                if(Math.abs(normTrend.get(i)) > maxProb){
                    maxProb = Math.abs(normTrend.get(i));
                    ind = i;
                }
            }
        }
        
        return this.inferTarget(ind, normTrend);
    
    }
    
    private int inferTarget(int ind, ArrayList<Float> normTrend){
        
        float goalVal = normTrend.get(0);
        float oppVal = normTrend.get(1);
        float meVal = normTrend.get(2);
        float goalRot = normTrend.get(3);
        float oppRot = normTrend.get(4);
        float meRot = normTrend.get(5);
        
        if(ind == 0){ // GOAL
            if(goalVal < 0){
                return 0;
            }
            else if(oppVal < 0 && oppVal < meVal){
                return 1;
            }
            else if(meVal < 0 && meVal < oppVal){
                return 2;
            }
        }
        else if(ind == 1){ //OPPONENT
            if(oppVal < 0){
                return 1;
            }
            else if(goalVal < 0 && goalVal < meVal){
                return 0;
            }
            else if(meVal < 0 && meVal < goalVal){
                return 2;
            }
        }
        else if(ind == 2){
            if(meVal < 0){   // ME
                return 2;
            }
            else if(oppVal < 0 && oppVal < goalVal){
                return 1;
            }
            else if(goalVal < 0 && goalVal < oppVal){
                return 0;
            }
        }
        else if(ind == 3){   //
            if(goalRot < 0){
                return 3;
            }
            else if(oppRot < 0 && oppRot < meRot){
                return 4;
            }
            else if(meRot < 0 && meVal < oppRot){
                return 5;
            }
        }
        else if(ind == 4){   //

            if(oppRot < 0){
                return 4;
            }
            else if(goalRot < 0 && goalRot < meRot){
                return 3;
            }
            else if(meRot < 0 && meRot < goalRot){
                return 5;
            }
        }
        else if(ind == 5){   //

            if(meRot < 0){
                return 5;
            }
            else if(oppRot < 0 && oppRot < goalRot){
                return 4;
            }
            else if(goalRot < 0 && goalRot < oppRot){
                return 3;
            }
        }
        
        return -1;
    }
    
    public void updateContexts(Context cont){
        
        ArrayList<Float> newContextData = new ArrayList<Float>();
        
    //    newContextData.add(cont.facingDirection); 
        newContextData.add(cont.relativeRotation);
        newContextData.add(cont.distToMe);
        newContextData.add(cont.goalRelRot);
        newContextData.add(cont.distToGoal);
        for(float f:cont.oppRelativeRots){
            newContextData.add(f);
        }
        for(float f:cont.oppDistances){
            newContextData.add(f);
        }
        
    //    System.out.println(contextMeans);
    //    System.out.println(contextSDs);
    //    System.out.println(newContextData);

        for(int i = 0; i < contextMeans.size(); i++){
                
            float oldMean = contextMeans.get(i);
            float newValue = newContextData.get(i);
            contextMeans.set(i, Conversions.updatedMean(oldMean, newValue, totalContexts));
            
            double newVar = Conversions.updatedVariance(contextSDs.get(i), newValue, oldMean, contextMeans.get(i), totalContexts, true);
            contextSDs.set(i, (float)newVar);
            
            float[] minMax = minMaxes.get(i);
            if(newValue < minMax[0]){
                minMax[0] = newValue;
            }
            if(newValue > minMax[1]){
                minMax[1] = newValue;
            }
        } 
        
    //    System.out.println(contextMeans);
//        System.out.println(contextSDs);
//        System.out.println("num of contexts " + totalContexts);
//
//        for(int i = 0; i < contextMeans.size(); i++){
//            System.out.println("mean " + contextMeans.get(i) + " min:" + minMaxes.get(i)[0] + " max:" + minMaxes.get(i)[1]);
//            float maxZ = (minMaxes.get(i)[1] - contextMeans.get(i))/ contextSDs.get(i);
//            float minZ = (minMaxes.get(i)[0] - contextMeans.get(i))/ contextSDs.get(i);
//            System.out.println("max Z: " + maxZ+ " minZ: " + minZ);
//        }
        
        
     //   System.out.println();
        
    }
    
    public boolean shouldSubsume(ContextActionPair testCap){
        
        ArrayList<Float> testContextData = testCap.getContextData();
        boolean[] inRange = new boolean[testContextData.size()];
                
        for(int i = 0; i < testContextData.size(); i++){
            float f = testContextData.get(i);
            if(f > minMaxes.get(i)[0] && f < minMaxes.get(i)[1]){
                inRange[i] = true;
            }
        } 
        
        int numWithin = 0;
        
        for(boolean b:inRange){
            if(b){
                numWithin++;
            }
        }
        
        return numWithin > 6;
    }
    
    
    
    public void increaseEvidence(){

        totalContexts++;
        if(totalContexts % 10 == 0){
            subsumeFlag = true;
        }
        if(totalContexts % 10 == 0){
            this.updateStats();
        }
    }
    
    private void updateStats(){
        
//        System.out.println(this.context);
//        System.out.println(this.getAction());
//        System.out.println(this.getChangeTrend());
//        System.out.println(this.getNormalizedTrend());
        
        for(int i = 0; i < contextMeans.size(); i++){
         //  System.out.println("mean " + contextMeans.get(i) + " min:" + minMaxes.get(i)[0] + " max:" + minMaxes.get(i)[1]);
            float maxZ = (minMaxes.get(i)[1] - contextMeans.get(i))/ contextSDs.get(i);
            float minZ = (minMaxes.get(i)[0] - contextMeans.get(i))/ contextSDs.get(i);
        //    System.out.println("max Z: " + maxZ+ " minZ: " + minZ);
            if(maxZ < 3 && minZ > -3){
                context.updateContext(contextMeans.get(i), i);
            }
        }   
    }
    
    public int getEvidenceNumber(){
        return totalContexts;
    }
    
    public boolean isMovementAmbiguous(){
        return movementAmbiguous;
    }
    
    private void initializeContextStats(){
  //     contextMeans.add(context.facingDirection);
       
       //player info
       contextMeans.add(context.relativeRotation);
       contextMeans.add(context.distToMe);
       
       //goal info
       contextMeans.add(context.goalRelRot);
       contextMeans.add(context.distToGoal);
       
       //opponent info
       for(float f:context.oppRelativeRots){
           contextMeans.add(f);
       }
       for(float f:context.oppDistances){
           contextMeans.add(f);
       }
       
       //SD info
       for(float f:contextMeans){
           contextSDs.add(0f);
           float[] minMax = {f,f};
           minMaxes.add(minMax);
       }
       
    }
    
    public ArrayList<Float> getContextData(){
        
        return context.getContextData();
    }
    
    public void setSubsumeFlag(boolean b){
        subsumeFlag = b;
    }
    
    public boolean getSubsumeFlag(){
        return subsumeFlag;
    }
    
    public boolean isDoNothing(){

        for(int i = 1; i < this.getNormalizedTrend().size(); i++){
            if(this.getNormalizedTrend().get(i) != 0){
                return false;
            }
        }
        return true;
    }
    
    public void increaseImitation(){
        timesImitated++;
     //   System.out.println("imitated " + timesImitated + " times");
    }
    
    public int getTimesImitated(){
        return timesImitated;
    }
    
}
