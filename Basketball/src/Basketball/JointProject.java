/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.characters.Character;
import VISIE.characters.JointProjectCharacter;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Divesh
 */
public class JointProject {
    
    private String jointProjectName;
    private int currentJointActionState;
    private int maxStates;
    private int role;  // 0 is initiator, 1 is receiver
    private ArrayList<Character> participants;
    private Character initiator;
    private ArrayList<Character> targetedCharacters; //candidates for joint project
    private int sharedID;
    private boolean isRecognized;
    private boolean isPerceived;
    private boolean perceivable;
    private boolean isAccepted;
    private long timeOfInitialAction;
    private long timeOfPerception;
    public static int JPIDCounter;
    
    public JointProject(String s, int r, ArrayList<Character> p, Character i){
        jointProjectName = s;
        maxStates = 3;
        role = r;
        targetedCharacters = p;
        participants = new ArrayList<Character>();
        participants.add(i);
        initiator = i;
        
        //initiator is higher at the bottom (pereception) level
        if(role == 0){
            currentJointActionState = 1;
        }
        else if(role == 1){
            currentJointActionState = 0;
        } 
        
        sharedID = (int)(Math.random() * 100);
    }
    
//    public static void incrementJPID(){
//        JPIDCounter++;
//    }
    
    
    //for when joint project is proposed by another
    public JointProject(Character i, int id){
        jointProjectName = "joint project proposal";
        maxStates = 3;
        role = 1;
        initiator = i;
        sharedID = id;        
        System.out.println("Shared ID " + sharedID);
    }
    
    public void advanceJointAction(){
        currentJointActionState++;
    }
    
    public int getCurrentJointActionState(){
        return currentJointActionState;
    }
    
    public void updateProjectName(String s){
        System.out.println("joint project recognized as " + s);
        jointProjectName = s;
    }
    
    public String getProjectName(){
        return jointProjectName;
    }
    
    public int getRole(){
        return role;
    }
    
    public int getSharedID(){
        return sharedID;
    }
    
    public Character getInitiator(){
        return initiator;
    }
    
    public ArrayList<Character> getTargetedCharacters(){
        return targetedCharacters;
    }
    
    public void addParticipant(Character c){
        participants.add(c);
    }
    
    public ArrayList<Character> getParticipants(){
        return participants;                
    }
    
    public boolean isCharacterTargeted(Character c){
        for(int i = 0; i < targetedCharacters.size(); i++){
            if(targetedCharacters.get(i).getID() == c.getID()){
                return true;
            }
        }
        return false;
    }
    
    public void setRecognized(boolean b){
        isRecognized = b;
    }
    
    public boolean isRecognized(){
        return isRecognized;
    }
    
    public void setPerceived(boolean b){
        isPerceived = b;
    }
    
    public boolean isPerceived(){
        return isPerceived;
    }
    
    public void setPerceivable(boolean b){
        perceivable = b;
    }
    
    public boolean isPerceivable(){
        return perceivable;
    }
    
    public void setAccepted(boolean b){
        isAccepted = b;
    }
    
    public boolean isAccepted(){
        return isAccepted;
    } 
    
    public boolean checkForDuplicate(JointProject jp){
        return (jointProjectName.equals(jp.getProjectName()) && this.role == jp.getRole() && this.testParticipantEquality(targetedCharacters, jp.getTargetedCharacters()));
    }
    
    public boolean checkForDuplicate(String n, int r, ArrayList<Character> p){
        return (jointProjectName.equals(n) && this.role == r && this.testParticipantEquality(targetedCharacters, p));
    }
    
    public boolean testParticipantEquality(ArrayList<Character> l1, ArrayList<Character> l2){
        ArrayList<Integer> s1 = new ArrayList<Integer>();
        ArrayList<Integer> s2 = new ArrayList<Integer>();
        
        for(int i = 0; i < l1.size(); i++){
            s1.add(l1.get(i).getID());
        }    
        
        for(int i = 0; i < l2.size(); i++){
            s2.add(l2.get(i).getID());
        } 
        
        Collections.sort(s1);
        Collections.sort(s2);
        
        return(s1.equals(s2));
    } 
    
    public void setInitialActionTime(){
        timeOfInitialAction = System.currentTimeMillis();
    }
    
    public void setPerceptionTime(){
        timeOfPerception = System.currentTimeMillis();
    }
    
    public long getElapsedTime(){
        long elapsed = System.currentTimeMillis() - timeOfInitialAction;
        return elapsed;
    }
    
    public long getElapsedPerceptionTime(){
        long elapsed = System.currentTimeMillis() - timeOfPerception;
        return elapsed;
    }

    
}
