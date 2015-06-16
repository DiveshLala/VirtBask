/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.characters;
import Basketball.Ball;
import Basketball.PlayerGesture;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.gesturerecognition.GestureRecognition;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.network.KinectClient;
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
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Ray;
import com.jme3.scene.shape.Box;



/**
 *
 * @author huang
 */
public class Player extends BasketballCharacter{

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
    private float mutualGazeAngle = 40;
    private float perceivedFieldOfVision = 55;
    protected PlayerGesture playerGesture;
    private boolean isKinect;
    private GhostControl ghostNode;
    private boolean ballBounce = false;

    public Player(CharacterControl p, float rad, int i, AnimatedModel model){
        mainNode = p;
        radius = rad;
        actionState = 0;
        characterID = i;
        characterModel = model;
        characterModel.initialize();
        torsoTurnSpeed = 1;
        characterModel.setTransparency(0.2f);
        isPlayer = true;
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
            return characterModel.getWorldCoordinateOfJoint("left finger");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right finger");
        }      
    }
    
    @Override
    public Vector3f getBallHandVector(){
        BPNewModel bm = (BPNewModel)characterModel;
        return bm.getBallPossessionNode().getWorldTranslation();
    }
    
    public Vector3f getElbowPosition(int hand){
        if(hand == 0){
            return characterModel.getWorldCoordinateOfJoint("left elbow");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right elbow");
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
            currentSpeed = 0.035f;
        else
            currentSpeed = 0;
    }

    public CharacterControl getMainNode(){
        return mainNode;
    }
    
    public void setWalkingActionState(boolean f){
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
    
    
    
    public void walk(Vector3f vec, int turnCode){ 
        mainNode.setWalkDirection(vec);
        this.doWalkingAnimations(vec, turnCode);
    }
    
    protected void doWalkingAnimations(Vector3f vec, int turnCode){
        
       
        if(turnCode == 3){
            this.playAnimation(2, "stepBack", 1, LoopMode.Loop);
        }
        else if(!vec.equals(Vector3f.ZERO)){
             this.playAnimation(2, "walk", 1, LoopMode.Loop);
        }
        else if(turnCode == 1){
            this.playAnimation(2, "turnLeft", 2, LoopMode.Loop); 
        }
        else if(turnCode == 2){
            this.playAnimation(2, "turnRight", 2, LoopMode.Loop);
        }
        else{
            this.playAnimation(2, "standingPose", 1, LoopMode.Loop);
        }
            
    }
    
    //turn model
    public void turnBody(){
        mainNode.setViewDirection(Conversions.degreesToNormalizedCoordinates(playerFacingDirection));
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
    
    @Override
    //character is known
    public boolean canSeeCharacter(Character c){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), c.getPosition());
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < mutualGazeAngle;
    }
    
    //any team mate
    protected boolean isLookingAtTeammate(){
        for(Character c:this.getTeamMates()){
            if(!c.equals(this) && this.isLookingAtTarget(c.getPosition())){
                return true;
            }
        }
        return false;
    }
    
     public boolean isLookingAtTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < mutualGazeAngle;
    }
     
     public void doBallManipulation(){
                           
         if(characterModel.getTypeIdentifier().equals("P")){      
                 //dribbling
                BPNewModel m = (BPNewModel)characterModel;
                
                if(this.getSpeed() > 0){
                    m.doDribbling(0.5f);
                    if(m.getCurrentAnimation(1).equals("preDribble")){
                        ball.updateBallInPossession();
                    }
                }
                else{
                  if(m.isBallDribbled()){
                      this.playAnimation(1, "postDribble", 1, LoopMode.DontLoop);
                  }
                    ball.updateBallInPossession();
                    ball.removeSpin();  
                }
//                
                if(m.isBallDribbled()){
                                        
                    BPNewModel bm = (BPNewModel)characterModel;
                    String s = bm.isDribbleBounce();
                    
               //     System.out.println(s);
                                       
                //    ball.dribbleInHand(this.getHandPosition(1));
                    
                    if(s.equals("bounce")){
                                                
                        if(ball.isBallInSpace(this.getBallHandVector()) && ball.isBouncingUp()){
                            ball.updateBallInPossession();
                        }
                        ball.dribbleInHand(this.getBallHandVector());
////                        System.out.println("bounce  " +  ball.getBallPosition());
              //          ballBounce = false;
                                               
                    }
                    else if(s.equals("hand")){
                         ball.updateBallInPossession();
             //            ballBounce = false;
                    }
                    else if(s.equals("let go") && ball.isBallInSpace(this.getBallHandVector())){
                            ball.bounceBall(mainNode.getViewDirection().normalize().mult(this.getSpeed())); 
//                             System.out.println("let go  " +  ball.getBallPosition());
//                            ballBounce = true;
  //                      }
                    }
                }
         }
     }
     
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
         characterModel.forceAnimation(channel, animationName, speed, l); 
    }
    
    public void turnHead(){
        characterModel.turnHead(45f);
    }
     
     public void updateGestures(){
        playerGesture.updatePredefinedGestures();
     }
     
     public void recordShootContexts(){
        for(BasketballCharacter bc:this.getTeamMates()){
           if(bc.getCharacterType().contains("Collab")){
               CollabAgent ca = (CollabAgent)bc;
               ca.recordShootContexts();
           }
       }
     }
     
     public void doBlocking(){
         if(this.getCurrentGestureName().toLowerCase().contains("block")){
            this.playAnimation(1, "standingPose", 1, LoopMode.Loop);
         }
         else{
             this.playAnimation(1, "blockLoop", 1, LoopMode.Loop);
         }

     }
     
     public void shootBall(){
         if(this.hasPossession){
            this.playAnimation(1, "shoot", 2, LoopMode.DontLoop);
         }
     }
     
     public float getTurnSpeed(){
         return torsoTurnSpeed;
     }
     
     public String getCurrentGestureName(){
         return characterModel.getCurrentAnimation(1); 
     }
     
     public String getCurrentGesture(int channel){
         return characterModel.getCurrentAnimation(channel);              
     }
     
     public void setPlayerTarget(Vector3f target){
         targetPosition = target;
     }
     
     public boolean hasReachedTarget(){
         return this.getPosition().distance(targetPosition) < 5f;
     }
     
     @Override
     public boolean isShooting(){
         return characterModel.getCurrentAnimation(1).contains("shoot");
     }
     
