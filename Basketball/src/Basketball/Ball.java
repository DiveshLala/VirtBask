/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;
import VISIE.Sound.BallSoundNode;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.Scene;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.characters.KinectPlayer;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.characters.SkilledAgent;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCreator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import java.util.Random;

/**
 *
 * @author DiveshLala
 */
public class Ball{
    
    Node parentNode;
    RigidBodyControl ballPhysicsNode;
    Vector3f ballPosition;
    int playerInPossession;
    int lastTouched;
    float radius;
    long latestBallShotTime;
    long latestBallPassTime;
    ArrayList<Integer> touchHistory;
    BallSoundNode ballSoundNode;
    
    public Ball(Vector3f initialPos, RigidBodyControl n, float r){
        
        ballPosition = initialPos;
        ballPhysicsNode = n;  
        radius = r;
        ballPhysicsNode.setFriction(0.5f);
        ballPhysicsNode.setRestitution(0.75f);
        ballPhysicsNode.setMass(0.65f);
        ballPhysicsNode.setCollisionGroup(2);
        adjustCollisionGroup(true);
        touchHistory = new ArrayList<Integer>();
        
   //     parentNode.getControl(null);
    }
        
    public void setMainNode(Node n){
        parentNode = n;
    }
    
    public void setSoundNode(BallSoundNode n){
        ballSoundNode = n;
    }
    
    public void playBounceSound(float force){
        ballSoundNode.playBounceSound(force);
    }
    
    public void playHittingGoalSound(float force){
       ballSoundNode.playHittingGoalSound(force); 
    }
    
    public Vector3f getBallPosition(){
        return ballPhysicsNode.getPhysicsLocation();
    }
    
    public void setBallPosition(Vector3f pos){
        ballPhysicsNode.setPhysicsLocation(pos);
    }
    
    public void setLastTouched(int i){
        lastTouched = i;
        this.updateTouchHistory(i);
    }
    
    public int getLastTouched(){
        return lastTouched;
    }
    
    public int getPenultimateTouch(){
        if(touchHistory.size() > 1){
            return touchHistory.get(touchHistory.size() - 2);
        }
        return touchHistory.get(0);
    }
     
    public void removeDamping(){
        ballPhysicsNode.setDamping(0, 0);
    }
    
    public void updateBallInPossession(){
         BasketballCharacter bc = (BasketballCharacter)SceneCharacterManager.getCharacterByID(playerInPossession);              
//         Vector3f vec = bc.getBallHandVector().subtract(0, radius, 0);
         ballPhysicsNode.setLinearVelocity(Vector3f.ZERO);
         Vector3f vec = bc.getBallHandVector();
         this.setBallPosition(vec);
    }
    
    public BasketballCharacter checkForStealing(Character possessor){
        BasketballAgent ba = (BasketballAgent)possessor;
        ArrayList<BasketballCharacter> opponents = ba.getOpponents();

        for(int i = 0; i < opponents.size(); i++){
            BasketballCharacter opp = opponents.get(i);
//            Vector3f lHandPos = opp.getHandPosition(0);
//            Vector3f rHandPos = opp.getHandPosition(1);
            
            if(opp.checkStealing(this)){
                return opp;
            }
        }
        
        return null;
    }
    
