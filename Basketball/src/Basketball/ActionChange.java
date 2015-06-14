/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.mathfunctions.Conversions;

/**
 *
 * @author Divesh
 */
public class ActionChange {
    
    public float posChange;
    public float fdChange;
    public float relRotChange;
    public float[] oppRRChange;
    public float dtoGChange;
    public float dtoMeChange;
    public float[] oppDistChange;
    
    public ActionChange(Action prev, Action next){
        
        posChange = next.getPosition().distance(prev.getPosition());
        fdChange = Conversions.minDistanceBetweenAngles(next.facingDirection, prev.facingDirection);
        relRotChange = next.relativeRotation - prev.relativeRotation;
        oppRRChange = new float[prev.oppRelativeRots.length];
        for(int i = 0; i < oppRRChange.length; i++){
            oppRRChange[i] = next.oppRelativeRots[i] - prev.oppRelativeRots[i];
        }
        dtoGChange = next.distToGoal - prev.distToGoal;
        dtoMeChange = next.distToMe - prev.distToMe;
        oppDistChange = new float[prev.oppDistances.length];
        for(int i = 0; i < oppDistChange.length; i++){
            oppDistChange[i] = next.oppDistances[i] - prev.oppDistances[i];
        }
        
    }
    
    @Override
    public String toString(){
        return "POS" + posChange + " FD" + fdChange + " RR" + relRotChange + " GOAL" + dtoGChange + " ME" + dtoMeChange;
    }
    
}
