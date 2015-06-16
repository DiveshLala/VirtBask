/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.characters;
import Basketball.Ball;
import Basketball.PlayerGesture;
import VISIE.Sound.CharacterSoundNode;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import java.util.ArrayList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Ray;



/**
 *
 * @author huang
 */
public class Player extends BasketballCharacter{

    protected CharacterControl mainNode;
    private float playerFacingDirection;
    private float headRotationAngle;
    private float torsoTurnSpeed;
    private float playerRotation;
 //   private float radius;
   // private float walkingSpeed = 0;
  //  private int actionState;
//    private int playerID;
    private Vector3f targetPosition;
 //   private AnimatedModel playerModel;
    private Spatial targetPointer;
    private boolean engagedInJP; 
    private float mutualGazeAngle = 30;
    private float perceivedFieldOfVision = 55;
    protected PlayerGesture playerGesture;

    public Player(CharacterControl p, float rad, int i, AnimatedModel model){
        mainNode = p;
        radius = rad;
        actionState = 0;
        characterID = i;
        targetPosition = new Vector3f(50f, 1f, 50f);
        characterModel = model;
        characterModel.initialize();
        torsoTurnSpeed = 2;
        characterModel.setTransparency(0.2f);
    }
    
    public void initializePlayerGesture(Ball b){
        playerGesture = new PlayerGesture(this, characterModel, b);
    }
    
    public void initializeGestureRecognition(){
   //     gestRec = new GestureRecognition(characterModel);
    }
    
    public void setTargetPointer(Spatial s){
        if(targetPointer == null){
            targetPointer = s;
            this.getRootNode().attachChild(targetPointer);
        }
    }
    
    private Node getRootNode(){
        Node n = (Node)mainNode.getUserObject();
        return n.getParent();
    }
    

    
    public Vector3f getHandPosition(int hand){
        if(hand == 0){
            return characterModel.getWorldCoordinateOfJoint("left hand");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right hand");
        }      
    }
    
    public Vector3f getShoulderPosition(int shoulder){
        if(shoulder == 0){
            return characterModel.getWorldCoordinateOfJoint("left shoulder");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right shoulder");
        }      
    }
    
    public float getFacingDirection(){
        return playerFacingDirection;
    }

    public Vector3f getPosition(){
        return mainNode.getPhysicsLocation();
    }
    
    public Vector3f getHeadPosition(){
        return characterModel.getWorldCoordinateOfJoint("head");
    }
    
    public Vector3f getElbowPosition(int arm){
        if(arm == 0){
        return characterModel.getWorldCoordinateOfJoint("left elbow");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right elbow");
        }
    }

    public void hasCollided(){}

    public void calculateSpeed(boolean isWalking){
        if(isWalking)
            currentSpeed = 0.05f;
        else
            currentSpeed = 0;
    }

    public CharacterControl getMainNode(){
        return mainNode;
    }

    public void setActionState(boolean f){
//        if(!f)
//            actionState = 0;
//        else
//            actionState = 1;
    }
    
    public void setActionState(int i){
        actionState = i;
        
        if(actionState == 0){
            this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
        }
        else if(actionState == 1){
       //     if(this.isInPossession()){
                this.playAnimation(1, "dribbleLoop", 0.5f, LoopMode.Loop);
       //     }
       //     else{
       //         this.playAnimation(1, "walk", 0.5f, LoopMode.Loop);
       //     }
        }
        else if(actionState == 2){
            this.playAnimation(1, "shootAction", 2, LoopMode.DontLoop);
        }
        
    }
    
    public int getActionState(){
        String anim = characterModel.getCurrentAnimation(1);
        return characterModel.getActionState(anim, 1);
    }
    
    public int getLowerBodyState(){
        String anim = characterModel.getCurrentAnimation(2);
        return characterModel.getActionState(anim, 2);
    }   
        
    public float getAnimationSpeed(int i){
        return characterModel.getAnimationSpeed(i);
    }
     
    public float getLowerAnimSpeed(int i){
        return characterModel.getAnimationSpeed(i);
    }
    
    public int isLooped(int i){
        if(characterModel.isLooped(i)){
            return 1;
        }    
        else{
            return 0;
        }
    }
    
    public void doNavigationStateChange(int newState){
        if(this.getActionState() < 2){
            this.setActionState(newState);
        }
    }

    public String getCharacterType(){
        return "P";
    }


    public int getGroupID(){
        return -1;
    }
    
    public void setPosition(Vector3f vec){
         mainNode.setPhysicsLocation(vec);
    }
    
    public Vector3f getPointingVector(int hand){
        return characterModel.getPointingVector(hand);
    }

