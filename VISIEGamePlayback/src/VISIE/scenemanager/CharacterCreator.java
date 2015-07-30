/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.characters.BasketballAgent;
import VISIE.models.ModelLoader;
import VISIE.models.AnimatedModel;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.models.BPNewModel;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import java.util.ArrayList;
/**
 *
 * @author DiveshLala
 */
public class CharacterCreator{
    
    private Node root;
    private BulletAppState bulletAppState;
    
    public CharacterCreator(Node r, BulletAppState bApp){
        root = r;
        bulletAppState = bApp;
    }
      
    public Player addPlayerCharacter(int id, String modelType, Vector3f startPosition, float scale){
        Player p;
        CharacterControl playerNode;
        AnimatedModel am = ModelLoader.createAnimatedModel(modelType, id, scale);
        Spatial model = am.getModel();
        CapsuleCollisionShape cc = am.getCollisionShapeForModel();
        float playerRadius = cc.getRadius();
        float playerHeight = cc.getHeight();
        playerNode = new CharacterControl(new CapsuleCollisionShape(playerRadius, playerHeight, 1), .05f);
        //        playerNode.setAngularFactor(0);
  //      playerNode.set
    //    playerNode.setJumpSpeed(20);
    //    playerNode.setFallSpeed(30);
     //   playerNode.setGravity(30);
  //      playerNode.set

        p = new Player(playerNode, playerRadius, id, am);
        p.setCharacterType("Player");

        Node n = new Node();
        n.setName("Player " + p.getID());

        n.attachChild(model);
        n.addControl(playerNode);
        root.attachChild(n);
        p.setExistenceNode(n);
        p.setSoundNodes();
        p.setPosition(startPosition);
    //    bulletAppState.getPhysicsSpace().add(playerNode);
        System.out.println("player created"); 
        return p;
    }
    
    public NonUserPlayer addNonUserPlayerCharacter(int id, String modelType, Vector3f startPosition, float scale){
        //Non-user player must be RigidBodyControl for collisions
        NonUserPlayer nup;
        RigidBodyControl playerNode;
        
        AnimatedModel am = ModelLoader.createAnimatedModel(modelType, id, scale);
        Spatial model = am.getModel();
        CapsuleCollisionShape cc = am.getCollisionShapeForModel();
        float playerRadius = cc.getRadius();
        float playerHeight = cc.getHeight();
        playerNode = new RigidBodyControl(cc, 100f);
        playerNode.setAngularFactor(0);
        playerNode.setMass(70);
        nup = new NonUserPlayer(playerNode, playerRadius, id, am);

        Node n = new Node();
        n.setName("Player " + nup.getID());

        n.attachChild(model);
        n.addControl(playerNode);
        root.attachChild(n);
        nup.setPosition(startPosition);
   //     bulletAppState.getPhysicsSpace().add(playerNode);
        nup.setExistenceNode(n);
        nup.setSoundNodes();
        System.out.println("non-user player created"); 
        return nup;
    }
    
    private CapsuleCollisionShape findCharacterBounds(Spatial s){
       BoundingBox b = (BoundingBox)s.getWorldBound();
       float radius = (b.getXExtent() + b.getZExtent())/2;
       return new CapsuleCollisionShape(radius, b.getYExtent(), 1);
   }
    
     public BasketballAgent addAgentCharacter(int id, String modelType, Vector3f startPosition, float scale){
        
        AnimatedModel am;
        BasketballAgent ba;

        am = ModelLoader.createAnimatedModel(modelType, id, scale);
        
        Spatial model = am.getModel();
        BPNewModel bm = (BPNewModel)am;
        CapsuleCollisionShape cc = am.getCollisionShapeForModel();
        RigidBodyControl character = new RigidBodyControl(cc, 100f);
        character.setAngularFactor(0);
        character.setFriction(0.8f);
        
        ba = new BasketballAgent(id, (BPNewModel)am, character, cc.getRadius(), cc.getHeight());
        ba.setCharacterType("BasketballAgent");

        
        Node n = new Node();
        n.setName("Basketball Player " + id);
        am.setParentCharacter(ba);
        n.setLocalTranslation(startPosition);
        n.attachChild(model);
        n.addControl(character);
        ba.setExistenceNode(n);
        ba.setSoundNodes();
        bulletAppState.getPhysicsSpace().add(character);
        root.attachChild(n); 
  //      ba.setSoundNodes();
        return ba;
    }
    
    public static Vector3f generateStartingPosition(int i){
        
        if(i == 0){
            return Court.getRandomHoopSidePosition();
        }
        else{
            return Court.getRandomNonHoopSidePosition();
        }
        
    }
    
 

    
}
