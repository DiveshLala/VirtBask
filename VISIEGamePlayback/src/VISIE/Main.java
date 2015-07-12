
package VISIE;

import Basketball.Ball;
import VISIE.AgentLogic.MainLogicThread;
import VISIE.navigation.UserNavigation;
import VISIE.recording.Log;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.*;
import VISIE.models.ModelLoader;
import VISIE.network.*;
import VISIE.characters.Character;
import VISIE.characters.*;
import com.jme3.animation.LoopMode;
import com.jme3.app.SettingsDialog;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.CharacterControl;
import java.util.List;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.system.AppSettings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.lwjgl.opengl.Display;
import com.jme3.renderer.ViewPort;
import javax.print.attribute.standard.Severity;
import java.util.ArrayList;
import java.net.InetAddress;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.Ray;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.AnalogListener;
import com.jme3.scene.shape.Box;
import java.io.BufferedReader;
import java.io.FileReader;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import com.jme3.input.controls.AnalogListener;
/**
 * Example 9 - How to make walls and floors solid.
 * This version uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */
public class Main extends SimpleApplication implements ActionListener, AnalogListener, PhysicsCollisionListener {

  private BulletAppState bulletAppState;
  private CharacterControl playerNode;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false, transaction = false;
  private float cameraRot;
  private ArrayList<Character> characterArray = new ArrayList<Character>();
  private boolean isRunning;
  private boolean ATypeCamera = false;
  private boolean BTypeCamera = false;
  private boolean topCamera = true;
  
  private Ball ball;
  
  private int numberOfTeams;
  private int membersPerTeam;
  private int playerTeamID; //0 is defednding, 1 is attacking
  private ArrayList<Team> teams = new ArrayList<Team>();
  private int offset = 0;
  
  
  //displaySettings     
  private CameraView camView;
  private int camState = 0;
  private static float timePerFrame;
  int collTime = 0;
  
  
 //I/O operations
  private Thread writeThread;

  //cultural dimensions of environment
//  private float individualism;
//  private float masculinity;
//  private float powerDistance;
//  private float uncertaintyAvoidance;
//  private float longTermOrientation;

  static boolean isImmersive = true;
 // static boolean hasFixedOrientation = false;
  
  float aspectRatio = 0.1f;
  
  //network settings
  Server TCPServer;
  UDPBroadcastServer broadcastServer;
  SensorClient sensorClient;
  
  private int newID = -1;
  private Vector3f newPos = null;
  private int targetsReached = 0;
  
 // public int IDCounter = 0;
  
  KinectClient kinectClient;

  private SceneCreator sceneCreator;
  private CharacterCreator characterCreator;
  private ObjectCreator objectCreator;
  private ModelLoader modelLoader;
  private SceneCharacterManager sceneCharacterManager;
  private SceneObjectManager sceneObjectManager;
  private UserNavigation userNavigation;
  private MainLogicThread agentThread;
  
  NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay();
  private Nifty nifty;
//  private boolean screenChange = false;
  private InitialSettings initialSettings = new InitialSettings();
  private String gameType;
  
  private int nextToRead = 0; 
  private boolean isPaused = false;
  private boolean[] vidPlayer = new boolean[3];  
  
  private int logTime = 0;
  private String logFileName = "20150710_125804.txt";
  private File writePFile;
  private File writeNUPFile;
  private File JAFile;
  private File distances;
  private BufferedReader reader;  
  
  private int currentReadLine = 0;
  private int playSpeed;
  private boolean isRewind = false;
  private ArrayList<String> data = new ArrayList<String>();
  private boolean[] keys = new boolean[6];
  private boolean[] cameras = new boolean[5];
  private int timeBetweenFrames = 1000;
  private long timeSinceLast;
  private int maxFrame;
  private float playerFD;
  private float nupFD;
 private Vector3f playerPos;
 private Vector3f nupPos;
  
  public static void main(String[] args) {
        Main gameApp = new Main();   
        AppSettings settings = new AppSettings(true);
        settings.setTitle("VIRTUAL BASKETBALL");
        gameApp.setDisplayStatView(false);
        gameApp.setDisplayFps(false);
        gameApp.setSettings(settings);
        gameApp.start();
    }

  public void simpleInitApp() {
      Logger.getLogger("").setLevel(Level.WARNING);
      playSpeed = 11 - (timeBetweenFrames/1000000);
      this.loadGame();
      flyCam.setDragToRotate(true);
  }
  
  //used to update camera frustum before app.restart()
  Callable<Void> changeCameraCallable = new Callable<Void>() {

        public Void call(){
            camView.changeImmersiveFieldOfView(initialSettings.getFOV());            
            return null;
        }
  };
  
