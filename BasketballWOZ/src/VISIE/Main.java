package VISIE;

import Basketball.Ball;
import Basketball.GameStateManagement;
import VISIE.characters.BasketballTeam;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.CharacterCreator;
import VISIE.scenemanager.CameraView;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import VISIE.scenemanager.Shop;
import VISIE.network.KinectClient;
import VISIE.network.NetworkConnections;
import VISIE.network.Client;
import VISIE.network.SensorClient;
import VISIE.network.NetworkMessagingProcessor;
import VISIE.network.UDPBroadcastClient;
import VISIE.models.ModelLoader;
import VISIE.characters.Player;
import VISIE.characters.Character;
import VISIE.characters.KinectPlayer;
import VISIE.characters.Team;
import VISIE.mathfunctions.StringProcessor;
import VISIE.navigation.UserNavigation;
import VISIE.scenemanager.ObjectCreator;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.animation.LoopMode;
import com.jme3.app.SettingsDialog;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import java.util.List;
import java.util.ArrayList;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Matrix3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.system.AppSettings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.lwjgl.opengl.Display;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener {

  private BulletAppState bulletAppState;
  private Spatial sceneModel;
  private RigidBodyControl landscape;
  private CharacterControl playerNode;
  private Geometry target;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false;
  private float rot;
  private Player p;
  private ArrayList<Character> characterArray = new ArrayList<Character>();
  private ArrayList<String> charactersToAdd = new ArrayList<String>();
  private ArrayList<Integer> charactersToRemove = new ArrayList<Integer>();
  private int numberOfCharacters = 30;
  private int groupNumber = 0;
  private boolean isRunning;
  private boolean ATypeCamera = false;
  private boolean BTypeCamera = false;
  private boolean topCamera = false;


  //displaySettings
  private static int displayHeight = 1386;
  private static int displayWidth = 768;
  private static int numberOfDisplays = 6;
  private int mainScreen;
  private Camera[] cameraArray;
  private float cameraAngle;
  private float angleOffset;
  private CameraView camView;
  private int camState = 0;
  int collTime = 0;


 //I/O operations
  private Thread writeThread;
  String path = "test.txt";

  private UserNavigation userNavigation;


  static boolean isImmersive = false;
  static boolean hasFixedOrientation = false;

  float aspectRatio = 0.1f;

  Client mainClient;
  UDPBroadcastClient broadcastClient;
  SensorClient sensorClient;
  KinectClient kinectClient;

  boolean isServer = false;

  boolean charactersLoaded = false;
  
  boolean suspendFlag;
  
  private SceneCreator sceneCreator;
  private ModelLoader modelLoader;
  private CharacterCreator characterCreator;
  private SceneCharacterManager sceneCharacterManager;
  private ObjectCreator objectCreator;
  private Ball ball;
  
  private String playerModelType = "D";
  private  float cameraRot;
  
  NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay();
  private Nifty nifty;
  private StartInterface startInterface;
  private InitialSettings initialSettings = new InitialSettings();
  
  private boolean startScreenCompleted = false;
  private static int gameState = 0;
  private GameStateManagement gsm;
  private ArrayList<Team> teams = new ArrayList<Team>();


 public static void main(String[] args) {
    Main gameApp = new Main();
    AppSettings settings = new AppSettings(true);
    settings.setTitle("VIRTUAL BASKETBALL");
    gameApp.setShowSettings(false);
    gameApp.setDisplayStatView(false);
    gameApp.setDisplayFps(false);
    gameApp.setSettings(settings);
    gameApp.setPauseOnLostFocus(false);
    gameApp.start();

//    if(isImmersive){
//        settings.setResolution(displayWidth * numberOfDisplays, displayHeight);
//        app.setShowSettings(false);
//        app.setSettings(settings);
//        Display.setLocation(-displayWidth, 0);
//    }
//        app.start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("").setLevel(Level.WARNING);
        this.executeStartScreen();
//        bulletAppState = new BulletAppState();
//        stateManager.attach(bulletAppState);
//        sceneCreator = new SceneCreator(rootNode, bulletAppState);
//        modelLoader = new ModelLoader(assetManager);
//        characterCreator = new CharacterCreator(rootNode, bulletAppState);  
//        sceneCharacterManager = new SceneCharacterManager(this, characterArray, characterCreator, rootNode, bulletAppState);
//        camView = new CameraView(cam, isImmersive, hasFixedOrientation);   
//        userNavigation = new UserNavigation(p, false, cam);
//        objectCreator = new ObjectCreator(rootNode, bulletAppState, this);
//        
//        String localHostAddress = "";
//        try{
//            localHostAddress =  InetAddress.getLocalHost().getHostName();
//            System.out.println(localHostAddress);
//        }
//        catch(Exception e){ 
//        }
//
//        kinectClient = (KinectClient)NetworkConnections.createClient(4, 1240, this, localHostAddress);
//        
//        
//        
//        mainClient = NetworkConnections.createClient(1, 1235, this, "10.229.54.66");
//
//        isRunning = true;
//        broadcastClient = NetworkConnections.createUDPBroadcastClient(1237, this, "10.229.54.66");
//        sensorClient = (SensorClient)NetworkConnections.createClient(3, 1234, this, "10.229.54.177");
//       
    }
    
    private void loadGame(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        sceneCreator = new SceneCreator(rootNode, bulletAppState);
        modelLoader = new ModelLoader(assetManager);
        characterCreator = new CharacterCreator(rootNode, bulletAppState);  
        sceneCharacterManager = new SceneCharacterManager(this, characterArray, characterCreator, rootNode, bulletAppState);
        camView = new CameraView(cam, isImmersive, hasFixedOrientation);   
        userNavigation = new UserNavigation(p, false, cam);
        objectCreator = new ObjectCreator(rootNode, bulletAppState, this);
    }
    
    
    public void finishLoading(InitialSettings i){
      
      initialSettings = i;
      startScreenCompleted = true;   
      
      mainClient = NetworkConnections.createClient(1, initialSettings.getTCPPort(), this, initialSettings.getServerAddress());
      broadcastClient = NetworkConnections.createUDPBroadcastClient(initialSettings.getUDPPort(), this, initialSettings.getServerAddress());

      if(initialSettings.getKinect()){
            kinectClient = (KinectClient)NetworkConnections.createClient(4, initialSettings.getKinectPort(), this, initialSettings.getKinectAddress());
      }
      if(initialSettings.getPressureSensor()){
          sensorClient = (SensorClient)NetworkConnections.createClient(3, initialSettings.getPressurePort(), this, initialSettings.getPressureAddress());
      }
      
      this.loadGame();      
                    
      guiViewPort.removeProcessor(niftyDisplay);   
      niftyDisplay.getNifty().exit();
      
       if(initialSettings.getImmersive()){
          isImmersive = true;
          camView.setupImmersiveCameras(renderManager, rootNode, numberOfDisplays, displayHeight, displayWidth);
          this.changeResolution(initialSettings.getResolution()[0] * initialSettings.getNumDisplays(), initialSettings.getResolution()[1]);
          this.enqueue(changeCameraCallable);
          Display.setLocation(-initialSettings.getResolution()[0] * initialSettings.getActivityScreen(), 0);
      }
      else{     
         this.changeResolution(initialSettings.getResolution()[0], initialSettings.getResolution()[1]);
      }
       
       isRunning = true;
       camState = 2;
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
    
    
   private void executeStartScreen(){
      
    niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    nifty = niftyDisplay.getNifty();
    Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
    Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
    startInterface = new StartInterface(this);
    nifty.fromXml("Interface/StartScreen.xml", "start", startInterface);
    guiViewPort.addProcessor(niftyDisplay);
    flyCam.setDragToRotate(true); 

  }
    

    @Override
    public void simpleUpdate(float tpf) {
        if(isRunning){
            if(sceneCreator.getPreLoadFlag()){
                viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
                if(isImmersive){
//                    camView.setupImmersiveCameras(renderManager, rootNode, numberOfDisplays, displayHeight, displayWidth);
                }
                this.loadBlack();
                this.setupKeys();
                sceneCreator.loadScene(assetManager, rootNode);
                sceneCreator.addLights(rootNode);
                sceneCreator.attachObjects(rootNode);
                sceneCreator.removePreLoadFlag();
                mainClient.confirmToServer();
            }
            
            sceneCharacterManager.updateSceneObjects();
            
            if(objectCreator.updateBallObject() && p != null){
                p.setBall(SceneObjectManager.getBall());
            }
            
            if(mainClient.charactersLoaded()){
                
                if(!viewPort.isEnabled()){
                    this.enableViewPorts();
                }
                
//                if(p.getTeamID() == -1 && broadcastClient != null){
//                    broadcastClient.requestTeamID(p.getID());
//                }
                
                if(gsm == null){
                    guiFont = assetManager.loadFont("Interface/Fonts/Moire.fnt");
                    BitmapFont JFont = assetManager.loadFont("Interface/Fonts/jjj.fnt");
                    gsm = new GameStateManagement(SceneObjectManager.getBall(), p, guiFont, JFont, initialSettings.getResolution());
                }
                this.playerMovement(tpf);
                this.updateAndSendPlayerSkeleton();
                this.sendPlayerPosition();
                this.askForBallPosition();
                NetworkMessagingProcessor.parseUpdateFromServer(broadcastClient.getInfoString(), characterArray, p.getID());
                NetworkMessagingProcessor.parseServerJointData(broadcastClient.getJointInfoString(), characterArray, p.getID());
 
                int[] stateInfo = NetworkMessagingProcessor.parseBallAndStateMessage(broadcastClient.getBallPosString());  
                p.cleanUpActionStates();
                
                if(gsm != null){
                    gsm.displayInstructions(stateInfo[0], guiNode, stateInfo[1]);
                    gsm.setScoreText(guiNode, stateInfo[2], stateInfo[3]);
                }
            }    
      }
    }
    
    private void loadBlack(){
        viewPort.setEnabled(false);
    }
    
    private void enableViewPorts(){
        viewPort.setEnabled(true);
    }
    
    
    
    public void updateTeamInfo(int[] info){
        //waits until player has been created
        while(p == null){
            try{
                Thread.sleep(100);
            }
            catch(InterruptedException e){}
        }
        
        Team t = null;
        
        for(int i = 0; i < teams.size(); i++){
            if(teams.get(i).getTeamID() == info[0]){
                t = teams.get(i);
                break;
            }
        }
        
        if(t == null){
            t= new BasketballTeam(info[0]);
            teams.add(t);
            t.addToTeam(SceneCharacterManager.getCharacterByID(info[1]));
        }
        else{
            t.addToTeam(SceneCharacterManager.getCharacterByID(info[1]));
        }
        
        if(info[1] == p.getID()){
            p.setTeam((BasketballTeam)t);
        }
    }
    
    public void doGameStateManagement(){
        
    }
    
    public void suspend(){
        suspendFlag = true;
    }
    
    public synchronized void resume(){
        suspendFlag = false;
        notifyAll();
    }
    
    public void loadNetworkEnvironment(String mapDirectory){
        /** Set up Physics */
        sceneCreator.flagSceneForCreation(mapDirectory);
  }
    
   private void updateAndSendPlayerSkeleton(){
      NetworkMessagingProcessor.parseKinectJointMessage(p);
      broadcastClient.sendSkeletonInformation(NetworkMessagingProcessor.createPlayerSkeletonInfoMessage(p));
      p.updateAndSendSkeleton(kinectClient, broadcastClient);
      
//      if(p instanceof VISIE.characters.KinectPlayer){
//                    
//          KinectPlayer kp = (KinectPlayer)p;
//
//          if(kinectClient.getCurrentGesture().equals("shoot") && p.canShoot()){
//             kp.playKinectGesture("Shoot");
//          }
//          else if(kinectClient.getCurrentGesture().equals("dribbleRight")){
//              kp.playKinectGesture("Dribble Right");
//          }
//          else if(kinectClient.getCurrentGesture().equals("pass")){
//              
//              if(p.getHandPosition(1).y - p.getElbowPosition(1).y > 1f){
//                  kp.playKinectGesture("");
//              }
//              else{
//                  kp.playKinectGesture("Pass");
//              }
//          }
//          
//          kp.updateGestures();
//          String gest = kp.getKinectGesture();
//          if(!gest.isEmpty()){
//            broadcastClient.sendMessage("GEST" + p.getID() + "," + gest);
//          }
//          else{
//            broadcastClient.sendMessage("GEST" + p.getID() + ",X");
//          }
//      }
//      else{
//          if(p.isInPossession()){
//              p.doDribblingAnimation();
//          }
//          else{
//             p.doNonPossessionGesture();
//          }
//      }
  }
   
   private void sendPlayerPosition(){
      broadcastClient.sendCurrentPosition(NetworkMessagingProcessor.createPlayerInformationMessage(p));
   }
   
   private void askForBallPosition(){
       broadcastClient.askForBall();
   }

  private void setupKeys() {
    inputManager.addMapping("Lefts",  new KeyTrigger(KeyInput.KEY_LEFT));
    inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
    inputManager.addMapping("Ups",    new KeyTrigger(KeyInput.KEY_UP));
    inputManager.addMapping("Downs",  new KeyTrigger(KeyInput.KEY_DOWN));
  //  inputManager.addMapping("Jumps",  new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("A Type Camera", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("B Type Camera", new KeyTrigger(KeyInput.KEY_B));
    inputManager.addMapping("Top Camera", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addListener(this, "Lefts");
    inputManager.addListener(this, "Rights");
    inputManager.addListener(this, "Ups");
    inputManager.addListener(this, "Downs");
//    inputManager.addListener(this, "Jumps");
    inputManager.addMapping("CamState0", new KeyTrigger(KeyInput.KEY_0));
//    inputManager.addMapping("CamState1", new KeyTrigger(KeyInput.KEY_1));
//    inputManager.addMapping("CamState2", new KeyTrigger(KeyInput.KEY_2));
    inputManager.addListener(this, "A Type Camera");
    inputManager.addListener(this, "B Type Camera");
    inputManager.addListener(this, "Top Camera");
    inputManager.addListener(this, "CamState0");
//    inputManager.addListener(this, "CamState1");
//    inputManager.addListener(this, "CamState2");
    
    //player animations

    inputManager.addMapping("Shoot Ball", new KeyTrigger(KeyInput.KEY_LSHIFT));
    inputManager.addListener(this, "Shoot Ball");
    inputManager.addMapping("Propose Pass", new KeyTrigger(KeyInput.KEY_C));
    inputManager.addListener(this, "Propose Pass");
    inputManager.addMapping("Pass Ball", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Pass Ball");
    
    
    inputManager.addMapping("Call for pass", new KeyTrigger(KeyInput.KEY_1));
    inputManager.addListener(this, "Call for pass");
    inputManager.addMapping("Accept Pass", new KeyTrigger(KeyInput.KEY_2));
    inputManager.addListener(this, "Accept Pass");
    
    inputManager.addMapping("Block", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addListener(this, "Block");
//    inputManager.addMapping("Point to goal", new KeyTrigger(KeyInput.KEY_V));
//    inputManager.addListener(this, "Point to goal");
     inputManager.addMapping("Celebration", new KeyTrigger(KeyInput.KEY_J));
     inputManager.addListener(this, "Celebration");
     
     inputManager.addMapping("UtteranceAffirmation", new KeyTrigger(KeyInput.KEY_5));
     inputManager.addListener(this, "UtteranceAffirmation");
     
     inputManager.addMapping("UtteranceEncourage", new KeyTrigger(KeyInput.KEY_6));
     inputManager.addListener(this, "UtteranceEncourage");
     
     inputManager.addMapping("UtteranceGreeting", new KeyTrigger(KeyInput.KEY_INSERT));
     inputManager.addListener(this, "UtteranceGreeting");
     
     inputManager.addMapping("UtteranceLikeBBall", new KeyTrigger(KeyInput.KEY_HOME));
     inputManager.addListener(this, "UtteranceLikeBBall");
     
     inputManager.addMapping("UtteranceLetsEnjoy", new KeyTrigger(KeyInput.KEY_PGUP));
     inputManager.addListener(this, "UtteranceLetsEnjoy");
     
     inputManager.addMapping("UtteranceDisappointment", new KeyTrigger(KeyInput.KEY_PGDN));
     inputManager.addListener(this, "UtteranceDisappointment");
     
     inputManager.addMapping("UtteranceYes", new KeyTrigger(KeyInput.KEY_Y));
     inputManager.addListener(this, "UtteranceYes");
     
     inputManager.addMapping("UtteranceThanks", new KeyTrigger(KeyInput.KEY_G));
     inputManager.addListener(this, "UtteranceThanks");
     
     inputManager.addMapping("UtteranceApology", new KeyTrigger(KeyInput.KEY_O));
     inputManager.addListener(this, "UtteranceApology");
     
     inputManager.addMapping("ReadyToPass", new KeyTrigger(KeyInput.KEY_V));
     inputManager.addListener(this, "ReadyToPass");
     
     inputManager.addMapping("UtteranceMark", new KeyTrigger(KeyInput.KEY_E));
     inputManager.addListener(this, "UtteranceMark");
     
     inputManager.addMapping("UtteranceSteal", new KeyTrigger(KeyInput.KEY_R));
     inputManager.addListener(this, "UtteranceSteal");
     
     inputManager.addMapping("UtteranceGoForward", new KeyTrigger(KeyInput.KEY_K));
     inputManager.addListener(this, "UtteranceGoForward");
  }
 
    public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Lefts")) {
        userNavigation.setDirectionKeys(value, 2);}
    else if (binding.equals("Rights")) {
      userNavigation.setDirectionKeys(value, 3);}
    else if (binding.equals("Ups")) {
      userNavigation.setDirectionKeys(value, 0);
    }
    else if (binding.equals("Downs")) {
      userNavigation.setDirectionKeys(value, 1);}
     else if (binding.equals("Jumps")) {
//      playerNode.jump();
    } else if (binding.equals("A Type Camera") && !value) {
      ATypeCamera = !ATypeCamera;
      BTypeCamera = false;
      topCamera = false;
    } else if (binding.equals("B Type Camera") && !value) {
      BTypeCamera = !BTypeCamera;
      ATypeCamera = false;
      topCamera = false;
    }
     else if (binding.equals("Top Camera") && !value) {
      BTypeCamera = false;
      ATypeCamera = false;
      topCamera = !topCamera;
    }
     else if (binding.equals("CamState0")) {
        camState = 0;
    }
    else if (binding.equals("CamState1")) {
        camState = 1;
    }
    else if (binding.equals("CamState2")) {
        camState = 2;
    }
     else if(binding.equals("Aspect Ratio")){
     }
     else if(binding.equals("Propose Pass") && !value && p.isInPossession()){
         p.playAnimation(1, "initiatePass", 1f, LoopMode.DontLoop);
     }
     else if(binding.equals("Shoot Ball") && !value && p.isInPossession() && p.canShoot()){
         p.shootBall();
     }
     else if(binding.equals("Pass Ball") && p.isInPossession()){
         p.playAnimation(1, "pass", 1f, LoopMode.DontLoop);
     }
     else if(binding.equals("Call for pass") && !p.isInPossession() && !value){
         double d = Math.random() * 3;
         if(d < 1){
            p.playAnimation(1, "callForPass", 0.75f, LoopMode.DontLoop);
         }
         else if(d < 2){
            p.playAnimation(1, "callForPass2", 0.75f, LoopMode.DontLoop);
         }
         else{
             p.playAnimation(1, "callForPass3", 0.75f, LoopMode.DontLoop);
         }
         
         this.playMyVoice("callforpass");
     }
     else if(binding.equals("Accept Pass") && !p.isInPossession() && !value){
         
         if(p.getCurrentGesture(1).equals("standingPose")){
              p.playAnimation(1, "receivePass", 0.75f, LoopMode.DontLoop);
         }
         else{
             p.playAnimation(1, "standingPose", 0.75f, LoopMode.DontLoop);
         }
     }
     else if(binding.equals("Block") && !p.isInPossession() && !value){             
        if(p.getCurrentGesture(1).toLowerCase().contains("block")){
             p.playAnimation(1, "standingPose", 0.75f, LoopMode.DontLoop);
        }
        else{
            p.playAnimation(1, "blockLoop", 0.75f, LoopMode.DontLoop);
        }
     }
      else if(binding.equals("Celebration") && !p.isInPossession() && !value){     
            p.playAnimation(1, "celebration", 1f, LoopMode.DontLoop);
            this.playMyVoice("celebration");
     }
    else if(binding.equals("ReadyToPass") && p.isInPossession() && !value){     
            this.playMyVoice("readytopass");
     }
     else if(binding.equals("UtteranceAffirmation") && !value){  
         this.playMyVoice("affirmation");
     }
      else if(binding.equals("UtteranceEncourage") && !value){  
         this.playMyVoice("encourage");
     }
     else if(binding.equals("UtteranceGreeting") && !value){ 
         p.playAnimation(1, "callForPass", 0.75f, LoopMode.DontLoop);
         this.playMyVoice("greeting");
     }
     else if(binding.equals("UtteranceLikeBBall") && !value){  
         this.playMyVoice("likebball");
     }
     else if(binding.equals("UtteranceLetsEnjoy") && !value){  
         this.playMyVoice("letsenjoy");
     }
    else if(binding.equals("UtteranceDisappointment") && !value){  
         this.playMyVoice("disappointment");
     }
     else if(binding.equals("UtteranceYes") && !value){  
         this.playMyVoice("yes");
     }
    else if(binding.equals("UtteranceThanks") && !value){  
         this.playMyVoice("thanks");
     }
    else if(binding.equals("UtteranceApology") && !value){  
         this.playMyVoice("apology");
     }
    else if(binding.equals("UtteranceMark") && !value){  
         this.playMyVoice("mark");
     }
    else if(binding.equals("UtteranceSteal") && !value){  
         this.playMyVoice("nicesteal");
     }
    else if(binding.equals("UtteranceGoForward") && !value){  
         this.playMyVoice("goforward");
     }
  }
    
    public void playMyVoice(String s){
         String utteranceToPlay = p.getUtterance(s); //ensure random play
         p.playUtterance(utteranceToPlay);
         mainClient.sendSoundInfo(utteranceToPlay);
    }

    public void collision(PhysicsCollisionEvent event){

    }
    
    public void flagPlayerForCreation(String str){
        SceneCharacterManager.flagNewPlayer(str);
    }
    
     public void flagBallForCreation(String str){
         objectCreator.flagBallForCreation(StringProcessor.processVector3fString(str));
     }
    
    public void flagNewCharacterForCreation(String messageStr){
        NetworkMessagingProcessor.characterCreationMessageSplitting(messageStr);
    }
    
    public void setPlayer(Player player){
        p = player;
        userNavigation.setPlayer(player);
    }

   private void playerMovement(float tpf){
       
        cameraRot = userNavigation.executeNavigation(isRunning, tpf, sensorClient);

        if(ATypeCamera)
            showViewPoint(1);
        else if(BTypeCamera)
            showViewPoint(2);
        else if(topCamera)
            topCamera();
        else
            showViewPoint(0);
    
   }

   private void showViewPoint(int characterID){
       

       if(!isImmersive){
            camView.showViewPoint(p.getPosition(), (float)Math.toDegrees(cameraRot), camState);
       }
       else{
           
           float f = p.getFacingDirection();
           
            if(p instanceof KinectPlayer){
                KinectPlayer kp = (KinectPlayer)p;
                f = kp.getCameraFacingDirection();
            }

            camView.showImmersiveViewPoint(p.getHeadPosition(), (float)Math.toDegrees(f), camState);

       }
   }

   private void topCamera(){
       cam.setLocation(new Vector3f(0, 100f, -10));
       cam.setRotation(new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0));
   }

   public Player getPlayer(){
        return p;
   }

//   public void flagCharactersForCreation(String s){
//       charactersToAdd.add(s);
//   }

   public void flagCharactersForRemoval(int id){
        charactersToRemove.add(id);
   }
   
   public String getPlayerModelType(){
       return playerModelType;
   }
   
   public boolean isKinect(){
     //  return kinectClient != null;
       return initialSettings.getKinect();
   }
   
   public void cleanUpOnExit(String fileDir){
              
        File f = new File(fileDir);
         if(f.exists()){
             f.deleteOnExit();
         }
   }
   

}
