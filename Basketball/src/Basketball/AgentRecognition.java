/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;

/**
 *
 * @author DiveshLala
 */
public class AgentRecognition {
    
    private BasketballAgent parentCharacter;
    
    public AgentRecognition(BasketballAgent ba){
        parentCharacter = ba;    
    }
    
    public boolean doRecognitionActivity(JointProject j, String movement, int b){
        if(b < 200){ //if initiator in JP, no need to recognize
            return false;   
        }
        else{ // if receiver, return true
            return true;
        }
    }
    
}
