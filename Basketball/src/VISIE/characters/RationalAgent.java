/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.AgentPlanning;
import Basketball.GameManager;
import Basketball.RationalPlanner;
import VISIE.models.BasketballPlayerModel;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author DiveshLala
 */
public class RationalAgent extends BasketballAgent{
    
    public RationalAgent(int i, BasketballPlayerModel am, RigidBodyControl p, float r, float height){
        super(i, am, p, r, height);
        planner = new RationalPlanner(this);
        float[] bodyParams = {1.25f,1.25f,1.25f,1.25f,1.25f,1.25f};
        abo.setBodyParameters(bodyParams);
    }
    
    @Override
    public void doUpdateActivity(){
        if(GameManager.getGameState() != 0){
            planner.possession.setStationaryTime();
            planner.nonPossession.setStationaryTime();
        }
        super.doUpdateActivity();
    }
    
}
