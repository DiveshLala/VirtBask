/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.AgentBodyOperations;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.models.BasketballPlayerModel;
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

    
    public BasketballAgent(int i, AnimatedModel am, RigidBodyControl p, float r, float height){
        characterID = i;
        mainNode = p;
        characterModel = am;
        characterModel.initialize();
        currentSpeed = 0.0f;
        radius = r;
        hasCollided = false;
        actionState = 0; // 0 = dribbling, 1 = pass, 2 = shoot
        abo = new AgentBodyOperations(this, characterModel);

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

   
    public void doBallManipulation(){
//        
//            BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
//        
//            //pass thrown
//            if(model.isBallPassed()){
//                float angleOfPass = this.getFacingDirection();
//                ball.passBall(angleOfPass);
//                this.removePossession();
//            }
//            
//            //dribbling
//            else if(model.isBallDribbled()){ 
//                float dribbleTime = model.getCurrentAnimationTimePercentage(1);
//                if(dribbleTime < 0.1){
//                    ball.updateBallInPossession();
//                }
//                else if(dribbleTime > 0.1 && dribbleTime < 0.15){
//                    ball.bounceBall(mainNode.getLinearVelocity());
//                }
//                else if(ball.isBallInSpace(characterModel.getWorldCoordinateOfJoint("right hand"))){
//                    ball.updateBallInPossession();
//                }
//            }
//            
//            else if(model.isBallShot()){
//                if(ball.shootBall()){
//                    this.removePossession();
//                    System.out.println("dfgdg " + this.getBehaviorState());
//                    System.out.println(model.getCurrentAnimation(1));
//                 //   this.setBehaviorState(1);
//                }
//            }
//            else{
//                ball.updateBallInPossession();
//            }   
    }
    
    public void playActionState(int actionState, float animationSpeed, int loopMode, int walkState, float walkSpeed){
        
        LoopMode l;
        
        if(loopMode == 0){
            l = LoopMode.DontLoop;
        }
        else{
            l = LoopMode.Loop;
        }
                
        String armAnim = characterModel.getArmAnimationName(actionState);
        String legAnim = characterModel.getLegAnimationName(walkState);
                
        this.playAnimation(1, armAnim, animationSpeed, l);
        this.playAnimation(2, legAnim, walkSpeed, LoopMode.Loop);
        
    }
    
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.forceAnimation(channel, animationName, speed, l); 
    }
    
    public void playDynamicAnimation(){
    // rotates model dynamically
    }
    

    public float getCurrentMovementProgress(int channelID){
        BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
        return model.getCurrentAnimationTimePercentage(channelID);
    }
    
    public void removeCharacter(){
    
    }
    
    public void setCharacterColours(ArrayList<Vector3f> colours){
    
    }
    

}
