/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
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
    
    
    public NonUserPlayer(RigidBodyControl p, float rad, int i, AnimatedModel model){
        characterID = i;
        modelNode = p;
        characterModel = model;
        characterModel.initialize();
        walkingSpeed = 0.0f;
        radius = rad;
        actionState = 1;

    }
    
    public AnimatedModel getModel(){
        return characterModel;
    }
    
    public String getCharacterType(){
        return "P";
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
        facingDirection = Conversions.adjustAngleTo360(degree);
       modelNode.setPhysicsRotation(new Quaternion().fromAngles(0, (float)Math.toRadians(degree), 0));
    }
    
    public void setFacingDirection(float degree){
        facingDirection = degree;
    }
    
    public void setAnimation(int actionState){
        characterModel.playActionState(actionState);
    }
    
//    public void setActionState(int state){
//        
//        actionState = state;
//                
//        if(state == 1){
//            characterModel.playAnimation(2, "walk", 1, LoopMode.Loop);
//        }
//        else if(state == 0){
//           characterModel.playAnimation(2, "standingPose", 1, LoopMode.Loop);
//           walkingSpeed = 0;
//        }
//    }
    
//    public void setSpeed(float speed){
//        walkingSpeed = speed;
//    }
    
    public void move(Vector3f dir){
         modelNode.setLinearVelocity(dir);
    }
    
//    public ArrayList<Vector3f> getModelColours(){
//      return playerModel.getColours();
//    }
    
    public void setFacingDirection(float headRotation, float torsoRotation){
            headRotationAngle = headRotation;
            bodyRotationAngle = torsoRotation;
            characterModel.turnBody(bodyRotationAngle);
            facingDirection = bodyRotationAngle;
    }
    
    public float getCharacterRotation(){
        return facingDirection;
    }
    
//    public void setExistenceNode(Node n){
//        existenceNode = n;
//    }
    
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
     
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.playAnimation(channel, animationName, speed, l); 
    }
   
    
}


