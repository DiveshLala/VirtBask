/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Sound;

import VISIE.scenemanager.SceneCreator;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public abstract class SoundNode {
    
    public ArrayList<AudioNode> soundNodes;
    public File soundFile;
    public Node parentNode;    
    
    public void createNodes(ArrayList<String> fileNames){
        
        soundNodes = new ArrayList<AudioNode>();
        
        for(String s:fileNames){
            String[] str = s.split(",");
            AudioNode sound = new AudioNode(SceneCreator.getAssetManager(), "Sounds/" + str[0], false);
            sound.setPositional(true);
            sound.setLooping(false);
            sound.setVolume(Integer.parseInt(str[1]));
            parentNode.attachChild(sound);
            soundNodes.add(sound);
        }
    }      
}
