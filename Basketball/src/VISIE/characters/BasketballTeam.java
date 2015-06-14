/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import VISIE.Games.Game;
import VISIE.Main;
import VISIE.scenemanager.CharacterCreator;
import VISIE.VISIEFileReader;
import VISIE.recording.Log;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author DiveshLala
 */
public class BasketballTeam extends Team{
    
    CharacterCreator characterCreator;
    
    public BasketballTeam(int i, CharacterCreator cc, int id){               
        numMembers = i;
        characterCreator = cc;
        teamID = id;
        players = new ArrayList<Character>();
    }
    
    public ArrayList<Character> initialisePlayers(int teamID, Ball ball, String agentType, int initialRole){
        
       //role = 0: defending, 1: attacking
       ArrayList<Vector3f> startPos = new ArrayList<Vector3f>();
              
       for(int i = 0; i < numMembers; i++){
           Vector3f vec = CharacterCreator.generateStartingPosition(initialRole);
           boolean tooClose = true;
           while(tooClose){
               int a = 0;
               for(int j = 0; j < startPos.size(); j++){
                   if(startPos.get(j).distance(vec) < 7.5f){
                       a++;
                   }
              }
              if(a < 1 && vec.distance(Court.getGoalPosition()) > 5){
                 tooClose = false; 
                 vec.setY(4);
              }
              else{
                  vec = CharacterCreator.generateStartingPosition(initialRole);
              }
           }
           startPos.add(vec);
       }
       String[][] config = VISIEFileReader.readAgentConfigurations(teamID, numMembers);
       return this.createCharacters(startPos, config, ball, agentType);
    }
      
    
    public ArrayList<Character> createCharacters(ArrayList<Vector3f> startPos, String[][] config, Ball ball, String agentType){
          
        
       ArrayList<Character> characterArray = new ArrayList<Character>();
       
       for(int i = 0; i < numMembers; i++){
           
//            System.out.println("config" + config[i][0]);
            String modelType = config[i][0];
            float scale = Float.parseFloat(config[i][1]);
           
            BasketballAgent bp;
            
            bp = characterCreator.addAgentCharacter(agentType, SceneCharacterManager.IDCounter, 1, modelType, startPos.get(i), scale);
            
            characterArray.add(bp); 
            SceneCharacterManager.IDCounter++;
            bp.abo.setFacingDirection(50);
            bp.setBehaviorState(1);
            bp.setTeam(this);
            bp.setBall(ball);
            players.add(bp);       
            Game.logNewPlayer(bp);
       }   
//       System.out.println(players.get(0).getModelTextures());
//       String s = players.get(0).getModelTextures().get(0);
//       String file = s.replace("!", "");
       this.calculateTeamColor();
       
       return characterArray;
    }  
       
    
    private void calculateTeamColor(){
        File f = new File("assets/Models/BasketballPlayer/shirtyellow.jpg");
        try{
            BufferedImage image = ImageIO.read(f);
            int c = image.getRGB(10,10);
            int  red = (c & 0x00ff0000) >> 16;
            int  green = (c & 0x0000ff00) >> 8;
            int  blue = c & 0x000000ff;
            super.setTeamColor(new Vector3f(red, green, blue));
        }
        catch(IOException e){
            System.out.println(e);
        }

    }
    
}
