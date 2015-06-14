/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import VISIE.characters.BasketballTeam;
import java.util.ArrayList;
import VISIE.characters.Character;
/**
 *
 * @author Divesh
 */
public class Dribbling extends Game{
    
    private int targetsReached = 0;
    
    public Dribbling(){}
    
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
      
          
     public void customizeEnvironment(){

          super.createPlayerTarget();

      }
     @Override
       public void updateGame(float tpf){
            super.updateGame(tpf);
            
           if(p.hasReachedTarget()){ 
              targetsReached++;
              super.createPlayerTarget();
              System.out.println(targetsReached);
          }
  
  }
     
    public void initializeGlobalSettings(){
          membersPerTeam = 1;
          numberOfTeams = 1;

      }
    
}
