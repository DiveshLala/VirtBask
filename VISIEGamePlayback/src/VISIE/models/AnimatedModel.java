/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.models;
import com.jme3.scene.Spatial;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.math.Vector3f;
import com.jme3.animation.Bone;
import com.jme3.math.Quaternion;
import com.jme3.asset.AssetManager;
import java.util.ArrayList;
import com.jme3.math.Vector2f;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Node;
/**
 *
 * @author huang
 */
//class that handles animation and modelling of robots
//abstract class which is a superclass of all robot models
// robot model subclasses may have different ways of expressing gestures

public abstract class AnimatedModel implements AnimEventListener{

    public String name;
    public Spatial model;
    public AnimChannel armChannel, legChannel;
    public AnimControl control;
    public ArrayList<ModelBone> modelBones = new ArrayList<ModelBone>();
    public ArrayList<Vector3f> colours = new ArrayList<Vector3f>();
    public ArrayList<String> textures = new ArrayList<String>();
    public float currentYRotationAngle;
    public abstract void removeUserControl();
    public abstract void applyColours();
   // public abstract void applyColours(ArrayList<Vector3f> colList);
    public abstract void turnBody(float headRotationAngle, float bodyRotationAngle);
    public abstract int getNumberOfColours();
    //public abstract Quaternion findParentInverseRotations(Bone startJoint);
    public abstract String jointNameConverter(String s);
    public abstract void playAnimation(int channelType, String animationType, float speed, LoopMode l);
     protected Vector2f rotVec = new Vector2f();
     protected float rotationAngle;
     private String boneRotationInfo = "";
     protected float modelRotationAngle = 0;
     protected Quaternion initialRotation;
     protected String typeIdentifier;
     protected AnimationStates animationStates;
     
     public CapsuleCollisionShape getCollisionShapeForModel(){
           BoundingBox b = (BoundingBox)model.getWorldBound();
           float radius = (b.getXExtent() + b.getZExtent())/2;
           return new CapsuleCollisionShape(radius, b.getYExtent(), 1);
      }

//    public void turnLeftGesture(){
//        if(!channel.getAnimationName().equals("leftTurn")){
//            channel.setAnim("leftTurn");
//            channel.setSpeed(0.00001f);
//        }
//    }
//    public void turnRightGesture(){
//    if(!channel.getAnimationName().equals("rightTurn")){
//            channel.setAnim("rightTurn");
//            channel.setSpeed(0.0001f);
//        }
//    }
//    public void waitingGesture(){
////     if(!channel.getAnimationName().equals("wait")){
////            channel.setAnim("wait");
////            channel.setLoopMode(LoopMode.Cycle);
////        }
//      channel.setAnim("walk");
//      channel.setSpeed(2f);
//      channel.setLoopMode(LoopMode.DontLoop);
//        
//    }
    
    public Spatial getModel(){
        return model;
    }
    
    public float getCurrentAnimationTimePercentage(int channelID){
        AnimChannel c;
    
        if(channelID == 1){
            c = armChannel;
            return c.getTime()/c.getAnimMaxTime();
        }
        else
            return 0;
    }
    
    public boolean animationTimeBetween(int channelID, float minPerc, float maxPerc){
        
        float currTime = this.getCurrentAnimationTimePercentage(channelID); 
        return(currTime > minPerc && currTime < maxPerc);
    }

