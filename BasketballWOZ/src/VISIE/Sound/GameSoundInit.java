/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Sound;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

/**
 *
 * @author Divesh
 */
public class GameSoundInit {
    
    private AssetManager assetManager;
    private Node rootNode;
    private AudioNode ambient;
    private static AudioNode goalScoreSound;
    private static AudioNode ballOutSound;
    
    public GameSoundInit(AssetManager am, Node root){
        assetManager = am;
        rootNode = root;
    //   this.loadAmbient();
        this.loadGoalScoreSound();
        this.loadBallOutSound();
    }
    
    private void loadAmbient(){
          ambient = new AudioNode(assetManager, "Sounds/bgmono.ogg", false); 
          ambient.setLooping(true);
          ambient.setPositional(false);   
          ambient.setVolume(0.5f);
          rootNode.attachChild(ambient);
          ambient.play();
    }
    
    private void loadGoalScoreSound(){
          goalScoreSound = new AudioNode(assetManager, "Sounds/goalscore.wav", false); 
          goalScoreSound.setLooping(false);
          goalScoreSound.setPositional(false);   
          goalScoreSound.setVolume(2f);
          rootNode.attachChild(goalScoreSound);
    }
    
    private void loadBallOutSound(){
          ballOutSound = new AudioNode(assetManager, "Sounds/ballout.wav", false); 
          ballOutSound.setLooping(false);
          ballOutSound.setPositional(false);   
          ballOutSound.setVolume(2f);
          rootNode.attachChild(ballOutSound);
    }
    
    public static void playGoalScoreSound(){
         goalScoreSound.playInstance();
    }
    
    public static void playBallOutSound(){
         ballOutSound.playInstance();
    }
        
        
    
}
