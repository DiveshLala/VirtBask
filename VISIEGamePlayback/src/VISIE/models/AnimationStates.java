/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;

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
    
    private ArrayList<String[]> animationMap;
    private ArrayList<String[]> walkingMap;    
    
    public AnimationStates(String animFile, String walkFile){
        
        
        animationMap = new ArrayList<String[]>();
        walkingMap = new ArrayList<String[]>();
        
        try{
          BufferedReader reader = new BufferedReader(new FileReader(animFile));
          String line;
          
          while((line = reader.readLine()) != null){
              String[] data = line.split(",");
              animationMap.add(data);
          } 
          
          reader = new BufferedReader(new FileReader(walkFile));
          
          while((line = reader.readLine()) != null){
              String[] data = line.split(",");
              walkingMap.add(data);
          } 
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
    }
    
    
    
    public int getAnimationState(String animationName){
        for(int i = 0; i < animationMap.size(); i++){
             if(animationMap.get(i)[1].equals(animationName)){
                 return Integer.parseInt(animationMap.get(i)[0]);
             }      
        }
        
        return 0;
    }
    
    public int getWalkingState(String animationName){
        for(int i = 0; i < walkingMap.size(); i++){
             if(walkingMap.get(i)[1].equals(animationName)){
                 return Integer.parseInt(walkingMap.get(i)[0]);
             }      
        } 
        return 0;
    }
    
    public String getAnimationName(int state){
        
        for(int i = 0; i < animationMap.size(); i++){
             if(Integer.parseInt(animationMap.get(i)[0]) == state){
                 return animationMap.get(i)[1];
             }      
        }
        
        return "";
    }
    
    public String getWalkingName(int state){
        
        for(int i = 0; i < walkingMap.size(); i++){
             if(Integer.parseInt(walkingMap.get(i)[0]) == state){
                 return walkingMap.get(i)[1];
             }      
        }
        
        return "";
    }
    
}
