/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.Ball;
import VISIE.Sound.CharacterSoundNode;
import VISIE.models.AnimatedModel;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public abstract class BasketballCharacter extends Character{
    
    public Ball ball;
    public boolean hasPossession;
    private BasketballTeam team;
    private float cameraRot;
    private boolean cameraInitialised = false;
    private CharacterSoundNode characterSoundNode;
    public abstract void turnBody(float f);
    
    public boolean isInPossession(){
        return hasPossession;
    }
    
    public void setBall(Ball b){
        ball = b;
    }
    
    public void setPossession(){
        hasPossession = true;
        ball.setPossession(this.getID());
    }
    
    public void removePossession(){
        System.out.println(this.getID() + " has removed possession");
        hasPossession = false;
    }    
    
    public Character getNearestCharacter(ArrayList<Character> candidates){
        float closestDist = 100000f;
        Character closestCharacter = null;
        for(int i = 0; i < candidates.size(); i++){
            if(this.getPosition().distance(candidates.get(i).getPosition()) < closestDist){
                closestCharacter = candidates.get(i);
                closestDist = this.getPosition().distance(closestCharacter.getPosition());
            }   
        }  
        return closestCharacter;
    }
    
    public boolean isShooting(){
        return characterModel.getCurrentAnimation(1).equals("shootAction");        
    }
    
    public boolean isPassing(){
        return characterModel.getCurrentAnimation(1).equals("passAction");
    }
    
    public boolean canSeeCharacter(Character c){
        if(this instanceof VISIE.characters.Player){
            Player p  = (Player)this;
            return p.isInPerceivedVision(c);
        }
        else if(this instanceof VISIE.characters.BasketballAgent){
            BasketballAgent ba = (BasketballAgent)this;
            return ba.perception.canSeeCharacter(c);
        }
        else{
            return false;
        }
    }
    
    public void setTeam(BasketballTeam t){
        team = t;
    }
    
    public int getTeamID(){
        return team.getTeamID();
    }
    
    public boolean playerIsTeamMate(BasketballCharacter bc){
        return bc.getTeamID() == this.getTeamID();    
    }
    
    public ArrayList<BasketballCharacter> getTeamMates(){
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray();
        ArrayList<BasketballCharacter> members = new ArrayList<BasketballCharacter>();
        for(int i = 0; i < c.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)c.get(i);
            if(bc.getTeamID() == this.getTeamID() && bc.getID() != this.getID()){
                members.add(bc);
            }
        }
        return members;
    }
    
   public ArrayList<BasketballCharacter> getOpponents(){
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray();
        ArrayList<BasketballCharacter> members = new ArrayList<BasketballCharacter>();
        for(int i = 0; i < c.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)c.get(i);
            if(bc.getTeamID() != this.getTeamID()){
                members.add(bc);
            }
        }
        return members;
       
   }
    
    public Vector3f get2DPosition(){
        return this.getPosition().setY(0);    
    }
    
    public float getCameraRot(){
        return cameraRot;
    }
    
    public void setCameraRot(float f){
        cameraRot = f;
    }
    
    public boolean getCameraInitialised(){
        return cameraInitialised;
    }
    
    public void initialiseCamera(){
        cameraInitialised = true;
    }
    
    public void setSoundNodes(){
        characterSoundNode = new CharacterSoundNode(existenceNode, "assets/Sounds/characterSounds.txt", this);
    }
    
     public void playUtterance(String s){
        characterSoundNode.playUtterance(s);
    }
     
     public String getArmAnimationName(int actionState){
        return characterModel.getArmAnimationName(actionState);
     }
     
     public String getLegAnimationName(int actionState){
        return characterModel.getLegAnimationName(actionState);
     }
     
     public void setAnimationFrame(int channel, String animName, float animTime){
         characterModel.setFrame(channel, animName, animTime);
     }
     
     public void setSkeletonJoints(ArrayList<String> jointInfo){
         
     }
     
}
