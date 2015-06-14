/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import VISIE.Games.Game;
import VISIE.mathfunctions.Conversions;
import VISIE.recording.Log;
import VISIE.scenemanager.Court;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author DiveshLala
 */
public class KnowledgeBase {
    
    public ArrayList<ContextActionPair> posKnowledge;
    public ArrayList<ContextActionPair> nonPosKnowledge;
    public ArrayList<SignalContexts> signalContextKnowledge;
    public ArrayList<ShootContexts> shootContextKnowledge;
    
    public KnowledgeBase(){
        posKnowledge = new ArrayList<ContextActionPair>();    
        nonPosKnowledge = new ArrayList<ContextActionPair>(); 
        signalContextKnowledge = new ArrayList<SignalContexts>();
        shootContextKnowledge = new ArrayList<ShootContexts>();
    }
    
    public void updateKnowledgeBase(Context prevContext, Context nextContext, String state){
        
        ActionChange ac = new ActionChange(prevContext, nextContext);
        ContextActionPair cap = new ContextActionPair(prevContext, ac);
        
        if(state.equals("pos")){
            this.update(posKnowledge, cap);
        }   
        else if(state.equals("nonpos")){
            this.update(nonPosKnowledge, cap);
        }
                
    }
    
    public void updateSignalKnowledgeBase(SignalContexts sc){
        signalContextKnowledge.add(sc);
    }
    
    public void updateShootKnowledgeBase(ShootContexts sc){
        shootContextKnowledge.add(sc);
    }
    
    private void update(ArrayList<ContextActionPair> kb, ContextActionPair cap){
        
        if(kb.isEmpty()){
            kb.add(cap);
        }
        else if(!cap.isDoNothing()){
            ContextActionPair capToMergeWith = null;
            boolean doMerge = false;
            double lowest = 10000;
            
            for(ContextActionPair a:kb){
                double val = this.compareContexts(a, cap); //compare contexts
                if(val < lowest){                           // if context is most similar
                    lowest = val;
                    if(this.compareKnowledge(a, cap)){     //if outcome is the same do merge
                        doMerge = true;
                        capToMergeWith = a;
                    }                
                    else{                                   //if not same dont merge
                        doMerge = false;
                    }
                }
            }
            
            if(!doMerge){
                kb.add(cap);
                Log.write("times.txt", "0," + Game.getElapsedTime());
//                if(posKnowledge.get(posKnowledge.size() - 1).equals(cap)){
//                    System.out.println("pos " + kb.size());
//                }
//                else{
//                    System.out.println("non pos " + kb.size());
//                }

   //             System.out.println("new CAP added: " + cap.getContext() + " " + cap.getAction() + cap.getChangeTrend());
            }
            else if(capToMergeWith != null){
 //               System.out.println("CAP to merge: " + cap.getContext() + " " + cap.getAction() + " " + cap.getChangeTrend());
 //               System.out.println("CAP to merge with: " + capToMergeWith.getContext() + " " + capToMergeWith.getAction() + " " + capToMergeWith.getChangeTrend());
                this.mergeKnowledge(cap, capToMergeWith);
  //              System.out.println("updated CAP: " + capToMergeWith.getContext() + " " + capToMergeWith.getAction() + " " + capToMergeWith.getChangeTrend());
    //            System.out.println("merge");
            }
        }
        
    }
    
    private void mergeKnowledge(ContextActionPair cap, ContextActionPair capToMergeWith){
        capToMergeWith.increaseEvidence();
        capToMergeWith.updateTrends(cap.getChangeTrend());
        capToMergeWith.updateContexts(cap.getContext());
    }
    
    private boolean compareKnowledge(ContextActionPair baseCap, ContextActionPair newCap){
        
        float similarityThreshold = 15;
        double contextSimilarity = this.compareContexts(baseCap, newCap);
        boolean isChangeSimilar = this.compareActionChanges(baseCap, newCap);
        
        if(contextSimilarity < similarityThreshold && isChangeSimilar){ //contexts and outcomes are similar
            return true;
        }
        else if(contextSimilarity < similarityThreshold && !isChangeSimilar){//similar context, diff outcome (check exclusive feature)
            return false;
        }
        else if(contextSimilarity > similarityThreshold && isChangeSimilar){//diff context same outcome - (check inclusive feature)
            return false;
        }
        else{//contexts and outcomes different
            return false;
        }
          
    }
    
