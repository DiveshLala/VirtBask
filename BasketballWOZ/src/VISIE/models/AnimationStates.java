/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;

import com.jme3.animation.AnimControl;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author DiveshLala
 */
public class AnimationStates {
    
    private Object[] animations;
    
    public AnimationStates(AnimControl control){
        
        animations = control.getAnimationNames().toArray();
        
    }
    
    
    
    public int getAnimationState(String animationName){
        
        for(int i = 0; i < animations.length; i++){
           String s = animations[i].toString();
           if(s.equals(animationName)){
               return i;
           }
        
        }        
        return 0;
    }
//    
    public int getWalkingState(String animationName){
        
        for(int i = 0; i < animations.length; i++){
           String s = animations[i].toString();
           if(s.equals(animationName)){
               return i;
           }
        
        }        
        return 0;
    }
//    
    public String getAnimationName(int state){
        
        if(animations.length >= state){
            String s = animations[state].toString();
            return s;
        }
        
        return "";
    }
//    
    public String getWalkingName(int state){
        
        if(animations.length >= state){
            String s = animations[state].toString();
            return s;
        }
        
        return "";
    }
    
}
