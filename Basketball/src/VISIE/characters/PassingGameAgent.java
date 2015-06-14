/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.AgentGesture;
import VISIE.models.BasketballPlayerModel;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author DiveshLala
 */
public class PassingGameAgent extends BasketballAgent{
    
    public PassingGameAgent(int i, BasketballPlayerModel am, RigidBodyControl p, float r, float height){
       super(i, am, p, r, height);
    }
    
    @Override
    public void setBehavior(){
        
    }
    
    @Override
    public void updateMovements(){
        
        if(this.isInPossession()){
            this.playAnimation(1, "passAction", 1, LoopMode.DontLoop);        
        }
       
    }
    
}