    public double compareContexts(ContextActionPair cap1, ContextActionPair cap2){
        
        //compare movement contexts through Euclidean distance
        Context con1 = cap1.getContext();
        Context con2 = cap2.getContext();        
        float tot = 0;
        
        tot += Math.pow(con1.distToGoal - con2.distToGoal, 2);
        tot += Math.pow(con1.distToMe - con2.distToMe, 2);
        tot += Math.pow(0.05 * (Conversions.minDistanceBetweenAngles(con1.facingDirection, con2.facingDirection)), 2);
        tot += Math.pow(0.05 * (con1.goalRelRot - con2.goalRelRot), 2);
        tot += Math.pow(0.05 * (con1.relativeRotation - con2.relativeRotation), 2);
        for(int i =0; i<con1.oppDistances.length; i++){
            tot += Math.pow(con1.oppDistances[i] - con2.oppDistances[i], 2);
            tot += Math.pow(0.05 * (con1.oppRelativeRots[i] - con2.oppRelativeRots[i]), 2);
        }
        
        return Math.sqrt(tot);
    }
    
    private boolean compareActionChanges(ContextActionPair baseCap, ContextActionPair newCap){
        
       if(baseCap.getTarget() == newCap.getTarget()){
           if(baseCap.getTarget() >= 0){
               return true;
           }
           else{
                ArrayList<Float> baseChange = baseCap.getChangeTrend();
                ArrayList<Float> newChange = newCap.getChangeTrend();
                float totDiff = 0; //difference between action changes

                if(baseChange.get(0) > 1 && newChange.get(0) > 1f){ // if change contains movement
                    for(int i = 0; i < 4; i++){
                        totDiff += Math.abs(baseChange.get(i) - newChange.get(i));
                    }        
                }
                else if(baseChange.get(0) < 1 && newChange.get(0) < 1){ // change is rotation only

                    for(int i = 4; i < baseChange.size(); i++){
                        totDiff += Math.abs(baseChange.get(i) - newChange.get(i));
                    }
               //     System.out.println(totDiff);
                }
                else{
                    return false;
                }
                return totDiff < 50;
              }
       }
       else{
           return false;
       }
        
//        ArrayList<Float> baseChange = baseCap.getChangeTrend();
//        ArrayList<Float> newChange = newCap.getChangeTrend();
//        
//        float totDiff = 0; //difference between action changes
//        
//        if(baseChange.get(0) > 0.1 && newChange.get(0) > 0.1f){ // if change contains movement
//            for(int i = 0; i < 4; i++){
//                totDiff += Math.abs(baseChange.get(i) - newChange.get(i));
//            }        
//        }
//        else if(baseChange.get(0) < 0.1 && newChange.get(0) < 0.1){ // change is rotation only
//            
//            for(int i = 4; i < baseChange.size(); i++){
//                totDiff += Math.abs(baseChange.get(i) - newChange.get(i));
//            }
//       //     System.out.println(totDiff);
//        }
////        else{
////            return false;
////        }
//        
//        return totDiff < 50;
    }
    
    //gets gesture and returns associated joint project
    public String gestureJPMapper(String gesture){
        
        if(gesture.equals("callForPass")){
            return "getAttention";
        }
        
        return "";
    
    }
    
