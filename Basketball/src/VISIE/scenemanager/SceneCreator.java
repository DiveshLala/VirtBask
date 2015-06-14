/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.mathfunctions.Conversions;
import VISIE.models.ModelLoader;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
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
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.effects.impl.BlendMode;
import java.io.File;
import java.io.IOException;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author DiveshLala
 */
public class SceneCreator{
    private static Node root;
    private BulletAppState bulletApp;
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private static Node environmentNode;
    private static Spatial sceneMesh;
    private RigidBodyControl scenePhysicsControl;
    private RigidBodyControl goalPhysicsControl; 
    private static String mapFileDirectory;
    private static AssetManager assetManager;
    private static Rectangle2D courtDimensions;
    private static Geometry restartMarker;
    private static Spatial ballMarker;
    private AnimChannel pointerAnim;
    private AnimControl pointerControl;
    private static Spatial directionalArrow;
    private static Spatial dribbleArrow;
    private static float periodicValue = 0;
    
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
        mapFileDirectory = scenepath;
        scenePhysicsControl.setRestitution(0.8f);
        scenePhysicsControl.setCollisionGroup(3);
        scenePhysicsControl.setFriction(0.9f);
        
        this.loadCourt();
        this.loadHoop();
        this.createBallMarker();
        this.createDirectionalArrow();
        this.createDribbleArrow();
  }
    
    public static String getSceneName(){
        return mapFileDirectory;
    }
    
//    public String bob(){
//        return scenePhysicsControl.getCollisionGroup() + " " + scenePhysicsControl.getCollideWithGroups();
//    }
    
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
        goalPhysicsControl.setRestitution(0.8f);
        goalModel.addControl(goalPhysicsControl);
        
        goalPhysicsControl.setRestitution(0.5f); 
            
        this.createRestartMarker();
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
         Box box1 = new Box(pos, 1, 0.5f, 1);
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
    
    public void createRestartMarker(){
         Box box1 = new Box(Court.getRestartLocation(), 1, 10, 1);
         Geometry geo = new Geometry("playerTarget", box1); 
         Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mat2.setColor("Color", new ColorRGBA(1,0,0,0.0f));
         mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
         geo.setMaterial(mat2);
         geo.setQueueBucket(Bucket.Transparent);
         restartMarker = geo;
         root.attachChild(restartMarker); 
    }
    
    public static void displayMarker(Vector3f col){
   //     System.out.println("maerker" + restartMarker.getMaterial());
        restartMarker.getMaterial().setColor("Color", new ColorRGBA(col.x,col.y,col.z,0.25f));
    }
    
    public static void hideMarker(){
        restartMarker.getMaterial().setColor("Color", new ColorRGBA(1,0,0,0));
    }
    
    public static String getMarkerColor(){
        String s = restartMarker.getMaterial().getParam("Color").getValueAsString();
        return s.replace(" ", ",");   
    }
    
    private void createBallMarker(){
        Spatial spat = ModelLoader.loadModel("Scenes/gameObjects/pointer.mesh.j3o");
        ballMarker = spat;
        spat.setLocalScale(0.05f);
        spat.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0));
    }
    
    public static void setBallMarker(Vector3f ballPos){
        if(ballMarker.getParent() == null){
            root.attachChild(ballMarker);
        }      
        
       ballMarker.rotate(0, 0, (float)Math.toRadians(1));
       ballMarker.setLocalTranslation(ballPos.add(0, 3, 0));
    }
    
    public static void removeBallMarker(){
        ballMarker.removeFromParent();
    }
    
    private void createDirectionalArrow(){
        Spatial spat = ModelLoader.loadModel("Scenes/gameObjects/arrow.mesh.j3o");
        directionalArrow = spat;
        spat.setLocalScale(0.04f);
        
        Node n = (Node)directionalArrow;
        for(int i = 0; i < n.getChildren().size(); i++){
            Geometry g = (Geometry)n.getChild(i);
            g.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            MatParam diff = g.getMaterial().getParam("Diffuse");
            ColorRGBA col = (ColorRGBA)diff.getValue();
            col.a = 0.5f;
            g.getMaterial().setColor("Diffuse", col);
            g.getMaterial().setColor("Ambient", col);
            g.setQueueBucket(Bucket.Transparent);  
        } 
    }
    
    private void createDribbleArrow(){
        Spatial spat = ModelLoader.loadModel("Scenes/gameObjects/arrow.mesh.j3o");
        dribbleArrow = spat;
        spat.setLocalScale(0.01f);
        spat.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(-90), 0, 0));
        
        Node n = (Node)dribbleArrow;
        for(int i = 0; i < n.getChildren().size(); i++){
            Geometry g = (Geometry)n.getChild(i);
            g.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            MatParam diff = g.getMaterial().getParam("Diffuse");
            ColorRGBA col = ColorRGBA.Red;
            col.a = 0.75f;
            g.getMaterial().setColor("Diffuse", col);
            g.getMaterial().setColor("Ambient", col);
            g.setQueueBucket(Bucket.Transparent);  
        } 
    }
    
    public static void setDribbleArrow(float f, Vector3f ballPos){
        if(dribbleArrow.getParent() == null){
            root.attachChild(dribbleArrow);
        }
        float yValue = 0.25f * (float)Math.sin(periodicValue) - 1;
        periodicValue += 0.01f;
        dribbleArrow.setLocalTranslation(ballPos.add(0, yValue, 0));
        dribbleArrow.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(-90), (float)Math.toRadians(f), 0));
    
    }
    
    public static void setDirectionalArrow(Vector3f playerPos){
        if(directionalArrow.getParent() == null){
            root.attachChild(directionalArrow);
        }   
        
        float angleToTarget = Conversions.originToTargetAngle(playerPos, Court.getRestartLocation());
        
        //rotation
        directionalArrow.setLocalRotation(new Quaternion().fromAngles(0, (float)Math.toRadians(angleToTarget + 180), 0));
        
        //translation
        Vector3f vec = Conversions.degreesToNormalizedCoordinates(angleToTarget);
        directionalArrow.setLocalTranslation(playerPos.add(vec.mult(2)).add(0, -1, 0));
    }
    
    public static void removeDirectionalArrow(){
        directionalArrow.removeFromParent();
    }
    
    public static void removeDribbleArrow(){
        dribbleArrow.removeFromParent();
    }
    
    public static void addSphereMarker(Vector3f pos, String name, float size, String color){
        
        Sphere s = new Sphere(4,4, size);
        Geometry g = new Geometry(name, s);
        g.setLocalTranslation(pos);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if(color.equals("blue")){
            mat.setColor("Color", ColorRGBA.Blue);
        }
        else if(color.equals("red")){
            mat.setColor("Color", ColorRGBA.Red);
        }
        g.setMaterial(mat);
        root.attachChild(g);
    
    }
    
    public static void removeSphereMarker(String name){
        for(int i = root.getChildren().size() - 1; i >= 0; i--){
            if(root.getChild(i).getName().equals(name)){
                root.detachChildAt(i);
            }
        }
    }
    
}
