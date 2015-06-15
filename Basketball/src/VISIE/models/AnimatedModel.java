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
import com.jme3.material.MatParam;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
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
    public abstract void turnBody(float angle);
    public abstract void turnHead(float angle);
    public abstract void resetHead();
    public abstract int getNumberOfColours();
    //public abstract Quaternion findParentInverseRotations(Bone startJoint);
    public abstract String jointNameConverter(String s);
    public abstract void noArmGesture();
    public abstract void standStill();
    public abstract void playAnimation(int channelType, String animationType, float speed, LoopMode l);
     protected Vector2f rotVec = new Vector2f();
     protected float rotationAngle;
     private String boneRotationInfo = "";
     protected Quaternion initialRotation;
     protected String typeIdentifier;
     protected AnimationStates animationStates;
     protected VISIE.characters.Character parentCharacter;
     protected String modelFilePath;
     
     public CapsuleCollisionShape getCollisionShapeForModel(){
           BoundingBox b = (BoundingBox)model.getWorldBound();
           float radius = (b.getXExtent() + b.getZExtent())/2;
           return new CapsuleCollisionShape(radius, b.getYExtent(), 1);
      }
     
     public void executeAnimation(AnimChannel channel, String animationName, float speed, LoopMode l){
           channel.setAnim(animationName);
           channel.setSpeed(speed);
           channel.setLoopMode(l);  
     }
     
    public void setParentCharacter(VISIE.characters.Character c){
        parentCharacter = c;
    }
    
    public void forceAnimation(int channelType, String animationType, float speed, LoopMode l){}
        
    public Spatial getModel(){
        return model;
    }
    
    public float getCurrentAnimationTimePercentage(int channelID){
        AnimChannel c;
    
        if(channelID == 1){
            c = armChannel;
            return c.getTime()/c.getAnimMaxTime();
        }
        else if(channelID == 2){
            c = legChannel;
            return c.getTime()/c.getAnimMaxTime();
        }
        else
            return 0;
    }
    
    public String getArmAnimationName(int state){
        return animationStates.getAnimationName(state);
    }
    
    public String getLegAnimationName(int state){
        return animationStates.getWalkingName(state);
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
        Bone b = s.getBone(boneName);       
        b.setUserControl(true);      
        b.setUserTransforms(Vector3f.ZERO, rotQuat, Vector3f.UNIT_XYZ);
  }
     
    public void animateBoneAngle(AnimControl cont, String boneName, Quaternion rotQuat){
        Skeleton s = cont.getSkeleton();
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
                 try{
                      rotationAngle = Float.parseFloat(s.substring(s.indexOf(":") + 1));
                      if(Float.isNaN(rotationAngle)){
                          rotationAngle = 0;
                      } 
                 }
                 catch(Exception e){
                     System.out.println("rotation error " + e);
                 }
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
         
        rotateSpine();    
     }
     
     protected void rotateSpine(){
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
        //     System.out.println(rotations.get(i));
       //      System.out.println(quat);
             animateBoneAngle(boneName, quat);
             
             
             for(int j = 0; j < modelBones.size(); j++){
                 if(boneName.equals(modelBones.get(j).getParentJointName())){
                     modelBones.get(j).setCurrentQuat(quat);
                     break;
                 }
             }
         }
     }
     
     public String getModelBoneRotationInfo(){
         
       boneRotationInfo = "";
        
       StringBuilder sb = new StringBuilder();
       
       for(int i = 0; i < modelBones.size(); i++){
           sb.append(modelBones.get(i).getParentJointName() + ":" + modelBones.get(i).getCurrentQuat() + ";");       
       }
       
       boneRotationInfo = sb.toString();
       
       return boneRotationInfo;
    }
     
//     public Quaternion getModelRotation(){
//         return model.getLocalRotation();
//     }
     
    //used for logging... uses index instead of names 
    public String getModelBonesForLogging(){
         
       boneRotationInfo = "";
        
       StringBuilder sb = new StringBuilder();
       
       for(int i = 0; i < modelBones.size(); i++){
           sb.append(modelBones.get(i).getBoneIndex() + ":" + modelBones.get(i).getCurrentQuat() + ";");       
       }
       
       boneRotationInfo = sb.toString();
       
       return boneRotationInfo;
    }
     
     public String getNUPBoneRotationInfo(){
         
        StringBuilder sb = new StringBuilder(); 
        Skeleton s = control.getSkeleton();
         
         for(int i = 0; i < modelBones.size(); i++){
             Bone b = s.getBone(modelBones.get(i).getParentJointName());
             sb.append(b.getName() + ":" + (b.getLocalRotation()) + ";");
         }
         return sb.toString();
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
    
    public Vector3f getBoneVectorOfModel(String parent, String child){
        
        return this.getWorldCoordinateOfJoint(child).subtract(this.getWorldCoordinateOfJoint(parent)).normalize();
        
    }
    
    public Vector3f getLocalCoordinateOfJoint(String s){
        
        Vector3f vec = new Vector3f();
        String jointName = jointNameConverter(s);
        
        if(jointName != null){ 
            vec = control.getSkeleton().getBone(jointName).getModelSpacePosition();
        }
        return vec;
     
    }
    
      
      public String getTypeIdentifier(){
          return typeIdentifier;
      }
      
      public boolean hasAnimationFinished(int i){
          if(i == 1){
              return (armChannel.getTime() / armChannel.getAnimMaxTime()) > 0.925f;
          }
          else if(i == 2){
              return (legChannel.getTime() / legChannel.getAnimMaxTime()) > 0.925f;
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
      
      public float getAnimationSpeed(int channel){
            if(channel == 1){
              return armChannel.getSpeed();
          }
          else if(channel == 2){
              return legChannel.getSpeed();
          }
          return 0;
      }
      
      public boolean isLooped(int channel){
          if(channel == 1){
              return (armChannel.getLoopMode().equals(LoopMode.Loop));
          }
          else if(channel == 2){
              return (legChannel.getLoopMode().equals(LoopMode.Loop));
          }
          return false;
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
      
      public void setTransparency(float value){
          
        Node n = (Node)model;
        for(int i = 0; i < n.getChildren().size(); i++){
            
            Node m = (Node)n.getChild(i);
            
            for(int j = 0; j < m.getChildren().size(); j++){
                if(!m.getChild(j).getName().contains("ghost")){
                    Geometry g = (Geometry)m.getChild(j);
                    g.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    MatParam diff = g.getMaterial().getParam("Diffuse");
                    ColorRGBA col = (ColorRGBA)diff.getValue();
                    col.a = value;
                    g.getMaterial().setColor("Diffuse", col);
                    g.getMaterial().setColor("Ambient", col);
                    g.setQueueBucket(Bucket.Transparent);
                }
            }
            
        }
    }
      
      public float getRotationAngle(){
          return rotationAngle;
      }
      
      public int getActionState(String animationName){
          return animationStates.getAnimationState(animationName);      
      }
      
      public String getModelFilePath(){
          return modelFilePath;
      }
      
      public void setModelFilePath(String s){
          modelFilePath = s;
      }
      
      public int getWalkingState(String animationName){
          return animationStates.getWalkingState(animationName);
      }
      
      public String getAnimationName(int state){
          return animationStates.getAnimationName(state);
        }
      
            
      public float getAnimationTime(int channel){
          if(channel == 1){
              return armChannel.getTime();
          }
          else if(channel == 2){
              return legChannel.getTime();
          }
          else{
              return 0;
          }
      }
}
