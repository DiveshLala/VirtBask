/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.characters;
import Basketball.Ball;
import Basketball.JointProject;
import Basketball.PlayerGesture;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.gesturerecognition.GestureRecognition;
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

    private CharacterControl mainNode;
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
    private GestureRecognition gestRec;
    private Spatial targetPointer;
    private boolean engagedInJP; 
    private float mutualGazeAngle = 30;
    private float perceivedFieldOfVision = 55;
    private PlayerGesture playerGesture;

    public Player(CharacterControl p, float rad, int i, AnimatedModel model){
        mainNode = p;
        radius = rad;
        actionState = 0;
        characterID = i;
        characterModel = model;
        characterModel.initialize();
        torsoTurnSpeed = 2;
    }
    
    public AnimatedModel getModel(){
        return characterModel;
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
    
    public boolean recognizesGestures(){
        return (gestRec != null);
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
        if(!f)
            actionState = 0;
        else
            actionState = 1;
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
    
    
    
    public void walk(Vector3f vec){

        mainNode.setWalkDirection(vec.mult(2));
        
        if(!vec.equals(Vector3f.ZERO)){
             characterModel.playAnimation(2, "walk", 1, LoopMode.Loop);
        }
        else{
            characterModel.playAnimation(2, "standingPose", 1, LoopMode.Loop);
        }
    }
    
    //turn model
    public void turnBody(){
  //      System.out.println("sss" + playerFacingDirection);
        mainNode.setViewDirection(Conversions.degreesToNormalizedCoordinates(playerFacingDirection));
    }
    
    public void setKinectAnimation(ArrayList<String> vec){ 
       characterModel.moveJoints(vec);
    //   playerFacingDirection = characterModel.getModelRotationAngle();
  //      System.out.println(playerFacingDirection);
    }
    
    public void setPointing(){
       targetPointer.setLocalTranslation(gestRec.getPointTargetWorld());
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
          
     public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.playAnimation(channel, animationName, speed, l); 
    }
     
     public void playKinectGesture(String animationName){
         playerGesture.setKinectGesture(animationName);
     }
          
     public void shootBall(){
         if(this.hasPossession){
            this.playAnimation(1, "shootAction", 1, LoopMode.DontLoop);
         }
     }
     
     public float getTurnSpeed(){
         return torsoTurnSpeed;
     }
     
     public String getCurrentGesture(int channel){
         return characterModel.getCurrentAnimation(channel);              
     }
     
     public String getCurrentKinectGesture(){
         return playerGesture.getKinectGesture();
     }
     
     public void setPlayerTarget(Vector3f target){
         targetPosition = target;
     }
     
     public boolean hasReachedTarget(){
         return this.getPosition().distance(targetPosition) < 5f;
     }
     
    public void setSkeletonJoints(ArrayList<String> jointInfo){
       characterModel.setJointRotations(jointInfo);
    }
}
