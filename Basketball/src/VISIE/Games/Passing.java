/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballTeam;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.characters.Character;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class Passing extends Game {
    
    public Passing(){
    
    }
    
   public void createTeamsAndAgents(){
       
       for(int i = 0; i < 2; i++){
           
           int agentsInTeam;
           
           if(i == playerTeamID){
               agentsInTeam = 2;
           }
           else{
                agentsInTeam = 1;
           }
           
           if(agentsInTeam < 0){ //adjust for NUPs
               agentsInTeam = 0;
           }
           
           BasketballTeam team = new BasketballTeam(agentsInTeam, characterCreator, i);

           if(team.getTeamID() == playerTeamID){
               p.setTeam(team);
               team.addToTeam(p);
           }  

           ArrayList<Character> c = team.initialisePlayers(i, ball, "None", 1);
           for(int j = 0; j < c.size(); j++){
               characterArray.add(c.get(j));
           }
           
           teams.add(team);
           if(team.getTeamID() == 1){
               team.setAttacking();
           }
       }
       
       //sets possession character
        p.setPossession();
       
       
      //set initial facing directions
       for(int i = 0; i < characterArray.size(); i++){
//           BasketballCharacter bc = (BasketballCharacter)characterArray.get(i);
//           
//           if(bc instanceof VISIE.characters.BasketballAgent){
//               BasketballAgent ba = (BasketballAgent)bc;
//               float initRot;
//               
//               if(startPossessionCharacter.getTeamID() == ba.getTeamID()){ 
//                   initRot = Conversions.originToTargetAngle(ba.getPosition(), Court.getHoopLocation());
//               }
//               else{
//                   initRot = Conversions.originToTargetAngle(ba.getPosition(), SceneCharacterManager.getCharacterInPossession().getPosition());
//               }
//               ba.abo.setFacingDirection(initRot, initRot);
//           }
           
       } 
   }
   
    public void customizeEnvironment(){
        
              float z = -4f;

              p.setPosition(new Vector3f(16, p.getPosition().y, -16));
              p.makePlayerTransparent();
              ArrayList<Character> players = SceneCharacterManager.getCharacterArray();

              for(Character c : players){              
                  if(c instanceof VISIE.characters.BasketballAgent){
                      BasketballAgent ba = (BasketballAgent)c;

                      if(ba.getTeamID() == p.getTeamID()){
                          ba.setPosition(new Vector3f(-9, ba.getPosition().y, z));
                          z = z - 24;
                      }
                      else{
                          ba.setPosition(new Vector3f(-9, ba.getPosition().y, -16));
                      }

                      ba.setTrainingBehaviorModule(p.getTeamID());
                  }
              }
    }
    
    public void initializeGlobalSettings(){
          membersPerTeam = 3;
          numberOfTeams = 2;

      }
    
}
