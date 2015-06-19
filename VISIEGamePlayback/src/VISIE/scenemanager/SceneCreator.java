/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.models.ModelLoader;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.effects.impl.BlendMode;
import java.io.File;
import java.io.IOException;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author DiveshLala
 */
public class SceneCreator {
    private static Node root;
    private BulletAppState bulletApp;
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private static Node environmentNode;
    private static Spatial sceneMesh;
    private RigidBodyControl scenePhysicsControl;
    private RigidBodyControl goalPhysicsControl; 
    private static String directory;
    private static AssetManager assetManager;
    private static Rectangle2D courtDimensions;

    
    public SceneCreator(Node r, BulletAppState bApp){
        root = r;
        bulletApp = bApp;
        environmentNode = new Node();
        environmentNode.setName("Environment");
    }
    
    
    public void addLights(Node root){
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1f));
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.clone().multLocal(1f)); // bright white light
        dl.setDirection(new Vector3f(-2.8f, -5.8f, 2.8f).normalize());
   //     dl.setDirection(new Vector3f(1, -1, 1).normalize());
     //   dl.setColor(ColorRGBA.White.clone().multLocal(0.75f)); 
        
        ambientLight = al;
        directionalLight = dl;
    } 
    
    public static AssetManager getAssetManager(){
        return assetManager;
    }
    
    public void loadScene(String scenepath, AssetManager am, Node root){
        
        assetManager = am;
        sceneMesh = ModelLoader.loadModel(scenepath);
        sceneMesh.setLocalScale(2f);
        CollisionShape sceneShape =  CollisionShapeFactory.createMeshShape(sceneMesh);
        scenePhysicsControl = new RigidBodyControl(sceneShape,  0);
        sceneMesh.addControl(scenePhysicsControl);
        sceneMesh.setName("Scene Mesh");
        directory = scenepath;
        scenePhysicsControl.setRestitution(0.8f);
        scenePhysicsControl.setCollisionGroup(3);
        
        this.loadCourt();
        this.loadHoop();
  }
    
    public static String getSceneName(){
        return directory;
    }
    
    private void loadHoop(){
        
        Spatial goalModel = ModelLoader.loadModel("Scenes/gameObjects/goal.j3o");
        goalModel.setName("goal");
        root.attachChild(goalModel);
        
        goalModel.setLocalScale(0.06f, 0.06f, 0.06f);
        goalModel.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(90), (float)Math.toRadians(-90), 0));
     //   goalModel.setLocalTranslation(-24f, 0, -15.5f);
        goalModel.setLocalTranslation(Court.calculateGoalPosition());
    //    goalModel.setLocalTranslation(Court.);
        
        CollisionShape goalShape = CollisionShapeFactory.createMeshShape(goalModel);
        goalPhysicsControl = new RigidBodyControl(goalShape, 0);
        goalModel.addControl(goalPhysicsControl);
        
        goalPhysicsControl.setRestitution(0.05f); 
    }
    
    private void loadCourt(){
        Spatial courtModel = ModelLoader.loadModel("Scenes/gameObjects/court.j3o");
        courtModel.setName("court");
        root.attachChild(courtModel);
        
        courtModel.setLocalScale(35f, 35f, 35f);
        courtModel.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(0), (float)Math.toRadians(90), (float)Math.toRadians(0)));
        courtModel.setLocalTranslation(8f, 0.45f, -15.5f);
        
        BoundingBox b = (BoundingBox)courtModel.getWorldBound();
        Vector3f courtTopLeft = b.getCenter().subtract(new Vector3f(b.getXExtent(), b.getYExtent(), b.getZExtent()));
        
        Court.initialiseCourtDimensions(courtTopLeft, b);
    }
    
    
    public void attachObjects(Node r){
        r.addLight(ambientLight);
        r.addLight(directionalLight);
        r.attachChild(environmentNode);
        environmentNode.attachChild(sceneMesh);
        bulletApp.getPhysicsSpace().add(scenePhysicsControl);  
        
        if(goalPhysicsControl != null){
            bulletApp.getPhysicsSpace().add(goalPhysicsControl); 
        }
    }
    
    public static Node getEnvironmentNode(){
        return environmentNode;
    }
    
    public static Spatial getSceneMesh(){
        return sceneMesh;
    }
    
    
    public static void addTargetNode(Vector3f pos){
         Box box1 = new Box(pos, 1, 10, 1);
         Geometry geo = new Geometry("playerTarget", box1); 
         Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mat2.setColor("Color", new ColorRGBA(1,0,0,0.5f));
         mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
         geo.setMaterial(mat2);
         root.attachChild(geo); 
    }
    
    public static void removeTargetNode(){
        if(root.getChild("target") != null){
            root.getChild("target").removeFromParent();
            System.out.println(root.getChildren());
        }
    }
    
    public static void changeTargetColour(String nodeName, boolean isBlue){
        Geometry g = (Geometry)root.getChild(nodeName);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if(isBlue){
            mat.setColor("Color", new ColorRGBA(0,0,1,0.5f));
        }
        else{
            mat.setColor("Color", new ColorRGBA(1,0,0,0.5f));
        }
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        g.setMaterial(mat);         
    }
    
}
