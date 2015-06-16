/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.characters.BasketballAgent;
import VISIE.characters.CollabAgent;
import VISIE.characters.CollabLearner;
import VISIE.characters.ImitationAgent;
import VISIE.characters.KinectPlayer;
import VISIE.models.ModelLoader;
import VISIE.models.AnimatedModel;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.PassingGameAgent;
import VISIE.characters.Player;
import VISIE.characters.RationalAgent;
import VISIE.characters.SkilledAgent;
import VISIE.models.BPNewModel;
import VISIE.models.BasketballPlayerModel;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
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
      
    public Player addPlayerCharacter(int id, String playerModelType, Node root, Vector3f startPosition, String[] textures, boolean isKinect, float scale){
        Player p;
        CharacterControl playerNode;
        AnimatedModel am = ModelLoader.createAnimatedModel(playerModelType, id, scale);
        Spatial model = am.getModel();
        
        am.addTextures(textures);
        CapsuleCollisionShape cc = am.getCollisionShapeForModel();
        float playerRadius = cc.getRadius();
        float playerHeight = cc.getHeight();
        playerNode = new CharacterControl(new CapsuleCollisionShape(playerRadius, playerHeight, 1), .05f);
        
//        CollisionShape ghostShape = new CapsuleCollisionShape(playerRadius * 3, playerHeight, 1);
//        GhostControl ghostNode = new GhostControl(ghostShape);

        if(!isKinect){
            p = new Player(playerNode, playerRadius, id, am);
        }
        else{
            p = new KinectPlayer(playerNode, playerRadius, id, am);
        }
        
        p.setCharacterType("Player");
        am.setParentCharacter(p);

        Node n = new Node();
        n.setName("Player " + p.getID()); 

        n.attachChild(model);
        n.addControl(playerNode);
        root.attachChild(n);
        p.setExistenceNode(n);
        p.setPosition(startPosition);
        bulletAppState.getPhysicsSpace().add(playerNode);
        p.setSoundNodes();
        return p;
    }
    
    public NonUserPlayer addNonUserPlayerCharacter(int id, String modelType, Node root, Vector3f startPosition, float scale){
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
         am.setParentCharacter(nup);
        
        CollisionShape ghostShape = new CapsuleCollisionShape(playerRadius * 3, playerHeight, 1);
        GhostControl ghostNode = new GhostControl(ghostShape);

        n.attachChild(model);
        n.addControl(playerNode);
        n.addControl(ghostNode);
        root.attachChild(n);
        nup.setPosition(startPosition);
        nup.setGhostNode(ghostNode);
        bulletAppState.getPhysicsSpace().add(playerNode);
        bulletAppState.getPhysicsSpace().add(ghostNode);
        nup.setExistenceNode(n);
        System.out.println("non-user player created"); 
        nup.setSoundNodes();
        return nup;
    }
    
    private CapsuleCollisionShape findCharacterBounds(Spatial s){
       BoundingBox b = (BoundingBox)s.getWorldBound();
       float radius = (b.getXExtent() + b.getZExtent())/2;
       return new CapsuleCollisionShape(radius, b.getYExtent(), 1);
   }
    
     public BasketballAgent addAgentCharacter(String agentType, int id, int agentRole, String modelType, Vector3f startPosition, float scale){
        
        AnimatedModel am;
        BasketballAgent ba;

        am = ModelLoader.createAnimatedModel(modelType, id, scale);
        
        Spatial model = am.getModel();
        BPNewModel bm = (BPNewModel)am;
        CapsuleCollisionShape cc = am.getCollisionShapeForModel();
        RigidBodyControl character = new RigidBodyControl(cc, 100f);
        character.setAngularFactor(0);
        character.setFriction(0.8f);
        
        if(agentType.equals("Imitation")){
            ba = new ImitationAgent(id, (BasketballPlayerModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("ImitationAgent");
        }
        else if(agentType.equals("Collab")){
            ba = new CollabAgent(id, (BPNewModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("CollabAgent");
        }
        else if(agentType.equals("CollabLearner")){
            ba = new CollabLearner(id, (BPNewModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("CollabLearner");
        }
        else if(agentType.equals("Skilled")){
            ba = new SkilledAgent(id, (BasketballPlayerModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("SkilledAgent");
        }
        else if(agentType.equals("Rational")){            
            ba = new RationalAgent(id, (BasketballPlayerModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("RationalAgent"); 
        }
        else if(agentType.equals("Passing")){
            ba = new PassingGameAgent(id, (BasketballPlayerModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("PassingAgent");  
        }
        else{
            ba = new BasketballAgent(id, (BPNewModel)am, character, cc.getRadius(), cc.getHeight());
            ba.setCharacterType("BasketballAgent");
        }
        
        Node n = new Node();
        n.setName("Basketball Player " + id);
        am.setParentCharacter(ba);
        n.setLocalTranslation(startPosition);
        n.attachChild(model);
        n.addControl(character);
        ba.setExistenceNode(n);
        bulletAppState.getPhysicsSpace().add(character);
        root.attachChild(n); 
        ba.initialiseTarget();
        ba.setSoundNodes();
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
