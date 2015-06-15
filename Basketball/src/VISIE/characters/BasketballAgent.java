/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import Basketball.AgentBodyOperations;
import Basketball.AgentPerception;
import Basketball.AgentPlanning;
import Basketball.Ball;
import Basketball.BehaviorModule;
import Basketball.GameManager;
import Basketball.GameStateManagement;
import Basketball.JointProjectManager;
import Basketball.TrainingBehaviorModule;
import VISIE.gesturerecognition.SignalInterpreter;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import VISIE.network.NetworkMessagingProcessor;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import java.util.Timer;
import java.util.TimerTask;

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
    
    //state parameters for robot model (shouldn't affect behavior)
 //   protected int actionState; // 0 = stfand, 1 = run, 2 = shoot
    protected BehaviorModule behaviorModule;
    
    protected int behaviouralState = 0;
    private Character focusedCharacter;

    private ArrayList<Float> bounds;
    public AgentBodyOperations abo;
    public AgentPerception perception;
    public AgentPlanning planner;
    private int possessionTime;
    private String attackStrategy = "";
    private String defenseStrategy = "";
    
    private boolean ballBounce = false;
    
//    private SignalInterpreter signalInterpreter;
//    private Timer signalFixedTimer = new Timer(true);
//    
//    private String currentAttention = "";
//    private JointProjectManager currentJP;
    
    public BasketballAgent(int i, AnimatedModel am, RigidBodyControl p, float r, float height){
        characterID = i;
        mainNode = p;
        characterModel = am;
        characterModel.initialize();
        agentModel = (BPNewModel)characterModel;
        currentSpeed = 0.0f;
        radius = r;
        hasCollided = false;
        actionState = 0; // 0 = dribbling, 1 = pass, 2 = shoot
        behaviorModule = new BehaviorModule(this);
        abo = new AgentBodyOperations(this, characterModel);
        perception = new AgentPerception(this);
        planner = new AgentPlanning(this);  

    }
    
    public void setTrainingBehaviorModule(int i){
        behaviorModule = new TrainingBehaviorModule(this, i);
    }
        
