/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BasketballPlayerModel;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

/**
 *
 * @author DiveshLala
 */
public class NonUserPlayer extends BasketballCharacter{
    
    protected RigidBodyControl modelNode;
    protected float bodyRotationAngle;
    protected float headRotationAngle;
    private String robotType;
    

    private float walkingSpeed;
    private float facingDirection;
    private float height;
    
    private Node existenceNode; 
    
    private boolean kinectPlayer;
    
    public NonUserPlayer(RigidBodyControl p, float rad, int i, AnimatedModel model){
        characterID = i;
        modelNode = p;
        characterModel = model;
        characterModel.initialize();
        walkingSpeed = 0.0f;
        radius = rad;
        actionState = 1;

    }
    
    public String getCharacterType(){
        return "P";
    }
    
    public AnimatedModel getCharacterModel(){
        return characterModel;
    }
    
    public Vector3f getPosition(){
        return modelNode.getPhysicsLocation();
    }
//    public int getActionState(){
//        return actionState;
//    }
    public float getFacingDirection(){
        return facingDirection;
    }
//    public float getRadius(){
//        return radius;
//    }
    public Vector3f getTargetPosition(){
        return modelNode.getPhysicsLocation();
    }
    
//    public float getSpeed(){
//        return walkingSpeed;
//    }
//    public float getHeadAngle(){
//        return headRotationAngle;
//    }
    
//    public int getID(){
//        return playerID;
//    }
    
    public int getGroupID(){
        return -1;
    }
    
    public void setHeadAngle(Vector3f vec){
        headRotationAngle = Conversions.originToTargetAngle(this.getPosition(), vec);
    }
    
    public void setPosition(Vector3f vec){
        modelNode.setPhysicsLocation(vec);
    }
    
    public void turnBody(float degree){
   //     modelNode.
       modelNode.setPhysicsRotation(new Quaternion().fromAngles(0, (float)Math.toRadians(degree), 0));
    }
    
    public void setFacingDirection(float degree){
        facingDirection = degree;
    }
    
//    public void setActionState(int state){
//        
//        actionState = state;
//                
//        if(state == 1){
//            playerModel.setWalkingAnimation(0.1f);
//        }
//        else if(state == 0){
//           playerModel.removeUserControl();
//           walkingSpeed = 0;
//        }
//    }
    
//    public void setSpeed(float speed){
//        walkingSpeed = speed;
//    }
    
    public void move(Vector3f dir, float speed){
         modelNode.setLinearVelocity(dir);
         characterModel.playAnimation(2, "walk", speed, LoopMode.Loop);

  //       characterModel.playAnimation(1, "dribbleLoop", 0.5f, LoopMode.Loop);
    }
    
    public void stopMoving(){
 //       characterModel.playAnimation(1, "standingPose", 1, LoopMode.Loop);
        characterModel.playAnimation(2, "standingPose", 1, LoopMode.Loop);
    }
    
//    public ArrayList<Vector3f> getModelColours(){
//      return playerModel.getColours();
//    }
    
    public void setFacingDirection(float headRotation, float torsoRotation){
            headRotationAngle = headRotation;
            bodyRotationAngle = torsoRotation;
            characterModel.turnBody(bodyRotationAngle);
    }
    
    public float getCharacterRotation(){
        return facingDirection;
    }
    
    public void setExistenceNode(Node n){
        existenceNode = n;
    }
    
    public void removeCharacter(){
        existenceNode.removeFromParent();
    }
    
    public RigidBodyControl getPhysicsNode(){
        return modelNode;
    }
    
//   public boolean isInArea(Vector3f loc, float dist){     
//      return CollisionMath.circleCollisionTest(this.getPosition(), loc, this.getRadius(), dist); 
//    }
   
   public void setFacingDirection(Vector3f vec){
            headRotationAngle = Conversions.originToTargetAngle(this.getPosition(), vec);
            bodyRotationAngle = Conversions.originToTargetAngle(this.getPosition(), vec);
            characterModel.turnBody(bodyRotationAngle);
    }
   
   public void setSkeletonJoints(ArrayList<String> jointInfo){
       characterModel.setJointRotations(jointInfo);
   }
   
//    public Spatial getCharacterMesh(){
//        return playerModel.getModel();
//    }
//    
//    public String getSkeletonRotations(){
//        return playerModel.getBoneRotationInfo();
//    }
    
//    public String getModelType(){
//        return "P";
//    }
    
    public Vector3f getHandPosition(int hand){
        if(hand == 0){
            return characterModel.getWorldCoordinateOfJoint("left hand");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right hand");
        }
    }
    
     public boolean isLookingAtTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < 10;
    }
     
     public void doBallManipulation(){}
     
     public void setCharacterColours(ArrayList<Vector3f> colours){
     
     }
       
     public void setKinectNUP(boolean b){
         kinectPlayer = b;
     }
     
     public void setActionState(int state, float animationSpeed, int loopMode){
          actionState = state;
          String anim = characterModel.getArmAnimationName(state);
          LoopMode l;
          
          if(loopMode == 0){
              l = LoopMode.DontLoop;
          }
          else{
              l = LoopMode.Loop;
          }
          
//          if(actionState == 0){
//              anim = "standingPose";
//              speed = animationSpeed;
//          }
//          else if(actionState == 1){
//              anim = "dribbleLoop";
//              speed = animationSpeed;
//          }
//          else if(actionState == 2){
//              anim = "shootAction";
//              speed = animationSpeed;
//          }
//          else{
//              anim = "standingPose";
//              speed = 0;
//          }
          this.playAnimation(1, anim, animationSpeed, l);
     }
     
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        //forced animations\
        characterModel.forceAnimation(channel, animationName, speed, l);
    //    characterModel.playAnimation(channel, animationName, speed, l); 
    }
    
    
}


