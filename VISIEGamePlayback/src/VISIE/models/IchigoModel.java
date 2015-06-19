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


/**
 *
 * @author DiveshLala
 */
public class IchigoModel extends AnimatedModel {
    
    
    private Quaternion initialRotation;
    private int torsoBoneIndex = 0;
    
    
    public IchigoModel(Spatial s, int id){
            model = s;
            Quaternion q = new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0);
            model.rotate(q);
            initialRotation = q;
            model.setLocalScale(0.3f);
            model.setLocalTranslation(0f, -2.8f, 0f);
            model.setName("Cute Robot " + id + " mesh");
            applyColours();
            typeIdentifier = "I";
    }
    
    public void removeUserControl(){
        armChannel.setLoopMode(LoopMode.DontLoop);
    }
    
   public void setWalkingAnimation(float walkingSpeed){
//       
//       if(channel.getLoopMode().equals(channel.getLoopMode().DontLoop))
//           channel.setLoopMode(LoopMode.Loop);
//           channel.setSpeed(walkingSpeed * 20);
   }
    
    public void applyColours(){}
    
    public void applyColours(ArrayList<Vector3f> colList){}
    
    public void turnBody(float headRotationAngle, float bodyRotationAngle){         
         float angleDifference = bodyRotationAngle - currentYRotationAngle;
         
     //    model.rotate(new Quaternion().fromAngleAxis((float)Math.toRadians(angleDifference), initialRotation.inverse().mult(Vector3f.UNIT_Y)));
         
         currentYRotationAngle = bodyRotationAngle;
         
         animateBoneAngle("08torso", headRotationAngle, Vector3f.UNIT_Z);

    }
    
    public int getNumberOfColours(){
        return 0;
    }
    public void doServingAnimation(){}
    
    public void initialize(){
        control = model.getControl(AnimControl.class);
        control.addListener(this);
        armChannel = control.createChannel();
        armChannel.setAnim("walk");
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
         ModelBone b4 = new ModelBone(s.getBone("02Lshoulder"), s.getBone("03Lelbow"), "ELSL", this);
         ModelBone b5 = new ModelBone(s.getBone("03Lelbow"), s.getBone("04Lhand"), "WLEL", this);
         ModelBone b6 = new ModelBone(s.getBone("05Rshoulder"), s.getBone("06Relbow"), "ERSR", this);
         ModelBone b7 = new ModelBone(s.getBone("06Relbow"), s.getBone("07Rhand"), "WRER", this);
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
      
       private void calculateRotation(Vector2f rotVec){
         
         Vector2f v1 = new Vector2f(1, 0);
         Vector2f v2 = rotVec.normalize();
         double angle = Math.atan2(v2.y, v2.x) - Math.atan2(v1.y, v1.x);
         modelRotationAngle = -(float)Math.toDegrees(angle);
         this.turnBody(modelRotationAngle, modelRotationAngle);
     
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
    
    public Vector3f findRotatedVector(Bone startJoint, Vector3f vec){
        return new Vector3f();
    }
    
    public void moveJoints(ArrayList<String> vectorStrings){
         super.moveJoints(vectorStrings);
         super.getBoneRotationInfo();  
         this.calculateRotation(rotVec);
    }
    
    public Vector3f calculatePointTarget(){
         Vector3f vec = new Vector3f();
         Vector3f ls = new Vector3f();
         Vector3f le = new Vector3f();
         Vector3f lhand = new Vector3f();
//         model.localToWorld(control.getSkeleton().getBone("02Lshoulder").getAttachmentsNode().getLocalTranslation(), ls);
//         model.localToWorld(control.getSkeleton().getBone("03Lelbow").getAttachmentsNode().getLocalTranslation(), le);
//         model.localToWorld(control.getSkeleton().getBone("04Lhand").getAttachmentsNode().getLocalTranslation(), lhand);        
         model.localToWorld(control.getSkeleton().getBone("02Lshoulder").getLocalPosition(), ls);
         model.localToWorld(control.getSkeleton().getBone("03Lelbow").getLocalPosition(), le);
         model.localToWorld(control.getSkeleton().getBone("04Lhand").getLocalPosition(), lhand); 
         vec = le.add(lhand.subtract(le).mult(5));
         return vec;
     }
    
       public String jointNameConverter(String s){
       return "";
   }
       
           public void playAnimation(int channelType, String animationType, float speed, LoopMode l){
    
    }
   

    
}
