/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.mathfunctions.Conversions;
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
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.io.File;

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
    private RigidBodyControl landscape;
    private boolean preLoadFlag = false;
    private String directoryName;
    private static AssetManager assetManager;
    private RigidBodyControl goalPhysicsControl; 
    private static Geometry restartMarker;
    private static Spatial ballMarker;
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
        al.setColor(ColorRGBA.White.mult(0.85f));
        DirectionalLight dl = new DirectionalLight();
     //   dl.setColor(ColorRGBA.White.clone().multLocal(2)); // bright white light
        dl.setDirection(new Vector3f(2.8f, -5.8f, -2.8f).normalize());
        dl.setColor(ColorRGBA.White.clone().multLocal(0.75f)); 
        
        ambientLight = al;
        directionalLight = dl;
        
        this.loadCourt();
        this.loadHoop();
        this.createBallMarker();
        this.createDirectionalArrow();
        this.createDribbleArrow();
    } 
    
    public void loadScene(String scenepath, AssetManager am, Node root){
        assetManager = am;
      //  sceneMesh = assetManager.loadModel(scenepath);
        sceneMesh = ModelLoader.loadModel(scenepath);
        sceneMesh.setLocalScale(2f);

        CollisionShape sceneShape =  CollisionShapeFactory.createMeshShape(sceneMesh);
        landscape = new RigidBodyControl(sceneShape,  0);
        sceneMesh.addControl(landscape);
        sceneMesh.setName("Scene Mesh");
  }
    
    public void loadScene(AssetManager am, Node root){
        
        assetManager = am;

      //  sceneMesh = assetManager.loadModel(scenepath);
        if(!directoryName.isEmpty()){
            System.out.println(directoryName);
            sceneMesh = ModelLoader.loadModel(directoryName);
            sceneMesh.setLocalScale(2f);

            CollisionShape sceneShape =  CollisionShapeFactory.createMeshShape(sceneMesh);
            landscape = new RigidBodyControl(sceneShape,  0);
            sceneMesh.addControl(landscape);
            sceneMesh.setName("Scene Mesh");
        }
  }
    
    
    public void attachObjects(Node r){
        r.addLight(ambientLight);
        r.addLight(directionalLight);
        r.attachChild(environmentNode);
        environmentNode.attachChild(sceneMesh);
        System.out.println(bulletApp + " " + landscape);
        bulletApp.getPhysicsSpace().add(landscape);   
    }
    
    public static Node getEnvironmentNode(){
        return environmentNode;
    }
    
    public static Spatial getSceneMesh(){
        return sceneMesh;
    }
    
    public void flagSceneForCreation(String dir){
        directoryName = dir;
        preLoadFlag = true;   
    }
    
    public boolean getPreLoadFlag(){
        return preLoadFlag;
    }
    
    public void removePreLoadFlag(){
        preLoadFlag = false;
    }
    
    public static AssetManager getAssetManager(){
        return assetManager;
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
    
    private void createRestartMarker(){
        Box box1 = new Box(Court.getRestartLocation(), 1, 10, 1);
        Geometry geo = new Geometry("playerTarget", box1);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(1,0,0,0f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geo.setMaterial(mat2);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        restartMarker = geo;
        root.attachChild(restartMarker);
    }
    
    public static void displayMarker(ColorRGBA col){
        restartMarker.getMaterial().setColor("Color", col);
    }
    
    public static void hideMarker(){
        restartMarker.getMaterial().setColor("Color", new ColorRGBA(1,0,0,0));
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
        ballMarker.setLocalTranslation(ballPos.add(0, 3f, 0));
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
            g.setQueueBucket(RenderQueue.Bucket.Transparent);
            
        }
    }
    
    public void createDribbleArrow(){
        Spatial spat = ModelLoader.loadModel("Scenes/gameObjects/arrow.mesh.j3o");
        dribbleArrow = spat;
        spat.setLocalScale(0.01f);
        spat.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(-90), 0, 0));
        
        Node n = (Node)dribbleArrow;
        for(int i = 0; i < n.getChildren().size(); i++){
            Geometry g = (Geometry)n.getChild(i);
            g.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            MatParam diff = g.getMaterial().getParam("Diffuse");
            ColorRGBA col = (ColorRGBA.Red);
            col.a = 0.75f;
            g.getMaterial().setColor("Diffuse", col);
            g.getMaterial().setColor("Ambient", col);
            g.setQueueBucket(RenderQueue.Bucket.Transparent);
            
        }
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
        directionalArrow.setLocalTranslation(playerPos.add(vec));
    }
    
    public static void setDribbleArrow(float f, Vector3f ballPosition){
        if(dribbleArrow.getParent() == null){
            root.attachChild(dribbleArrow);
        }
        float yValue = 0.25f * (float)Math.sin(periodicValue) - 1;
        periodicValue += 0.01f;
        dribbleArrow.setLocalTranslation(ballPosition.add(0, yValue, 0));
        dribbleArrow.setLocalRotation(new Quaternion().fromAngles((float)Math.toRadians(-90), (float)Math.toRadians(f), 0));
        
    }
    
    public static void removeDirectionalArrow(){
        directionalArrow.removeFromParent();
    }
    
    public static void removeDribbleArrow(){
        dribbleArrow.removeFromParent();
    }
    
    
}
