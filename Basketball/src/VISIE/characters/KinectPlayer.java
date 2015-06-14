/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import Basketball.GameManager;
import Basketball.KinectGesture;
import VISIE.Games.Game;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class KinectPlayer extends Player{
    
    private float KinectFacingDirection;
    private float undribbledDistance = 0;
    private boolean isTravelling = false;
    private Vector3f prevPos = new Vector3f();
        
    public KinectPlayer(CharacterControl p, float rad, int i, AnimatedModel model){
        super(p, rad, i, model);
        prevPos = this.get2DPosition();
    }
    
    @Override
    public void doBallManipulation(){
        
        KinectGesture kg = (KinectGesture)playerGesture;
        
        if(kg.isDribbling()){
            ball.dribbleInHand(this.getHandPosition(1));
            if(ball.isBallInSpace(this.getHandPosition(2)) && ball.isBouncingUp()){
                kg.setDribbling(false);
                ball.updateBallInPossession();
            }  
        }
        else if(!kg.isDribbling() && kg.getCurrentKinectGesture().toLowerCase().startsWith("dribble")){
            System.out.println("start dribble");
            kg.setDribbling(true);
            ball.bounceBall(mainNode.getViewDirection().normalize().mult(this.getSpeed()));
        }
        else{
             ball.updateBallInPossession();
             ball.reduceSpin();
        }
    }
    
    public void updateGestures(){
        KinectGesture kg = (KinectGesture)playerGesture;
        kg.playKinectGestures();
    }
     
     public void playKinectGesture(String animationName){
         KinectGesture kg = (KinectGesture)playerGesture;
         kg.setKinectGesture(animationName);
     }
     
     public void initializePlayerGesture(Ball b){
         playerGesture = new KinectGesture(this, characterModel, b);
     }
     
     public void setKinectAnimation(ArrayList<String> vec){
       float rotation = characterModel.getRotationAngle();
       characterModel.moveJoints(vec);
       
       if(!Float.isNaN(rotation) && this.getSpeed() > 0){
            characterModel.turnBody(rotation);
       }
       else{
           characterModel.turnBody(0);
       }
    }
     
     
     @Override
     public float getFacingDirection(){
         if(!Float.isNaN(characterModel.getRotationAngle())){
            return Conversions.adjustAngleTo360(super.getFacingDirection() + characterModel.getRotationAngle());
         }
         else{
             return super.getFacingDirection();
         }
     }
     
     public float getCameraDirection(){
         return super.getFacingDirection();
     }
     
      public boolean isShooting(){
          KinectGesture kg = (KinectGesture)playerGesture;
          return kg.getShooting();
      }
      
      public boolean isPassing(){
          KinectGesture kg = (KinectGesture)playerGesture;
          return kg.getPassing();
      }
      
      public boolean isDribbling(){
          KinectGesture kg = (KinectGesture)playerGesture;
          return kg.isDribbling();
      }
      
      public void throwPass(){
          KinectGesture kg = (KinectGesture)playerGesture;
          kg.throwPass();      
      }
      
      @Override
      public void walk(Vector3f vec, int turnCode){
          
                  
        if(undribbledDistance < 20){
            mainNode.setWalkDirection(vec.mult(2));
            isTravelling = false;
        }
        else{
            mainNode.setWalkDirection(Vector3f.ZERO);
            isTravelling = true;
            vec = Vector3f.ZERO;
        }
        
        if(this.isInPossession() && Game.getGameType().equals("Team") && GameManager.getGameState() == 0){
            Vector3f f = this.get2DPosition().clone();
            float incr = f.distance(prevPos);
            prevPos = f;
            undribbledDistance += incr;
        }
        else{
            undribbledDistance = 0;
        }
        
        super.doWalkingAnimations(vec, turnCode);
      }
      
     public void resetUndribbledDistance(){
         undribbledDistance = 0;
     }
     
     public boolean isTravelling(){
         return isTravelling;
     }
     
     @Override
     public String getCurrentGestureName(){
          KinectGesture kg = (KinectGesture)playerGesture;
          return kg.getCurrentKinectGesture();
     }
     
     public void updateBallPossession(Ball b, Vector3f vec){
           if(b.isCloseToPlayer(this)){
               b.adjustCollisionGroup(true);
           }
           else{
               b.adjustCollisionGroup(false);
           }

           if(!this.isDribbling()){
               b.setBallPosition(vec);
           }
     }
     
     public void updateBallNoPossession(){
            if(ball.isCloseToPlayer(this)){
               ball.adjustCollisionGroup(true);
           }
           else{
               ball.adjustCollisionGroup(false);
           }
     }
     
     public void stopBall(){
         ball.stopBall();
     }
    
     @Override
     public boolean isKinectPlayer(){
         return true;
     }
     
     public boolean isGettingAttention(){
     
       return (GameManager.getGameState() == 0 && 
               SceneCharacterManager.getCharacterInPossession() != null &&
               !SceneCharacterManager.getCharacterInPossession().equals(this) &&
               this.playerIsTeamMate(SceneCharacterManager.getCharacterInPossession()) &&
               playerGesture.areArmsRaised());
     }
}
