/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.Ball;
import VISIE.Sound.CharacterSoundNode;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.scenemanager.Court;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public abstract class BasketballCharacter extends Character{
    
    public Ball ball;
    public boolean hasPossession;
    private BasketballTeam team;
    private CharacterSoundNode characterSoundNode;

    public boolean isInPossession(){
        return hasPossession;
    }
    
    public void setBall(Ball b){
        ball = b;
        
    }
    
    public void setPossession(){
        hasPossession = true;
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
    
//    public boolean canSeeCharacter(Character c){
////        if(this instanceof VISIE.characters.Player){
////            Player p  = (Player)this;
////            return p.isInPerceivedVision(c);
////        }
////        else if(this instanceof VISIE.characters.BasketballAgent){
////            BasketballAgent ba = (BasketballAgent)this;
////            return ba.perception.canSeeCharacter(c);
////        }
////        else{
//            return false;
////        }
//    }
    
    public void setTeam(BasketballTeam t){
        team = t;
    }
    
    public int getTeamID(){
        if(team == null){
            return -1;
        }
        return team.getTeamID();
    }
    
    public boolean canShoot(){
        float threshold = 50;
        float angleToGoal = Conversions.originToTargetAngle(this.getPosition().setY(0), Court.getHoopLocation().setY(0));
        return Conversions.minDistanceBetweenAngles(this.getFacingDirection(), angleToGoal) < threshold;
    }
    
    public void setSoundNodes(){
        characterSoundNode = new CharacterSoundNode(existenceNode, "assets/Sounds/characterSounds.txt");
    }
    
    public String getUtterance(String s){
        return characterSoundNode.getUtterance(s);
    }
    
    public void playUtterance(String s){
        characterSoundNode.playUtterance(s);
    }
}
