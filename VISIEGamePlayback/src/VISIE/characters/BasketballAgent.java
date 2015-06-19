/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.AgentBodyOperations;
import Basketball.AgentJPMessaging;
import Basketball.AgentPerception;
import Basketball.AgentPlanning;
import Basketball.AgentRecognition;
import Basketball.AgentUptake;
import Basketball.Ball;
import Basketball.BehaviorModule;
import Basketball.JointProject;
import Basketball.JointProjectManagerModule;
import Basketball.TrainingBehaviorModule;
import VISIE.mathfunctions.Conversions;
import VISIE.JointProjectXMLProcessor;
import VISIE.models.AnimatedModel;
import VISIE.models.BasketballPlayerModel;
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
public class BasketballAgent extends BasketballCharacter implements JointProjectCharacter{
    
    protected RigidBodyControl mainNode;
    protected BasketballPlayerModel agentModel;
 //   protected float torsoRotationAngle;
    public List<Vector3f> targetPositions = new ArrayList<Vector3f>();

    //physical parameters
    private float maxSpeed;

  //  private float radius;
    private boolean hasCollided;
    
    //state parameters for robot model (shouldn't affect behavior)
 //   protected int actionState; // 0 = stand, 1 = run, 2 = shoot
    protected BehaviorModule behaviorModule;
    public JointProjectManagerModule JPManager;
    public AgentJPMessaging JPMessaging;
    
    protected int behaviouralState = 0;
    private Character focusedCharacter;

    private ArrayList<Float> bounds;
    public AgentBodyOperations abo;
    public AgentPerception perception;
    public AgentPlanning planner;
    public AgentUptake uptake;
    public AgentRecognition recognition;
    
    public BasketballAgent(int i, BasketballPlayerModel am, RigidBodyControl p, float r, float height){
        characterID = i;
        mainNode = p;
        characterModel = am;
        characterModel.initialize();
        agentModel = (BasketballPlayerModel)characterModel;
        currentSpeed = 0.0f;
        radius = r;
        hasCollided = false;
        actionState = 0; // 0 = dribbling, 1 = pass, 2 = shoot
        behaviorModule = new BehaviorModule(this);
        JPManager = new JointProjectManagerModule(this);
        abo = new AgentBodyOperations(this, characterModel);
        perception = new AgentPerception(this);
        JPMessaging = new AgentJPMessaging(this);
        planner = new AgentPlanning(this);
        uptake = new AgentUptake(this);
        recognition = new AgentRecognition(this);
    }
    
    public AnimatedModel getModel(){
        return characterModel;
    }
    
