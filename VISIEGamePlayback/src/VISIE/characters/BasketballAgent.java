/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.AgentBodyOperations;
import Basketball.AgentPerception;
import Basketball.Ball;

import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.models.BPNewModel;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Divesh
 */
public class BasketballAgent extends BasketballCharacter{
    
    protected RigidBodyControl mainNode;
    protected BPNewModel agentModel;
 //   protected float torsoRotationAngle;
    public List<Vector3f> targetPositions = new ArrayList<Vector3f>();

    //physical parameters
    private float maxSpeed;

  //  private float radius;
    private boolean hasCollided;
    
    
    protected int behaviouralState = 0;
    private Character focusedCharacter;

    private ArrayList<Float> bounds;
    public AgentBodyOperations abo;
    public AgentPerception perception;
    
    public BasketballAgent(int i, BPNewModel am, RigidBodyControl p, float r, float height){
        characterID = i;
        mainNode = p;
        characterModel = am;
        characterModel.initialize();
        agentModel = (BPNewModel)characterModel;
        currentSpeed = 0.0f;
        radius = r;
        hasCollided = false;
        actionState = 0; // 0 = dribbling, 1 = pass, 2 = shoot

        abo = new AgentBodyOperations(this, characterModel);
        perception = new AgentPerception(this);

    }
    
    public AnimatedModel getModel(){
        return characterModel;
    }
    
    public void turnBody(float f){
        this.abo.setFacingDirection(f, f);
    }
        
    public String getCharacterType(){
        return characterType;
    }

    public Vector3f getPosition(){
        return mainNode.getPhysicsLocation();
    }
    
    public float getFacingDirection(){
        return abo.getFacingDirection();
    }
    
    public boolean isLookingAtTarget(Vector3f target){
       return perception.isLookingAtTarget(target);
    }

    public void setPosition(Vector3f vec){
        mainNode.setPhysicsLocation(vec);
    }
    
    public void setFocusedCharacter(Character c){
        focusedCharacter = c;
    }
    
    public Character getFocusedCharacter(){
        return focusedCharacter;
    }
           
    public void walk(Vector3f direction, float speed){
        mainNode.setLinearVelocity(direction.mult(speed));
    }
    
    public float getCharacterRotation(){
        return this.getFacingDirection();
    }
    
    public RigidBodyControl getPhysicsNode(){
        return mainNode;
    }
    
    public Vector3f getHandPosition(int hand){
        if(hand == 0){
            return characterModel.getWorldCoordinateOfJoint("left hand");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right hand");
        }
    }

    
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.playAnimation(channel, animationName, speed, l); 
    }
    
    public void playDynamicAnimation(){
    // rotates model dynamically
    }
    
    

    
    public String getCurrentAnimations(){
        return characterModel.getCurrentAnimation(1) + "," + characterModel.getCurrentAnimation(2);
    }
    
}
