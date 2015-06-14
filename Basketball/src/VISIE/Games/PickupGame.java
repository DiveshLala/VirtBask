/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import Basketball.GameManager;
import VISIE.InitialSettings;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballTeam;
import VISIE.characters.Character;
import VISIE.characters.CollabAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.recording.Log;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class PickupGame extends Game{
    
    private BasketballCharacter previousCharacterTouch = null;
    private Box pointer = new Box(0.3f,0.3f,0.3f);
    
    public PickupGame(){    
      this.setTimeLimit(900);
    }
    
    public void createTeamsAndAgents(){
       
        //for each team
       for(int i = 0; i < numberOfTeams; i++){
           
           //get number agents in team
           int agentsInTeam;
           
           if(i == playerTeamID){
               agentsInTeam = membersPerTeam - numberOfNUPs - 1;
           }
           else{
                agentsInTeam = membersPerTeam;
           }
           
           if(agentsInTeam < 0){ //adjust for NUPs
               agentsInTeam = 0;
           }
           
           //create team
           BasketballTeam team = new BasketballTeam(agentsInTeam, characterCreator, i);
           ArrayList<Character> c = new ArrayList<Character>();
           
           
           //add player to team
           if(team.getTeamID() == playerTeamID){
               int initialRole = 0;
               if(initialSettings.getOffense()){
                   initialRole = 1;
                   team.setAttacking();
               }
               p.setTeam(team);
               team.addToTeam(p);
               c = team.initialisePlayers(i, ball, "Collab", initialRole);
           }  
           
           else{
               int initialRole = 0;
               if(!initialSettings.getOffense()){
                   initialRole = 1;
                   team.setAttacking();
               }
               c = team.initialisePlayers(i, ball, "None", initialRole);
               for(Character cha:c){
                   BasketballAgent ba = (BasketballAgent)cha;
                   ba.setDefenseStrategy("mantoman");
               }
           }
           
           for(int j = 0; j < c.size(); j++){
               characterArray.add(c.get(j));
           }
           
           //add team to list
           teams.add(team);
//           if(team.getTeamID() == 1){
//               team.setAttacking();
//           }
       }
       
       //sets possession character
       BasketballCharacter startPossessionCharacter;
       if(numberOfTeams > 1 && !initialSettings.getOffense()){
            startPossessionCharacter = (BasketballCharacter)teams.get(0).getRandomTeamMate();
       }
       else{
           startPossessionCharacter = p;
       }
       
       startPossessionCharacter.setPossession();
       
       startPossessionCharacter.setBehaviorState(0);
       
      //set initial facing directions and signal interpreter
       for(int i = 0; i < characterArray.size(); i++){
           BasketballCharacter bc = (BasketballCharacter)characterArray.get(i);
           bc.initializePositions(startPossessionCharacter);
       }
           
       
   }
    
    public void initializeGlobalSettings(){
          membersPerTeam = initialSettings.getNumPlayers();
          numberOfTeams = 2;
          playerTeamID = 1;
     }
    
     public void customizeEnvironment(){
     
     }
     
     @Override
     public void updateGame(float tpf){
         if(!gameOver){
            super.updateGame(tpf);
            this.updateTeamInformation();
            this.checkPassing();
            gsm.doGameStateManagement(guiNode);
            textManager.displayInstructions(guiNode);
            this.displayTimeInformation();
            
//            if(rootNode.getChild("bob") != null){
//                rootNode.detachChildNamed("bob");
//            }
//             Geometry geo = new Geometry("bob", pointer);
//             geo.setLocalTranslation(p.getBallHandVector());
//
//             
//             Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
//             mat1.setColor("Color", ColorRGBA.Blue);
//             geo.setMaterial(mat1);
//            
//                         rootNode.attachChild(geo);
         }
         else if(!recordBatchFlag){
             this.recordBatchData();
             recordBatchFlag = true;
             
         }
     }
     
    private void updateTeamInformation(){
      
      if(SceneCharacterManager.getCharacterByID(ball.getLastTouched()) != null){
            Character c = SceneCharacterManager.getCharacterByID(ball.getLastTouched());
            for(int i = 0; i < teams.size(); i++){
                if(teams.get(i).isMember(c)){
                    teams.get(i).setAttacking();
                }
                else{
                    teams.get(i).setDefending();
                }
            }
      }
  }
    
    private void checkPassing(){
        if(GameManager.getGameState() == 0){
            BasketballCharacter posChar = SceneCharacterManager.getCharacterInPossession();
            if(posChar != null){
                if(previousCharacterTouch == null){
                    previousCharacterTouch = posChar;
                    
                }
                else if(previousCharacterTouch != posChar && posChar.playerIsTeamMate(previousCharacterTouch)){
                    previousCharacterTouch = posChar;
                    gsm.setTwoTouch(true);
                }           
            }
        
        }
        else{
            gsm.setTwoTouch(false);
            previousCharacterTouch = null;   
        }
    } 
    

    
    @Override
    public void recordBatchData(){
        ArrayList<Character> chars = SceneCharacterManager.getCharacterArray();
        for(Character c:chars){
            BasketballCharacter bc = (BasketballCharacter)c;
            bc.recordBatchData();
        }
    }
        
}