//     public boolean isPassing(){
//         return false;
//     }
     
     public void jump(){
         mainNode.setJumpSpeed(13);
        mainNode.jump();
     }
     
     public int getActionState(){
         
         String anim = this.getCurrentGesture(1);
         return characterModel.getActionState(anim);
//         if(anim.equals("standingPose")){
//             return 0;
//         }
//         else if(anim.equals("dribbleLoop")){
//             return 1;
//         }
//         else if(anim.equals("shootAction")){
//             return 2;
//         }
//         else{
//             return 0;
//         }
     }
     
     public int getWalkingState(){
         String anim = characterModel.getCurrentAnimation(2);
         return characterModel.getWalkingState(anim);
//         if(this.getSpeed() > 0){
//             return 1;
//         }
//         else{
//             return 0;
//         }
    }
     
     public void setGhostNode(GhostControl gc){
         ghostNode = gc;
     }
     
     public GhostControl getGhostNode(){
         return ghostNode;
     }
     
     public void updateBallPossession(Ball b, Vector3f vec){
         ball.setBallPosition(vec);
     }
     
     public boolean checkStealing(Ball b){
       return this.getHandPosition(0).distance(b.getBallPosition()) < 1.5f 
               || this.getHandPosition(1).distance(b.getBallPosition()) < 1.5f;

     }
     
     public void updateBallNoPossession(){
         
     }
     
     
     
     public void stopBall(){}
     
     @Override
     public void adjustCollisionGroup(){
         ball.adjustCollisionGroup(true);
     }
     
     @Override
     public boolean isKinectPlayer(){
         return false;
     }
     
     @Override
     public void doUpdateActivity(){}
     
     @Override
    public String getCharacterLogString(){
        return "P";
    }
     
     public String logCharacterData(){
         
         StringBuilder s = new StringBuilder();
         
        s.append(this.getID() + "P$");
        s.append(this.getPosition() + "$");
        s.append(this.getFacingDirection() + "$");
        if(this.isKinectPlayer()){
              s.append(this.getLoggedSkeletonRotations() + "$");
        }
        else{
              s.append(this.getActionState() + "$");   
        }
        s.append(this.getWalkingState() + "$");
        s.append(String.format("%.3f", this.getAnimationTime(1)) + "$");
        s.append(String.format("%.3f", this.getAnimationTime(2)) + "$");
        
        return s.toString();
     }
     
     public boolean checkArmRaise(){
         return playerGesture.areArmsRaised();
     }
     
    
          
     
}
