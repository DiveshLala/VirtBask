/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.SceneCreator;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.animation.Skeleton;
import com.jme3.animation.Bone;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import java.util.ArrayList;
import com.jme3.animation.LoopMode;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.MatParam;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Divesh
 */
public class BasketballPlayerModel extends AnimatedModel{
    
    Map<String, String> jointMap;

    public BasketballPlayerModel(Spatial s, int id){
            model = s;
            initialRotation = new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0);
            model.rotate((float)Math.toRadians(90), 0, 0);
            model.setLocalScale(0.028f);
            model.setLocalTranslation(0f, -3.5f, 0f);
            model.setName("Basketball player " + id + " mesh");
            typeIdentifier = "D";
            applyColours();
            this.createJointMapper();
            animationStates = new AnimationStates("config/actionstates.txt", "config/walkingstates.txt");
    }
     
    public CapsuleCollisionShape getCollisionShapeForModel(){
           BoundingBox b = (BoundingBox)model.getWorldBound();
           float radius = (b.getXExtent() + b.getZExtent())/3;
           return new CapsuleCollisionShape(radius, b.getYExtent() + 2, 1);
      }
    
    //takes array of textures in order: shirt, skin, shorts, shoes, face
    public void addTextures(String[] tex){
          
        for(int i = 0; i < tex.length; i++){
            this.addGroupTexture(i, tex[i]);
            textures.add(tex[i] + "!");
        }       
    }
    
    private void addGroupTexture(int groupID, String textureName){
        
        Node n = (Node)model;
        
        TextureKey tk = new TextureKey("Models/BasketballPlayer/" + textureName, false);
        Texture imageTexture = SceneCreator.getAssetManager().loadTexture(tk);
        Geometry g = (Geometry)n.getChild(groupID);
        g.getMaterial().setTexture("DiffuseMap", imageTexture);
       
        //set ambience by making it same as diffuse texture
        MatParam mp = g.getMaterial().getParam("Diffuse");
        ColorRGBA col = (ColorRGBA)mp.getValue();
        g.getMaterial().setColor("Ambient", col);
    }
    
     public void applyColours(){
//        Node n = (Node)model;
//            for(int i = 0; i < n.getChildren().size(); i++){
//                float red = 0, green = 0, blue = 0;
//                Geometry g = (Geometry)n.getChild(i);
//                Material mat = g.getMaterial();
//                if(i == 0){//trim and eye colour
//                    red = (float)Math.random() * 0.7f;
//                    green = (float)Math.random() * 0.7f;
//                    blue = (float)Math.random() * 0.7f;
//                }
//                else if (i == 1){//main body colour
//                    red = 0.5f + ((float)Math.random() * 0.2f);
//                    green = 0.5f + ((float)Math.random() * 0.2f);
//                    blue = 0.5f + ((float)Math.random() * 0.2f);
//                }
//                    Vector3f  v = new Vector3f(red, green, blue);
//                    colours.add(v);
//                    mat.setColor("Diffuse", new ColorRGBA(1f, 1f, 1f, 1f));
//                    mat.setColor("Ambient", new ColorRGBA(red, green, blue, 1f));
//                    mat.setBoolean("UseAlpha", true);
//            }
    }

   public void removeUserControl(){
        Skeleton s = control.getSkeleton();
        for(int i = 0; i < s.getBoneCount(); i++){
             Bone b = s.getBone(i);
             b.setUserControl(false);
        }
   }

     public void turnBody(float headRotationAngle, float bodyRotationAngle){
         animateBoneAngle("rootJoint", bodyRotationAngle, Vector3f.UNIT_Y);
         animateBoneAngle("neckJoint", headRotationAngle, Vector3f.UNIT_Z);
     }
     
     public ArrayList<Vector3f> getColours(){
         return colours;
     }
     
     public int getNumberOfColours(){
         return 2;
     }
     
     public void initialize(){
        super.initialize();
        armChannel.setAnim("standingPose");
        this.initializeModelBones();
        armChannel.addFromRootBone("centreShoulderJoint");
                legChannel.addFromRootBone("leftHipJoint");
        legChannel.addFromRootBone("rightHipJoint");
  //      legChannel.setAnim("standingPose");
     }

   