    public void setCharacterRotationRadians(float dir){
       float degrees = (float)Math.toDegrees(dir);
       if(degrees > 360)
           playerFacingDirection = degrees%360;
       else if(degrees < 0)
           playerFacingDirection  = 360 - Math.abs(degrees%360);
       else
           playerFacingDirection  = degrees;
    }
    
    public float getCharacterRotation(){
        return playerFacingDirection;
    }
    
    public float getCharacterRotationRadians(){
        return (float)Math.toRadians(playerFacingDirection);
    }
    
    public void removeCharacter(){}
    
    public CharacterControl getPhysicsNode(){
        return mainNode;
    }
        
    public void walk(Vector3f vec, int turnCode){
        mainNode.setWalkDirection(vec);
        this.doWalkingAnimations(turnCode, vec);
    }
    
    protected void doWalkingAnimations(int turnCode, Vector3f vec){
        
        if(turnCode == 3){
           characterModel.playAnimation(2, "stepBack", 1, LoopMode.Loop); 
        }
        else if(!vec.equals(Vector3f.ZERO)){
            characterModel.playAnimation(2, "walk", 1, LoopMode.Loop);
        }
        else if(turnCode == 1){
            characterModel.playAnimation(2, "stepLeft", 2, LoopMode.Loop); 
        }
        else if(turnCode == 2){
            characterModel.playAnimation(2, "stepRight", 2, LoopMode.Loop);
        }
        else{
            characterModel.playAnimation(2, "standingPose", 1, LoopMode.Loop);
        }
    
    }
    
    //turn model
    public void turnBody(){
        mainNode.setViewDirection(Conversions.degreesToNormalizedCoordinates(playerFacingDirection));
    }
    
    public void setKinectAnimation(ArrayList<String> vec){ 
       characterModel.moveJoints(vec);
       playerFacingDirection = characterModel.getModelRotationAngle();
  //      System.out.println(playerFacingDirection);
    }
    
    
    public void setHeadRotationRadians(float dir){
       float degrees = (float)Math.toDegrees(dir);
       if(degrees > 360)
           headRotationAngle = degrees%360;
       else if(degrees < 0)
           headRotationAngle = 360 - Math.abs(degrees%360);
       else
          headRotationAngle = degrees;
    }
    
    public boolean isInPerceivedVision(Character c){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), c.getPosition());
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < mutualGazeAngle;
    }
    
     public boolean isLookingAtTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < mutualGazeAngle;
    }
     
     public void doDribblingAnimation(){        
         
         BPNewModel m = (BPNewModel)characterModel;
//
        if(this.getSpeed() > 0){
            m.doDribbling(0.5f);
        }
        else{
          if(m.isBallDribbled()){
              this.playAnimation(1, "postDribble", 1, LoopMode.DontLoop);
          }                    
        }

     }
     
     public void doNonPossessionGesture(){
         
         BPNewModel bm = (BPNewModel) characterModel;
         String currentGest = characterModel.getCurrentAnimation(1);
        
        if(currentGest.contains("shoot") && bm.hasAnimationFinished(1)){
            this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
        }
        else if(currentGest.startsWith("pass") && bm.hasAnimationFinished(1)){
            this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
        }
        else if(currentGest.contains("callForPass")){
            if(bm.hasAnimationFinished(1)){
               this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
            }
        }   
        else if(this.getSpeed() > 0){
            if(!currentGest.toLowerCase().contains("block")){
                this.playAnimation(1, "walk", 1, LoopMode.Loop);
            }
        }
        else{
            if(!currentGest.contains("shoot") && 
               !currentGest.startsWith("pass") &&
               !currentGest.startsWith("receivePass") &&
               !currentGest.toLowerCase().contains("block") &&
               !currentGest.toLowerCase().contains("celebration")){
               String legGesture = this.getCurrentGesture(2);
               this.playAnimation(1, legGesture, 1, LoopMode.Loop);
            }
        }
     }
     
     public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.forceAnimation(channel, animationName, speed, l); 
    }
     
     public void playDynamicAnimation(String animationName){
         playerGesture.setDynamicGesture(animationName);
     }
     
     public void updateGestures(){
         playerGesture.updateGestures();
     }
     
     public void shootBall(){
         if(this.hasPossession){
            this.playAnimation(1, "shoot", 2, LoopMode.DontLoop);
         }
     }
     
     public float getTurnSpeed(){
         return torsoTurnSpeed;
     }
     
     public String getCurrentGesture(int channel){
         return characterModel.getCurrentAnimation(channel);              
     }
     
     public void setCharacterColours(ArrayList<Vector3f> colours){
     
     }
     
     public void setCharacterTextures(ArrayList<String> texList){}
     
     public void cleanUpActionStates(){
        if(this.getActionState() == 2){
            if(characterModel.hasAnimationFinished(1)){
                this.removePossession();
                this.setActionState(0); 
            }
        }
    }
}
