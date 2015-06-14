/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import Basketball.GameManager;
import Basketball.GameStateManagement;
import static VISIE.Games.Game.timeGameStarted;
import VISIE.Main;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.BasketballTeam;
import VISIE.characters.CollabAgent;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.recording.Log;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

/**
 *
 * @author DiveshLala
 */
public class Simulation extends Game{
        
    public Simulation(){
        this.setTimeLimit(3600);
    }
    
    protected void createTeamsAndAgents(){
            //for each team
    for(int i = 0; i < numberOfTeams; i++){
           
           //get number agents in team
           int agentsInTeam = membersPerTeam;
           
           //create team
           BasketballTeam team = new BasketballTeam(agentsInTeam, characterCreator, i);
           ArrayList<VISIE.characters.Character> c = new ArrayList<VISIE.characters.Character>();
           if(i == 1){
               c = team.initialisePlayers(i, ball, "Mixed,Rational,Collab", 1);
               team.setAttacking();
           }
           else{
               c = team.initialisePlayers(i, ball, "None", 0);
               for(Character cha:c){
                   BasketballAgent ba = (BasketballAgent)cha;
                   ba.setDefenseStrategy("markPossessor");
                   ba.abo.setStealingCapability(0); //ensure no stealing
               }
           }
           
           for(int j = 0; j < c.size(); j++){
               characterArray.add(c.get(j));
           }
           
           //add team to list
           teams.add(team);
       }
       
       //sets possession character
       BasketballCharacter startPossessionCharacter;

       startPossessionCharacter = (BasketballCharacter)teams.get(1).getRandomTeamMate();
       startPossessionCharacter.setPossession();
       
        BasketballAgent ba = (BasketballAgent)startPossessionCharacter;
        ba.setBehaviorState(0);

       
      //set initial facing directions and signal interpreter
       for(int i = 0; i < characterArray.size(); i++){
           BasketballCharacter bc = (BasketballCharacter)characterArray.get(i);
           bc.initializePositions(startPossessionCharacter);  
       }    
    }
    
    protected void initializeGlobalSettings(){
          membersPerTeam = initialSettings.getNumPlayers();
          numberOfTeams = 2;    
    }
    protected void customizeEnvironment(){
        super.topCamera();
        BTypeCamera = false;
        ATypeCamera = false;
        topCamera = true;   
    }
    
    @Override
    public void createPlayer(){
    }
    
    @Override
    public void updateGame(float tpf){
        if(!gameOver){
            agentThread.updateAgents(tpf);
            agentThread.updateBall();
            sceneCharacterManager.updateSceneObjects(); 
            sceneObjectManager.updateBallPhysics();
            gsm.doAttackingSimulationManagement(guiNode);
    //        this.updateTimeRemaining();
            this.displayTimeInformation();
        }
        else{
            this.recordBatchData();
            System.exit(0);
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
    
    @Override
    protected void setupKeys(){
        super.setupKeys();
        
        //reset
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addListener(this, "Reset");
    }
    
    public void resetAttack(){
        GameManager.setInState();
        for(Character c:SceneCharacterManager.getCharacterArray()){
            BasketballCharacter bc = (BasketballCharacter)c;
            if(bc.getTeamID() == 1){
                bc.setPosition(Court.getRandomNonHoopSidePosition().setY(5));
            }
            else{
                bc.setPosition(Court.getRandomHoopSidePosition().setY(5));
            }
        }       
        
    }
    
    
}
