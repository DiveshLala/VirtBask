/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Sound;

import Basketball.Ball;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class BallSoundNode extends SoundNodes {
     
    private long lastBounceTime;
    private ArrayList<AudioNode> bounceNodes;
   
    public BallSoundNode(Node parent, Ball b, String filePath){
        
        parentNode = parent;
        
        ArrayList<String> soundFiles = readFiles(filePath);
        createNodes(soundFiles);
        this.groupBounceSounds();
        
    }
    
    private void groupBounceSounds(){
        
        bounceNodes = new ArrayList<AudioNode>();
        
        for(AudioNode an:soundNodes){
            if(an.getName().contains("bounce")){
                bounceNodes.add(an);
            }
        }
    }
    
    public void playBounceSound(float force){
        
        //stops double playback
        if(Math.abs(lastBounceTime - System.currentTimeMillis()) > 200){
            if(force > 5){
                double d = (Math.random() * bounceNodes.size());
                int ind = (int)Math.floor(d);
                AudioNode an = bounceNodes.get(ind);
                an.play();
                lastBounceTime = System.currentTimeMillis();
            }
        }
    }
    
    public void playHittingGoalSound(float force){
        AudioNode an = getNodeByName("ballwood");
        an.play();
    }
    
}