    public void updateBallWithoutPossession(){
                       
      //  this.removeSpin();
        this.reduceSpin();
        this.adjustCollisionGroup(false);
        ArrayList<Character> clist = SceneCharacterManager.getCharacterArray();
        for(int i = 0; i < clist.size(); i++){
            
            Character c = clist.get(i);
            CollisionResults results = new CollisionResults();
            parentNode.collideWith(c.getCharacterMesh().getWorldBound(), results);
            BasketballCharacter bc = (BasketballCharacter)c;
            bc.updateBallNoPossession();
//            if(c instanceof VISIE.characters.KinectPlayer){
//                if(this.isCloseToPlayer((KinectPlayer)c)){
//                    this.adjustCollisionGroup(true);
//                }
//                else{
//                    this.adjustCollisionGroup(false);
//                }
//            }
//            else if(c instanceof VISIE.characters.NonUserPlayer){
//                if(this.isCloseToNUP((NonUserPlayer)c)){
//                    this.adjustCollisionGroup(true);
//                }
//                else{
//                    this.adjustCollisionGroup(false);
//                }
//            }
            
            if(results.size() > 0){
                
                boolean playerIsTeamMate = bc.playerIsTeamMate((BasketballCharacter)SceneCharacterManager.getCharacterByID(lastTouched));
                boolean reactionTimeOK = (System.currentTimeMillis() - latestBallPassTime) > 400 && (System.currentTimeMillis() - latestBallShotTime) > 400;

                if(!bc.isShooting() && !bc.isPassing() && bc.canSeeBall(this)
                   && (playerIsTeamMate || reactionTimeOK)){                      
                        bc.setPossession();
                        bc.setActionState(0);
                        bc.stopBall();
                        bc.playCatchSound();
                        System.out.println(playerInPossession + " is now in possession");    
                }
            }
//            else if(this.checkForCapture(c)){
//                bc.setPossession();
//                bc.setActionState(0);
//                bc.stopBall();
//                System.out.println(playerInPossession + " has captured possession");   
//            }
        }
    }
    
        
    private boolean checkForCapture(Character c){
        
        if(c.getID() == lastTouched){
            return false;
        }        
               
        Vector3f leftHandPos = c.getHandPosition(0);
        Vector3f rightHandPos = c.getHandPosition(1);
        
        return (leftHandPos.distance(this.getBallPosition()) < 1f || rightHandPos.distance(this.getBallPosition()) < 1f);
    
    }
    
    public void setPossession(int i){
        playerInPossession = i;
        BasketballCharacter bc = (BasketballCharacter)SceneCharacterManager.getCharacterByID(i);
        bc.adjustCollisionGroup();
//        if(SceneCharacterManager.getCharacterByID(playerInPossession) instanceof VISIE.characters.Player){
//             this.adjustCollisionGroup(true);
//         }
//         else{
//             this.adjustCollisionGroup(false);
//         }
    }
    
    public void passBall(float angle){
        
        float yForce = 20;
        this.applyForceToBall(angle, yForce);
        this.reduceSpin();
        latestBallPassTime = System.currentTimeMillis();
        BasketballCharacter bc = (BasketballCharacter) SceneCharacterManager.getCharacterByID(lastTouched);
        bc.playPassSound();
   //     playerInPossession = -1;
    }
    
    public void passBall(Character passer, Character receiver){
        
        
        
        Vector3f ballPos = this.getBallPosition();
        Vector3f target = receiver.getPosition();
        
     //   float angle = Conversions.originToTargetAngle(passer.getPosition(), receiver.getPosition());
    //    this.passBall(angle);
        float releaseAngle = (float)Math.toRadians(20);
        float powerMultiplier = 2f;
        float h = target.y - ballPos.y;
        double l = Math.sqrt(Math.pow(target.x - ballPos.x, 2) + Math.pow(target.z - ballPos.z, 2));
        double a = l/Math.cos(releaseAngle);
        double b = 9.8/(2 * ((l * Math.tan(releaseAngle)) - h));
        double initialVelocity = a * Math.sqrt(b);
        Vector3f targetVector = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(this.getBallPosition(), target));

        float heightForce = (float)initialVelocity * (float)Math.sin(releaseAngle);
       float lengthForce = (float)initialVelocity * (float)Math.cos(releaseAngle);  
//        
        Vector3f forceVector = new Vector3f(targetVector.x * lengthForce * powerMultiplier, heightForce, targetVector.z * lengthForce * powerMultiplier);
//        
  //      if(Vector3f.isValidVector(forceVector)){
        this.applyForceToBall(forceVector);
        this.reduceSpin();
        latestBallPassTime = System.currentTimeMillis();
        