  private void changeResolution(int width, int height){ 
      settings.setResolution(width, height);
      this.setSettings(settings);
      this.restart();
  }
  
  private void loadGame(){
      
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    modelLoader  = new ModelLoader(assetManager);
    characterCreator = new CharacterCreator(rootNode, bulletAppState);      
    sceneCreator = new SceneCreator(rootNode, bulletAppState);
    objectCreator = new ObjectCreator(rootNode, bulletAppState);
    sceneCharacterManager = new SceneCharacterManager(this, characterArray, characterCreator, rootNode, bulletAppState);
    camView = new CameraView(cam, initialSettings.getImmersive(), initialSettings.getFixedOrientation());
    JointProjectXMLProcessor jmp = new JointProjectXMLProcessor();
    BehaviorXMLProcessor bp = new BehaviorXMLProcessor();
    
    createEnvironment();
    createBall();
    sceneObjectManager = new SceneObjectManager(ball);
    this.loadLogFile();
    
    isRunning = true;     
    cam.setLocation(new Vector3f(-50, 10, -50));
    Quaternion camQuat = new Quaternion().fromAngles(0, (float)Math.toRadians(90), 0);
    cam.setAxes(camQuat);   
 //   flyCam.setDragToRotate(true);
    this.setPauseOnLostFocus(false);
    cameras[0] = true;
    
    this.createLogFile();
    
  }
  
  
  

  private void createEnvironment(){
    /** Set up Physics */
    viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
    this.setupKeys();

    sceneCreator.addLights(rootNode);
    sceneCreator.loadScene("Scenes/town/main.j3o", assetManager, rootNode);
    sceneCreator.attachObjects(rootNode);
  }


