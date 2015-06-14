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
public class SoundInit {
    
    private AssetManager assetManager;
    private Node rootNode;
    private AudioNode bg;
    
    public SoundInit(AssetManager am, Node root){
        assetManager = am;
        rootNode = root;
    //    this.loadAmbient();
        this.setPlayerSound();
        this.setBasketballSound();
    }
    
    private void loadAmbient(){
          bg = new AudioNode(assetManager, "Sounds/bgmono.ogg", false); 
          bg.setLooping(true);
          bg.setPositional(false);   
          bg.setVolume(2);
          System.out.println(bg + " " + rootNode);
          rootNode.attachChild(bg);
          bg.play();
    }
    
    private void setPlayerSound(){
            
    }
    
    private void setBasketballSound(){
        
    }
    
//    public void play(){
//        AudioNode bob = new AudioNode(assetManager, "Sounds/bounce1.wav", false);
//        bob.setPositional(false);
//        bob.setLooping(false);
//        bob.setVolume(5);
//        rootNode.attachChild(bob);
//        bob.play();
//        
//        System.out.println("dddd");        
//    
//    }
    
}
