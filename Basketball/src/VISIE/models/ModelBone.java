/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;
import VISIE.models.AnimatedModel;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.math.Transform;
import com.jme3.math.Matrix3f;

/**
 *
 * @author huang
 */
public class ModelBone {
    
    private Bone parentJoint;
    private Bone childJoint;
    private Quaternion initialParentRotation;
    private Vector3f initialVector;
    private Vector3f currentBoneVector;
    private String header;
    private AnimatedModel model;
    private float i = 0.01f;
    public Quaternion spineQuat = new Quaternion();
    public Quaternion currentQuat = new Quaternion();
    public int boneIndex;
    
    public ModelBone(Bone p, Bone c, String h, AnimatedModel am){
        
        parentJoint = p;
        childJoint = c;
        
        initialParentRotation = parentJoint.getWorldBindRotation();
        initialVector = childJoint.getWorldBindPosition().normalize().clone();
        currentBoneVector = initialVector;
        header = h;
        model = am;
                
    }
    
    public void setBoneIndex(Skeleton s){    
        boneIndex = s.getBoneIndex(parentJoint);
    }
    
    public void setCurrentModelBoneVector(Vector3f vec){
        currentBoneVector = vec;
    }
    
    public Vector3f getCurrentModelBoneVector(){
        return currentBoneVector;
    }
 
    public void moveModelBone(){ 
            //find value of current bone vector in parent's local rotation space
            //inverse of parent joint * inverse rotations of joints in hierarchy  *  normalization of current bone vector 
            Vector3f vec = initialParentRotation.inverse().mult(model.findParentInverseRotations(parentJoint).mult(currentBoneVector.normalize())); 
            vec.normalizeLocal();
            
            //find rotation angle between initial and current bone vectors
            Vector3f axis = initialVector.cross(vec);
            float angle = (float)(Math.acos(vec.dot(initialVector)));            
            axis.normalizeLocal();  
            Quaternion q2 = new Quaternion().fromAngleAxis(angle, axis);
            
            currentQuat = q2;
            
            //apply rotation to model
            model.animateBoneAngle(parentJoint.getName(), q2); 
    }  
    
    public boolean checkKinectHeader(String s){
        return (header.equals(s));
    }
    
    public Quaternion getCurrentQuat(){
        return currentQuat;
    }
    
    public String getParentJointName(){
        return parentJoint.getName();
    }
    
    public void setCurrentQuat(Quaternion q){
        currentQuat = q;
    }
    
    public Vector3f getModelSpaceBoneVector(){
        return(parentJoint.getModelSpacePosition().subtract(childJoint.getModelSpacePosition()));    
    }
    
    public int getBoneIndex(){
           return boneIndex;  
    }
    

}
