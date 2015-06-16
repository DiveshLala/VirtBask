/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import Basketball.GameStateManagement;
import Basketball.KinectGesture;
import VISIE.models.AnimatedModel;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class KinectPlayer extends Player{
    
    private float undribbledDistance = 0;
    private boolean isTravelling = false;
    private Vector3f prevPos = new Vector3f();
        
    public KinectPlayer(CharacterControl p, float rad, int i, AnimatedModel model){
        super(p, rad, i, model);
        playerGesture = new KinectGesture(this, model, ball);
        prevPos = this.getPosition();
    }
    
    public void doBallManipulation(){
         KinectGesture kg = (KinectGesture)playerGesture;
         if(kg.getCurrentKinectGesture().equals("")){
             ball.updateBallInPossession();
             ball.reduceSpin();
         }
    }
    
    public void updateGestures(){
        KinectGesture kg = (KinectGesture)playerGesture;
        kg.playKinectGestures();
    }
     
     public void playKinectGesture(String animationName){
         String g = animationName;
         KinectGesture kg = (KinectGesture)playerGesture;
         kg.setKinectGesture(g);
     }
     
     public void initializePlayerGesture(Ball b){
         playerGesture = new KinectGesture(this, characterModel, b);
     }
     
     public void setKinectAnimation(ArrayList<String> vec){ 
       characterModel.moveJoints(vec);
       float rotation = characterModel.getRotationAngle();
       if(!Float.isNaN(rotation)){
           characterModel.turnBody(rotation);
       }
    }
     
     public float getFacingDirection(){
         return super.getFacingDirection() + characterModel.getRotationAngle();
     }
     
     public float getCameraFacingDirection(){
         return super.getFacingDirection();
     }
     
     //empty - do nothing
     public void playAnimation(int channel, String animationName, float speed, LoopMode l){
         
     }
     
     public String getKinectGesture(){
         KinectGesture kg = (KinectGesture)playerGesture;
         if(kg.getPassing()){
             return "pass";
         }
         else if(kg.getShooting()){
             return "shoot";
         }
         else if(kg.getDribbling()){
             return "dribble";
         }
         else{
             return "";
         }
     }
     
     public void setBall(Ball b){
         KinectGesture kg = (KinectGesture)playerGesture;
         kg.setBall(b);
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
         
      // super.walk(vec, turnCode);
         this.doWalkingAnimations(turnCode, vec);
         
        if(this.isInPossession() && GameStateManagement.getGameState() == 0){
             Vector3f f = this.getPosition().clone().setY(0);
             float incr = f.distance(prevPos);
             prevPos = f;
             undribbledDistance += incr;
         }
         else{
             undribbledDistance = 0;
         }
     }
     
     public void resetUndribbledDistance(){
         undribbledDistance = 0;
     }
     
     public boolean isTravelling(){
         return isTravelling;
     }
}
