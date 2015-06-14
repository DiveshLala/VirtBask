/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.models.AnimatedModel;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class AgentGesture {
    
    ArrayList<String> frames;
    int size;
    AnimatedModel characterModel;
    int currentFrame = -1;
    String gestureType;
    int ballManipulationFrame = -1;
    
    public AgentGesture(ArrayList<String> record, AnimatedModel model){
        frames = (ArrayList<String>)record.clone();
        characterModel = model;
    }
    
    public int getSize(){
        return size;
    }
    
    public void setManipulationFrame(int i){
        ballManipulationFrame = i;
    }
    
    public void setGestureType(String s){
        gestureType = s;
    }
    
    public String getGestureType(){
        return gestureType;
    }
    
    public void startPlayback(){
        currentFrame = 0;
    }
    
    public void stopPlayback(){
        currentFrame = -1;
    }
    
    public int getCurrentFrame(){
        return currentFrame;
    }
    
    public int getBallManipulationFrame(){
        return ballManipulationFrame;
    }
    
    public void playGesture(){
        
            ArrayList<String> pose = new ArrayList<String>();
            String s = frames.get(currentFrame);
            String[] t = s.split(";");
            for(int i = 0; i < t.length; i++){
                pose.add(t[i]);
            }
            characterModel.setJointRotations(pose);
         
            if(currentFrame >= frames.size() - 1){
                currentFrame = -1;
            }  
            else{
                currentFrame++;
            }
    }
    
}
