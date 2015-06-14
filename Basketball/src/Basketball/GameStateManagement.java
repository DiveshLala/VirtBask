/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.Sound.GameSoundInit;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.KinectPlayer;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.characters.Team;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DiveshLala
 */
public class GameStateManagement {
    
    Ball ball;
    ArrayList<Team> teams;
    static int[] score;
    GUITextManager textmanager;
    private boolean twoTouch = false;

    
    public GameStateManagement(Ball b, ArrayList<Team> t, GUITextManager tm){
        ball = b;
        teams = t;
        score = new int[2];
        textmanager = tm;
    }
        
    public void doGameStateManagement(Node guiNode){
            
          int state = GameManager.getGameState();
          int attackingTeam = GameManager.getAttackingTeam();
          BasketballCharacter possessionCharacter = SceneCharacterManager.getCharacterInPossession();

          if(state != 0){    
              //forbids player from grabbing ball between turns
              if(possessionCharacter != null && possessionCharacter.getTeamID() == attackingTeam){
                    possessionCharacter.removePossession();
                    possessionCharacter.setActionState(1);
              }
              else if(this.isBallOut()){
                  this.doOutOfBoundsActivity();
              }
              else if(possessionCharacter != null){
                  
                  if(state == 3){
                        GameManager.setPossessionSwapState();
                  }

                if(state == 2 && 
                   possessionCharacter.getPosition().setY(0).distance(Court.getRestartLocation()) < 5 &&
                   !this.isBallOut()){
                    GameManager.setInState();
                    GameManager.setAttackingTeam(possessionCharacter.getTeamID());
                    
                    BasketballCharacter bc = (BasketballCharacter)possessionCharacter;
                    bc.resetPossessionTime();
                    
                    for(BasketballCharacter teammate:bc.getTeamMates()){
                        if(teammate.getCharacterType().toLowerCase().contains("agent")){
                           BasketballAgent ba = (BasketballAgent)teammate;
                           ba.refreshNonPossessionTarget();
                        }
                    }
                }
              }
          }
                    
          if(state == 0){
              if(this.isBallOut()){//ball is out
                   this.doOutOfBoundsActivity();
                   GameSoundInit.playBallOutSound();
              }         
              else if(possessionCharacter != null &&
                   possessionCharacter.getTeamID() != attackingTeam){//ball is stolen
                   GameManager.setPossessionSwapState();
              }
              else if(this.getBallPos().distance(Court.getHoopLocation()) < 0.75f && 
                    Math.abs(this.getBallPos().y - Court.getHoopLocation().y) < 0.2f &&
                    ball.getBallTravellingDirection().y < 0){//ball in goal
                    GameManager.setScoreState();
                    ball.stopBall();     
                    if(twoTouch){
                        score[attackingTeam] = score[attackingTeam]+ 2;
                    }
                    else{
                        score[attackingTeam]++;
                    }
                    GameSoundInit.playGoalScoreSound();
                    textmanager.setScoreText(guiNode);
              }  
          }

          //process game when in
          if(state == 1 && 
             possessionCharacter != null &&
             possessionCharacter.getTeamID() != attackingTeam &&
             !this.isBallOut()){
              this.doResetActivity();
          }
          
          if(state != 0){
              for(int i = 0; i< teams.size(); i++){
                  if(teams.get(i).getTeamID() != attackingTeam){
                        Team t = teams.get(i); 
                        SceneCreator.displayMarker(t.getTeamColor());
                        break;
                }
              }
          }
          else{
              SceneCreator.hideMarker();
          }    
    }
    
    public void doAttackingSimulationManagement(Node guiNode){
        this.doGameStateManagement(guiNode);
        int state = GameManager.getGameState(); 
        
        if(state != 0){
            GameManager.setAttackingTeam(0);
            if(state != 1 && SceneCharacterManager.getCharacterInPossession() == null){
                float distBall = 100000000;
                BasketballAgent closest = null;
                for(VISIE.characters.Character c: SceneCharacterManager.getCharacterArray()){
                    BasketballAgent ba = (BasketballAgent)c;
                    if(ba.getTeamID() == 1 && ba.get2DPosition().distance(ball.getBallPosition()) < distBall){
                        distBall = ba.get2DPosition().distance(ball.getBallPosition());
                        closest = ba;
                    }                    
                }
                if(closest != null){
                    closest.setPossession();
                }      
            }
        }
    }
        
    public boolean isBallOut(){
      VISIE.characters.Character c = SceneCharacterManager.getCharacterInPossession();
      
      if(c != null){
          return !Court.isInsideCourt(c.getPosition());
      }
      else{
          return !Court.isInsideCourt(ball.getBallPosition());
      }
    }
    
    private void doOutOfBoundsActivity(){
        
         GameManager.setOutState();
         
         if(SceneCharacterManager.getCharacterInPossession() != null){
             SceneCharacterManager.getCharacterInPossession().removePossession();
         }
         
         ball.setBallPosition(Court.getRestartLocation().clone().add(0, 0.5f, 0));
         ball.stopBall();
  }
    
    public Vector3f getBallPos(){
      return ball.getBallPosition();
  }
    
    private void doResetActivity(){
      
      GameManager.setInState();
      GameManager.setAttackingTeam(SceneCharacterManager.getCharacterInPossession().getTeamID());
      
    //  if(SceneCharacterManager.getCharacterInPossession() instanceof VISIE.characters.BasketballAgent){
        BasketballCharacter bc =  (BasketballCharacter)SceneCharacterManager.getCharacterInPossession();
        bc.resetPossessionTime();
   //   }   
  }
    
    
    public static int[] getScore(){
        return score;
    }
    
    public void setTwoTouch(boolean b){
        twoTouch = b;
    }
        
}
