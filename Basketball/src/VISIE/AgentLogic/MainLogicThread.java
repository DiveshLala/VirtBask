/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.AgentLogic;
import Basketball.Ball;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.Character;
import VISIE.characters.NonUserPlayer;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;
import com.jme3.math.Vector3f;
import java.util.Timer;

/**
 *
 * @author DiveshLala
 */
public class MainLogicThread {
    
    private ArrayList<Character> characterArray;
    private Ball ball;
    
    public MainLogicThread(ArrayList<Character> c, Ball b){
        characterArray = c;
        ball = b;
    }
    
    public void updateAgents(float tpf){ 
        
        for(int i = 0; i < characterArray.size(); i++){
            
        //    if(characterArray.get(i) instanceof VISIE.characters.BasketballAgent){
                characterArray.get(i).doUpdateActivity();
//                BasketballAgent b = (BasketballAgent)characterArray.get(i);   
//                    b.setBehavior();
//                    b.updateMovements();
//                    b.updateAgentAttention();
    //        }
//            else if(characterArray.get(i) instanceof VISIE.characters.NonUserPlayer){
//                NonUserPlayer nup = (NonUserPlayer)characterArray.get(i);
//           //     nup.get
//            }
        }
    }
    
    public void updateBall(){
        
        BasketballCharacter bc = SceneCharacterManager.getCharacterInPossession();
        
        if(bc != null){// a character has possession
            bc.doBallManipulation();
            ball.setLastTouched(bc.getID());
        }
        else{ // ball is loose
            ball.updateBallWithoutPossession();
        }
    }
    
    
    
}
