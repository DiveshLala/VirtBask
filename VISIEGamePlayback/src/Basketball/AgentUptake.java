/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.JointProjectXMLProcessor;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.Character;

/**
 *
 * @author DiveshLala
 */
public class AgentUptake {
    
    private BasketballAgent parentCharacter;
    
    public AgentUptake(BasketballAgent ba){
        parentCharacter = ba;
    }
    
    public int actionFeasibility(JointProject jp, Character c){
        //acceptance
        
        String s = JointProjectXMLProcessor.getActivity(jp.getProjectName(), "uptake", "receiver");
        
        if(s.equals("examine goal")){
            return 1;
        }
        else if(s.equals("receive ball")){
            BasketballCharacter bc = (BasketballCharacter)c;
            
            //successfull pass
            if(parentCharacter.isInPossession()){
                return 1;
            }
            //waiting for ball
            //if waiting too long, abort project
            else if(bc.isInPossession()){
                if(jp.getElapsedPerceptionTime() < 3000){
                    return 2;
                }
                else{
                    return 0;
                }
            }
            //ball is in neither hands
            //if pass hasnt been accomplished within certain time, abort project
            else{
                if(jp.getElapsedTime() > 3000){
                    System.out.println("missed pass!");
                    return 0;
                }
                else{
                    return 2;
                }
            }
        }
        
        return -1;
    }
    
}
