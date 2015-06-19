/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package VISIE.models;
import com.jme3.scene.Spatial;
import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.animation.Skeleton;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import java.util.ArrayList;
import com.jme3.math.Vector2f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;


/**
 *
 * @author DiveshLala
 */
public class KinectUserModel extends AnimatedModel{
        
    private int torsoBoneIndex = 0;
    
    
    public KinectUserModel(Spatial s, int id){
            model = s;
            Quaternion q = new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0);
            model.rotate(q);
            initialRotation = q;
            model.setLocalScale(0.05f);
            model.setLocalTranslation(0.3f, -1.3f, 0f);
            model.setName("Kinect model " + id + " mesh");
            typeIdentifier = "K";
            
            //for non-user players
          //  if(isTransparent){
             //   applyColours();
          //  }
    }
    
    public void removeUserControl(){
        armChannel.setLoopMode(LoopMode.DontLoop);
    }
    
   public void setWalkingAnimation(float walkingSpeed){
   }
   
   public void stopWalkingAnimation(){
   }
    
    public void applyColours(){
    }
        
    public void applyColours(ArrayList<Vector3f> colList){}
    
    public void turnBody(float headRotationAngle, float bodyRotationAngle){         
         float angleDifference = bodyRotationAngle - currentYRotationAngle;
         
         currentYRotationAngle = bodyRotationAngle;
         
         model.rotate(new Quaternion().fromAngleAxis((float)Math.toRadians(angleDifference), initialRotation.inverse().mult(Vector3f.UNIT_Y)));
         
         modelRotationAngle = currentYRotationAngle;
     //    currentYRotationAngle = bodyRotationAngle;
       //  animateBoneAngle("torsoJoint", headRotationAngle, Vector3f.UNIT_Z);

    }
    
    public int getNumberOfColours(){
        return 0;
    }
    public void doServingAnimation(){}
    
    public void initialize(){
        System.out.println(model.getControl(AnimControl.class));
        control = model.getControl(AnimControl.class);
        control.addListener(this);
        armChannel = control.createChannel();
    //    channel.setAnim("walk");
        initializeModelBones();

        
  //      for(int i = 0; i < modelBones.size(); i++){
//            modelBones.get(0).setCurrentModelBoneVector(new Vector3f(0f, 0f, 0f).normalize());
////   //         modelBones.get(1).setCurrentModelBoneVector(new Vector3f(0f, 1f, 0f));
////  //      }
////        
////        for(int i = 0; i < modelBones.size(); i++){
////     //       modelBones.get(i).setCurrentModelBoneVector(new Vector3f(1f, 0f, 0f));
//            modelBones.get(0).moveModelBone();
//        }
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
         
         torsoBoneIndex = 0;
     }
     
//      public void moveJoints(ArrayList<String> vectorStrings){
////         for(int i = 0; i < vectorStrings.size(); i++){
////             String s = vectorStrings.get(i);
////             String header = s.substring(0, s.indexOf(":"));
////             
////             if(header.equals("ROTA")){
////                 float x, z;
////                 try{
////                      x = Float.parseFloat(s.substring(s.indexOf("X") + 1, s.indexOf("Y") - 1));
////                      z = Float.parseFloat(s.substring(s.indexOf("Z") + 1, s.length()));
////                      Vector2f vec = new Vector2f(x,z);
////              //        this.calculateRotation(vec);
////                 }
////                 catch(Exception e){}
////             }
////             
////             else{
////              for(int j = 0; j < modelBones.size(); j++){
////                    if(modelBones.get(j).checkKinectHeader(header)){
////                          float x, y, z;
////                          try{
////                              x = Float.parseFloat(s.substring(s.indexOf("X") + 1, s.indexOf("Y") - 1));
////                              y = Float.parseFloat(s.substring(s.indexOf("Y") + 1, s.indexOf("Z") - 1));
////                              z = Float.parseFloat(s.substring(s.indexOf("Z") + 1, s.length()));
////                              Vector3f vec = new Vector3f(-x,y,-z);
////                              modelBones.get(j).setCurrentModelBoneVector(vec);
////                          }
////                          catch(Exception e){}
////                        break;
////                    }
////                }
////             }
////        }
///         
////         for(int i = 0; i < modelBones.size(); i++){
////             modelBones.get(i).moveModelBone();         
////         }
//         
//     }
      
//       private void calculateRotation(Vector2f rotVec){
//         
//         Vector2f v1 = new Vector2f(1, 0);
//         Vector2f v2 = rotVec.normalize();
//         double angle = Math.atan2(v2.y, v2.x) - Math.atan2(v1.y, v1.x);
//         modelRotationAngle = -(float)Math.toDegrees(angle);
//         this.turnBody(modelRotationAngle, modelRotationAngle);
//     
//     }
     
     private void calculateRotation(){       
         modelRotationAngle = rotationAngle;
         this.turnBody(modelRotationAngle, modelRotationAngle);
     
     }
       