//    public Vector3f getWorldCoordinateOfJoint(String s){
//        Vector3f vec = new Vector3f();
//        String jointName = this.jointNameConverter(s);
//        
//        if(jointName != null){  
//            Vector3f v1 = new Vector3f();
//            Vector3f v2 = new Vector3f();
//            Vector3f v3 = new Vector3f();
//            Vector3f v4 = new Vector3f();
//            
//            
//         //   model.localToWorld(control.getSkeleton().getBone(jointName).getWorldBindInversePosition().negate(), vec);
//            model.localToWorld(control.getSkeleton().getBone(jointName).getModelSpacePosition(), vec);
////            model.localToWorld(control.getSkeleton().getBone("rootJoint").getWorldBindInversePosition().negate(), v1);
////            model.localToWorld(control.getSkeleton().getBone("headJoint").getWorldBindInversePosition().negate(), v2);
////            model.localToWorld(control.getSkeleton().getBone("rightHandJoint").getWorldBindInversePosition().negate(), v3);
////            model.localToWorld(control.getSkeleton().getBone("leftKneeJoint").getWorldBindInversePosition().negate(), v4);
////            
////            
////            System.out.println("root " + v1);
////            System.out.println("head " + v2);
////            System.out.println("rh " + v3);
////            System.out.println("lk " + v4);
//            
//        }
//        
//        return vec;
//    }
    
    
    //takes a string of a body part and returns the corresponding model joint
    public String jointNameConverter(String s){       
        return jointMap.get(s);
    }
    
    private void createJointMapper(){
        
        jointMap = new HashMap();
        jointMap.put("left hand", "leftHandJoint");
        jointMap.put("right hand", "rightHandJoint");
        jointMap.put("left shoulder", "leftShoulderJoint");
        jointMap.put("right shoulder", "rightShoulderJoint");
        
        jointMap.put("leftHandJoint", "leftHandJoint");
        jointMap.put("rightHandJoint", "rightHandJoint");
        jointMap.put("leftShoulderJoint", "leftShoulderJoint");
        jointMap.put("rightShoulderJoint", "rightShoulderJoint");
        jointMap.put("head", "headJoint");
    }
    
    public void playAnimation(int channelType, String animationType, float speed, LoopMode l){
        
        if(channelType == 1){ //arm channel
            this.playArmAnimation(animationType, speed, l);
        }
        else if(channelType == 2){
            this.playLegAnimation(animationType, speed, l); 
        }
    }
    
    public void playArmAnimation(String animationName, float speed, LoopMode l){
       //if no animation in channel       
        
        if(armChannel.getAnimationName() == null){
            armChannel.setAnim("standingPose");
        }
        
        if(!armChannel.getAnimationName().equals(animationName)){
                armChannel.setAnim(animationName);
                armChannel.setSpeed(speed);
                armChannel.setLoopMode(l);
        }
    }
    
    public void playLegAnimation(String animationName, float speed, LoopMode l){
        if(legChannel.getAnimationName() == null){
            legChannel.setAnim("standingPose");
            legChannel.setSpeed(speed);
        }
        if(!legChannel.getAnimationName().equals(animationName)){
                legChannel.setAnim(animationName);
                legChannel.setSpeed(speed);
                legChannel.setLoopMode(l);         
        }
    }
    
    //is a  ratio
    public float getCurrentAnimationTimePercentage(int channelID){
        AnimChannel c;
    
        if(channelID == 1){
            c = armChannel;
            return c.getTime()/c.getAnimMaxTime();
        }
        else
            return 0;
        
    }
    
    public boolean isBallPassed(){
        if(armChannel.getAnimationName() != null && armChannel.getAnimationName().equals("passAction")){
            float percentTime = this.getCurrentAnimationTimePercentage(1);
            float releasePointTime = 0.2f;
            if(percentTime > releasePointTime){
                return true;
            }
        }   
        return false;
    }    
    
    public boolean isBallDribbled(){
       if(armChannel.getAnimationName() != null && armChannel.getAnimationName().equals("dribbleLoop")){
           return true;
        }   
        return false;     
    }
    
    public void doDribbling(float speed){
        
        if(!armChannel.getAnimationName().contains("drib")){
            this.playArmAnimation("preDribble", 1, LoopMode.DontLoop);            
        }
        if(armChannel.getAnimationName().equals("preDribble") && armChannel.getTime() >= armChannel.getAnimMaxTime()){
            this.playArmAnimation("dribbleLoop", 0.5f, LoopMode.Loop);
        }
        if(armChannel.getAnimationName().equals("dribbleLoop")){
            if(this.getCurrentAnimationTimePercentage(1) > 0.5f){
                armChannel.setSpeed(speed);
            }           
            else{
                armChannel.setSpeed(1f);
            }
        }
    }
    
    public boolean isBallShot(){
        if(armChannel.getAnimationName() != null && armChannel.getAnimationName().equals("shootAction")){
            float percentTime = this.getCurrentAnimationTimePercentage(1);
            float releasePointTime = 0.5f;
            if(percentTime > releasePointTime){
                return true;
            }
        }   
        return false;
    
    } 
    
    public void standStill(){
        this.playArmAnimation("standingPose", 1, LoopMode.Loop);    
        this.playLegAnimation("standingPose", 1, LoopMode.Loop);   
    }
    
    public Vector3f getPointingVector(int hand){
        if(hand == 0){
           return this.getWorldCoordinateOfJoint("leftHandJoint").subtract(this.getWorldCoordinateOfJoint("leftShoulderJoint"));
        }
        else{
           return this.getWorldCoordinateOfJoint("rightHandJoint").subtract(this.getWorldCoordinateOfJoint("rightShoulderJoint"));
        }
    }
    
     public void initializeModelBones(){
         
         Skeleton s = control.getSkeleton();
         
    //     ModelBone b0 = new ModelBone(s.getBone("08torso"), s.getBone("01neck"), "SCSp", this);
//         ModelBone b1 = new ModelBone(s.getBone("01neck"), s.getBone("00head"), "HdSC", this);
   //     ModelBone b2 = new ModelBone(s.getBone("01neck"), s.getBone("02Lshoulder"), "SLSC", this);
   //      ModelBone b3 = new ModelBone(s.getBone("01neck"), s.getBone("05Rshoulder"), "SRSC", this);
         ModelBone b4 = new ModelBone(s.getBone("leftShoulderJoint"), s.getBone("leftElbowJoint"), "ELSL", this);
         ModelBone b5 = new ModelBone(s.getBone("leftElbowJoint"), s.getBone("leftWristJoint"), "WLEL", this);
         ModelBone b6 = new ModelBone(s.getBone("rightShoulderJoint"), s.getBone("rightElbowJoint"), "ERSR", this);
         ModelBone b7 = new ModelBone(s.getBone("rightElbowJoint"), s.getBone("rightWristJoint"), "WRER", this);
//      //   ModelBone b8 = new ModelBone(s.getBone("09Lhip"), s.getBone("10Lknee"), "KLHL", this);
//      //   ModelBone b9 = new ModelBone(s.getBone("12Rhip"), s.getBone("13Rknee"), "KRHR", this);
   //       modelBones.add(b0);
//      //   modelBones.add(b1);
//  modelBones.add(b2);   
 // modelBones.add(b3);
         modelBones.add(b4); 
         modelBones.add(b5);
         modelBones.add(b6);
         modelBones.add(b7);
//         modelBones.add(b8);
//         modelBones.add(b9);
     }
     
     @Override
      public Vector3f convertWorldToModelVector(Vector3f initialVec, Vector3f facingDirection){
          
          float initVecAngle = Conversions.originToTargetAngle(initialVec, initialVec.mult(2));
          float facingDirectionAngle = Conversions.originToTargetAngle(facingDirection, facingDirection.mult(2));
          
          Vector3f modelSpaceAngle = new Vector3f();
          
          float a = Conversions.findOppositeAngle(facingDirectionAngle);
          float b = initVecAngle - a;
          float c = Conversions.adjustAngleTo360(b);
          
          modelSpaceAngle = Conversions.degreesToNormalizedCoordinates(c);
          modelSpaceAngle.setY(initialVec.getY());
          
          return modelSpaceAngle;
      }
     
      public void playActionState(int actionState){
 
            if(actionState == 1){
                this.playAnimation(2, "walk", 1, LoopMode.Loop);
            }
            else if(actionState == 0){
               this.playAnimation(2, "standingPose", 1, LoopMode.Loop);
            }

      }
      
      public void setShooting(boolean b){
          if(b){
              armChannel.setAnim("shootAction");
              armChannel.setSpeed(0);
          }
          else{
              armChannel.setAnim("standingPose");
          }
      
      }
}
