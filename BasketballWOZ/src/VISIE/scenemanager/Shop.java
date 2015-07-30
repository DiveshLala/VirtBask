/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author DiveshLala
 */
public class Shop{
    
    private Vector3f shopLocation;
    private Vector3f counterLocation;
    private int waitingCustomers;
    private float orderTime;
    
    public Shop(Node stallNode){
        //calculates counter location
        shopLocation = stallNode.getWorldTranslation();    
        Node bob = new Node();
        Node c = new Node();
        bob.attachChild(c);
        c.setLocalTranslation(new Vector3f(0, 10f, 0f));
        bob.rotate(stallNode.getWorldRotation());
        Vector3f vec = new Vector3f(c.getWorldTranslation().add(shopLocation));
        counterLocation = new Vector3f(vec.getX(), 0, vec.getZ());
        System.out.println(shopLocation);
        System.out.println(counterLocation);
    }
    
    public Vector3f getShopLocation(){
        return shopLocation;
    }
    
}
