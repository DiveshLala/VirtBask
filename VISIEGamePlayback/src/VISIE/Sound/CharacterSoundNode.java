/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Sound;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.mathfunctions.Conversions;
import VISIE.recording.Log;

/**
 *
 * @author Divesh
 */
public class CharacterSoundNode extends SoundNodes{
    
    public ArrayList<AudioNode> footStepsNodes;
    public ArrayList<AudioNode> utteranceNodes;
    private long lastFootstepTime;
    private String soundFlag = "";
    private VISIE.characters.Character parentCharacter;
    
    public CharacterSoundNode(Node n, String filePath, Character c){
        
        parentNode = n;
        parentCharacter = c;
                
        ArrayList<String> soundFiles = readFiles(filePath);
        createNodes(soundFiles);
        this.groupFootstepSounds();
        this.groupUtterances();
        
    }
    
    private void groupFootstepSounds(){
        
        footStepsNodes = new ArrayList<AudioNode>();        
        for(AudioNode an:soundNodes){
            if(an.getName().contains("footsteps")){
                footStepsNodes.add(an);
            }
        }
    }
    
    
    private void groupUtterances(){
        
        utteranceNodes = new ArrayList<AudioNode>();        
        for(AudioNode an:utteranceNodes){
            if(an.getName().contains("utterance")){
                utteranceNodes.add(an);
            }
        }   
    }
    
    public void playFootstep(String footstepType){
        
        if(Math.abs(lastFootstepTime - System.currentTimeMillis()) > 200){
                
            double d = Math.random() * footStepsNodes.size();
            int ind = (int)Math.floor(d);
            AudioNode an = footStepsNodes.get(ind);

            if(footstepType.equals("run")){
                an.setVolume(0.1f);
            }
            else if(footstepType.equals("walk")){
                an.setVolume(0.05f);
            }
            else if(footstepType.equals("stepBack")){
                an.setVolume(0.025f);
            }
            else if(footstepType.startsWith("step")){
                an.setVolume(0.01f);
            }
            else if(footstepType.startsWith("turn")){
                an.setVolume(0.01f);
            }
            an.play();
            lastFootstepTime = System.currentTimeMillis();
        }

    }
    
    public void playPassSound(){
        AudioNode an = getNodeByName("pass");
        an.play();
    }
    
    public void playShootSound(){
        AudioNode an = getNodeByName("shoot");
        an.play();
    }
    
    public void playCatchSound(){
        AudioNode an = getNodeByName("catch");
        an.play();
    }
    
    //only use for WOZ clients - threading problems
    public void flagUtterance(String s){
        soundFlag = s;
    }
        
    public void playUtterance(String s){
        AudioNode an = getNodeByName(s);
        an.play();
    
    }
    
    
    
    
}
