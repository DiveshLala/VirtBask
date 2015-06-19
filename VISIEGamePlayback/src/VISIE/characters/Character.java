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
    
    public void setSpeed(float f){
        currentSpeed = f;
    }
    
    public float getSpeed(){
        return currentSpeed;
    }
    
    public int getID(){
        return characterID;
    }
    
    public ArrayList<Vector3f> getModelColours(){
        return characterModel.getModelColours();
    }
    
    public ArrayList<String> getModelTextures(){
        return characterModel.getModelTextures();
    }
    
    public boolean isInArea(Vector3f loc, float dist){     
      return CollisionMath.circleCollisionTest(this.getPosition(), loc, this.getRadius(), dist); 
    }
    
    public Spatial getCharacterMesh(){
        return characterModel.getModel();
    }
    
//    public String getSkeletonRotations(){
//        return characterModel.getBoneRotationInfo();
//    }  
    
    public void setCharacterType(String type){
        characterType = type;
    }
    
    public void setExistenceNode(Node n){
        existenceNode = n;
    }
    
    public void removeExistence(){
        existenceNode.removeFromParent();
    }
    
    public abstract Vector3f getPosition();
        
 //   public abstract void setFacingDirection(float torsoRotation, float headRotation);
    
    public abstract boolean isLookingAtTarget(Vector3f target);
    
    public abstract float getFacingDirection();
    
 //   public abstract void setHeadAngle(Vector3f vec);
    
    public abstract void setPosition(Vector3f vec);
    
    public abstract float getCharacterRotation();
    
    public abstract PhysicsControl getPhysicsNode();
    
    public abstract Vector3f getHandPosition(int hand);
    
    
//    public String getCharacterType();
//    public String getModelType();
//    public Vector3f getPosition();
//    public int getActionState();
//    public void setFacingDirection(Vector3f vec);
//    public void setFacingDirection(float torsoRotation, float headRotation);
//    public boolean isLookingAtTarget(Vector3f target);
//    public void setHeadAngle(Vector3f vec);
//    public float getFacingDirection();
//    public float getRadius();
//    public Vector3f getTargetPosition();
//    public float getSpeed();
//    public float getHeadAngle();
//    public int getID();
//    public void setPosition(Vector3f vec);
//    public void setActionState(int state);
//    public void setSpeed(float speed);
//    public ArrayList<Vector3f> getModelColours();
//    public float getCharacterRotation();
//    public void removeCharacter();
//    public PhysicsControl getPhysicsNode();
//    public boolean isInArea(Vector3f loc, float dist);
//    public Spatial getCharacterMesh();
//    public String getSkeletonRotations();
//    public Vector3f getHandPosition();
    
}
