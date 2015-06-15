/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import Basketball.Ball;
import Basketball.GUITextManager;
import Basketball.GameManager;
import Basketball.GameStateManagement;
import VISIE.AgentLogic.MainLogicThread;
import VISIE.InitialSettings;
import VISIE.Sound.GameSoundInit;
import VISIE.StartInterface;
import VISIE.models.ModelLoader;
import VISIE.network.KinectClient;
import VISIE.network.NetworkConnections;
import VISIE.network.SensorClient;
import VISIE.network.Server;
import VISIE.network.UDPBroadcastServer;
import VISIE.recording.Log;
import VISIE.scenemanager.CharacterCreator;
import VISIE.scenemanager.ObjectCreator;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import VISIE.characters.Character;
import VISIE.characters.Player;
import VISIE.navigation.UserNavigation;
import VISIE.scenemanager.CameraView;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.RenderManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.animation.LoopMode;
import VISIE.characters.KinectPlayer;
import VISIE.scenemanager.SceneObjectManager;
import VISIE.VISIEFileReader;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballTeam;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Team;
import VISIE.mathfunctions.Conversions;
import VISIE.network.NetworkMessagingProcessor;
import VISIE.scenemanager.Court;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.Track;
import com.jme3.asset.TextureKey;
import com.jme3.audio.Listener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.font.BitmapFont;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 *
 * @author Divesh
 */
public abstract class Game implements ActionListener, Runnable, PhysicsCollisionListener, AnimEventListener{
    
    
    protected InitialSettings initialSettings;
    private int gameState = 0;
    private UDPBroadcastServer broadcastServer;
    private Server TCPServer;
    private KinectClient kinectClient;
    private SensorClient sensorClient;
    
    private static boolean isLogging;
    private static String path;
    
    private BulletAppState bulletAppState;
    private AppStateManager stateManager;
    
    private ModelLoader modelLoader;
    protected AssetManager assetManager;
    protected CharacterCreator characterCreator;
    private SceneCreator sceneCreator;
    private ObjectCreator objectCreator;
    protected SceneCharacterManager sceneCharacterManager;
    protected SceneObjectManager sceneObjectManager;
    
    protected int membersPerTeam;
    private static String gameType;
    protected int numberOfTeams;
    protected int playerTeamID;
    protected int numberOfNUPs;
    protected ArrayList<Character> characterArray = new ArrayList<Character>();
    protected ArrayList<Team> teams = new ArrayList<Team>();
    protected MainLogicThread agentThread;
    
    private UserNavigation userNavigation;
    
    
    private CameraView camView;
    private Camera cam;
    private ViewPort viewPort;
    private RenderManager renderManager;
    protected boolean ATypeCamera;
    protected boolean BTypeCamera;
    protected boolean topCamera;
    private int camState;
    private float cameraRot;
    
    protected InputManager inputManager;
    private boolean isRunning;
    
    protected Node rootNode;
    protected Node guiNode;
    
    protected Ball ball;
    protected Player p;
    private String playerModelType = "P";
    
    protected GameStateManagement gsm;
    protected GUITextManager textManager;
    protected boolean loadingComplete = false;
    protected boolean clientsReady = true;
    protected long clientWaitStartTime;
    protected float loadTimeLimit;
    protected static long timeGameStarted;
    
    
    protected abstract void createTeamsAndAgents();
    protected abstract void initializeGlobalSettings();
    protected abstract void customizeEnvironment();
    protected GameSoundInit gameSounds;
    
    protected int maxTime = 1000000;
    protected int timeRemaining = 0;
    protected int timeLimitSeconds = -1;
    protected boolean gameOver = false;
    protected boolean recordBatchFlag = false;
    
    protected Listener listener;
    
    private Spatial tester;
    float rot = 0.1f;
      
