/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.BehaviorXMLProcessor;
import VISIE.characters.BasketballAgent;
import VISIE.characters.JointProjectCharacter;
import VISIE.characters.Character;

/**
 *
 * @author DiveshLala
 */
public class AgentJPMessaging{
    
    BasketballAgent parentCharacter;
    
    public AgentJPMessaging(JointProjectCharacter jpc){
        parentCharacter = (BasketballAgent)jpc;
    }
    
    public void sendJPPerceptionSignal(BasketballAgent ba, JointProject j){
        ba.receiveJPPerceptionSignal(parentCharacter, j);        
    }
    
    public void receiveJPPerceptionSignal(Character c, JointProject j){
        if(parentCharacter.perception.canSeeCharacter(c)){
            if(!parentCharacter.JPManager.jointProjectExists(j.getSharedID()) && j.isCharacterTargeted(parentCharacter)){
                this.manageIncomingJointProject(c, j);
            }
        }
        else{
   //         character is not part of the joint project
        }                   
    }
    
    //managers incoming joint projects which may or may not be consecutive
    private void manageIncomingJointProject(Character c, JointProject jp){
        
        //not yet engaged in JP - intialise JP
        if(parentCharacter.getBehaviorState() < 100){
            parentCharacter.receiveJointProjectFrom(c, jp.getSharedID());
            parentCharacter.receiveSharedProjectInfo(jp.getSharedID(), "", 1);
        }
        else if(parentCharacter.JPManager.isWaiting()){ //check if the JP is valid (new)
            if(!BehaviorXMLProcessor.isPrecedingBehavior(jp.getProjectName(), parentCharacter.getBehaviorState())){
                parentCharacter.receiveJointProjectFrom(c, jp.getSharedID());
                parentCharacter.receiveSharedProjectInfo(jp.getSharedID(), "", 1);
                parentCharacter.JPManager.removeWaiting();
            }
        }
    }
    
    public void receiveSharedProjectInfo(int JPID, String attribute, int updateType){
        parentCharacter.JPManager.updateJointProjectStatus(JPID, attribute, updateType);
    }
    
    public void receiveJointProjectFrom(Character sender, int jpID){
        JointProject j = new JointProject(sender, jpID);
        parentCharacter.JPManager.addJointProject(j);

        //   200 is state for joint project proposal "type unknown"
        parentCharacter.setBehaviorState(200);
        parentCharacter.JPManager.setBehaviorState(200);
        
        //if sent by player, also update perception signal
        if(sender instanceof VISIE.characters.Player){
            j.setInitialActionTime();
        }
        
        System.out.println("joint project received by " + parentCharacter.getID() + " from " + j.getInitiator().getID());
    }
    
    public void sendAbortNotification(JointProject jp){
        
        //aborter is initiator
        if(jp.getParticipants() != null){
            for(int i = 0; i < jp.getParticipants().size(); i++){
                JointProjectCharacter c  = (JointProjectCharacter)jp.getParticipants().get(i);
                c.receiveAbortNotification(this.parentCharacter, jp);
            }           
        }
        //aborter is receiver
        else{
            if(jp.getInitiator().getCharacterType().equals("BasketballAgent")){
                JointProjectCharacter c  = (JointProjectCharacter)jp.getInitiator();
                
                //send notification to initiator and itself
                c.receiveAbortNotification(this.parentCharacter, jp);
                parentCharacter.receiveAbortNotification(parentCharacter, jp);
            }
            else{//human user
                parentCharacter.receiveAbortNotification(parentCharacter, jp); 
            }
        }
    }
    
    
    
}
