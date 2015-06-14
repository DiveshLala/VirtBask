/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import Basketball.*;
import VISIE.mathfunctions.Conversions;
import com.jme3.math.Vector3f;

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
    public float goalRotChange;
    public Vector3f posVectorChange;
    public Vector3f endPos;
    
    public ActionChange(Context prev, Context next){
        
        endPos = next.getPosition();
        posVectorChange = next.getPosition().subtract(prev.getPosition());
        posChange = next.getPosition().distance(prev.getPosition());
        fdChange = Conversions.minDistanceBetweenAngles(next.facingDirection, prev.facingDirection);
        relRotChange = next.relativeRotation - prev.relativeRotation;
        oppRRChange = new float[prev.oppRelativeRots.length];
        for(int i = 0; i < oppRRChange.length; i++){
            oppRRChange[i] = next.oppRelativeRots[i] - prev.oppRelativeRots[i];
        }
        dtoGChange = next.distToGoal - prev.distToGoal;
        dtoMeChange = next.getPosition().distance(prev.myPos) - (prev.getPosition().distance(prev.myPos));
        oppDistChange = new float[prev.oppPositions.length];
        for(int i = 0; i < oppDistChange.length; i++){
            float prevDistance = prev.getPosition().distance(prev.oppPositions[i]);
            float newDistance = next.getPosition().distance(prev.oppPositions[i]);
            oppDistChange[i] = newDistance - prevDistance;
        }
        goalRotChange = next.goalRelRot - prev.goalRelRot;        
    }
    
    @Override
    public String toString(){
        return "POS" + posChange + " FD" + fdChange + " RR" + relRotChange + " GOAL" + dtoGChange + " ME" + dtoMeChange + " GOALROT" + goalRotChange;
    }
    
}
