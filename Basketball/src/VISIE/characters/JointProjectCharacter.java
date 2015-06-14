/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import VISIE.characters.Character;
import java.util.ArrayList;
import Basketball.JointProject;


/**
 *
 * @author DiveshLala
 */
public interface JointProjectCharacter {
    
    public void receiveJointProjectFrom(Character sender, int JPID);
    public void receiveSharedProjectInfo(int id, String projectName, int updateType);
    public void receiveRecognitionConfirmation(Character sender, int JPID, String receivedAction);
    public void sendJPPerceptionSignal(BasketballAgent ba, JointProject j);
    public void receiveJPPerceptionSignal(Character c, JointProject j);
    public void receiveAbortNotification(Character c, JointProject jp);
    
}
