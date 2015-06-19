/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;

/**
 *
 * @author DiveshLala
 */
public class Scene {
    
    private static Node sceneNode;
    
    public static void setScene(Node n){
        sceneNode = n;
 //       System.out.println(n.getChildren());
    }
      
    public static Node getSceneNode(){
        return sceneNode;
    }
    
//    public static Geometry getGeometry(){
//            
//    }
    
    
    
}
