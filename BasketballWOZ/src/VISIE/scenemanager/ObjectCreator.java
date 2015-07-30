/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.models.ModelLoader;
import VISIE.models.AnimatedModel;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import Basketball.Ball;
import VISIE.Main;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class ObjectCreator {
    
    private Node rootNode;
    private Main parentClass;
    private BulletAppState bulletAppState;
    private ArrayList<Vector3f> ballToAdd = new ArrayList<Vector3f>();
    
    public ObjectCreator(Node r, BulletAppState bApp, Main m){
        rootNode = r;
        bulletAppState = bApp;
        parentClass = m;
    }
    
    public Ball createBall(Vector3f initialPos, Node root){
        
        Ball b;
        RigidBodyControl ballNode;
        
        Spatial ballModel = ModelLoader.loadModel("Scenes/gameObjects/ball.mesh.xml");
        ballModel.setLocalScale(0.65f);
        SphereCollisionShape sc = this.findSphereBounds(ballModel);
        ballNode = new RigidBodyControl(sc, 1f);
        ballNode.setMass(1f);
        ballNode.setRestitution(0.8f);
        b = new Ball(initialPos, ballNode, sc.getRadius());
        
        Node n = new Node();
        n.attachChild(ballModel);
        n.addControl(ballNode);
        root.attachChild(n);
        b.setMainNode(n);
        b.setBallPosition(initialPos);
     //   bulletAppState.getPhysicsSpace().add(ballNode);
        
        return b;
    }
    
    private SphereCollisionShape findSphereBounds(Spatial s){
       BoundingBox b = (BoundingBox)s.getWorldBound();
       float radius = (b.getXExtent() + b.getZExtent())/2;
       return new SphereCollisionShape(radius); 
    }
    
    public void flagBallForCreation(Vector3f pos){
        ballToAdd.add(pos);
    }
    
    public boolean updateBallObject(){
        
        if(!ballToAdd.isEmpty()){
            Ball b = this.createBall(ballToAdd.get(0), rootNode);
            SceneObjectManager.setBall(b);
            ballToAdd.clear();
            return true;
        }
        
        return false;
    }
}
