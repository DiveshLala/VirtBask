/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Sound;

import VISIE.scenemanager.SceneCreator;
import com.jme3.audio.AudioNode;
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
public abstract class SoundNodes {
    
    public ArrayList<AudioNode> soundNodes;
    public File soundFile;
    public Node parentNode;    
    
    
    //creates sound nodes - one .wav file per node
    public void createNodes(ArrayList<String> fileNames){
        
        soundNodes = new ArrayList<AudioNode>();
        
        for(String s:fileNames){
            String[] str = s.split(",");
            System.out.println(str[0]);
            AudioNode sound = new AudioNode(SceneCreator.getAssetManager(), "Sounds/" + str[0], false);
            sound.setPositional(true);
            sound.setLooping(false);
            sound.setReverbEnabled(false);
            sound.setVolume(Float.parseFloat(str[1]));
            sound.setName(str[2]);
            parentNode.attachChild(sound);
            soundNodes.add(sound);
        }
    }      
    
    //reads txt file listing sounds that the object will play
    public ArrayList<String> readFiles(String filePath){
        
        soundFile = new File(filePath);
        ArrayList<String> files = new ArrayList<String>();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(soundFile));
            String line = br.readLine();

            while (line != null) {
                files.add(line);
                line = br.readLine();
            }
        } catch(IOException e) {
            System.out.println("ball sound file not found!");
        }
        
        return files;
    }
    
    protected AudioNode getNodeByName(String name){
        for(AudioNode an:soundNodes){
            if(an.getName().equals(name)){
                return an;
            }
        }    
        return null;
    }
}
