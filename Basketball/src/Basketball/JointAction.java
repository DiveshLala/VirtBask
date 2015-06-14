/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

/**
 *
 * @author Divesh
 */
public class JointAction {
    
    private int jointActionID;
    private int currentJointActionState;
    private int maxStates;
    
    
    public JointAction(){
        
        currentJointActionState = 0;
        maxStates = 3;
    
    }
    
    public void advanceJointAction(){
        currentJointActionState++;
    }
    
    public int getCurrentJointActionState(){
        return currentJointActionState;
    }
    
}