    public String getKnowledgeText(){
        
        StringBuilder sb = new StringBuilder();
        String x = System.getProperty("line.separator");
        
        sb.append("POS KNOWLEDGE:" + x);
        for(ContextActionPair cap:posKnowledge){
            int target = cap.getTarget();
            sb.append(cap.getContextData() + x);
            sb.append(cap.getChangeTrend().get(0) + ", " + cap.getNormalizedTrend() + ", " + target + x);
        }
        sb.append(x);
        sb.append("NONPOS KNOWLEDGE:" + x);
        for(ContextActionPair cap:nonPosKnowledge){
            int target = cap.getTarget();
            sb.append(cap.getContextData() +  x );
            sb.append(cap.getChangeTrend().get(0) + ", " + cap.getNormalizedTrend() + ", " + target + x);
        }
        sb.append(x);
        sb.append("SIGNAL KNOWLEDGE:" + x);
        for(SignalContexts sc:signalContextKnowledge){
            if(!sc.getSignalContexts().isEmpty()){
                Context cont = sc.getSignalContexts().get(sc.getSignalContexts().size() - 1);
                int hasBall = (sc.isSignalWithBall()) ? 1 : 0;
                String signal = sc.getOutput();
                sb.append(cont.getContextData() + "," + signal + ", " + hasBall + x);
            }
        }
        sb.append(x);
        sb.append("SHOOT KNOWLEDGE:" + x);
        for(ShootContexts sc:shootContextKnowledge){
            Context cont = sc.getShootContexts().get(sc.getShootContexts().size() - 1);
            sb.append(cont.getContextData() + x);
        }
        
        String s = sb.toString();
        
        s = s.replaceAll("\\[", "");
        s = s.replaceAll("\\]", "");
         
        return s;
    
    }
    
    public String getSortedContexts(){
        
        StringBuilder sb = new StringBuilder();
        String x = System.getProperty("line.separator");
            
        Collections.sort(posKnowledge, new Comparator<ContextActionPair>(){
            
            public int compare(ContextActionPair cap1, ContextActionPair cap2){
                Integer bob = cap1.getEvidenceNumber();
                return bob.compareTo(cap2.getEvidenceNumber());  
            }
    
        });
        Collections.sort(nonPosKnowledge, new Comparator<ContextActionPair>(){
            
            public int compare(ContextActionPair cap1, ContextActionPair cap2){
                Integer bob = cap1.getEvidenceNumber();
                return bob.compareTo(cap2.getEvidenceNumber());  
            }
    
        });
        
        Collections.reverse(posKnowledge);
        Collections.reverse(nonPosKnowledge);
        
        ArrayList<ContextActionPair> posImitatedContexts = new ArrayList<ContextActionPair>();
        ArrayList<ContextActionPair> nonposImitatedContexts = new ArrayList<ContextActionPair>();
        
        int totalImitated = 0;        
//        
        sb.append("POS CONTEXTS:" + x);
        for(ContextActionPair cap:posKnowledge){
            totalImitated += cap.getTimesImitated();
            int target = cap.getTarget();
            sb.append(cap.getContextData() + x);
            sb.append(cap.getChangeTrend().get(0) + ", " + cap.getNormalizedTrend() + ", " + target + x);
            sb.append("evidence number = " + cap.getEvidenceNumber() + x);
            sb.append("times imitated = " + cap.getTimesImitated() + x);
            if(cap.getTimesImitated() > 0){
                posImitatedContexts.add(cap);
            }
        }
        
        sb.append("total contexts =" + posKnowledge.size() + "total imitated =" + totalImitated + x);
        sb.append(x);
        
        totalImitated = 0;

        sb.append("NONPOS CONTEXTS:" + x);
        for(ContextActionPair cap:nonPosKnowledge){
            totalImitated += cap.getTimesImitated();
            int target = cap.getTarget();
            sb.append(cap.getContextData() +  x );
            sb.append(cap.getChangeTrend().get(0) + ", " + cap.getNormalizedTrend() + ", " + target + x);
            sb.append("evidence number = " + cap.getEvidenceNumber() + x);
            sb.append("times imitated = " + cap.getTimesImitated() + x);
            if(cap.getTimesImitated() > 0){
                nonposImitatedContexts.add(cap);
            }
        }        
        sb.append("total contexts =" + nonPosKnowledge.size() + "total imitated =" + totalImitated + x);
        sb.append(x);
        
        sb.append("POS IMITATED" + x);
        for(ContextActionPair cap:posImitatedContexts){
            sb.append(cap.getTimesImitated() +  "," + cap + "," + cap.getTarget() + "," + x);
        }
        sb.append(x);
        sb.append("NONPOS IMITATED" + x);
        for(ContextActionPair cap:nonposImitatedContexts){
            sb.append(cap.getTimesImitated() + "," + cap + "," + cap.getTarget() + "," + x);
        }
        
        String s = sb.toString();
        return s;
        
    }
    
}
