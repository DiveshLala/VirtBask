/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import VISIE.scenemanager.CharacterCreator;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
    
    public BasketballTeam(int id){
        teamID = id;
        players = new ArrayList<Character>();
    }
    
//    public ArrayList<Character> initialisePlayers(int initialRole, Ball ball){
//        
//       //role = 0: defending, 1: attacking
//       ArrayList<Vector3f> startPos = new ArrayList<Vector3f>();
//       
//       for(int i = 0; i < numMembers; i++){
//           Vector3f vec = CharacterCreator.generateStartingPosition(initialRole);
//           boolean tooClose = true;
//           while(tooClose){
//               int a = 0;
//               for(int j = 0; j < startPos.size(); j++){
//                   if(startPos.get(j).distance(vec) < 7.5f){
//                       a++;
//                   }
//              }
//              if(a < 1){
//                 tooClose = false; 
//                 vec.setY(4);
//              }
//              else{
//                  vec = CharacterCreator.generateStartingPosition(0);
//              }
//           }
//           startPos.add(vec);
//       }
//       String[][] config = VISIEFileReader.readAgentConfigurations(initialRole, numMembers);
//       return this.createCharacters(startPos, config, ball);
//    }
    
//    public ArrayList<Character> createCharacters(ArrayList<Vector3f> startPos, String[][] config, Ball ball){
//        
//       ArrayList<Character> characterArray = new ArrayList<Character>();
//          
//       for(int i = 0; i < numMembers; i++){
//            String[] textureArray = config[i];
//            String modelType = textureArray[0];
//            Vector3f startPosition = startPos.get(i); 
//            String[] bpTex = Arrays.copyOfRange(textureArray, 1, 6);
//            BasketballAgent bp = (BasketballAgent)characterCreator.addBasketballCharacter(SceneCharacterManager.IDCounter, 1, modelType, startPosition, bpTex);
//            characterArray.add(bp); 
//            SceneCharacterManager.IDCounter++;
//            bp.abo.setFacingDirection(50, 50);
//            bp.setTeam(this);
//            bp.setBall(ball);
//            players.add(bp);
//       }   
//       
//       return characterArray;
//    }     
//    
}
