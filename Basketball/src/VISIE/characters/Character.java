/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.characters;
import VISIE.models.AnimatedModel;
import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import com.jme3.scene.Spatial;
import VISIE.mathfunctions.CollisionMath;
import com.jme3.scene.Node;

/**
 *
 * @author huang
 */
public abstract class Character{
    
    protected AnimatedModel characterModel;  
    protected int actionState;
    protected float radius;
    protected float currentSpeed;
    protected int characterID;
    protected String characterType;
    protected Node existenceNode;
    
    public abstract String getCharacterType();
    
    public String getModelType(){
        return characterModel.getTypeIdentifier();
    }
    
    public float getRadius(){
        return radius;
    }
    
    public void setActionState(int i){
        actionState = i;
    }
    
    public int getActionState(){
        return actionState;
    }
    
    public String getModelAnimationName(int i){
        return characterModel.getAnimationName(i);
    }
    
    public void setSpeed(float f){
        currentSpeed = f;
    }
    
    public float getSpeed(){
        return currentSpeed;
    }
    
    public int getID(){
        return characterID;
    }
    
    public String getModelFilePath(){
        return characterModel.getModelFilePath();
    }
    
    public ArrayList<Vector3f> getModelColours(){
        return characterModel.getModelColours();
    }
    
    public ArrayList<String> getModelTextures(){
        return characterModel.getModelTextures();
    }
    
    public float getModelScale(){
        return characterModel.getModel().getLocalScale().getX();
    }
    
    public boolean isInArea(Vector3f loc, float dist){     
      return CollisionMath.circleCollisionTest(this.getPosition(), loc, this.getRadius(), dist); 
    }
    
    public Spatial getCharacterMesh(){
        return characterModel.getModel();
    }
    
    public String getSkeletonRotations(){
        return characterModel.getModelBoneRotationInfo();
    }  
    
    public String getLoggedSkeletonRotations(){
        return characterModel.getModelBonesForLogging();
    }
    
    public void setCharacterType(String type){
        characterType = type;
    }
    
    public void setExistenceNode(Node n){
        existenceNode = n;
    }
    
    public void removeExistence(){
        existenceNode.removeFromParent();
    }
    
    public void makeTransparent(float value){
        characterModel.setTransparency(value);
    }
    
    public float getAnimationSpeed(int i){
        return characterModel.getAnimationSpeed(i);
    }
    
    public float getAnimationPercentageTime(int i){
        return characterModel.getCurrentAnimationTimePercentage(i);
    }
        
    public int isLooped(int i){
        if(characterModel.isLooped(i)){
            return 1;
        }
        else{
            return 0;
        }
    }
    
    public float getAnimationTime(int channel){
        return characterModel.getAnimationTime(channel);
    }
    
    public String getCurrentGestureName(){
        return "";
    }
    
    public void cleanUp(){
    
    }
    
    public boolean isKinectPlayer(){
        return false;
    }
    
    public boolean isNUP(){
        return false;
    }
    
    public void doNUPGestureActivity(String gesture){}
    
    public abstract Vector3f getPosition();
        
 //   public abstract void setFacingDirection(float torsoRotation, float headRotation);
    
    public abstract boolean isLookingAtTarget(Vector3f target);
    
    public abstract float getFacingDirection();
        
 //   public abstract void setHeadAngle(Vector3f vec);
    
    public abstract void setPosition(Vector3f vec);
    
    public abstract float getCharacterRotation();
    
    public abstract PhysicsControl getPhysicsNode();
    
    public abstract Vector3f getHandPosition(int hand);
    
    public abstract int getWalkingState();
    
    public abstract String logCharacterData();
    
    public abstract void doUpdateActivity();
    
    
}
