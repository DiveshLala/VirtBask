/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.RationalAgent;

/**
 *
 * @author DiveshLala
 */
public class RationalPlanner extends AgentPlanning{
    
    public RationalPlanner(RationalAgent ra){
        super(ra);
    }
    
    @Override
    public void decidePossessionAction(int behaviorState){
        
        
        if(behaviorState == 0){//has ball
            parentCharacter.setActionState(0); //dribbling to target
        //    possession.makePossessionDecision();
        }
        
        else if(behaviorState == 2){//shooting
            parentCharacter.setActionState(2); //shooting
        }
        
        possession.makeRationalDoubleTeamDecision();
        nonPossession.setStationaryTime();
    }
    
    @Override
    public void makeNonPossessionAttackDecision(){
        nonPossession.gotoDoubleTeamFreeSpace();
        possession.setStationaryTime();
    }
    
}
