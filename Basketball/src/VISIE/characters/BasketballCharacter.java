/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.Ball;
import VISIE.Games.Game;
import VISIE.Sound.CharacterSoundNode;
import VISIE.mathfunctions.Conversions;
import VISIE.mathfunctions.Polygon2D;
import VISIE.models.AnimatedModel;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public abstract class BasketballCharacter extends Character{
    
    public Ball ball;
    public boolean hasPossession;
    private BasketballTeam team;
    protected boolean isPlayer;
    protected GhostControl handGhost;
    protected CharacterSoundNode characterSoundNode;
    protected CharacterControl mainNode;
    
    public abstract void doBallManipulation();
    public abstract void checkBallStealing(Ball b);
    public abstract boolean checkStealing(Ball b);
    public abstract void updateBallNoPossession();
    public abstract void stopBall();
    public abstract Vector3f getBallHandVector();
    
    public String getCharacterModelPath(){
        return this.getModelFilePath();
    }
    
    
    
    public boolean isInPossession(){
        return hasPossession;
    }
        
    public void setBall(Ball b){
        ball = b;
        
//        //sets ball for the agent planner
//        if(this instanceof VISIE.characters.BasketballAgent){
//            BasketballAgent ba = (BasketballAgent)this;
//            ba.planner.setBall(b);
//        }
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
    
    public BasketballCharacter getNearestTeammate(){
        
        float closestDist = 10000000;
        BasketballCharacter closestTeammate = null;
        ArrayList<BasketballCharacter> teammates = this.getTeamMates();
        
        for(int i = 0; i < teammates.size(); i++){
            if(this.get2DPosition().distance(teammates.get(i).get2DPosition()) < closestDist){
                closestTeammate = teammates.get(i);
                closestDist = this.get2DPosition().distance(teammates.get(i).get2DPosition());
            }
        }
        
        return closestTeammate;
        
    }
    
    public boolean isShooting(){
        return characterModel.getCurrentAnimation(1).contains("shoot");        
    }
    
    public boolean isPassing(){
    //    System.out.println(this.getID() + " " + this.getCurrentGestureName() + " " + this.getAnimationPercentageTime(1));
        return characterModel.getCurrentAnimation(1).contains("pass");
    } 
    
    public void triggerClientSound(){
        characterSoundNode.playClientUtterance();        
    }
    
    public void flagUtterance(String s){
        characterSoundNode.flagUtterance(s);
    }
    
    public boolean canSeeCharacter(Character c){
        return false;
    }
    
    public void setTeam(BasketballTeam t){
        team = t;
    }
    
    public void adjustCollisionGroup(){
        ball.adjustCollisionGroup(false);
    }
    
    public void resetPossessionTime(){}
    
    public void resetNonPossessionTime(){}
        
    public int getTeamID(){
        if(team == null){
            return -1;
        }
        return team.getTeamID();
    }
    
    public boolean playerIsTeamMate(BasketballCharacter bc){//WILL RETURN TRUE IF COMPARING SAME CHARACTER
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
   
   public float getNearestCharacterDist(ArrayList<BasketballCharacter> characters){
       
       float curDist = 1000000;
       
       for(int i = 0; i < characters.size(); i++){
           if(this.get2DPosition().distance(characters.get(i).get2DPosition()) < curDist){
               curDist = this.get2DPosition().distance(characters.get(i).get2DPosition());
           }
       }   
       return curDist;
       
   }
   
   public ArrayList<BasketballCharacter> getAllOtherCharacters(){
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray();
        ArrayList<BasketballCharacter> members = new ArrayList<BasketballCharacter>();
        for(int i = 0; i < c.size(); i++){
            BasketballCharacter bc = (BasketballCharacter)c.get(i);
            if(bc.getID() != this.getID()){
                members.add(bc);
            }
        }
        return members;
   }
    
    public Vector3f get2DPosition(){
        return this.getPosition().clone().setY(0);    
    }
    
    public void makePlayerTransparent(){
        BasketballPlayerModel bm = (BasketballPlayerModel)characterModel;
        bm.setTransparency(0.3f);
    }
    
    @Override
    public void cleanUp(){
        if(this.hasPossession){
            this.removePossession();
        }
    }
    
    public boolean canShoot(){
        
        Vector3f ballPos = ball.getBallPosition().setY(0);
        Vector3f goalPos = new Vector3f(Court.getHoopLocation().x, 0, Court.getHoopLocation().z);
        float dist = ballPos.distance(goalPos);
        
        if(dist < 5){
            return false;
        }
        else if(dist > 45){
            return false;
        }
        
        float threshold = 20;
        float angleToGoal = Conversions.originToTargetAngle(this.get2DPosition(), goalPos); 
        return Conversions.minDistanceBetweenAngles(this.getFacingDirection(), angleToGoal) < threshold;
     
    }
    
    
    public boolean canPass(BasketballCharacter target){
        float maxDistance = 20;
        return this.get2DPosition().distance(target.get2DPosition()) < maxDistance;
    }
    
    public boolean canDoClearPass(BasketballCharacter teammate, float passLeeway){
         
         ArrayList<BasketballCharacter> opponents = this.getOpponents();
         
         for(BasketballCharacter bc : opponents){
             if(this.isInPassingLane(teammate, bc, passLeeway)){
                 return false;
             }
         }
         
         return true;
         
     }
    
     public boolean isInPassingLane(BasketballCharacter teammate, BasketballCharacter opponent, float leeway){
                 
        float[] xPoints = new float[4];
        float[] yPoints = new float[4];
        
        float angle = Conversions.originToTargetAngle(this.get2DPosition(), teammate.get2DPosition());
        Vector3f passVector = Conversions.degreesToNormalizedCoordinates(angle);
        
        Vector3f lMe = new Vector3f(-passVector.z, 0, passVector.x);
        Vector3f rMe = new Vector3f(passVector.z, 0, -passVector.x);
        
        Vector3f corner1 = this.get2DPosition().add(lMe.mult(leeway));
        Vector3f corner2 = this.get2DPosition().add(rMe.mult(leeway));
        Vector3f corner3 = teammate.get2DPosition().add(rMe.mult(leeway));
        Vector3f corner4 = teammate.get2DPosition().add(lMe.mult(leeway));
        
        xPoints[0] = corner1.x;
        xPoints[1] = corner2.x;
        xPoints[2] = corner3.x;
        xPoints[3] = corner4.x;
        
        yPoints[0] = corner1.z;
        yPoints[1] = corner2.z;
        yPoints[2] = corner3.z;
        yPoints[3] = corner4.z;
         
        Polygon2D poly = new Polygon2D(xPoints, yPoints, 4);  
        return Conversions.isPointInsidePolygon(opponent.get2DPosition().x, opponent.get2DPosition().z, poly);
        
    }
    
    public boolean canSeeBall(Ball b){
        float angleBetween = Conversions.originToTargetAngle(this.getPosition(), b.getBallPosition());
        float f = Conversions.minDistanceBetweenAngles(angleBetween, this.getFacingDirection());
        return f < 60;                       
    }
    
    public void setSoundNodes(){
        characterSoundNode = new CharacterSoundNode(existenceNode, "assets/Sounds/characterSounds.txt", this);
    }
    
    public void playWalkingSound(){
                
        BPNewModel bp = (BPNewModel)characterModel;
        String footstepType = characterModel.getCurrentAnimation(2);
        
        if(bp.hasFootTouchedGround()){
            characterSoundNode.playFootstep(footstepType);
        }
    }
    
    public void playPassSound(){
        characterSoundNode.playPassSound();
    }
    
    public void playCatchSound(){
        characterSoundNode.playCatchSound();
    }
    
    public void playUtterance(String s){
        characterSoundNode.playUtterance(s);
    }
            
    public Vector3f[] getArmVectors(){
        
//        Vector3f lShoToElb = (characterModel.getWorldCoordinateOfJoint("left shoulder").subtract(characterModel.getWorldCoordinateOfJoint("left elbow"))).normalize();
//        Vector3f lElbToHand = (characterModel.getWorldCoordinateOfJoint("left elbow").subtract(characterModel.getWorldCoordinateOfJoint("left hand"))).normalize();
//        Vector3f rShoToElb = (characterModel.getWorldCoordinateOfJoint("right shoulder").subtract(characterModel.getWorldCoordinateOfJoint("right elbow"))).normalize();
//        Vector3f rElbToHand = (characterModel.getWorldCoordinateOfJoint("right elbow").subtract(characterModel.getWorldCoordinateOfJoint("right hand"))).normalize();
        

        Vector3f lShoToElb = (characterModel.getLocalCoordinateOfJoint("left shoulder").subtract(characterModel.getLocalCoordinateOfJoint("left elbow"))).normalize();
        Vector3f lElbToHand = (characterModel.getLocalCoordinateOfJoint("left elbow").subtract(characterModel.getLocalCoordinateOfJoint("left hand"))).normalize();
        Vector3f rShoToElb = (characterModel.getLocalCoordinateOfJoint("right shoulder").subtract(characterModel.getLocalCoordinateOfJoint("right elbow"))).normalize();
        Vector3f rElbToHand = (characterModel.getLocalCoordinateOfJoint("right elbow").subtract(characterModel.getLocalCoordinateOfJoint("right hand"))).normalize();
  //      System.out.println(a);
        
//        Vector3f lShoToElb = characterModel.getBoneVectorOfModel("left shoulderr", "left elbow");
//        Vector3f lElbToHand = characterModel.getBoneVectorOfModel("left elbow", "left hand");
//        Vector3f rShoToElb = characterModel.getBoneVectorOfModel("right shoulderr", "right elbow");
//        Vector3f rElbToHand = characterModel.getBoneVectorOfModel("right elbow", "right hand");
        
        Vector3f[] values = {lShoToElb, lElbToHand, rShoToElb, rElbToHand};
        return values;
    }
    
    public float getShootPenalty(){
        return 1;
    }
    
    public boolean isPlayer(){
        return isPlayer;
    }
    
    public void initializePositions(BasketballCharacter bc){}
    
    public void setBehaviorState(int i){}
    
    public void recordBatchData(){}
    

        
}