    /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setupKeys() {
      
//    inputManager.addMapping("Playback", new KeyTrigger(KeyInput.KEY_SPACE));
//    inputManager.addListener(this, "Playback");
    
    
    inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
    inputManager.addListener(this, "Pause");
    
        
    inputManager.addMapping("DecreaseSpeed", new KeyTrigger(KeyInput.KEY_I));
    inputManager.addListener(this, "DecreaseSpeed");
    
//    inputManager.addMapping("FastFastForward", new KeyTrigger(KeyInput.KEY_DOWN));
//    inputManager.addListener(this, "FastFastForward");
    
    inputManager.addMapping("IncreaseSpeed", new KeyTrigger(KeyInput.KEY_O));
    inputManager.addListener(this, "IncreaseSpeed");
    
    inputManager.addMapping("Rewind", new KeyTrigger(KeyInput.KEY_H));
    inputManager.addListener(this, "Rewind");
    
    inputManager.addMapping("CamForward", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addListener(this, "CamForward");
    
    inputManager.addMapping("CamLeft", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addListener(this, "CamLeft");
    
    inputManager.addMapping("CamBackward", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(this, "CamBackward");
    
    inputManager.addMapping("CamRight", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addListener(this, "CamRight");
    
        inputManager.addMapping("UpRot", new KeyTrigger(KeyInput.KEY_UP));
    inputManager.addListener(this, "UpRot");
    
        inputManager.addMapping("DownRot", new KeyTrigger(KeyInput.KEY_DOWN));
    inputManager.addListener(this, "DownRot");
    
        inputManager.addMapping("LeftRot", new KeyTrigger(KeyInput.KEY_LEFT));
    inputManager.addListener(this, "LeftRot");
    
        inputManager.addMapping("RightRot", new KeyTrigger(KeyInput.KEY_RIGHT));
    inputManager.addListener(this, "RightRot");
    
    inputManager.addMapping("IncrementForward", new KeyTrigger(KeyInput.KEY_PERIOD));
    inputManager.addListener(this, "IncrementForward");
    
    inputManager.addMapping("IncrementBack", new KeyTrigger(KeyInput.KEY_COMMA));
    inputManager.addListener(this, "IncrementBack");
    
    
//    inputManager.addMapping("Rewind", new KeyTrigger(KeyInput.KEY_W));
//    inputManager.addListener(this, "Rewind");
//    
//    inputManager.addMapping("Rewind", new KeyTrigger(KeyInput.KEY_W));
//    inputManager.addListener(this, "Rewind");
    
    
    
        inputManager.addListener(this, "CamState0");
    inputManager.addListener(this, "CamState1");
    inputManager.addListener(this, "CamState2");
        inputManager.addMapping("CamState0", new KeyTrigger(KeyInput.KEY_0));
    inputManager.addMapping("CamState1", new KeyTrigger(KeyInput.KEY_1));
    inputManager.addMapping("CamState2", new KeyTrigger(KeyInput.KEY_2));
    
    inputManager.addMapping("TopCamera", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addListener(this, "TopCamera");
    
    inputManager.addMapping("Skip", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Skip");

  }

    /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Playback") && !value) {
        System.out.println("Played at " + (nextToRead));
    } 
    else if (binding.equals("CamState0")) {
        for(int i = 0; i < cameras.length; i++){
            cameras[i] = false;
        }
        cameras[0] = true;
    }
    else if (binding.equals("CamState1") && !value) {
        for(int i = 0; i < cameras.length; i++){
            cameras[i] = false;
        }
        cameras[1] = true;
        
    //    this.showViewPoint(p.getID());
    }
    else if (binding.equals("CamState2")) {
        for(int i = 0; i < cameras.length; i++){
            cameras[i] = false;
        }
        cameras[2] = true;
    }
     else if (binding.equals("Pause") && !value) {
        isPaused = !isPaused;
        if(isPaused){
            System.out.println("Paused at " + currentReadLine);
        }
        else{
            System.out.println("Unpaused");
        }
    }
    else if (binding.equals("IncreaseSpeed") && !value) {
        
        timeBetweenFrames -= 1000000;
        
        if(timeBetweenFrames < 1000000){
            timeBetweenFrames = 1000000;
        }
        
        playSpeed = 11 - timeBetweenFrames/1000000;
        
        
    }
    else if (binding.equals("DecreaseSpeed") && !value) {
        
        timeBetweenFrames += 1000000;
        
        if(timeBetweenFrames > 10000000){
            timeBetweenFrames = 10000000;
        }
        
        playSpeed = 11 - (timeBetweenFrames/1000000);

    }
    else if (binding.equals("Rewind") && !value) {
        isRewind = !isRewind;
        
        if(isRewind){
            System.out.println("rewinding ");
        }
        else{
            System.out.println("play forward");
        }
        
    }
    
    
    else if (binding.equals("FastFastForward")) {
        vidPlayer[2] = value;
    }
    else if (binding.equals("TopCamera") && !value) {
        topCamera = !topCamera;
    }
    else if (binding.equals("Skip") && !value) {
        if(isRewind){
            if(currentReadLine < 2500){
                currentReadLine = 10;
            }
            else{
                currentReadLine -= 2500;
            }
        }
        else{
            if(currentReadLine + 2500 > data.size() - 1){
                currentReadLine = data.size() - 1;
            }
            else{
                currentReadLine += 2500;
            }
        }
    }
    else if (binding.equals("IncrementForward") && !value && isPaused) {
        currentReadLine += 10;

    }
    else if (binding.equals("IncrementBack") && !value && isPaused) {
        currentReadLine -= 10;
        if(currentReadLine < 10){
            currentReadLine = 10;
        }
    }
    
//    if(binding.equals("CamForward") && !value){
//        cam.setLocation(cam.getLocation().add(cam.getDirection().mult(2)));
//    }

  }
  
 // private AnalogListener analogListener = new AnalogListener(){
  public void onAnalog(String binding, float value, float tpf) {
      
    float camSpeed = 0.1f;
      
    if(binding.equals("CamForward")){
        cam.setLocation(cam.getLocation().add(cam.getDirection().mult(camSpeed)));
    }
    else if(binding.equals("CamBackward")){
        cam.setLocation(cam.getLocation().subtract(cam.getDirection().mult(camSpeed)));
    }
    
    if(binding.equals("CamLeft")){
        float oldX = cam.getDirection().getX();
        float oldZ = cam.getDirection().getZ();
        float cosAngle = (float)Math.cos(Math.toRadians(-90));
        float sinAngle = (float)Math.sin(Math.toRadians(-90));
        float newX = (oldX * cosAngle) - (oldZ * sinAngle);
        float newZ = (oldZ * cosAngle) + (oldX * sinAngle);
        Vector3f newVec = new Vector3f(newX, 0, newZ);
        cam.setLocation(cam.getLocation().add(newVec.mult(camSpeed)));
    }
    else if(binding.equals("CamRight")){
        float oldX = cam.getDirection().getX();
        float oldZ = cam.getDirection().getZ();
        float cosAngle = (float)Math.cos(Math.toRadians(90));
        float sinAngle = (float)Math.sin(Math.toRadians(90));
        float newX = (oldX * cosAngle) - (oldZ * sinAngle);
        float newZ = (oldZ * cosAngle) + (oldX * sinAngle);
        Vector3f newVec = new Vector3f(newX, 0, newZ);
        cam.setLocation(cam.getLocation().add(newVec.mult((camSpeed))));
    }
    
    if(binding.equals("UpRot")){
        cam.setLocation(cam.getLocation().add(new Vector3f(0, 0.01f, 0)));
//        Quaternion q = cam.getRotation().mult(new Quaternion().fromAngles((float)Math.toRadians(-0.1f), 0, 0));
//        cam.setRotation(q);
    //    cam.setAxes(q);
    }
    else if(binding.equals("DownRot")){
        cam.setLocation(cam.getLocation().subtract(new Vector3f(0, 0.01f, 0)));
//        Quaternion q = cam.getRotation().mult(new Quaternion().fromAngles((float)Math.toRadians(0.1f), 0, 0));
//        cam.setRotation(q);
   //     cam.setAxes(q);
    //    cam.
    }
    
    
    if(binding.equals("LeftRot")){
        Quaternion q = cam.getRotation().mult(new Quaternion().fromAngles(0, (float)Math.toRadians(0.1f), 0));
        cam.setRotation(q);
    
    }
    else if(binding.equals("RightRot")){
        Quaternion q = cam.getRotation().mult(new Quaternion().fromAngles(0, (float)Math.toRadians(-0.1f), 0));
    //    cam.setRotation(q);
        cam.setRotation(q);
    }
        
  }
  
  
   private void createBall(){
       ball = objectCreator.createBall(new Vector3f(-5f, 10, -14f), rootNode);
   } 

   private void showViewPoint(){
       
        Character c = null;
        ArrayList<Character> characters = SceneCharacterManager.getCharacterArray();
        
        if(cameras[1] == true){
            for(int i = 0; i < characters.size(); i++){
                if(characters.get(i) instanceof Player){
                    c = characters.get(i);
                    break;
                }
            }
            
            Player player = (Player)c;
            
            float diff =Conversions.minDistanceBetweenAngles(player.getCameraRot(), player.getFacingDirection());          
            if(diff > 30){
                player.setCameraRot(player.getFacingDirection());
            }
            
            Quaternion q = new Quaternion().fromAngleAxis((float)Math.toRadians(player.getCameraRot()), new Vector3f(0,1,0));
            float a = Conversions.findOppositeAngle(player.getCameraRot());
            
            Vector3f vec = Conversions.degreesToNormalizedCoordinates(a).mult(20);
            cam.setLocation(player.getPosition().add(vec).add(0, 3, 0));
            cam.setRotation(q);
            
        }
        else if(cameras[2] == true){
            for(int i = 0; i < characters.size(); i++){
                if(characters.get(i) instanceof NonUserPlayer){
                    c = characters.get(i);
                    break;
                }
            }
                        
            NonUserPlayer player = (NonUserPlayer)c;
            
            float diff =Conversions.minDistanceBetweenAngles(player.getCameraRot(), player.getFacingDirection());          
            if(diff > 30){
                player.setCameraRot(player.getFacingDirection());
            }           
            
            Quaternion q = new Quaternion().fromAngleAxis((float)Math.toRadians(player.getCameraRot()), new Vector3f(0,1,0));
            float a = Conversions.findOppositeAngle(player.getCameraRot());
            
            Vector3f vec = Conversions.degreesToNormalizedCoordinates(a).mult(20);
            cam.setLocation(player.getPosition().add(vec).add(0, 3, 0));
            cam.setRotation(q);
        }
        
   }

   private void topCamera(){
       cam.setLocation(new Vector3f(0, 100f, -10));
       cam.setRotation(new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0));
   }


   public void collision(PhysicsCollisionEvent event){
       
    }
   
  @Override
  public void simpleUpdate(float tpf) {
      
              
        //createLoading();
            if(isRunning){  
                
                boolean isSame = true;
                
                 if(currentReadLine == 0){
                     this.doPlayBack(0);
                     timeSinceLast = System.currentTimeMillis() - offset;
                 }
                 
                 if(!isPaused){                      
                    if(!isRewind && currentReadLine + 1 < data.size()){
                        int i = this.getNextFrame(currentReadLine);
                        
                        if(i != currentReadLine){
                            isSame = false;
                        }
                        this.doPlayBack(i);
                        currentReadLine = i; 

                    }
                    else if(isRewind && currentReadLine - 1 >= 0){
                        int i = this.getPreviousFrame(currentReadLine);
                        this.doPlayBack(i);
                        currentReadLine = i;
                    }
                 }
                 
                if(!isSame){ 
                    timeSinceLast = System.currentTimeMillis() - offset;
                }
                this.showViewPoint();
         //       this.displayInfo();
          }
  }
  
  private int getNextFrame(int index){
      
      String s = data.get(index + 1);
      if(s.startsWith("NEW")){
          return index + 1;
      }
      else{
          long curFrameTime = 1000;
          
          if(!data.get(index).startsWith("NEW")){
              String a = data.get(index);
              String[] t = a.split("\\$", 2);
              curFrameTime = Long.parseLong(t[0]);
          }
          
          long curTime = System.currentTimeMillis();
          long elapsedTime = curTime - timeSinceLast;
          int closest = 1000;
          
          for(int i = 0; i < data.size() - index; i++){
                String dataLine = data.get(index + i);
                String[] t = dataLine.split("\\$", 2);
                
                if(t[0].startsWith("N")){
                   return index + 1;
                }
                
                long nextFrameTime = Integer.parseInt(t[0]);
                long diff = nextFrameTime - curFrameTime;                
        //        System.out.println(i + " " + elapsedTime + " " + diff);
                
                int j = (int)Math.abs(elapsedTime - diff);
                 
                if(j > closest){
                    return index + i - 1;
                }
                else{
                    offset = (int)(elapsedTime - diff);
                    closest = j;
                }
          }
          
          return index;
      }
  }
  
  private int getPreviousFrame(int index){
      
      String s = data.get(index - 1);
      if(s.startsWith("NEW")){
          return index - 1;
      }
      else{
          long curFrameTime = 1000;
          
          if(!data.get(index).startsWith("NEW")){
              String a = data.get(index);
              String[] t = a.split("\\$", 2);
              curFrameTime = Long.parseLong(t[0]);
          }
          
          long curTime = System.currentTimeMillis();
          long elapsedTime = curTime - timeSinceLast;
          
          for(int i = 1; i < data.size() + index; i++){
                String dataLine = data.get(index - i);
                String[] t = dataLine.split("\\$", 2);
                long previousFrameTime = Integer.parseInt(t[0]);
                long diff =  curFrameTime - previousFrameTime;
                if(elapsedTime < diff){//can move
                    return index - i;
                }
          }
          
          return index - 1;
      }
  }

  
  public static float getFrameRate(){
     // System.out.println
      return Math.min(timePerFrame, 0.05f);
  }
  
  
  public void loadLogFile(){
      try{
        reader = new BufferedReader(new FileReader(logFileName));
        String s;
        while((s = reader.readLine()) != null){
            data.add(s);
        }
      }
      catch(IOException e){
          System.out.println(e);
      }
  }
  
  public void pauseGame(){
      isRunning = !isRunning;
  }
  
  public void doPlayBack(int frameNumber){
      
      String s = data.get(frameNumber);
            
      if(s != null && !s.startsWith("*")){
          
        if(s.startsWith("NEW")){
            this.createCharacter(s);
         //   currentReadLine++;
        }  
        else{  
            String[] linedata = s.split("\\$"); 
            
            if(linedata[1].startsWith("B")){//first data is ball position
               String vec = linedata[1].substring(1, linedata[1].length());
               ball.setBallPosition(NetworkMessagingProcessor.stringToVector(vec));
               this.setCharacterInfo(linedata, 2);
          }
            else if(linedata[1].startsWith("U")){//data is uttrerance
                this.playUtterance(linedata);
            }
        }
      }
      
  }
  
  private void playUtterance(String[] linedata){
      int charID = Integer.parseInt(linedata[2]);
      String utterance = linedata[3];
      BasketballCharacter bc = (BasketballCharacter)SceneCharacterManager.getCharacterByID(charID);
      bc.playUtterance(utterance);
              
    }
  
  private void setCharacterInfo(String[] data, int startIndex){
            
      String type = data[startIndex];
      String str = type.replaceAll("[^\\d.]", "");

      int id = Integer.parseInt(str);
      Vector3f newPos;
      float fd;
      int actionState;
      int walkingState;
      float speed;
      
      if(SceneCharacterManager.getCharacterByID(id) != null){
          BasketballCharacter bc = (BasketballCharacter)SceneCharacterManager.getCharacterByID(id);
          
          newPos = NetworkMessagingProcessor.stringToVector(data[startIndex + 1]);
          bc.setPosition(newPos);
          
          fd = Float.parseFloat(data[startIndex + 2]);
          bc.turnBody(fd);
          
          if(data[startIndex + 3].length() > 5){
                String skelRot = data[startIndex + 3];
                bc.setSkeletonJoints(NetworkMessagingProcessor.parseJointData(skelRot));
        //        System.out.println(data[0] + " " + id + " " + skelRot);
          }
          else{
                actionState = Integer.parseInt(data[startIndex + 3]);
                String s = bc.getArmAnimationName(actionState);
                float animTime = Float.parseFloat(data[startIndex + 5]);
                bc.setAnimationFrame(1, s, animTime);
         //       System.out.println(data[0] + " " + id + " " + s + " " + animTime);
          }
          
          walkingState = Integer.parseInt(data[startIndex + 4]);
          String t = bc.getLegAnimationName(walkingState);
          float animTime = Float.parseFloat(data[startIndex + 6]);
          bc.setAnimationFrame(2, t, animTime); 
          if(id == 0){
              System.out.println(data[0]);
          }
          
                    
          if(type.contains("P") && bc.getCameraInitialised()){
              bc.initialiseCamera();
              bc.setCameraRot(fd);
          }
          
          if(type.contains("N") && bc.getCameraInitialised()){
              bc.initialiseCamera();
              bc.setCameraRot(fd);
          }                 
      }
      
      if(startIndex + 7 < data.length){
          this.setCharacterInfo(data, startIndex + 7);
      }
      
    }
   
  
//  public void setPlayerInfo(String[] data, int startIndex){
//      
//      int a = data[startIndex].indexOf("P");
//      int id = Integer.parseInt(data[startIndex].substring(0, a));
//      Vector3f newPos;
//      float fd;
//      int actionState;
//      int walkingState;
//      float speed;
//            
//      if(SceneCharacterManager.getCharacterByID(id) != null){
//          Player p = (Player)SceneCharacterManager.getCharacterByID(id);
//         
//          newPos = NetworkMessagingProcessor.stringToVector(data[startIndex + 1]);
//          p.setPosition(newPos);
//          
//          fd = Float.parseFloat(data[startIndex + 2]);
//          p.setCharacterRotationRadians((float)Math.toRadians(fd));
//          p.setHeadRotationRadians((float)Math.toRadians(fd));
//          p.turnBody();
//          playerFD = fd;
//          playerPos = newPos;
//          
//          if(!p.getCameraInitialised()){
//              p.initialiseCamera();
//              p.setCameraRot(fd);
//          }
//          
//          try{
//            if(data[startIndex + 3].length() > 5){
//                String skelRot = data[startIndex + 3];
//                p.setSkeletonJoints(NetworkMessagingProcessor.parseJointData(skelRot));
//            }
//            else{
//                actionState = Integer.parseInt(data[startIndex + 3]);
//                String s = p.getModel().getArmAnimationName(actionState);
//                float animTime = Float.parseFloat(data[startIndex + 5]);
//                p.getModel().setFrame(1, s, animTime);
//            }
//            
//            walkingState = Integer.parseInt(data[startIndex + 4]);
//            String t = p.getModel().getLegAnimationName(walkingState);
//            float animTime = Float.parseFloat(data[startIndex + 6]);
//            p.getModel().setFrame(2, t, animTime);
//            
//            if(!isPaused){
//                if(walkingState == 3){
//                    p.setCameraRot(p.getCameraRot() + (0.33f * p.getTurnSpeed() * 0.25f));
//                }
//                else if(walkingState == 4){
//                    p.setCameraRot(p.getCameraRot() - (0.33f * p.getTurnSpeed() * 0.25f));
//                }
//            }
//            
////            Quaternion lShoulder = p.getModel().getQuatBoneRotation("leftShoulderJoint");
////            Quaternion rShoulder = p.getModel().getQuatBoneRotation("rightShoulderJoint");
////            Quaternion lElbow = p.getModel().getQuatBoneRotation("leftElbowJoint");
////            Quaternion rElbow = p.getModel().getQuatBoneRotation("rightElbowJoint");
////            
////         //   System.out.println(lShoulder);
////            String s1 = (lShoulder.getX() + "," + lShoulder.getY() + "," + lShoulder.getZ() + "," + lShoulder.getW() + ",");
////            String s2 = (rShoulder.getX() + "," + rShoulder.getY() + "," + rShoulder.getZ() + "," + rShoulder.getW() + ",");
////            String s3 = (lElbow.getX() + "," + lElbow.getY() + "," + lElbow.getZ() + "," + lElbow.getW() + ",");
////            String s4 = (rElbow.getX() + "," + rElbow.getY() + "," + rElbow.getZ() + "," + rElbow.getW());
////            
////            String full = currentReadLine - 4 + "," + s1 + s2 + s3 + s4;
//            
//     //       Log.write(writePFile.getPath(), full);
//            
//          }
//          catch(NumberFormatException e){
//              String skelRot = data[startIndex + 3];
//              p.setSkeletonJoints(NetworkMessagingProcessor.parseJointData(skelRot));
//          }
//      }  
//  }
  
//  private void setAgentInfo(String[] data, int startIndex){
//      
//      int a = data[startIndex].indexOf("A");
//      int id = Integer.parseInt(data[startIndex].substring(0, a));
//      Vector3f newPos;
//      float fd;
//      int actionState;
//      int walkingState;
//      float speed;
//            
//      if(SceneCharacterManager.getCharacterByID(id) != null){
//          BasketballAgent ba = (BasketballAgent)SceneCharacterManager.getCharacterByID(id);
//         
//          newPos = NetworkMessagingProcessor.stringToVector(data[startIndex + 1]);
//          ba.setPosition(newPos);
//          
//          fd = Float.parseFloat(data[startIndex + 2]);
//          ba.abo.setFacingDirection(fd, fd);  
//          
//          actionState = Integer.parseInt(data[startIndex + 3]);
//          String s = ba.getModel().getArmAnimationName(actionState);
//          float animTime = Float.parseFloat(data[startIndex + 5]);
//          ba.getModel().setFrame(1, s, animTime);
//          
//          walkingState = Integer.parseInt(data[startIndex + 4]);
//          String t = ba.getModel().getLegAnimationName(walkingState);
//          animTime = Float.parseFloat(data[startIndex + 6]);
//          ba.getModel().setFrame(2, t, animTime);  
//                 
//      }  
//  }
  
//  private void setNUPInfo(String[] data, int startIndex){
//      
//      int a = data[startIndex].indexOf("N");
//      int id = Integer.parseInt(data[startIndex].substring(0, a));
//      Vector3f newPos;
//      float fd;
//      int actionState;
//      int walkingState;
//      float speed;
//      
//      if(SceneCharacterManager.getCharacterByID(id) != null){
//          NonUserPlayer nup = (NonUserPlayer)SceneCharacterManager.getCharacterByID(id);
//         
//          newPos = NetworkMessagingProcessor.stringToVector(data[startIndex + 1]);
//          nup.setPosition(newPos);
//          
//          fd = Float.parseFloat(data[startIndex + 2]);
//          nup.setFacingDirection(fd, fd); 
//          nupFD = fd;
//          nupPos = newPos;          
//          
//          if(!nup.getCameraInitialised()){
//              nup.initialiseCamera();
//              nup.setCameraRot(fd);
//            //  System.out.println(nup.getCameraRot());
//          }
//          
//          try{
//            if(data[startIndex + 3].length() > 5){
//                String skelRot = data[startIndex + 3];
//                nup.setSkeletonJoints(NetworkMessagingProcessor.parseJointData(skelRot));
//            }
//            else{
//                actionState = Integer.parseInt(data[startIndex + 3]);
//                String s = nup.getModel().getArmAnimationName(actionState);
//                float animTime = Float.parseFloat(data[startIndex + 5]);
//                nup.getModel().setFrame(1, s, animTime);
//            }
//            
//            walkingState = Integer.parseInt(data[startIndex + 4]);
//            String t = nup.getModel().getLegAnimationName(walkingState);
//            float animTime = Float.parseFloat(data[startIndex + 6]);
//            nup.getModel().setFrame(2, t, animTime);
//            
//            if(!isPaused){
//                if(walkingState == 3){
//                    nup.setCameraRot(nup.getCameraRot() + (0.33f * 2 * 0.25f));
//                }
//                else if(walkingState == 4){
//                    nup.setCameraRot(nup.getCameraRot() - (0.33f * 2 * 0.25f));
//                }
//            }
//            
////            Quaternion lShoulder = nup.getModel().getQuatBoneRotation("leftShoulderJoint");
////            Quaternion rShoulder = nup.getModel().getQuatBoneRotation("rightShoulderJoint");
////            Quaternion lElbow = nup.getModel().getQuatBoneRotation("leftElbowJoint");
////            Quaternion rElbow = nup.getModel().getQuatBoneRotation("rightElbowJoint");
////            
////         //   System.out.println(lShoulder);
////            String s1 = (lShoulder.getX() + "," + lShoulder.getY() + "," + lShoulder.getZ() + "," + lShoulder.getW() + ",");
////            String s2 = (rShoulder.getX() + "," + rShoulder.getY() + "," + rShoulder.getZ() + "," + rShoulder.getW() + ",");
////            String s3 = (lElbow.getX() + "," + lElbow.getY() + "," + lElbow.getZ() + "," + lElbow.getW() + ",");
////            String s4 = (rElbow.getX() + "," + rElbow.getY() + "," + rElbow.getZ() + "," + rElbow.getW());
////            
////            String full = currentReadLine - 4 + "," + s1 + s2 + s3 + s4;
//                            
//
//       //     System.out.println(playerPos.distance(nup.getPosition()));
//         //   Log.write(distances.getPath(), currentReadLine + "," + playerPos.distance(nup.getPosition()) + "");
//          }
//          catch(NumberFormatException e){
//              System.out.println(e);
//              String skelRot = data[startIndex + 3];
//              nup.setSkeletonJoints(NetworkMessagingProcessor.parseJointData(skelRot));
//          }
//      }  
 // }
  
  private void createCharacter(String s){
      String modelType;
      String characterType;
      int id;
      Vector3f startPos;
      
      String[] data = s.split("!");
      characterType = data[0].replace("NEW", "");
      modelType = data[1];
      id = Integer.parseInt(data[2]);
      
      boolean isPresent = false;
      
      for(int i = 0; i< SceneCharacterManager.getCharacterArray().size(); i++){
          if(SceneCharacterManager.getCharacterArray().get(i).getID() == id){
              isPresent = true;
              break;
          }
      }
          
      
      String vec = data[data.length -1];
      startPos = NetworkMessagingProcessor.stringToVector(vec);
      
      if(!isPresent){
                
          if(characterType.toLowerCase().contains("agent")){//agengt type     
            BasketballAgent ba = characterCreator.addAgentCharacter(id, modelType, startPos, 0.48f);
            characterArray.add(ba);
          }
          else if(characterType.startsWith("P")){ //player type
              Player p = characterCreator.addPlayerCharacter(id, modelType, startPos, 0.48f);
              characterArray.add(p);
          }
          else if(characterType.startsWith("N")){  //NUP type
              NonUserPlayer nup = characterCreator.addNonUserPlayerCharacter(id, modelType, startPos, 0.48f);
              characterArray.add(nup);
          }   
          
      }
  }
  
  private void displayInfo(){
  
      guiNode.detachAllChildren();
      String text = "";
      
      if(cameras[0] == true){
          text = "Free camera\n";
      }
      else if(cameras[1] == true){
          text = "Tracking player 0\n";
      }
      else if(cameras[2] == true){
          text = "Tracking player 1\n";
      }
      
      if(isPaused){
          text = text.concat("PAUSED");
      }
      else if(isRewind){
          text = text.concat("REWIND:" + playSpeed);
      }
      else{
          text = text.concat("PLAY: " + playSpeed);
      }
      
      BitmapText instructionText = new BitmapText(guiFont, false);
      instructionText.setSize(guiFont.getCharSet().getRenderedSize());
      instructionText.setText(text);
      instructionText.setLocalTranslation(settings.getWidth()/2 - instructionText.getLineWidth()/2, settings.getHeight() * 0.9f, 0);
      instructionText.setColor(ColorRGBA.Red);
      instructionText.setName("instructions");
      instructionText.setBox(new Rectangle(0, 0, instructionText.getLineWidth(), instructionText.getLineHeight()));
      instructionText.setAlignment(BitmapFont.Align.Center);
      guiNode.attachChild(instructionText);
      
      BitmapText frameText = new BitmapText(guiFont, false);
      frameText.setSize(guiFont.getCharSet().getRenderedSize());
      frameText.setText(currentReadLine + "");
      frameText.setLocalTranslation(settings.getWidth() - frameText.getLineWidth(), settings.getHeight() * 0.9f, 0);
      frameText.setColor(ColorRGBA.Red);
      frameText.setName("frame");
      frameText.setBox(new Rectangle(0, 0, frameText.getLineWidth(), frameText.getLineHeight()));
      frameText.setAlignment(BitmapFont.Align.Center);
      guiNode.attachChild(frameText);
  }
  
  private void createLogFile(){
      writePFile = new File("player.txt");
      writeNUPFile = new File("nup.txt");
      JAFile = new File("ja.txt");
      distances = new File("distances.txt");
  }
  
  private void calculateMutualGaze(){
            
      if(playerPos != null && nupPos != null){
      
        float playerLookingNUP = Conversions.originToTargetAngle(playerPos, nupPos);
        float pDiff = Conversions.minDistanceBetweenAngles(playerLookingNUP, playerFD);

        float NUPLookingPlayer = Math.abs(Conversions.originToTargetAngle(nupPos, playerPos));
        float nupDiff = Math.abs(Conversions.minDistanceBetweenAngles(NUPLookingPlayer, nupFD));

         Log.write(JAFile.getPath(), currentReadLine + "," + pDiff + "," + nupDiff);

      }
  }
  
  
  
}