    public void finishLoading(){
        
        
        gameType = initialSettings.getGameType(); 
        gameState = 1;
        loadTimeLimit = 5;
        
      if(initialSettings.getMultiPlayer()){
          this.setupNetwork("Server", "", initialSettings.getTCPPort(), initialSettings.getUDPPort()); 
          clientWaitStartTime = System.nanoTime();
          
          while(Conversions.nanoToSecond(System.nanoTime() - clientWaitStartTime) < loadTimeLimit){
              try{
                Thread.sleep(100);
              }
              catch(Exception e){}
              
              //keeps extending time
              if(TCPServer.getNumberOfUsers() == 0){
                  clientWaitStartTime = System.nanoTime();
              }
          }
          clientsReady = false;
          numberOfNUPs = TCPServer.getNumberOfUsers();
      }
      if(initialSettings.getKinect()){
          this.setupNetwork("Kinect", initialSettings.getKinectAddress(), initialSettings.getKinectPort(), 0);
      }
      if(initialSettings.getPressureSensor()){
          this.setupNetwork("Pressure Sensor", initialSettings.getPressureAddress(), initialSettings.getPressurePort(), 0);
      }
                      
      isLogging = initialSettings.getRecording();
       if(isLogging){           
           String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
           path = timeStamp + ".txt";
       }
       
       Log.clearFile("times.txt");
       
        loadingComplete = true;
//         
    } 
    
    public void loopNodes(Node n, String s){
        
      //  for()
        
    }
    
    public void clientsAreReady(){
        clientsReady = true;
    }
    