        BasketballCharacter bc = (BasketballCharacter) SceneCharacterManager.getCharacterByID(lastTouched);
        bc.playPassSound();
  //          return true;
 //       }
//        else{
//            return false;
//        }
    }
    
    
    
    public void bounceBall(Vector3f movementVector){
        Vector3f yForce = Vector3f.UNIT_Y.mult(-15f);
        Vector3f force = movementVector.add(yForce);
        this.applyForceToBall(force.mult(1.5f));
        this.removeSpin();
    }
    
    //ensures ball bounces up to hand of player when dribbling
    public void dribbleInHand(Vector3f handPos){      
        this.setBallPosition(new Vector3f(handPos.x, this.getBallPosition().y, handPos.z));
    }
    
    public boolean isValidShoot(){
        Vector3f ballPos = this.getBallPosition();
        Vector3f goalPos = new Vector3f(Court.getHoopLocation());
        Vector3f goal2D = new Vector3f(Court.getHoopLocation().x, 0, Court.getHoopLocation().z);
        float releaseAngle;
        BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
        
        if(ballPos.distance(goal2D) < 20){
            releaseAngle = (float)Math.toRadians(70);
        }
        else if(possessor!= null && possessor.getNearestCharacterDist(possessor.getOpponents()) < 3f){
            releaseAngle = (float)Math.toRadians(70);
        }
        else{
            releaseAngle = (float)Math.toRadians(50);
        }
        
        float h = goalPos.y - ballPos.y;
        double l = Math.sqrt(Math.pow(goalPos.x - ballPos.x, 2) + Math.pow(goalPos.z - ballPos.z, 2));
        double a = l/Math.cos(releaseAngle);
        double b = 9.8/(2 * ((l * Math.tan(releaseAngle)) - h));
        double initialVelocity = a * Math.sqrt(b);
        Vector3f targetVector = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(this.getBallPosition(), goalPos));

        float heightForce = (float)initialVelocity * (float)Math.sin(releaseAngle);
        float lengthForce = (float)initialVelocity * (float)Math.cos(releaseAngle);  
        
        
        Vector3f forceVector = new Vector3f(targetVector.x * lengthForce, heightForce, targetVector.z * lengthForce);
        Vector3f modVec = this.calculateShootingError(forceVector);
        
        
        if(Vector3f.isValidVector(forceVector)){
            this.applyForceToBall(modVec);
            this.reduceSpin();
            latestBallShotTime = System.currentTimeMillis();
            BasketballCharacter bc = (BasketballCharacter) SceneCharacterManager.getCharacterByID(lastTouched);
            bc.playPassSound();
            return true;
        }
        else{
            return false;
        }

    }
    
    private Vector3f calculateShootingError(Vector3f idealVec){
        float xError = 0;
        float xMult = 0;
        float yError = 0;
        float yMult = 0;
        float zError = 0;
        float zMult = 0;
        float angleToGoal = Conversions.originToTargetAngle(this.getBallPosition(), Court.getHoopLocation());
        float distanceToGoal = this.getBallPosition().distance(Court.getHoopLocation());
        float facingDirection = SceneCharacterManager.getCharacterByID(playerInPossession).getFacingDirection();
        float angleDiff = Math.abs(angleToGoal - facingDirection);
        float shotError = 0;
        float distError = 0;
        float penalty;
        
        if(SceneCharacterManager.getCharacterInPossession() != null){
            penalty = SceneCharacterManager.getCharacterInPossession().getShootPenalty();
        }
        else{
            penalty = 1;
        }
        //    System.out.println("angle diff " + angleDiff + "dist to goal " + distanceToGoal);
        
        if(angleDiff < 2f){
            shotError += 0;
        }
        else if(angleDiff < 10f){
            shotError += 1f;
        }
        else{
            shotError += 3f;
        }
        
        if(distanceToGoal < 10f){
            distError += 0; 
        }
        else if(distanceToGoal < 20f){
            distError += 1f;
        }
        else{
            distError += 2f;
        }
        
        //0.03 for max mult
        xMult = idealVec.x * (0.03f * shotError);
        yMult = idealVec.y * (0.03f * distError);
        zMult = idealVec.z * (0.03f * shotError);
        
        Random r = new Random();
        
        xError = (float)r.nextGaussian() * xMult; 
        yError = (float)r.nextGaussian() * yMult;
        zError = (float)r.nextGaussian() * zMult;
        
 //       System.out.println("mults " + xMult + " " + yMult + " " + zMult);
  //      System.out.println("errors " + xError + " " + yError + " " + zError);
        
        return new Vector3f(idealVec.x + (xError * penalty), idealVec.y + (yError * penalty), idealVec.z + (zError * penalty));
    }
    
    private void applyForceToBall(float angle, float yForce){
        ballPhysicsNode.setLinearVelocity(Conversions.degreesToNormalizedCoordinates(angle).mult(yForce).add(0, 5, 0));
    }
    
    private void applyForceToBall(Vector3f force){
        ballPhysicsNode.setLinearVelocity(force);
    }
    
    public boolean isBallInSpace(Vector3f pos){
        Vector3f ballPos = this.getBallPosition();
        return ballPos.distance(pos) < radius;
    }
    
    public void removeSpin(){
        ballPhysicsNode.setPhysicsRotation(Quaternion.ZERO);
        ballPhysicsNode.setAngularFactor(0);
        ballPhysicsNode.setDamping(0.0f, 0f);
    }
    
    public void reduceSpin(){
        ballPhysicsNode.setAngularFactor(0.02f);
        this.removeDamping();
    }
    
    public void adjustCollisionGroup(boolean isTurnedOn){
                          
            
        if(isTurnedOn){
            ballPhysicsNode.addCollideWithGroup(3);
            ballPhysicsNode.removeCollideWithGroup(1);
//            System.out.println(ballPhysicsNode.getLinearVelocity());
//            System.out.println("cg " + ballPhysicsNode.getCollisionGroup());
//            System.out.println("cwg " + ballPhysicsNode.getCollideWithGroups());

        }
        else{
            ballPhysicsNode.addCollideWithGroup(1);
            ballPhysicsNode.addCollideWithGroup(2);
//            System.out.println(ballPhysicsNode.getLinearVelocity());
//            System.out.println("cg " + ballPhysicsNode.getCollisionGroup());
//            System.out.println("cwg " + ballPhysicsNode.getCollideWithGroups());
        }
    }
    
    public boolean isBouncingUp(){
       return ballPhysicsNode.getLinearVelocity().getY() > 0;
    }
    
    public void stopBall(){
        ballPhysicsNode.setLinearVelocity(Vector3f.ZERO);
        ballPhysicsNode.setAngularVelocity(Vector3f.ZERO);
        this.removeDamping();
        this.removeSpin();
    }
    
    public boolean isCloseToPlayer(Player kp){
        
//        for(int i = 0; i < kp.getGhostNode().getOverlappingObjects().size(); i++){
//            Node n = (Node)kp.getGhostNode().getOverlappingObjects().get(i).getUserObject();
//            if(n.getName().equals("Ball")){
//                return true;
//            }   
//        }
        
        return false;       
    }
    
    public boolean isCloseToNUP(NonUserPlayer nup){
        
        for(int i = 0; i < nup.getGhostNode().getOverlappingObjects().size(); i++){
            Node n = (Node)nup.getGhostNode().getOverlappingObjects().get(i).getUserObject();
            if(n.getName().equals("Ball")){
                return true;
            }   
        }
        
        return false;       
    }
    
    public Vector3f getBallTravellingDirection(){
        return ballPhysicsNode.getLinearVelocity();
    }
    
    public boolean isBeingShot(){
        return (System.currentTimeMillis() - latestBallShotTime) < 3000;
    }
    
    private void updateTouchHistory(int playerID){

        if(touchHistory.isEmpty() || touchHistory.get(touchHistory.size() - 1) != playerID){
            touchHistory.add(playerID);
        }
    }
    
    public void makeStatic(){
        ballPhysicsNode.setLinearVelocity(Vector3f.ZERO);
    }
    
    
}
