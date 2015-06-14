
package VISIE;

import Basketball.Ball;
import Basketball.GameManager;
import Basketball.GameStateManagement;
import VISIE.AgentLogic.MainLogicThread;
import VISIE.Games.Dribbling;
import VISIE.Games.Game;
import VISIE.Games.ImitationGame;
import VISIE.Games.Passing;
import VISIE.Games.PickupGame;
import VISIE.Games.Shooting;
import VISIE.Games.Simulation;
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
import com.jme3.scene.shape.Box;
import java.io.BufferedReader;
import java.io.FileReader;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Example 9 - How to make walls and floors solid.
 * This version uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */
public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener {

  private Player p;
  private ArrayList<Character> characterArray = new ArrayList<Character>();
  private boolean isRunning;
  
  private int playerTeamID; //0 is defednding, 1 is attacking
  private int numberOfNUPs = 0;
  
  
  //displaySettings     
  private CameraView camView;
  private int camState = 0;
  private static float timePerFrame;
  int collTime = 0;
  
  
 //I/O operations
  private static boolean isLogging;
  private int logTime = 0;
  private Thread writeThread;
  static String path;

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
  protected SceneCharacterManager sceneCharacterManager;
  private SceneObjectManager sceneObjectManager;
  private UserNavigation userNavigation;
  private MainLogicThread agentThread;
  
  private String playerModelType = "D";
  
  private boolean startScreenCompleted = false;
  private boolean loadingState;
  private Thread loadingThread;
  
  NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay();
  private Nifty nifty;
  private StartInterface startInterface;
  private InitialSettings initialSettings = new InitialSettings();
  private String gameType;
  private int gameState = 0; // 0 = game not initialised, 1 = waiting for client entries, 2 = game is being played
  public  GameStateManagement gsm;
  private Game game;
  
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
        
    
  }

  public void simpleInitApp() {
      Logger.getLogger("").setLevel(Level.WARNING);
      this.executeStartScreen();
  }
  
  public void finishLoading(InitialSettings i){
      
      initialSettings = i;
      gameType = i.getGameType();
      
      if(gameType.equals("Team")){ 
          game = new PickupGame();
      }
      else if(gameType.equals("Dribbling")){
          game = new Dribbling();
      }
      else if(gameType.equals("Passing")){
          game = new Passing();
      }
      else if(gameType.equals("Imitation")){
          game = new ImitationGame();
      }
      else if(gameType.equals("Simulation")){
          game = new Simulation();
      }
      else{
          game = new Shooting();
      }

      game.setManagers(stateManager, assetManager, renderManager, inputManager);
      game.setNodes(rootNode, guiNode);
      game.setCameraInfo(cam, viewPort);
      game.setSettings(initialSettings); 
      game.setAudioListener(listener);
      
      //create loading screen thread
      loadingState = true;
      loadingThread = new Thread(game);
      loadingThread.start();
      loadingThread.setDaemon(true);
  }
  
  //used to update camera frustum before app.restart()
  Callable<Void> changeCameraCallable = new Callable<Void>() {

        public Void call(){
            game.refreshCameras(initialSettings.getFOV());
           // camView.changeImmersiveFieldOfView(initialSettings.getFOV());            
            return null;
        }
  };
  
  
    //used to update camera frustum before app.restart()
  Callable<Void> loadScreenCallable = new Callable<Void>() {

        public Void call(){
            startInterface.setClientWaitingMessage(game.getClientNumber(), game.loadTimeRemaining());      
            return null;
        }
  }; 
  
  private void changeResolution(int width, int height){
      settings.setResolution(width, height);
      this.setSettings(settings);
      this.restart();
  }
  
  private void cleanUpLoadingScreen(){      
      guiViewPort.removeProcessor(niftyDisplay);   
      niftyDisplay.getNifty().exit();
      
       if(initialSettings.getImmersive()){
          this.changeResolution(initialSettings.getResolution()[0] * initialSettings.getNumDisplays(), initialSettings.getResolution()[1]);
          this.enqueue(changeCameraCallable);
          Display.setLocation(-initialSettings.getResolution()[0] * initialSettings.getActivityScreen(), 0);
      }
      else{     
         this.changeResolution(initialSettings.getResolution()[0], initialSettings.getResolution()[1]);
      }
       
       startScreenCompleted = true;  
       isRunning = true;
  }
  
  public int getPlayerTeamID(){
      return playerTeamID;
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

    /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {}


   public void collision(PhysicsCollisionEvent event){
       
   }
   
  @Override
  public void simpleUpdate(float tpf) {
            
     if(loadingState){
         
       if(initialSettings.getMultiPlayer()){  
           ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
           exec.submit(loadScreenCallable);        
           exec.shutdown();
       }
         
        if(game != null && game.isLoadingComplete()){
              this.cleanUpLoadingScreen();
              game.loadGame();
              loadingState = false;
        }
      }
      
      if(startScreenCompleted){    
        //createLoading();
              if(isRunning){                                  
                timePerFrame = tpf;
               
                game.updateSceneObjects();
                
                if(game.areClientsReady()){
                    game.updateGame(tpf);
                } 
                
                System.out.println("hello");
          }
      }
      

  }
  
  public static float getFrameRate(){
     // System.out.println
      return Math.min(timePerFrame, 0.05f);
  }

  
  public void pauseGame(){
      isRunning = !isRunning;
//      if(isRunning)
//          bulletAppState.setSpeed(1.0f);
//      else
//          bulletAppState.setSpeed(0);
  }
  
  public Vector3f getPlayerPos(){
      return p.getPosition(); 
  }
  
  public ArrayList<Character> getCharacterPositions(){
      return characterArray;
  }

  
  //checks if number of non user players are added in the array
  private boolean areClientsAdded(){
      return numberOfNUPs == SceneCharacterManager.getNumberOfNUPS();
  }
  
  public void refresh(){
      sceneCharacterManager.updateSceneObjects();
  }
  
//  public AssetManager getAssetManager(){
//      return assetManager;
//  }
  
}

