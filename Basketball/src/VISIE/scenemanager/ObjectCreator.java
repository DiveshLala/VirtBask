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
import VISIE.Sound.BallSoundNode;

/**
 *
 * @author DiveshLala
 */
public class ObjectCreator {
    
    private Node rootNode;
    private BulletAppState bulletAppState;
    
    public ObjectCreator(Node r, BulletAppState bApp){
        rootNode = r;
        bulletAppState = bApp;
    }
    
    public Ball createBall(Vector3f initialPos, Node root){
        
        Ball b;
        RigidBodyControl ballNode;
        
        Spatial ballModel = ModelLoader.loadModel("Scenes/gameObjects/ball.mesh.xml");
        ballModel.setLocalScale(0.65f);
        SphereCollisionShape sc = this.findSphereBounds(ballModel);
        sc.setScale(Vector3f.UNIT_XYZ.mult(0.9f)); //used to ensure collisions are smooth
        ballNode = new RigidBodyControl(sc, 1f);
        ballNode.setRestitution(0.8f);
        b = new Ball(initialPos, ballNode, sc.getRadius());
        
        Node n = new Node();
        n.attachChild(ballModel);
        n.addControl(ballNode);
        n.setName("Ball");
        root.attachChild(n);
        b.setMainNode(n);
        b.setBallPosition(initialPos);
        bulletAppState.getPhysicsSpace().add(ballNode);
        BallSoundNode bsn = new BallSoundNode(n, b, "assets/Sounds/ballSounds.txt");
        b.setSoundNode(bsn);
        return b;
    }
    
    private SphereCollisionShape findSphereBounds(Spatial s){
       BoundingBox b = (BoundingBox)s.getWorldBound();
       float radius = (b.getXExtent() + b.getZExtent())/2;
       System.out.println(radius);
       return new SphereCollisionShape(radius); 
    }
    
}
