/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.animation.Skeleton;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import com.jme3.math.Quaternion;

/**
 *
 * @author DiveshLala
 */
public class ShopKeeperModel extends AnimatedModel{
    
    public ShopKeeperModel(Spatial s, int id){
//        super(s, id);
        model = s;
        model.rotate((float)Math.toRadians(90), 0, 0);
        model.setLocalScale(0.2f);
        model.setLocalTranslation(0f, -3.1f, 0f);
        model.setName("ShopKeeper " + id + " mesh");
        applyColours();
        typeIdentifier = "S";
    }
    
    public void applyColours(){

        Node n = (Node)model;
        for(int i = 0; i < n.getChildren().size(); i++){
            float red = 0, green = 0, blue = 0;
            Geometry g = (Geometry)n.getChild(i);
            Material mat = g.getMaterial();
            if(i == 0){//trim and eye colour
                red = 0.5f;
                green = 0.2f;
                blue = 0.2f;
            }
            else if (i == 1){//main body colour
                red = 0.2f;
                green = 0.4f;
                blue = 0.4f;
            }
                Vector3f  v = new Vector3f(red, green, blue);
                colours.add(v);
                mat.setColor("Diffuse", new ColorRGBA(1f, 1f, 1f, 1f));
                mat.setColor("Ambient", new ColorRGBA(red, green, blue, 1f));
                mat.setBoolean("UseAlpha", true);
        }
    }
    
       public void removeUserControl(){
        Skeleton s = control.getSkeleton();
        Bone b = s.getBone("RAJoint");
        b.setUserControl(false);
        b = s.getBone("LAJoint");
        b.setUserControl(false);
   }

     public void turnBody(float headRotationAngle, float bodyRotationAngle){
         animateBoneAngle("headJoint", headRotationAngle, Vector3f.UNIT_Y);
         animateBoneAngle("rootJoint", bodyRotationAngle, Vector3f.UNIT_Y);
     }
     
     public ArrayList<Vector3f> getColours(){
         return colours;
     }
     
     public int getNumberOfColours(){
         return 2;
     }
     
      public void doServingAnimation(){
         //System.out.println("hello");
         animateBoneAngle("LAJoint", 270, Vector3f.UNIT_X);
         animateBoneAngle("RAJoint", 270, Vector3f.UNIT_X);
     }
      
      public Quaternion findParentInverseRotations(Bone startJoint){
         return null;
     
     }
      
         public String jointNameConverter(String s){
       return "";
   }
         
             public void playAnimation(int channelType, String animationType, float speed, LoopMode l){
    
    }
   
      

    
}
