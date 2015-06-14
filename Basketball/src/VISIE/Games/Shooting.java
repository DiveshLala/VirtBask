/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import VISIE.characters.BasketballTeam;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.scenemanager.SceneCreator;

/**
 *
 * @author Divesh
 */
public class Shooting extends Game{
    
    private int targetsReached = 0;
    
    public Shooting(){}
    
    public void createTeamsAndAgents(){
    
           BasketballTeam team = new BasketballTeam(0, characterCreator, playerTeamID);
           p.setTeam(team);
           team.addToTeam(p);

           ArrayList<Character> c = team.initialisePlayers(playerTeamID, ball, "None", 1);
           for(int j = 0; j < c.size(); j++){
               characterArray.add(c.get(j));
           }
           
           teams.add(team);
           if(team.getTeamID() == 1){
               team.setAttacking();
           }
       
       //sets possession character
       p.setPossession();

    }
    
    public void initializeGlobalSettings(){
          membersPerTeam = 1;
          numberOfTeams = 1;

      }
    
     public void customizeEnvironment(){

          super.createPlayerTarget();
     }
     
     @Override     
     public void updateGame(float tpf){
         super.updateGame(tpf);
         if(p.hasReachedTarget()){   
              SceneCreator.changeTargetColour("playerTarget", true);                    
              if(p.getCurrentGestureName().equals("Shoot")){
                  targetsReached++;
                  this.createPlayerTarget();
                  System.out.println(targetsReached);
              }          
         }
         else{
             SceneCreator.changeTargetColour("playerTarget", false);
         }
         
     }
    
}
