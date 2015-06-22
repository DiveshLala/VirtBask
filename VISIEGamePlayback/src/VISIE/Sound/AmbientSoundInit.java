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
public class AmbientSoundInit {
    
    private AssetManager assetManager;
    private Node rootNode;
    private AudioNode bg;
    
    public AmbientSoundInit(AssetManager am, Node root){
        assetManager = am;
        rootNode = root;
    //   this.loadAmbient();
    }
    
    private void loadAmbient(){
          bg = new AudioNode(assetManager, "Sounds/bgmono.ogg", false); 
          bg.setLooping(true);
          bg.setPositional(false);   
          bg.setVolume(0.5f);
          rootNode.attachChild(bg);
          bg.play();
          System.out.println("ambient sound loaded");
    }
    
}