    public void setTrainingBehaviorModule(int i){
        behaviorModule = new TrainingBehaviorModule(this, i);
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
    
    public void setBehaviorState(int b){
        behaviorModule.setBehaviorState(b);
    }
    
    public int getBehaviorState(){
        return behaviorModule.getBehaviorState();
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
    
    public void initialiseTarget(){
        planner.setTargetPosition(this.getPosition());
    }
    
    public void updateJointProjects(){ 
        JPManager.updateJointProjects(behaviorModule.getBehaviorState());
    }
       
    public void updateMovements(){        
        //for arm channel
        if(behaviorModule.isEngagedInJointProject()){
            // animations have already been called     
//                if(!planner.isTargetReached()){ //target reached
//                    abo.moveTowardsTarget(planner.getTargetPosition());
//                }
//                else{                           //stop
//                    this.playAnimation(2, "standingPose", 1, LoopMode.Loop);
//                    this.setSpeed(0);
//                }
        }
        else{
            BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
            //animations are according to agent state
                        
            if(actionState == 0){             //dribbling
                
                if(!planner.isTargetReached()){ //target reached
                    abo.moveTowardsTarget(planner.getTargetPosition(), 5);
                }
                else{                           //stop
                    agentModel.standStill();
                    this.setSpeed(0);
                }
                
                if(this.getSpeed() > 0){
                   model.doDribbling(this.getSpeed()/10);
                   characterModel.playAnimation(2, "walk", this.getSpeed()/2, LoopMode.Loop);
                }
            }
            
            else if(actionState == 1){  //no ball
                
                if(!planner.isTargetReached()){ //target reached
                    abo.moveTowardsTarget(planner.getTargetPosition(), 5);
                }
                else{                           //stop
                    agentModel.standStill();
                    this.setSpeed(0);
                    
                    BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
                    
                    if(possessor != null){
                        if(possessor.playerIsTeamMate(this)){
                             abo.turnBodyToTarget(possessor.getPosition());  
                        }
                        else{
                            abo.turnBodyToTarget(planner.getClosestOpponent().getPosition());
                        }
                    }
                }
                
                if(this.getSpeed() > 0){
                   characterModel.playAnimation(1, "standingPose", this.getSpeed()/2, LoopMode.Loop);
                   characterModel.playAnimation(2, "walk", this.getSpeed()/2, LoopMode.Loop);
                }    
            }
            
            else if(actionState == 2){            //shooting
           //     System.out.println("sdsds");
                if(perception.isLookingAtTarget(Court.getHoopLocation())){
                    model.playArmAnimation("shootAction", 0.75f, LoopMode.DontLoop);
                }
                else{
                    abo.turnBodyToTarget(Court.getHoopLocation());
                }
            }
        }
    }
    
    public void doBallManipulation(){
        
            BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
        
            //pass thrown
            if(model.isBallPassed()){      
                BasketballCharacter target = planner.getPassingTarget();
                
                if(target != null){
                    ball.passBall(this, target);
                }
                else{
                    System.out.println("ffff");
                    ball.passBall(this.getFacingDirection());
                }
                
                this.removePossession();
            }
            
            //dribbling
            else if(model.isBallDribbled()){ 
                float dribbleTime = model.getCurrentAnimationTimePercentage(1);
                if(dribbleTime < 0.1){
                    ball.updateBallInPossession();
                }
                else if(dribbleTime > 0.1 && dribbleTime < 0.15){
                    ball.bounceBall(mainNode.getLinearVelocity());
                }
                else if(ball.isBallInSpace(characterModel.getWorldCoordinateOfJoint("right hand"))){
                    ball.updateBallInPossession();
                }
            }
            
            else if(model.isBallShot()){
                if(ball.shootBall()){
                    this.removePossession();
//                    System.out.println("dfgdg " + this.getBehaviorState());
//                    System.out.println(model.getCurrentAnimation(1));
                 //   this.setBehaviorState(1);
                }
            }
            else{
                ball.updateBallInPossession();
            }   
    }
    
    public void playAnimation(int channel, String animationName, float speed, LoopMode l){
        characterModel.playAnimation(channel, animationName, speed, l); 
    }
    
    public void playDynamicAnimation(){
    // rotates model dynamically
    }
    
    
    public void setBehavior(){
        behaviorModule.updateBehavior();
    }
    
    public void receiveJointProjectFrom(Character sender, int jpID){
          JPMessaging.receiveJointProjectFrom(sender, jpID);
    }

    public boolean isRecognitionLimitReached(String jpName, String movementName){
        float[] data = JointProjectXMLProcessor.getMovementLimit(jpName, movementName, 1);  
        return this.getCurrentMovementProgress((int)data[1]) >= data[0];
    }
    
    public float getCurrentMovementProgress(int channelID){
   //     System.out.println(agentModel.getCurrentAnimationTimePercentage(channelID));
        BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
        return model.getCurrentAnimationTimePercentage(channelID);
    }
    
    public void receiveSharedProjectInfo(int JPID, String attribute, int updateType){
        JPMessaging.receiveSharedProjectInfo(JPID, attribute, updateType);
    }
    
    public void doRecognitionConfirmation(JointProject jp){
        String activity = JointProjectXMLProcessor.getRecognitionConfirmationAction(jp.getProjectName()); 
        if(activity.equals("no action")){
            if(jp.getInitiator().getCharacterType().equals("BasketballAgent")){
                BasketballAgent ba = (BasketballAgent)jp.getInitiator();
                ba.receiveRecognitionConfirmation(this, jp.getSharedID(), "no action");
            }    
        }
        else{ // human user

        }  
    }
    
    public void receiveRecognitionConfirmation(Character c, int JPID, String receivedAction){
       JPManager.updateJointProjectStatus(JPID, receivedAction, 3);   
    }
    
    public void receiveAbortNotification(Character c, JointProject jp){
       JPManager.processAbortedProject(c, jp);   
    }
    
    public void sendJPPerceptionSignal(BasketballAgent ba, JointProject j){
       JPMessaging.sendJPPerceptionSignal(ba, j);
    }
    
    public void receiveJPPerceptionSignal(Character c, JointProject j){
       if(!(JPManager.isEngagedInJPWithUser() && c instanceof VISIE.characters.Player)){
            JPMessaging.receiveJPPerceptionSignal(c, j);
       }                    
    }
    
    public String getCurrentAnimations(){
        return characterModel.getCurrentAnimation(1) + "," + characterModel.getCurrentAnimation(2);
    }
    
}