    public void initialize(){
        control = model.getControl(AnimControl.class);
        control.addListener(this);
        armChannel = control.createChannel();
        legChannel = control.createChannel();
    }

  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName){
  }

  public void onAnimChange(AnimControl control, AnimChannel channel, String animName){
  }
  
  public void addTextures(String[] textures){
  }

    //gets current angle of body part and rotates it along axis for rendering
  //current angle is in degrees
   public void animateBoneAngle(String boneName, float currentAngle, Vector3f rotationAxis){
        Skeleton s = control.getSkeleton();
        //channel.setLoopMode(LoopMode.DontLoop);
        Bone b = s.getBone(boneName);
        b.setUserControl(true);
        Quaternion q = new Quaternion();
        q.fromAngleAxis((float)Math.toRadians(currentAngle), rotationAxis);
        b.setUserTransforms(Vector3f.ZERO, q, Vector3f.UNIT_XYZ);
  }
   
     public void animateBoneAngle(String boneName, Quaternion rotQuat){
        Skeleton s = control.getSkeleton();
        //channel.setLoopMode(LoopMode.DontLoop);
        Bone b = s.getBone(boneName);       
        b.setUserControl(true);      
        b.setUserTransforms(Vector3f.ZERO, rotQuat, Vector3f.UNIT_XYZ);
  }

   public void setWalkingAnimation(float walkingSpeed){
//       
//       if(legChannel.getLoopMode().equals(legChannel.getLoopMode().DontLoop))
//           legChannel.setLoopMode(LoopMode.Loop);
//            legChannel.setSpeed(walkingSpeed);
   }
   
   public void stopWalkingAnimation(){
//      if(channel.getLoopMode().equals(channel.getLoopMode().Loop))
//           channel.setLoopMode(LoopMode.DontLoop);
          
   }

    public float[] getBoneRotation(String boneName){
       float f[];
       Skeleton s = control.getSkeleton();
       Bone b = s.getBone(boneName);
       f = b.getLocalRotation().toAngles(null);
       return f;
   }
    
    public Quaternion getQuatBoneRotation(String boneName){
       Quaternion q;
       Skeleton s = control.getSkeleton();
       Bone b = s.getBone(boneName);
       q = b.getLocalRotation();
       return q;
    }
    
    public ArrayList<Vector3f> getModelColours(){
        return colours;
    }
    
    public ArrayList<String> getModelTextures(){
        return textures;
    }
    
    public void returnToInitialPose(){
        armChannel.setLoopMode(LoopMode.DontLoop);
        legChannel.setLoopMode(LoopMode.DontLoop);
        Skeleton s = control.getSkeleton();
        
        for(int i = 0; i<s.getBoneCount(); i++){
            animateBoneAngle(s.getBone(i).getName(), 0, Vector3f.UNIT_XYZ);           
        }
    }
    
    public void moveSingleJoint(String header, Vector3f vec){
      for(int j = 0; j < modelBones.size(); j++){
            if(modelBones.get(j).checkKinectHeader(header)){
                  float x, y, z;
                  try{
                      x = vec.x;
                      y = vec.y;
                      z = vec.z;
                      Vector3f newVec = new Vector3f(x,y,z);
                      modelBones.get(j).setCurrentModelBoneVector(newVec);
                      modelBones.get(j).moveModelBone();
                  }
                  catch(Exception e){
                      System.out.println(e);
                  }
                break;
            }
        }
    }
    
     public void moveJoints(ArrayList<String> vectorStrings){
      //  Vector2f rotVec = new Vector2f();
                   
         for(int i = 0; i < vectorStrings.size(); i++){
             String s = vectorStrings.get(i);
             String header;
             
             try{
                header = s.substring(0, s.indexOf(":"));
             }
             catch(java.lang.StringIndexOutOfBoundsException e){
                 header = "";
             }
                
             if(header.equals("ROTA")){
                 float x, z;
                 try{
                     rotationAngle = Float.parseFloat(s.substring(s.indexOf(":") + 1));
//                      x = Float.parseFloat(s.substring(s.indexOf("X") + 1, s.indexOf("Y") - 1));
//                      z = Float.parseFloat(s.substring(s.indexOf("Z") + 1, s.length()));
//                      rotVec = new Vector2f(x,z);
                 }
                 catch(Exception e){}
             }
             
             else{
              for(int j = 0; j < modelBones.size(); j++){
                    if(modelBones.get(j).checkKinectHeader(header)){
                          float x, y, z;
                          try{
                              x = Float.parseFloat(s.substring(s.indexOf("X") + 1, s.indexOf("Y") - 1));
                              y = Float.parseFloat(s.substring(s.indexOf("Y") + 1, s.indexOf("Z") - 1));
                              z = Float.parseFloat(s.substring(s.indexOf("Z") + 1, s.length()));
                              Vector3f vec = new Vector3f(-x,y,-z);
                              modelBones.get(j).setCurrentModelBoneVector(vec);
                          }
                          catch(Exception e){}
                        break;
                    }
                }
             }
        }
         
         for(int i = 0; i < modelBones.size(); i++){
             modelBones.get(i).moveModelBone();         
         }
     }
     
     public void setJointRotations(ArrayList<String> rotations){
                 
         for(int i  = 0; i < rotations.size(); i++){
             float x = 0, y = 0, z = 0, w = 1;
             String str = rotations.get(i);
             String boneName = str.substring(0, str.indexOf(":"));
             String floatStr = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
             String[] floats = floatStr.split(", ");
             for(int j = 0; j < floats.length; j++){
                 x = Float.parseFloat(floats[0]);
                 y = Float.parseFloat(floats[1]);
                 z = Float.parseFloat(floats[2]);
                 w = Float.parseFloat(floats[3]);
             }
             Quaternion quat = new Quaternion(x, y, z, w);
             Quaternion q2 = quat.inverse();
             Quaternion q3 = q2.mult(new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0));
             
             animateBoneAngle(boneName, quat);
         }
     
     }
     
     public String getBoneRotationInfo(){
        
       boneRotationInfo = "";
        
       StringBuilder sb = new StringBuilder();
       
       for(int i = 0; i < modelBones.size(); i++){
           sb.append(modelBones.get(i).getParentJointName() + ":" + modelBones.get(i).getCurrentQuat() + ";");       
       }
       
       boneRotationInfo = sb.toString();
       return boneRotationInfo;
    }
     
     public float getModelRotationAngle(){
         return modelRotationAngle;
     }
     
      public Vector3f calculatePointTarget(){
         return Vector3f.ZERO;
     }
      
    public Vector3f getWorldCoordinateOfJoint(String s){
        Vector3f vec = new Vector3f();
        String jointName = jointNameConverter(s);
        
        if(jointName != null){ 
            model.localToWorld(control.getSkeleton().getBone(jointName).getModelSpacePosition(), vec);
        }
        return vec;
    }
    
      
      public String getTypeIdentifier(){
          return typeIdentifier;
      }
      
      public boolean hasAnimationFinished(int i){
          if(i == 1){
           return (armChannel.getTime() > (armChannel.getAnimMaxTime() - 0.01));
          }
          else if(i == 2){
           return (legChannel.getTime() > (legChannel.getAnimMaxTime() - 0.01)); 
          }
          else{
              return true;
          }
      }
      
      public void stopAnimationChannel(int i){
          AnimChannel c;
          if(i == 1){
              c = armChannel;
          }
          else if(i == 2){
              c = legChannel;
          }
          else{
              c = null;
          }

          if(c != null){  
              c.setTime(c.getAnimMaxTime());
              c.setLoopMode(LoopMode.DontLoop);
          }
      }
      
      public String getCurrentAnimation(int channel){
          if(channel == 1){
              return armChannel.getAnimationName();
          }
          else if(channel == 2){
              return legChannel.getAnimationName();
          }
          return "";
      }
      
      public Vector3f getPointingVector(int hand){
          return Vector3f.ZERO;
      }
      
      public Quaternion findParentInverseRotations(Bone startJoint){
         if(startJoint.getParent() == null){
             return initialRotation.inverse();
         }

         else{
            if(startJoint.getParent().getLocalRotation().inverse() != null){
                //returns quaternion concatenation in order from child to parent recursively
                //i.e. child local rotation * parent local rotation * grandparent local rotation....
                return startJoint.getParent().getLocalRotation().inverse().mult(findParentInverseRotations(startJoint.getParent()));
            }
            else{
                return new Quaternion();
            }
        }
     }
      
      public Vector3f convertWorldToModelVector(Vector3f initialVec, Vector3f facingDirection){
          return null;
      }
      
      public void playActionState(int actionState){
          
      }
      
      public boolean isCollidedWith(Node n){
          CollisionResults results = new CollisionResults();
          n.collideWith(this.getModel().getWorldBound(), results); 
          return results.size() > 0;
      }
      
        public String getAnimationName(int state){
          return animationStates.getAnimationName(state);
      }
        
        public String getArmAnimationName(int state){
        return animationStates.getAnimationName(state);
    }
    
    public String getLegAnimationName(int state){
        return animationStates.getWalkingName(state);
    }
    
    public void setFrame(int channel, String animationName, float time){
       if(channel == 1){
           armChannel.setAnim(animationName);
           armChannel.setTime(time);
       }
       else if(channel == 2){
           legChannel.setAnim(animationName);
           legChannel.setTime(time);
       }
    }
}