//      public Quaternion findParentInverseRotations(Bone startJoint){
//         if(startJoint.getParent() == null){
//             return initialRotation.inverse();
//         }
//
//         else{
//            if(startJoint.getParent().getLocalRotation().inverse() != null){
//                //returns quaternion concatenation in order from child to parent recursively
//                //i.e. child local rotation * parent local rotation * grandparent local rotation....
//                return startJoint.getParent().getLocalRotation().inverse().mult(findParentInverseRotations(startJoint.getParent()));
//            }
//            else{
//                return new Quaternion();
//            }
//        }
//
//     }
    
    public Vector3f findRotatedVector(Bone startJoint, Vector3f vec){
        return new Vector3f();
    }
    
    public void moveJoints(ArrayList<String> vectorStrings){
         this.calculateRotation();
         super.moveJoints(vectorStrings);
         super.getBoneRotationInfo();  
   //      this.calculateRotation(rotVec);
    }
    
    public Vector3f calculateLeftPointTarget(){
         Vector3f vec = new Vector3f();
         Vector3f ls = new Vector3f();
         Vector3f le = new Vector3f();
         Vector3f lhand = new Vector3f();
   //     model.localToWorld(control.getSkeleton().getBone("leftShoulderJoint").getAttachmentsNode().getLocalTranslation(), ls);
   //      model.localToWorld(control.getSkeleton().getBone("leftElbowJoint").getAttachmentsNode().getLocalTranslation(), le);
   //      model.localToWorld(control.getSkeleton().getBone("leftHandJoint").getAttachmentsNode().getLocalTranslation(), lhand);  
         model.localToWorld(control.getSkeleton().getBone("leftShoulderJoint").getLocalPosition(), ls);
         model.localToWorld(control.getSkeleton().getBone("leftElbowJoint").getLocalPosition(), le);
         model.localToWorld(control.getSkeleton().getBone("leftHandJoint").getLocalPosition(), lhand);  
         vec = le.add(lhand.subtract(le).mult(10f));
         return vec;
     }
    
    public Vector3f calculateRightPointTarget(){
         Vector3f vec = new Vector3f();
         Vector3f rs = new Vector3f();
         Vector3f re = new Vector3f();
         Vector3f rhand = new Vector3f();
         model.localToWorld(control.getSkeleton().getBone("rightShoulderJoint").getLocalPosition(), rs);
         model.localToWorld(control.getSkeleton().getBone("rightElbowJoint").getLocalPosition(), re);
         model.localToWorld(control.getSkeleton().getBone("rightHandJoint").getLocalPosition(), rhand);        
         vec = re.add(rhand.subtract(re).mult(10f));
         return vec;
     } 
    
    public Ray calculateLeftPointingRay(){
        Ray r = new Ray();
        Vector3f origin = new Vector3f();
        Vector3f x = new Vector3f();
        model.localToWorld(control.getSkeleton().getBone("leftElbowJoint").getLocalPosition(), origin);
        model.localToWorld(control.getSkeleton().getBone("leftHandJoint").getLocalPosition(), x);
        r.setOrigin(origin);
        r.setDirection(x.subtract(origin));
        return r;
    }
    
     public Ray calculateRightPointingRay(){
        Ray r = new Ray();
        Vector3f origin = new Vector3f();
        Vector3f x = new Vector3f();
        model.localToWorld(control.getSkeleton().getBone("rightElbowJoint").getLocalPosition(), origin);
        model.localToWorld(control.getSkeleton().getBone("rightHandJoint").getLocalPosition(), x);
        r.setOrigin(origin);
        r.setDirection(x.subtract(origin));
        return r;
    }
     
    public void setHeadRotation(Vector3f facingDirection, Vector3f pointDirection){             
        double b = (Math.atan2(pointDirection.z, pointDirection.x) - Math.atan2(facingDirection.z, facingDirection.x));
        float deg = -(float)Math.toDegrees(b);
        Quaternion quat;
        if(deg > 90){
            quat = new Quaternion().fromAngles(0, 0, 90);
        }
        else if(deg < -90){
            quat = new Quaternion().fromAngles(0, 0, (float)Math.toRadians(-90));
        } 
        else{
            quat = new Quaternion().fromAngles(0, 0, (float)Math.toRadians(deg));
        }   
        animateBoneAngle("headJoint", quat);
    }
    
    public String getHeadRotationInfo(){
        String quat = control.getSkeleton().getBone("headJoint").getLocalRotation().toString(); 
        return "headJoint:" + quat + ";";  
    }
    
    public float getHeadRotationAngle(){
        Quaternion q =  control.getSkeleton().getBone("headJoint").getLocalRotation(); 
        float[] f = new float[3];
        q.toAngles(f);
        return (float)Math.toDegrees(f[2]);
    }
    
       public String jointNameConverter(String s){
       return "";
   }
       
   public void playAnimation(int channelType, String animationType, float speed, LoopMode l){
    
    }
   
    
    
}
