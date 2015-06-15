/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.animation.Track;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Divesh
 */
public class BPNewModel extends AnimatedModel{
    
    Map<String, String> jointMap;
    AnimControl clothesControl;
    AnimChannel clothesUpper;
    AnimChannel clothesLower;
    Node modelRoot;
    ArrayList<String[]> animOverrides;
    Node ballPosNode;

    
    public BPNewModel(Spatial s, int id, float scale){
        
        modelRoot = (Node)s;

        for(int i = 0; i < modelRoot.getChildren().size(); i++){
            if(modelRoot.getChild(i).getClass().equals(Node.class)){
                model = (Node)modelRoot.getChild(i);
                break;   
            }
        }
        initialRotation = new Quaternion().fromAngles((float)Math.toRadians(0), 0, 0);
        model.setLocalScale(scale);
        model.setLocalTranslation(0f, -3.5f, 0f);
        typeIdentifier = "P";
        this.createJointMapper();

        animOverrides = new ArrayList<String[]>();
        String[] bob = {"walk","run"};
        animOverrides.add(bob);
               
    }
        
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName){
               
        //strange behavior of initiate pass
        if(animName.equals("initiatePass")){
            armChannel.setTime(armChannel.getAnimMaxTime() * 0.99f);
            armChannel.setSpeed(0);
            clothesUpper.setTime(armChannel.getAnimMaxTime() * 0.99f);
            clothesUpper.setSpeed(0);
        }    
    
    }
    
    @Override
    public CapsuleCollisionShape getCollisionShapeForModel(){
           BoundingBox b = (BoundingBox)model.getWorldBound();
           float radius = (b.getXExtent() + b.getZExtent())/3;
           return new CapsuleCollisionShape(radius, b.getYExtent() + 1, 1);
      }
    
    @Override
    public void initialize(){
           
        Node n = (Node)model;
        
        for(int i = 0; i < n.getChildren().size(); i++){
                Node o = (Node)n.getChild(i);
                for(int j = 0; j < o.getChildren().size(); j++){
                    Geometry g = (Geometry)o.getChild(j);
                    MatParam mp = g.getMaterial().getParam("Diffuse");
                    ColorRGBA col = (ColorRGBA)mp.getValue();
                    g.getMaterial().setColor("Ambient", col);
                }
        }
        
        for(int i = 0; i < n.getChildren().size(); i++){ 
            Spatial s = n.getChild(i);
            if(s.getControl(AnimControl.class) != null){
                if(s.getName().contains("Body")){
                   control = s.getControl(AnimControl.class);
                   control.addListener(this);
                   armChannel = control.createChannel();
                   legChannel = control.createChannel();
                   
                   armChannel.addFromRootBone("spine");
                   legChannel.addToRootBone("toe.R");
                   legChannel.addToRootBone("toe.L");
                }
                else if(s.getName().contains("clothes")){
                   clothesControl = s.getControl(AnimControl.class);
                   clothesControl.addListener(this);
                   clothesUpper = clothesControl.createChannel();
                   clothesLower = clothesControl.createChannel();
                   
                   clothesUpper.addFromRootBone("spine");
                   clothesLower.addToRootBone("toe.R");
                   clothesLower.addToRootBone("toe.L");            
                }
            }
        }
        
        armChannel.setAnim("standingPose");
        legChannel.setAnim("standingPose");
        clothesUpper.setAnim("standingPose");
        clothesLower.setAnim("standingPose");
        
        animationStates = new AnimationStates(control);
                    
        this.initializeModelBones();
        this.setBallPossessionNode();
        
        
    }
    
    public boolean isBallShot(){
        if(armChannel.getAnimationName() != null && armChannel.getAnimationName().contains("shoot")){
            float percentTime = this.getCurrentAnimationTimePercentage(1);
            float releasePointTime = 0.65f;
            if(percentTime > releasePointTime){
                return true;
            }
        }   
        return false;
    
    } 
    
    public boolean isBallPassed(){
    //    System.out.println("checking");
        if(armChannel.getAnimationName() != null && armChannel.getAnimationName().startsWith("pass")){
            float percentTime = this.getCurrentAnimationTimePercentage(1);
            float releasePointTime = 0.15f;
            if(percentTime > releasePointTime){
                return true;
            }
        }   
        return false;
    }    
    
    public void noArmGesture(){}
    
    public void standStill(){
        this.playLegAnimation("standingPose", 1f, LoopMode.Loop);
    }
    
    public void doDribbling(float speed){
        
        if(!armChannel.getAnimationName().contains("drib")){
            this.playArmAnimation("preDribble", 1, LoopMode.DontLoop);            
        }
        if(armChannel.getAnimationName().equals("preDribble") && armChannel.getTime() >= armChannel.getAnimMaxTime()){
            this.playArmAnimation("dribbleLoop", 0.5f, LoopMode.Loop);
        }        
    }
    
    public void playArmAnimation(String newAnimation, float speed, LoopMode l){
        
//        if(modelRoot.getName().contains("carl")){
//            System.out.println("arm "+ armChannel.getAnimationName() + " " + newAnimation + " " + modelRoot.getName());
//            System.out.println("leg " + legChannel.getAnimationName());
//        }
        
        if(armChannel.getAnimationName() == null){
            armChannel.setAnim("standingPose");
            clothesUpper.setAnim("standingPose");
        }
        
        if(!armChannel.getAnimationName().equals(newAnimation)){
            
            if(armChannel.getAnimationName().equals("standingPose")){   //override standing pose
                this.executeAnimation(armChannel, newAnimation, speed, l); 
            }
            else if(newAnimation.equals("standingPose")){  //if standing pose wait until anim finished/continue dribbling
                if(armChannel.getAnimationName().contains("dribble")){
                    this.executeAnimation(armChannel, newAnimation, speed, l);
                }
                else if(legChannel.getAnimationName().equals("standingPose")){
                   if(armChannel.getAnimationName().startsWith("walk")){
                       this.executeAnimation(armChannel, newAnimation, speed, l);
                   }
                }
                else if(super.hasAnimationFinished(1)){
                    this.executeAnimation(armChannel, newAnimation, speed, l);
                }
            }
            else if(newAnimation.equals("preDribble")){ //force predribble
                this.executeAnimation(armChannel, newAnimation, speed, l);
            }
            else if(newAnimation.startsWith("initiatePass") && !armChannel.getAnimationName().startsWith("pass")){
                this.executeAnimation(armChannel, newAnimation, speed, l); //force pass and initiate pass
            }
            else if(super.hasAnimationFinished(1)){ //wait until current animation has finished
                 this.executeAnimation(armChannel, newAnimation, speed, l);
            }
            else{
                if(legChannel.getAnimationName().startsWith("standingPose") &&
                   armChannel.getAnimationName().startsWith("walk")){
                    this.executeAnimation(armChannel, newAnimation, 1, l);
                }
                if(legChannel.getAnimationName().startsWith("turn") && newAnimation.startsWith("turn")){ //execute leg channel animations
                    this.executeAnimation(armChannel, newAnimation, 1, l);
                }
                else if(legChannel.getAnimationName().startsWith("walk") && newAnimation.startsWith("walk")){
                    this.executeAnimation(armChannel, newAnimation, 1, l);
                }
                else if(legChannel.getAnimationName().startsWith("run") && newAnimation.startsWith("run")){
                    this.executeAnimation(armChannel, newAnimation, 1, l);
                }
            }
        }
    }
    
    public void playLegAnimation(String newAnimation, float speed, LoopMode l){
        
//        if(modelRoot.getName().contains("alan")){
//            System.out.println(legChannel.getAnimationName() + " " + newAnimation + " " + modelRoot.getName() + " " + this.getCurrentAnimationTimePercentage(2));
//        }
//        
        if(legChannel.getAnimationName() == null){
            legChannel.setAnim("standingPose");
            clothesLower.setAnim("standingPose");
        }
        
        if(!legChannel.getAnimationName().equals(newAnimation)){
                                                            
            if(legChannel.getAnimationName().equals("standingPose")){
                this.executeAnimation(legChannel, newAnimation, speed, l); 
            }
            else if(newAnimation.equals("standingPose")){ 
                if(armChannel.getAnimationName().equals("standingPose")){
                    this.executeAnimation(legChannel, newAnimation, speed, l);
                }
                else if(this.hasFootTouchedGround()){
                    this.executeAnimation(legChannel, newAnimation, speed, l);
                }
            }
            else if(super.hasAnimationFinished(2) || this.hasFootTouchedGround()){
                    this.executeAnimation(legChannel, newAnimation, speed, l);
            }
//            else{
//                if(this.canLegAnimOverride(legChannel.getAnimationName(), newAnimation)){
//                   this.executeAnimation(legChannel, newAnimation, speed, l);
//                }
//            }
        }
        else if(legChannel.getAnimationName().startsWith("turn")){
            this.setTurningLoop();
        }
    }
    
    public void synchronizeChannels(int channel){
        if(channel == 1){
            armChannel.setAnim(legChannel.getAnimationName());
            armChannel.setTime(legChannel.getTime());
            armChannel.setLoopMode(legChannel.getLoopMode());
            armChannel.setSpeed(legChannel.getSpeed());
        }
        else{
            legChannel.setAnim(armChannel.getAnimationName());
            legChannel.setTime(armChannel.getTime());
            legChannel.setLoopMode(armChannel.getLoopMode());
            legChannel.setSpeed(armChannel.getSpeed());
        }
    }
    
    //can override without waiting for animation to stop
    private boolean canLegAnimOverride(String cur, String newAnim){
        
        if(cur.startsWith("run") && newAnim.startsWith("walk")){
            return true;
        }
//        if(cur.startsWith("step") && newAnim.startsWith("walk")){
//            return true;
//        }
//        if(cur.startsWith("turn") && newAnim.startsWith("turn")){
//            return true;
//        }
        
        if(cur.startsWith("walk") && newAnim.startsWith("turn") && parentCharacter.getSpeed() == 0){
            return true;
        }
        if(cur.startsWith("run") && newAnim.startsWith("turn") && parentCharacter.getSpeed() == 0){
            return true;
        }
        if(cur.startsWith("turn") && newAnim.startsWith("walk") && parentCharacter.getSpeed() > 0){
            return true;
        }
        if(cur.startsWith("turn") && newAnim.startsWith("run") && parentCharacter.getSpeed() > 0){
            return true;
        }        
        return false;
    }
    
    private boolean canArmAnimOverride(String cur, String newAnim){
        
        if(cur.startsWith("run") && newAnim.startsWith("walk")){
            return true;
        }
        
        if(cur.startsWith("dribbleLoop") && newAnim.startsWith("postDribble")){
            return true;
        }
        
//        if(cur.startsWith("walk") && newAnim.startsWith("preBlock")){
//            return true;
//        }
        
        if(cur.startsWith("walk") && newAnim.startsWith("turn")){
            return true;
        }
        if(cur.startsWith("walk") && newAnim.startsWith("step")){
            return true;
        }
        
        return false;
    }
    
   // @Override
    public boolean hasFootTouchedGround(){
        
        float[] cpoints = this.getAnimationCheckpoints(legChannel.getAnimationName());
        float threshold = legChannel.getSpeed() * 0.025f;
        
        if(cpoints[0] != -1){
                        
            float f = this.getCurrentAnimationTimePercentage(2);
                                    
            for(float cpoint:cpoints){
                if(Math.abs(f - cpoint) < threshold){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    //checkpoints where leg channel animations have the feet touching the ground
    //used for stopping animations and for triggering sounds
    private float[] getAnimationCheckpoints(String animation){
        
        
        if(animation.startsWith("walk")){
            return new float[]{0.12f, 0.25f, 0.42f, 0.59f, 0.77f, 0.97f};
        }
        else if(animation.startsWith("run")){
            return new float[]{0.5f, 1f};
        }
        else if(animation.startsWith("stepBack")){
            return new float[]{0.15f, 0.28f, 0.45f, 0.62f, 0.8f, 1f};
        }
        else if(animation.startsWith("step")){
            return new float[]{0.5f, 0.95f};
        }
        else if(animation.startsWith("turn")){
            return new float[]{0.1f, 0.5f, 0.8f};
        }
        
        return new float[]{-1f};
    }
   
    
    @Override
    public void executeAnimation(AnimChannel channel, String animationName, float speed, LoopMode l){
        
        channel.setAnim(animationName);
        channel.setSpeed(speed);
        channel.setLoopMode(l);       
        
        if(channel.equals(armChannel)){        
           clothesUpper.setAnim(animationName);
           clothesUpper.setSpeed(speed);
           clothesUpper.setLoopMode(l);
        }
        else if(channel.equals(legChannel)){
            clothesLower.setAnim(animationName);
            clothesLower.setSpeed(speed);
            clothesLower.setLoopMode(l);            
        }
     }
    
    public boolean isBallDribbled(){
       if(armChannel.getAnimationName() != null && armChannel.getAnimationName().equals("dribbleLoop")){
           return true;
        }   
        return false;     
    }
    
    //return state of ball in dribble action
    public String isDribbleBounce(){
        
        if(armChannel.getAnimationName().equals("dribbleLoop")){
        
            float f = getCurrentAnimationTimePercentage(1);
            
            if(f < 0.03){
                return "hand";
            }
            else if(f < 0.05){
                return "let go";
            }
            else if(f < 0.10){
                return "bounce";
            }
            else if(f < 0.28){
                return "hand";
            }
            else if(f < 0.30){
                return "let go";
            }
            else if(f < 0.35){
                return "bounce";
            }
            else if(f < 0.54){
                return "hand";
            }
            else if(f < 0.56){
                return "let go";
            }
            else if(f < 0.6){
                return "bounce";
            }
            else if(f < 0.79){
                return "hand";
            }
            else if(f < 0.81){
                return "let go";
            }
            else if(f < 0.9){
                return "bounce";
            }

            return "hand";
        
        }
        
        return "hand";
    }
        
    
    public void removeUserControl(){
        Skeleton body = control.getSkeleton();
        Skeleton clothes = clothesControl.getSkeleton();
        for(int i = 0; i < body.getBoneCount(); i++){
             Bone b = body.getBone(i);
             b.setUserControl(false);
             b = clothes.getBone(i);
             b.setUserControl(false);
        }
    }
    
    public void applyColours(){}

    public void turnBody(float angle){
        Quaternion q = new Quaternion();
        q.fromAngles(0f, (float)Math.toRadians(angle), 0f);
        model.setLocalRotation(q);
    }
    
    @Override
    public void animateBoneAngle(String boneName, Quaternion rotQuat){
        
        super.animateBoneAngle(control, boneName, rotQuat);
        super.animateBoneAngle(clothesControl, boneName, rotQuat);

  }
    
    public void doBlocking(float speed){
        
        if(!armChannel.getAnimationName().toLowerCase().contains("block")){
            this.playArmAnimation("preBlock", 1, LoopMode.DontLoop);            
        }
        if(armChannel.getAnimationName().equals("preBlock") && super.hasAnimationFinished(1)){
            this.playArmAnimation("blockLoop", speed, LoopMode.Loop);
        }
    }
    
    public int getNumberOfColours(){
        return 0;
    }

    public String jointNameConverter(String s){
        return jointMap.get(s);
    }
    
    public void playAnimation(int channelType, String animationType, float speed, LoopMode l){
        
        AnimChannel channel;
        
        if(channelType == 1){
            channel = armChannel;
            this.playArmAnimation(animationType, speed, l);
        }
        else{
            channel = legChannel;
            this.playLegAnimation(animationType, speed, l);
        }
        
    }
    
    //use for players
    @Override
    public void forceAnimation(int channelType, String animationType, float speed, LoopMode l){
        
        AnimChannel channel;
        
        if(channelType == 1){
            channel = armChannel;
            if(!armChannel.getAnimationName().equals(animationType)){
                this.executeAnimation(channel, animationType, speed, l);
            }
        }
        else{
            channel = legChannel;
            if(!legChannel.getAnimationName().equals(animationType)){
                this.executeAnimation(channel, animationType, speed, l);
            }
            else if(legChannel.getAnimationName().startsWith("turn")){ //do turning loop
                this.setTurningLoop();
            }
        }     
    }
    
    private void createJointMapper(){
        
        jointMap = new HashMap();
        jointMap.put("left hand", "hand.L");
        jointMap.put("right hand", "hand.R");
        jointMap.put("left shoulder", "upper_arm.L");
        jointMap.put("right shoulder", "upper_arm.R");
        jointMap.put("left elbow", "forearm.L");
        jointMap.put("right elbow", "forearm.R");
        jointMap.put("right finger", "f_index.01.R");
        jointMap.put("right ring", "f_ring.01.R");
        
        jointMap.put("leftHandJoint", "leftHandJoint");
        jointMap.put("rightHandJoint", "rightHandJoint");
        jointMap.put("leftShoulderJoint", "leftShoulderJoint");
        jointMap.put("rightShoulderJoint", "rightShoulderJoint");
        jointMap.put("leftElbowJoint", "leftElbowJoint");
        jointMap.put("rightElbowJoint", "rightElbowJoint");
        jointMap.put("head", "head");
    }
    
    //gets position of where ball should be when in possession
    public Vector3f getBallHandVector(){
        
  //      Vector3f handJoint = getWorldCoordinateOfJoint("right hand");
        Vector3f indexJoint = getWorldCoordinateOfJoint("right finger");
//        Vector3f ringJoint = getWorldCoordinateOfJoint("right ring");
//        
//        Vector3f vecA = indexJoint.subtract(handJoint);
//        Vector3f vecB = ringJoint.subtract(handJoint);
//        Vector3f cross = vecA.cross(vecB).normalize();
//               
//        return indexJoint.add(cross.mult(0.75f));
        return indexJoint;
    }
    
    public void setBallPossessionNode(){
        Node n = (Node)model;
        Node m = (Node)n.getChild(0);        
        
        SkeletonControl sc = m.getControl(SkeletonControl.class);

       Node handNode = sc.getAttachmentsNode("ballPosBone");
       handNode.setName("ghostPos");       
       ballPosNode = new Node();
       handNode.attachChild(ballPosNode);
    }
    
    public Node getBallPossessionNode(){                  
        return ballPosNode;
    }
    
    public void initializeModelBones(){
         
         Skeleton s = control.getSkeleton();
         
         ModelBone b4 = new ModelBone(s.getBone("upper_arm.L"), s.getBone("forearm.L"), "ELSL", this);
         ModelBone b5 = new ModelBone(s.getBone("forearm.L"), s.getBone("hand.L"), "WLEL", this);
         ModelBone b6 = new ModelBone(s.getBone("upper_arm.R"), s.getBone("forearm.R"), "ERSR", this);
         ModelBone b7 = new ModelBone(s.getBone("forearm.R"), s.getBone("hand.R"), "WRER", this);
         ModelBone b8 = new ModelBone(s.getBone("spine"), s.getBone("chest"), "SCSP", this);
   //       modelBones.add(b0);
//      //   modelBones.add(b1);
//  modelBones.add(b2);   
 // modelBones.add(b3);
         modelBones.add(b4); 
         modelBones.add(b5);
         modelBones.add(b6);
         modelBones.add(b7);
         modelBones.add(b8);
//         modelBones.add(b9);
         
         for(ModelBone mb:modelBones){
             mb.setBoneIndex(s);
         }
         
     }
    
    @Override
    public void rotateSpine(){
        Quaternion q1 = new Quaternion().fromAngles(getBoneRotation("spine"));
        Quaternion q2 = new Quaternion().fromAngles(0, 0, (float)Math.toRadians(-rotationAngle));
        Quaternion q3 = q2.mult(q1);
        animateBoneAngle("spine", q3);
    }
    
    private void setTurningLoop(){
        
        if(armChannel.getAnimationName().startsWith("turn") && this.getCurrentAnimationTimePercentage(2) > 0.9f){
            armChannel.setTime(0.1f * armChannel.getAnimMaxTime());
            clothesUpper.setTime(0.1f * clothesUpper.getAnimMaxTime());
        }
        
        if(legChannel.getAnimationName().startsWith("turn") && this.getCurrentAnimationTimePercentage(2) > 0.9f){
            legChannel.setTime(0.1f * legChannel.getAnimMaxTime());
            clothesLower.setTime(0.1f * clothesLower.getAnimMaxTime());
        }
        
    }
    
    public void turnHead(float angle){
                
        System.out.println("head turn " + angle);
        Quaternion q = new Quaternion();
        q.fromAngles(0, 0, (float)Math.toRadians(angle));
        Bone b = control.getSkeleton().getBone("neck");
        System.out.println(b.getLocalRotation());
        this.animateBoneAngle("neck", b.getLocalRotation().inverse());
    }
    
    public void resetHead(){
//     //   System.out.println(this.getCurrentAnimation(1));
//        Bone b = control.getSkeleton().getBone("neck");
//   //     System.out.println(b.getLocalRotation() + " " + b.getModelSpaceRotation() + " " + b.getWorldBindRotation() + " " + b.getWorldBindInverseRotation());
//        this.animateBoneAngle("neck", new Quaternion(0, 0, 0, 1));
    }
    
}
