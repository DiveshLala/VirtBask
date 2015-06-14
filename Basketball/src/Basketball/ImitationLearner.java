/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.CollabAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class ImitationLearner {
    
    CollabPlanner parentPlanner;
    
    public ImitationLearner(CollabPlanner cp){
        parentPlanner = cp;
    }
    
    public void decideNextAction(ArrayList<ActionChange> changeList, ArrayList<Action> states){
        
        //if no learned, do imitation
        this.doRawImitation(changeList, states);
        
        
        //else
        
    }
    
    private void doRawImitation(ArrayList<ActionChange> changeList, ArrayList<Action> states){
        
        CollabAgent parentCharacter = (CollabAgent)parentPlanner.parentCharacter;
        
        int mostRecent = 5;
        
        if(changeList.size() > mostRecent){
            
            float posTrend = 0;
            float fdTrend = 0;
            float rrTrend = 0;
            float goalTrend = 0;
            float meTrend = 0;
            float oppDistTrend = 0;
            
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
            }
            
            if(posTrend > 0.1){//move somewhere
                                
                //find ratio of where moved (to goal, to player, opponents, etc.)
                float tot = Math.abs(goalTrend) + Math.abs(oppDistTrend) + Math.abs(meTrend);
                float goalRat = Math.abs(goalTrend/tot) * 100;
                float oppRat = Math.abs(oppDistTrend/tot) * 100;
                float meRat = Math.abs(meTrend/tot) * 100;
                
                //infer focus of attention of possessor
                if(goalRat == Math.max(goalRat, Math.max(oppRat, meRat)) && goalTrend < 0){
                    float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), Court.getGoalPosition());
                    parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                }
                else if(oppRat == Math.max(oppRat, Math.max(goalRat, meRat)) && oppDistTrend < 0){
                    float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), parentCharacter.getOpponents().get(0).getPosition());
                    parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                  //  parentPlanner.setTargetPosition(parentCharacter.getOpponents().get(0).getPosition());
                }
                else if(meRat == Math.max(meRat, Math.max(goalRat, oppRat)) && meTrend < 0){
                    float angle = Conversions.originToTargetAngle(parentCharacter.getPosition(), parentCharacter.getTeamMates().get(0).getPosition());
                    parentPlanner.setTargetPosition(parentCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(angle).mult(posTrend)));
                }
                else{
                    parentPlanner.setTargetPosition(parentCharacter.getPosition());
                }            
                
            }
            
        }
    }
    
    
  //  private get 
    
}