    public boolean areClientsReady(){
        return clientsReady;
    }
    
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName){
  }

  public void onAnimChange(AnimControl control, AnimChannel channel, String animName){
  }
    
    
    public void loadGame(){
                        
       this.initializeGlobalSettings();
       this.loadGameObjects();          
       this.loadSounds();
            
       BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Moire.fnt");
       BitmapFont JFont = assetManager.loadFont("Interface/Fonts/jjj.fnt");
       
       textManager = new GUITextManager(p, ball, guiFont, JFont, initialSettings.getResolution());
       gsm = new GameStateManagement(ball, teams, textManager);
       
       if(initialSettings.getImmersive()){
           textManager.setMainScreen(initialSettings.getMainScreen());
       }
        textManager.setScoreText(guiNode);
       
       if(initialSettings.getMultiPlayer()){
            this.loadBlack();
       }
       loadingComplete = true;    
       timeGameStarted = System.nanoTime();
       bulletAppState.getPhysicsSpace().addCollisionListener(this);
 //      bulletAppState.getPhysicsSpace().enableDebug(assetManager);

    }
    
    private void loadBlack(){
                
        viewPort.setEnabled(false);
        
        if(initialSettings.getMultiPlayer()){
            camView.blackoutViewPorts();
        }
    }
    
    public void enableViewPorts(){
        
        viewPort.setEnabled(true);
        
        if(initialSettings.getMultiPlayer()){
            camView.enableViewPorts();
        }
    }
    
    public void loadSounds(){
        
       gameSounds = new GameSoundInit(assetManager, rootNode);
        
    }
    
      private void setupNetwork(String network, String address, int port1, int port2){
      
          if(network.equals("Server")){
              broadcastServer = NetworkConnections.createBroadcastServer(port2, this);
              TCPServer = NetworkConnections.createTCPServer(port1, this, broadcastServer);
          }
          else if(network.equals("Kinect")){
              kinectClient = (KinectClient)NetworkConnections.createClient(4, port1, this, address);
          }
          else if(network.equals("Pressure Sensor")){
              sensorClient = (SensorClient)NetworkConnections.createClient(3, port1, this, address);
          }
      
    }
          
      private void loadGameObjects(){
      
//        if(isLogging){
//            Log.clearFile(path);
//            Log.write(path, "*------------------------------------*");
//        }

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        modelLoader  = new ModelLoader(assetManager);
        characterCreator = new CharacterCreator(rootNode, bulletAppState);      
        sceneCreator = new SceneCreator(rootNode, bulletAppState);
        objectCreator = new ObjectCreator(rootNode, bulletAppState);
        sceneCharacterManager = new SceneCharacterManager(this, characterArray, characterCreator, rootNode, bulletAppState);
        camView = new CameraView(cam, initialSettings.getImmersive(), initialSettings.getFixedOrientation());

        this.createEnvironment();
        this.createBall();
        sceneObjectManager = new SceneObjectManager(ball);
        createPlayer();
        createTeamsAndAgents();
        
        if(p != null){
            p.initializePlayerGesture(ball);
        }

        agentThread = new MainLogicThread(characterArray, ball);

        isRunning = true;

        userNavigation = new UserNavigation(p, initialSettings.getFixedOrientation(), cam);
        this.customizeEnvironment();

        gameState = 2; // now in middle of game
  }
      
   private void createEnvironment(){
        /** Set up Physics */
        viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        if(initialSettings.getImmersive()){
              int nod = initialSettings.getNumDisplays();
              int dispH = initialSettings.getResolution()[1];
              int dispW = initialSettings.getResolution()[0];
              float fov = initialSettings.getFOV();
              int ms = initialSettings.getMainScreen();
              camView.setupImmersiveCameras(renderManager, rootNode, nod, dispH, dispW, fov, ms);
        }
        this.setupKeys();

        sceneCreator.addLights(rootNode);
        sceneCreator.loadScene("Scenes/town/main.j3o", assetManager, rootNode);
        sceneCreator.attachObjects(rootNode);
  }
   
   protected void setupKeys() {
      
    inputManager.addMapping("Lefts",  new KeyTrigger(KeyInput.KEY_LEFT));
    inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
    inputManager.addMapping("Ups",    new KeyTrigger(KeyInput.KEY_UP));
    inputManager.addMapping("Downs",  new KeyTrigger(KeyInput.KEY_DOWN));
    inputManager.addMapping("Jumps",  new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
    inputManager.addMapping("A Type Camera", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("B Type Camera", new KeyTrigger(KeyInput.KEY_B));
    inputManager.addMapping("Top Camera", new KeyTrigger(KeyInput.KEY_T));
    inputManager.addMapping("Aspect Ratio", new KeyTrigger(KeyInput.KEY_O));
    inputManager.addMapping("CamState0", new KeyTrigger(KeyInput.KEY_0));
    inputManager.addMapping("CamState1", new KeyTrigger(KeyInput.KEY_1));
    inputManager.addMapping("CamState2", new KeyTrigger(KeyInput.KEY_2));
    inputManager.addMapping("Increase FOV", new KeyTrigger(KeyInput.KEY_HOME));
    inputManager.addMapping("Decrease FOV", new KeyTrigger(KeyInput.KEY_END));
    inputManager.addMapping("Get ball", new KeyTrigger(KeyInput.KEY_B));
    inputManager.addListener(this, "Lefts");
    inputManager.addListener(this, "Rights");
    inputManager.addListener(this, "Ups");
    inputManager.addListener(this, "Downs");
    inputManager.addListener(this, "Jumps");
    inputManager.addListener(this, "Pause");
    inputManager.addListener(this, "A Type Camera");
    inputManager.addListener(this, "B Type Camera");
    inputManager.addListener(this, "Top Camera");
    inputManager.addListener(this, "Aspect Ratio");
    inputManager.addListener(this, "Transaction");
    inputManager.addListener(this, "CamState0");
    inputManager.addListener(this, "CamState1");
    inputManager.addListener(this, "CamState2");
    inputManager.addListener(this, "Increase FOV");
    inputManager.addListener(this, "Decrease FOV");
    inputManager.addListener(this, "Get ball");
    
    //player animations
    inputManager.addMapping("Accept Pass", new KeyTrigger(KeyInput.KEY_H));
    inputManager.addListener(this, "Accept Pass");
    inputManager.addMapping("Shoot Ball", new KeyTrigger(KeyInput.KEY_K));
    inputManager.addListener(this, "Shoot Ball");
    inputManager.addMapping("Propose Pass", new KeyTrigger(KeyInput.KEY_L));
    inputManager.addListener(this, "Propose Pass");
    inputManager.addMapping("Pass Ball", new KeyTrigger(KeyInput.KEY_SEMICOLON));
    inputManager.addListener(this, "Pass Ball");
    inputManager.addMapping("Call for pass", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(this, "Call for pass");
    inputManager.addMapping("Head turn", new KeyTrigger(KeyInput.KEY_V));
    inputManager.addListener(this, "Head turn");
    
    //ball triggers
    inputManager.addMapping("BallTriggerShoot", new KeyTrigger(KeyInput.KEY_X));
    inputManager.addListener(this, "BallTriggerShoot");
    inputManager.addMapping("BallTriggerPass", new KeyTrigger(KeyInput.KEY_Z));
    inputManager.addListener(this, "BallTriggerPass");
    inputManager.addMapping("NewBall", new KeyTrigger(KeyInput.KEY_P));
    inputManager.addListener(this, "NewBall");
    
    //recording data
    inputManager.addMapping("Record batch data", new KeyTrigger(KeyInput.KEY_Q));
    inputManager.addListener(this, "Record batch data");
    
    inputManager.addMapping("Sound", new KeyTrigger(KeyInput.KEY_V));
    inputManager.addListener(this, "Sound");
  }

    /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Lefts")) {
        userNavigation.setDirectionKeys(value, 2);
    } else if (binding.equals("Rights")) {
        userNavigation.setDirectionKeys(value, 3);
    } else if (binding.equals("Ups")) {
        userNavigation.setDirectionKeys(value, 0);
    } else if (binding.equals("Downs")) {
        userNavigation.setDirectionKeys(value, 1);
    } else if (binding.equals("Pause") && !value) {
    //    pauseGame();
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
     else if(binding.equals("Transaction") && !value){
         ball.removeDamping();                  
     }
    else if(binding.equals("Jumps") && !value){
        p.jump();
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
     else if(binding.equals("Call for pass") && !p.isInPossession()){
         p.playAnimation(1, "callForPass3", 0.75f, LoopMode.DontLoop);
     }
     else if(binding.equals("Accept Pass") && !p.isInPossession() && !value){
         
         if(p.getCurrentGesture(1).equals("standingPose")){
              p.playAnimation(1, "receivePass", 0.75f, LoopMode.DontLoop);
         }
         else{
             p.playAnimation(1, "standingPose", 0.75f, LoopMode.DontLoop);
         }
     }
     else if(binding.equals("Head turn")){
         p.turnHead();
     }
     else if(binding.equals("BallTriggerShoot") && !value && p.isInPossession()){
         
         if(p.isKinectPlayer()){
            KinectPlayer kp = (KinectPlayer)p;
            kp.playKinectGesture("Shoot");
         }
     }
     
     else if(binding.equals("BallTriggerPass") && !value && p.isInPossession()){
         if(p.isKinectPlayer()){
            KinectPlayer kp = (KinectPlayer)p;
            kp.throwPass();
         }
//         kp.playKinectGesture("Pass");
     }
    
     else if(binding.equals("Get ball") && !value){
         ball.setPossession(p.getID());
         p.setPossession();
     }
     else if(binding.equals("NewBall") && !value){
         
         if(SceneCharacterManager.getCharacterInPossession() == null){
             ball.makeStatic();
             ball.setBallPosition(Court.getMidCourtLocation().add(0, 20f, 0));
             GameManager.setInState();
         }
     }
     else if(binding.equals("Record batch data") && !value){
         this.recordBatchData();
     }
     else if(binding.equals("Reset") && !value){
         Simulation sim = (Simulation)this;
         sim.resetAttack();
     }
    else if(binding.equals("Sound") && !value){
  //      ball.makeBounceSound();
//        soundInitializor.play();
////         kp.playKinectGesture("Pass");
     }
    
  }
  
    protected void recordBatchData(){
        
        ArrayList<Character> chars = SceneCharacterManager.getCharacterArray();
        for(Character c:chars){
            BasketballCharacter bc = (BasketballCharacter)c;
            bc.recordBatchData();
        }
    }
  
    private void createBall(){
       ball = objectCreator.createBall(new Vector3f(-5f, 10, -14f), rootNode);
   }
    
   protected void createPlayer(){ 
     Vector3f startPosition = CharacterCreator.generateStartingPosition(playerTeamID);
     startPosition.setY(4);
     String[] textures = VISIEFileReader.readPlayerConfiguration(Integer.toString(playerTeamID));
     p = characterCreator.addPlayerCharacter(sceneCharacterManager.IDCounter, playerModelType, rootNode, startPosition, textures, kinectClient != null, 0.48f);
     characterArray.add(p);
     UserNavigation.setInitialCameraRotation((float)Math.toRadians(-90));
     SceneCharacterManager.IDCounter++;
     p.setBall(ball);
     logNewPlayer(p);
  }
   
 public static void logNewPlayer(BasketballCharacter ba){
      if(isLogging){
          String characterType = ba.getCharacterLogString();
          
          StringBuilder s = new StringBuilder();             
          s.append("NEW" + ba.getModelType() + "!" + characterType + "!" + ba.getID() + "!");
          
          for(int i = 0; i < ba.getModelTextures().size(); i++){
              s.append(ba.getModelTextures().get(i));
          }        
          s.append(ba.getPosition());
          Log.write(path, s.toString());
          System.out.println("logged new");
      }
  }
  
    public void createPlayerTarget(){
      
      Vector3f targetPos = new Vector3f();
      
      if(rootNode.getChild("playerTarget") != null){
          rootNode.getChild("playerTarget").removeFromParent();
      }   
      if(gameType.equals("Dribbling")){
        targetPos = Conversions.generateRandomPosition(p.getPosition(), 20f, 40f);
      }
      else if(gameType.equals("Shooting")){
        targetPos = Conversions.generateRandomPosition(Court.getGoalPosition(), 15f, 25f);
      }
      SceneCreator.addTargetNode(targetPos);
      p.setPlayerTarget(targetPos);
  
  }
    
    public void setJumpingPlayer(){
      p.jump();
  }
    
  public int getGameState(){
      return gameState;
  }  
  
    public int getPlayerTeamID(){
      return playerTeamID;
  }
    
  public void setNUPToTeam(NonUserPlayer nup){
      BasketballTeam bt = null;
      
      for(int i = 0; i < teams.size(); i++){
          if(p.getTeamID() == teams.get(i).getTeamID()){
              bt = (BasketballTeam)teams.get(i);
              break;
          }
      }  
      nup.setTeam(bt);
      bt.addToTeam(nup);
     // broadcastServer.setMessage("TEAM" + bt.getTeamID() + "," + nup.getID());
     // TCPServer.broadcastTeamMessage("TEAM" + bt.getTeamID() + "," + nup.getID());
  }
  
  public void updateGame(float tpf){
                
            this.playerMovement(tpf);
            p.updateGestures();
            agentThread.updateAgents(tpf);
            agentThread.updateBall();
            sceneCharacterManager.updateSceneObjects(); 
            sceneObjectManager.updateBallPhysics();
            
            //sounds
            listener.setLocation(p.getPosition()); 
            sceneCharacterManager.updateCharacterSounds();
            
       //     sceneCharacterManager.showCollisionLines();
            sceneCharacterManager.showAgentTargetPositions(1);
            sceneCharacterManager.showAgentTargetPositions(2);
            sceneCharacterManager.showAgentTargetPositions(3);

            //needed to set update ball position
            if(broadcastServer != null){
                String posChar = "null";
                if(SceneCharacterManager.getCharacterInPossession() != null){
                    posChar = SceneCharacterManager.getCharacterInPossession().getID() + "";
                } 
                broadcastServer.setBallMessage(ball.getBallPosition().toString() + "!" + posChar);
                broadcastServer.setGameStateMessage(NetworkMessagingProcessor.createGameStateMessage(timeRemaining)); 
            }

          if(p.isKinectPlayer()){
            this.updatePlayerSkeleton();
          }

          if(isLogging){
              this.writeToFile();
          }
//          
//          if(tester!= null){
//              tester.rotate(0, (float)Math.toRadians(0.05), 0);
//          
//          }
//                   
          
          
  }
      
  
    public boolean getRunning(){
      return isRunning;
  }
  
  
   protected void playerMovement(float tpf){
    
    cameraRot = userNavigation.executeNavigation(isRunning, tpf, sensorClient);
    
    if(ATypeCamera)
        showViewPoint(1);
    else if(BTypeCamera)
        showViewPoint(2);
    else if(topCamera)
        this.topCamera();
    else
        showViewPoint(0); 
  //  System.out.println(p.getPosition());
   }
   
      private void showViewPoint(int characterID){
       
       float f = p.getFacingDirection();
       
       if(p.isKinectPlayer()){
           KinectPlayer kp = (KinectPlayer)p;
           f = kp.getCameraDirection();
       }
       
       if(!initialSettings.getImmersive()){
            camView.showViewPoint(p.getPosition(), (float)Math.toDegrees(cameraRot), camState);
       }
       else{
            camView.showImmersiveViewPoint(p.getHeadPosition(), f, camState);
       }
   }
      
     protected void topCamera(){
       cam.setLocation(new Vector3f(0, 100f, -10));
       cam.setRotation(new Quaternion().fromAngles((float)Math.toRadians(90), 0, 0));
   }  

  protected void updatePlayerSkeleton(){
            
          KinectPlayer kp = (KinectPlayer)p;
          NetworkMessagingProcessor.parseKinectJointMessage(kp);  
          
     //     System.out.println(kinectClient.getCurrentGesture() + " " + p.isInPossession() + " " + kp.isDribbling());

          if(kinectClient.getCurrentGesture().equals("shoot") && p.canShoot() && !kp.isDribbling()){
     //         System.out.println("shoot " + (p.getHandPosition(1).y - p.getShoulderPosition(1).y));
              kp.playKinectGesture("Shoot");
          }
          else if(kinectClient.getCurrentGesture().equals("dribbleRight") && p.isInPossession()){
              kp.playKinectGesture("Dribble Right");
          }
          else if(kinectClient.getCurrentGesture().equals("pass") && p.isInPossession() && !kp.isDribbling()){
              //check for false positive pass
              if(!initialSettings.getGameType().equals("Passing") &&
                  p.getHandPosition(1).y - p.getElbowPosition(1).y > 1f){
                  System.out.println("false positive");
         //        kp.playKinectGesture("Shoot");
                  kp.playKinectGesture("");
              }
              else{
                 
                kp.playKinectGesture("Pass");
              }
          }
          else if(kp.isGettingAttention()){
              kp.playKinectGesture("callForPass");
          }
          else{
              kp.playKinectGesture("");
          }
//          
//          if(!kp.getCurrentGestureName().isEmpty()){
//            System.out.println(kp.getCurrentGestureName());
//          }
  }
  
  
  
      
    private void writeToFile(){
      ArrayList<Character> players = SceneCharacterManager.getCharacterArray();
      StringBuilder s = new StringBuilder();
      
      s.append(System.currentTimeMillis() + "$");
      s.append("B" + ball.getBallPosition() + "$");
      
      for(int i = 0; i < players.size(); i++){
          Character c = players.get(i);
          s.append(c.logCharacterData());
      }
      
      Log.write(path, s.toString());
      
  }
       
       public void setManagers(AppStateManager asm, AssetManager am, RenderManager rm, InputManager im){
           stateManager = asm;
           assetManager = am;
           renderManager = rm;
           inputManager = im;
       
       }
       
       public void setNodes(Node root, Node gui){
           rootNode = root;
           guiNode = gui;
       }
       
       public void setCameraInfo(Camera c, ViewPort vp){
           cam = c;
           viewPort = vp;
           camState = 2;
       }
       
       public static String getGameType(){
           return gameType;
       }
       
       public static float getElapsedTime(){
           return (System.nanoTime() - timeGameStarted)/1000000000f;
       }
       
       public void run(){
           finishLoading();
       }
       
       public void setSettings(InitialSettings is){
           initialSettings = is;
       }
       
       public boolean isLoadingComplete(){
            return loadingComplete;
       }
       
       public int getClientNumber(){
           if(TCPServer != null){
               return TCPServer.getNumberOfUsers();
           }
           else{
               return 0;
           }
       }
       
       public float loadTimeRemaining(){
           return loadTimeLimit - Conversions.nanoToSecond(System.nanoTime() - clientWaitStartTime);
      //     return startTime;
       }
       
       public void refreshCameras(float fov){
           camView.changeImmersiveFieldOfView(fov);
       }
       
        protected float updateTimeRemaining(){
            Float timeElapsed = Conversions.nanoToSecond(System.nanoTime() - timeGameStarted); 
            timeRemaining = timeLimitSeconds - timeElapsed.intValue();
            return timeLimitSeconds - timeElapsed;
        }

        public void setTimeLimit(int seconds){
            timeLimitSeconds = seconds;
        }
        
        protected void displayTimeInformation(){
            
            if(timeLimitSeconds >= 0 && timeRemaining >= 0){
                String timeRemainingString = Conversions.secondsConversionString(this.updateTimeRemaining());
                textManager.setTimeText(timeRemainingString, guiNode);
            }
            else if(timeRemaining < 0){
                textManager.setTimeText("GAME OVER", guiNode);
                gameOver = true;
            }       
        
        }
        
        public void setAudioListener(Listener l){
            listener = l;
        }
                
        public void collision(PhysicsCollisionEvent event){
            
            if(event.getNodeA().getName().equals("Ball")){
                if(event.getNodeB().getName().contains("Scene")){
                    float force = event.getAppliedImpulse();
                    if(force > 2){
                        ball.playBounceSound(force);
                    }
                }
                else if(event.getNodeB().getName().equals("goal")){
                    float force = event.getAppliedImpulse();
                    ball.playHittingGoalSound(force);
                }
                
            }
        }
        
        public void updateSceneObjects(){
            sceneCharacterManager.updateSceneObjects();
        }
        
}
