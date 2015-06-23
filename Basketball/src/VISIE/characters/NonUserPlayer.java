/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.Ball;
import Basketball.KinectGesture;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
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
    private GhostControl ghostNode;
    
    private boolean isKinectNUP;
    private String currentKinectGesture = "";
    private boolean isDribbling = false;
    private boolean ballBounce;
    
    
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
        return "N";
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
    
    @Override
    public Vector3f getBallHandVector(){
        BPNewModel bm = (BPNewModel)characterModel;
        return bm.getBallHandVector();
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
    
    public void setAnimation(int actionState, float speed, int loopMode){
        
        LoopMode l;
        String anim = characterModel.getAnimationName(actionState);
        
        if(loopMode == 0){
            l = LoopMode.DontLoop;
        }
        else{
            l = LoopMode.Loop;
        }

        this.playAnimation(1, anim, speed, l);

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
            characterModel.turnBody(headRotationAngle);
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
            characterModel.turnBody(headRotationAngle);
    }
   
   public void setSkeletonJoints(ArrayList<String> jointInfo){
       characterModel.setJointRotations(jointInfo);
   }
   
   public void doGestureActivity(String gesture){
       
       currentKinectGesture = gesture;
       
       if(gesture.equals("shoot") && this.canShoot()){
        //   ball.shootBall();
           this.removePossession();
       }
       else if(gesture.equals("pass")){
             Character c = this.getPassTarget();
            
            if(c != null){
                if(this.canPass((BasketballCharacter)c)){
                        ball.passBall(this, c);
                }
                else{
                    ball.passBall(this, c);
                }
            }
            else{
                ball.passBall(this.getFacingDirection());
            }

           this.removePossession();
       }
       else if(gesture.equals("dribble")){
            if(this.getHandPosition(1).distance(ball.getBallPosition()) < 2f && !isDribbling){
                ball.bounceBall(Conversions.degreesToNormalizedCoordinates(this.getFacingDirection()).mult(0));
                isDribbling = true;
            }
            
   //         System.out.println(isDribbling + " " + ball.isBouncingUp() + " " + (p.getHandPosition(1).y - ball.getBallPosition().y));
     //       ball is being dribbled - put back in hand on way up
            if(isDribbling && ball.isBouncingUp() && ball.getBallPosition().y > 2f){
                isDribbling = false;
                currentKinectGesture = "X";
            }
         //  ball.bounceBall(Conversions.degreesToNormalizedCoordinates(this.getFacingDirection()).mult(0));
       }
       
        if(isDribbling && currentKinectGesture.equals("X")){
                if(ball.isBouncingUp() && ball.getBallPosition().y > 2f){
                     isDribbling = false;
                     currentKinectGesture = "";
                 }
        }
       
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
            return characterModel.getWorldCoordinateOfJoint("left finger");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right finger");
        }      
    }
    
     public boolean isLookingAtTarget(Vector3f target){
        float targetAngle = Conversions.originToTargetAngle(this.getPosition(), target);
        return Conversions.minDistanceBetweenAngles(targetAngle, headRotationAngle) < 10;
    }
     
     public void doBallManipulation(){
         
        if(this.isKinectNUP){
            this.doKinectNUPBallManipulation();
        }
        else{
            
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
                  //     System.out.println("ddssdsd");
//                     if(m.isBallDribbled()){
//                         this.playAnimation(1, "postDribble", 1, LoopMode.DontLoop);
//                     }
                       ball.updateBallInPossession();
                       ball.removeSpin();  
                   }
   //                
                   if(m.isBallDribbled()){

                       BPNewModel bm = (BPNewModel)characterModel;
                       String s = bm.isDribbleBounce();

                   //    ball.dribbleInHand(this.getHandPosition(1));

                       if(s.equals("bounce")){

                           if(ball.isBallInSpace(this.getHandPosition(2)) && ball.isBouncingUp()){
                               ball.updateBallInPossession();
                           }
                           ball.dribbleInHand(this.getHandPosition(1));
                           ballBounce = false;

                       }
                       else if(s.equals("hand")){
                            ball.updateBallInPossession();
                            ballBounce = false;
                       }
                       else if(s.equals("let go")){
                           if(ball.getBallPosition().distance(m.getWorldCoordinateOfJoint("right finger")) < 0.9
                               && !ballBounce){ 
                               ball.bounceBall(Conversions.degreesToNormalizedCoordinates(this.getFacingDirection()).mult(this.getSpeed())); 
                               ballBounce = true;
                           }
                       }
                   }
            }
        
        }     
     }
     
   
   public void doKinectNUPBallManipulation(){
       
       if(!currentKinectGesture.equals("dribble")){
           ball.updateBallInPossession();
       }
   }  
     
     
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.forceAnimation(channel, animationName, speed, l);
  //      characterModel.playAnimation(channel, animationName, speed, l); 
    }
    
    public int getActionState(){
         
         String anim = this.getCurrentGesture(1);
         return characterModel.getActionState(anim);
     }
    
     public String getCurrentGesture(int channel){
         return characterModel.getCurrentAnimation(channel);              
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
    
    public void setKinectNUP(){
        isKinectNUP = true;
    }
    
    public boolean getKinectNUP(){
        return isKinectNUP;
    }
    
    public void setGhostNode(GhostControl g){
        ghostNode = g;
    }
    
    public GhostControl getGhostNode(){
        return ghostNode;
    }
    
    private Character getPassTarget(){
        Character targetedCharacter = null; 
        float minAngle = 100000000f;
        ArrayList<Character> c = SceneCharacterManager.getCharacterArray(); 
        
         for(int i = 0 ; i < c.size(); i++){
             BasketballCharacter testChar = (BasketballCharacter)c.get(i);
             if(this.getID() != testChar.getID() && this.getTeamID() == testChar.getTeamID()){
                 float targetAngle = Conversions.originToTargetAngle(this.getPosition(), testChar.getPosition());
                 float testAngle = Conversions.minDistanceBetweenAngles(this.getFacingDirection(), targetAngle);
                 if(testAngle < minAngle && testAngle < 50f){
                     targetedCharacter = testChar;
                     minAngle = testAngle;
                 }
             }
         }
         
         if(minAngle < 15){
             return targetedCharacter;
         }
         else{
             return null;
         }
     }
    
    public boolean isDribbling(){
        return isDribbling;
    }
    
    public void updateBallPossession(Ball b, Vector3f vec){
        if(b.isCloseToNUP(this)){
                b.adjustCollisionGroup(true);
            }
            else{
                b.adjustCollisionGroup(false);
            }
            
            if(this.getKinectNUP() && this.isDribbling()){
            }
            else{
                b.setBallPosition(vec); 
            }
    }
    
        public boolean checkStealing(Ball b){
       return this.getHandPosition(0).distance(b.getBallPosition()) < 1.5f 
               || this.getHandPosition(1).distance(b.getBallPosition()) < 1.5f;

     }
        
      public void updateBallNoPossession(){
            if(ball.isCloseToNUP(this)){
                ball.adjustCollisionGroup(true);
            }
            else{
                ball.adjustCollisionGroup(false);
            }
     }
      
      public void stopBall(){
      }
      
    public void doUpdateActivity(){}
      
    public String getCharacterLogString(){
        return "N";
    }
    
    public String logCharacterData(){
         
        StringBuilder s = new StringBuilder();
         
        s.append(this.getID() + "N$");
        s.append(this.getPosition() + "$");
        s.append(this.getFacingDirection() + "$");

        if(this.getKinectNUP()){
              s.append(this.getLoggedSkeletonRotations()+ "$");
        }
        else{
              s.append(this.getActionState() + "$");       
        }
        s.append(this.getWalkingState() + "$");
        s.append(String.format("%.3f", this.getAnimationTime(1)) + "$");
        s.append(String.format("%.3f", this.getAnimationTime(2)) + "$");
        
        return s.toString();
     }
    
    @Override
    public void doNUPGestureActivity(String gesture){
        this.doGestureActivity(gesture);
    }
    
    public void handleNUPPredefinedGestures(){
            
        BPNewModel bm = (BPNewModel) characterModel;
        String currentGest = characterModel.getCurrentAnimation(1);
                 
         if(currentGest.contains("shoot") && bm.isBallShot()){
             this.doShooting();
         }
         else if(currentGest.equals("pass")){
             this.doPassGesture();
         }
//         else if(currentGest.equals("initiatePass")){
//            Character c = this.getGestureTarget();
//            ArrayList<Character> list = new ArrayList<Character>();
//            list.add(c);
//         }
    }
            
    private void doPassGesture(){
     
        BPNewModel bm = (BPNewModel)characterModel;     
        Character c = this.getPassTarget();
        ArrayList<Character> list = new ArrayList<Character>();
        list.add(c);

         if(bm.isBallPassed()){
             if(c != null){
                 if(this.canPass((BasketballCharacter)c)){
                    ball.passBall(this, c);
                 }
                 else{
                    ball.passBall(this.getFacingDirection());
                 }
             }
             else{
                 ball.passBall(this.getFacingDirection());
             }
             this.removePossession();
         }
        
    }
    
    private void doShooting(){
        
        if(ball.isValidShoot()){
            this.removePossession();
        }            
    }
    
    @Override
    public boolean isNUP(){
        return true;
    }
    
    @Override
    public void playPassSound(){
        this.flagUtterance("pass");
    }
        
//    public String getSkeletonRotations(){
//        
//        return characterModel.getNUPBoneRotationInfo();
//    }  
    
}