//    public void setSignalInterpreter(String filePath){
//        signalInterpreter = new SignalInterpreter(this, filePath);
//        signalFixedTimer.schedule(new TimerTask(){
//            @Override
//            public void run(){
//                updateSignalInterpreter();
//           //     System.out.println("sssfd");
//            }
//        }, 1, 10);
//    }
    
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
       
    public void move(Vector3f direction, float speed){
        mainNode.setLinearVelocity(direction.mult(speed));
    }
    
    @Override
    public void adjustCollisionGroup(){
        ball.adjustCollisionGroup(true);
    }
    
    public float getCharacterRotation(){
        return this.getFacingDirection();
    }
    
    public RigidBodyControl getPhysicsNode(){
        return mainNode;
    }
        
    public Vector3f getHandPosition(int hand){
        if(hand == 0){
            return characterModel.getWorldCoordinateOfJoint("left finger");
        }
        else{
            return characterModel.getWorldCoordinateOfJoint("right finger");
        }
    }
    
    public void initialiseTarget(){
        planner.setTargetPosition(this.getPosition());
    }
    
    public void refreshNonPossessionTarget(){
        planner.nonPossession.findFreeSpace();
    }
    
    @Override
    public Vector3f getBallHandVector(){
        BPNewModel bm = (BPNewModel)characterModel;
        return bm.getBallPossessionNode().getWorldTranslation();
    }
           
    public void updateMovements(){ 
          
        int behaviorState = behaviorModule.getBehaviorState(); 
                
//        System.out.println(behaviorState + " " + this.getID());
        
            BPNewModel model = (BPNewModel)characterModel;
     //       System.out.println("sdd " + model.getBallPossessionNode().getWorldTranslation());
            //animations are according to agent state
                        
            if(behaviorState == 0 && this.isInPossession()){             //dribbling
                
                if(!planner.isTargetReached(2f)){ //target not reached 
                                                            
                    if(characterModel.getCurrentAnimation(1).contains("block")){
                        this.abo.blockingTransition();
                    }
                    else if(GameManager.getGameState() != 0){
                        abo.moveTowardsTarget(planner.getTargetPosition(), true, true);
                    }
                    else{
                        abo.moveTowardsTarget(planner.getTargetPosition(), false, true);
                    }
                }
                else{                           //stop
                    BasketballCharacter bc = this.getNearestTeammate();
                    this.setSpeed(0);
                    abo.turnBodyToTarget(bc.getPosition());
                }
                abo.doDribblingAnimations();                
            }
            
            else if(behaviorState == 1){  //no ball running
                                
                if(!planner.isTargetReached(0.5f)){ //target reached
                    abo.moveTowardsTarget(planner.getTargetPosition(), false, false);
     //               System.out.println("moving " + getCurrentGestureName());
                }
                else{                           //stop
               //     agentModel.standStill();
                    //transitions block to standingPose
                    if(characterModel.getCurrentAnimation(1).equals("blockLoop")){
                        this.abo.blockingTransition();
                    }
                    else{
                        agentModel.noArmGesture();
                    }
                          
                    this.setSpeed(0);
                    
                    BasketballCharacter possessor = SceneCharacterManager.getCharacterInPossession();
                    
                    if(possessor != null){
                        if(possessor.playerIsTeamMate(this)){
    //                        System.out.println("turn to team mate");
                           abo.turnBodyToTarget(possessor.getPosition()); 
                        }
                        else{
                           abo.turnBodyToTarget(planner.getClosestOpponent().getPosition());
                        }                        
                    }
                }
            }
            
            else if(behaviorState == 2){            //shooting
           //     System.out.println("sdsds");
                if(perception.isLookingAtTarget(Court.getHoopLocation())){
                    model.playArmAnimation("shoot", 2f, LoopMode.DontLoop);
        //            model.playLegAnimation("standingPose", 1, LoopMode.Loop);
                }
                else{
                    this.setSpeed(0);
                    abo.turnBodyToTarget(Court.getHoopLocation());
                }
            }
            
            else if(behaviorState == 3){        //defending/blocking
                if(!planner.isTargetReached(0.5f)){ //target reached
                    abo.moveTowardsTarget(planner.getTargetPosition(), false, false);                            
                }
                else{
                    this.setSpeed(0);
                }
                abo.doBlocking();
               
            }
            else if(behaviorState == 4){ // passing
                Character c = planner.getMyClosestTeamMate();
                this.doTurnAndPass(c);
            }
            else if(behaviorState == 5){ // have possession, turn towards player
                                
                this.setSpeed(0);
                
                Character c = planner.getMyClosestTeamMate();
                if(!perception.isWithinGaze(c.getPosition(), 10f)){ 
                    System.out.println("within gaze");
                    abo.turnBodyToTarget(c.getPosition());
                }
                else{
                    planner.possession.setStationaryTime();
                    System.out.println("turn and pass ");
                    model.playArmAnimation("initiatePass", 1, LoopMode.DontLoop);
                    this.doTurnAndPass(c);
                }
            }
            else if(behaviorState == 6){    // get attention of teammate in possession 
                Character c = planner.getMyClosestTeamMate();
                this.setSpeed(0);
                if(!perception.isWithinGaze(c.getPosition(), 10f)){         
                    abo.turnBodyToTarget(c.getPosition());
                }
                else{
                    model.playArmAnimation("callForPass", 0.75f, LoopMode.Loop);
                }  
            }
            else if(behaviorState == 7){
                model.standStill();
//                model.doCelebrations((float)(Math.random() * 0.5f) + 0.75f);            
            }
            else if(behaviorState == 8){
                Character c = planner.getMyClosestTeamMate();
                this.doDesperatePass(c);
            }
    }
    
    public void doBallManipulation(){
        
            BPNewModel model = (BPNewModel)characterModel;
            
            //pass thrown
            if(model.isBallPassed()){
                BasketballCharacter target = planner.getPassingTarget();
                
                if(target != null){
                    ball.passBall(this, target);
                }
                else{
                    ball.passBall(this.getFacingDirection());
                }
                
                this.removePossession();
            }
            
            //dribbling
            else if(model.isBallDribbled()){
                    
                    BPNewModel bm = (BPNewModel)characterModel;
                    String s = bm.isDribbleBounce();                         
                    
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
                        
                       
                        if(ball.getBallPosition().distance(model.getWorldCoordinateOfJoint("right finger")) < 0.9
                            && !ballBounce){ 
                            Vector3f fd = Conversions.degreesToNormalizedCoordinates(this.getFacingDirection());
                            ball.bounceBall(fd.mult(this.getSpeed())); 
                            ballBounce = true;
                        }
                    }
                }
            
            else if(model.isBallShot()){
                if(ball.isValidShoot()){
                    this.removePossession();
                    for(BasketballCharacter bc:this.getTeamMates()){
                        if(bc.getCharacterType().equals("CollabAgent")){
                            CollabAgent ca = (CollabAgent)bc;
                            ca.recordShootContexts();
                        }
                    }
                }
            }
            else{
                ball.updateBallInPossession();
            }   
    }
    
    public void doTurnAndPass(Character c){
        
        if(perception.isWithinGaze(c.getPosition(), 10f) && c.isLookingAtTarget(this.get2DPosition())){
            BPNewModel model = (BPNewModel)characterModel;
            model.playArmAnimation("pass", 0.75f, LoopMode.DontLoop);
        }
        else{
            abo.turnBodyToTarget(c.getPosition());
        }       
    }
    
    public void doDesperatePass(Character c){
        if(perception.isWithinGaze(c.getPosition(), 10f)){
            BasketballPlayerModel model = (BasketballPlayerModel)characterModel;
            model.playArmAnimation("pass", 0.75f, LoopMode.DontLoop);
      //     model.playLegAnimation("standingPose", 1, LoopMode.Loop);
        }
        else{
            abo.turnBodyToTarget(c.getPosition());
//            abo.doTurningAnimation();
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
        
    public float getCurrentMovementProgress(int channelID){
   //     System.out.println(agentModel.getCurrentAnimationTimePercentage(channelID));
        BPNewModel model = (BPNewModel)characterModel;
        return model.getCurrentAnimationTimePercentage(channelID);
    }
        
    public String getCurrentAnimations(){
        return characterModel.getCurrentAnimation(1) + "," + characterModel.getCurrentAnimation(2);
    }
    
    @Override
    public String getCurrentGestureName(){
        return characterModel.getCurrentAnimation(1);        
    }
    
    public String getLegAnimationName(){
        return characterModel.getCurrentAnimation(2);   
    }
    
    @Override    
    public int getActionState(){ 
         String[] anims = this.getCurrentAnimations().split(",");
         String anim = anims[0];
         return characterModel.getActionState(anim);
    }
    
    public int getWalkingState(){
         String[] anims = this.getCurrentAnimations().split(",");
         String anim = anims[1];
         return characterModel.getWalkingState(anim);
    }
    
    public BPNewModel getModel(){
        return (BPNewModel)characterModel;
    }
    
    @Override
    public void setPossession(){
        super.setPossession();
        planner.resetPossessionTime();
    }
    
    public void copyPose(String pose){
        ArrayList<String> rots = NetworkMessagingProcessor.parseJointData(pose);
        characterModel.setJointRotations(rots);
    }
    
    public void setShirtTexture(String texName){
        BasketballPlayerModel bm = (BasketballPlayerModel)characterModel;
        bm.setShirtTexture(texName);
    }
    
    public void setFaceAndSkinTexture(String faceTex, String skinTex){
        BasketballPlayerModel bm = (BasketballPlayerModel)characterModel;
        bm.setFaceTexture(faceTex, skinTex);
    }
    
    public void updateAgentAttention(){}
    
    @Override
    public float getShootPenalty(){
        return abo.getShootPenalty();
    }
    
    public void setAttackStrategy(String s){
        attackStrategy = s;
    }
    
    public void setDefenseStrategy(String s){
        defenseStrategy = s;
    }
    
    public String getAttackStrategy(){
        return attackStrategy;
    }
    
    public String getDefenseStrategy(){
        return defenseStrategy;
    }
    
    public void updateBallPossession(Ball b, Vector3f vec){
        if(GameManager.getGameState() == 0 && 
            this.getPosition().distance(Court.getRestartLocation()) > 7.5f){

            BasketballCharacter stealer = ball.checkForStealing(this);

            if(stealer != null){
                this.removePossession();
                this.setBehaviorState(1);
                stealer.setPossession();
            }
            else{
                b.setBallPosition(vec);
            }
        }
        else{
            b.setBallPosition(vec);
        }
    
    }
    
    public boolean checkStealing(Ball b){
       float stealingCapability = abo.getStealingCapability();
       return this.getHandPosition(0).distance(b.getBallPosition()) < stealingCapability 
               || this.getHandPosition(1).distance(b.getBallPosition()) < stealingCapability;

     }
    
    public void updateBallNoPossession(){
     }
    
    public void stopBall(){
        this.setBehaviorState(0);
    }
    
    @Override
    public void resetPossessionTime(){
        planner.resetPossessionTime();
    }

    public Vector3f doStalemateActivity(){
        BasketballCharacter bc = (BasketballCharacter)planner.getMyClosestTeamMate();
        return bc.getPosition();
    }
    
    @Override
    public void doUpdateActivity(){
        setBehavior();
        updateMovements();
        updateAgentAttention();
    }
    
    @Override
    public String getCharacterLogString(){
        return "A";
    }
    
    public String logCharacterData(){
         
        StringBuilder s = new StringBuilder();
         
        s.append(this.getID() + "A$");
        s.append(this.getPosition() + "$");
        s.append(this.getFacingDirection() + "$");
        s.append(this.getActionState() + "$");
        s.append(this.getWalkingState()  + "$");
        s.append(String.format("%.3f", this.getAnimationTime(1)) + "$");
        s.append(String.format("%.3f", this.getAnimationTime(2)) + "$");
        
        return s.toString();
     }
    
    @Override
    public void initializePositions(BasketballCharacter posChar){
        
        float initRot;

        if(posChar.getTeamID() == this.getTeamID()){ 
            initRot = Conversions.originToTargetAngle(this.getPosition(), Court.getHoopLocation());
        }
        else{
            initRot = Conversions.originToTargetAngle(this.getPosition(), SceneCharacterManager.getCharacterInPossession().getPosition());
        }
        
        this.abo.setFacingDirection(initRot);
    }
    
    @Override
    public void setBall(Ball b){
        super.setBall(b);
        this.planner.setBall(b);
    }
    
    @Override
    public boolean canSeeCharacter(Character c){
        return perception.canSeeCharacter(c);
    }
    
    public ArrayList<Geometry> getCollisionLines(){
        
        ArrayList<Geometry> geoms = new ArrayList<Geometry>();
        Vector3f vec = Conversions.degreesToNormalizedCoordinates(this.getFacingDirection()).mult(2.5f);
        Line line = new Line(this.getPosition(), this.planner.getTargetPosition());
        line.setLineWidth(2);
        Geometry geometry = new Geometry("Bullet", line);
        geoms.add(geometry);
        
        float increment = 0.5f;
        float incrementAngle = Conversions.adjustAngleTo360(this.getFacingDirection() + 90);
        int mult = 1;
        
        for(int i = 1; i < 5; i++){
            
            Vector3f incDiff = Conversions.degreesToNormalizedCoordinates(incrementAngle);
            if(i%2 == 0){
                incDiff.negateLocal();
            }
            else{
                mult++;
            }
            incDiff.multLocal(increment * mult);
            
            Vector3f newOrigin = this.getPosition().add(incDiff);
            Line l = new Line(newOrigin, newOrigin.add(vec));
            l.setLineWidth(2);
            Geometry g = new Geometry("Bullet", l);
            geoms.add(g);
        }
       
        return geoms;
    }
    
}
