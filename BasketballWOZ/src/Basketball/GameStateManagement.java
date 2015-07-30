/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.KinectPlayer;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.characters.Team;
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

/**
 *
 * @author DiveshLala
 */
public class GameStateManagement {
    
    Ball ball;
    Player player;
    BitmapFont guiFont;
    BitmapFont JFont;
    int[] resolution;
    static int gameState;
    
    public GameStateManagement(Ball b, Player p, BitmapFont f, BitmapFont jf, int[] r){
        ball = b;
        player = p;
        guiFont = f;
        JFont = jf;
        resolution = r;
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

    
    public Vector3f getBallPos(){
      return ball.getBallPosition();
  }

    
  public void displayInstructions(int state, Node guiNode, int attackingTeam){
      
      guiNode.detachChildNamed("instructions");
      guiNode.detachChildNamed("JInstructions");
      SceneCreator.removeBallMarker();
      SceneCreator.removeDirectionalArrow();
      SceneCreator.removeDribbleArrow();
      
      if(state == 0 && player instanceof VISIE.characters.KinectPlayer){   
          KinectPlayer kp = (KinectPlayer)player;
          if(kp.isTravelling()){
//              String s = "Please dribble the ball!\n";
//              String jText = "ドリブルお願いします！\n";
//              this.setGUIText(s, guiNode);
//              this.setJapaneseText(jText, guiNode);
              SceneCreator.setDribbleArrow(player.getFacingDirection(), ball.getBallPosition());
          }   
      }
      else if(state == 1){
          String s = "Ball is out!\n";
          String jText = "ボールはアウト\n";
          
          if(player.getTeamID() != attackingTeam){
              s = s + "Retrieve the ball from the restart location!";
          }
          else{
              s = s + "Opponent will restart play!";
          }
          this.setGUIText(s, guiNode);
          
          if(player.getTeamID() != attackingTeam){
              jText = jText + "スタート位置にはボールに取って";
          }
          else{
              jText = jText + "相手はプレイを再スタートします";
          }
          this.setJapaneseText(jText, guiNode);
                        
          SceneCreator.setBallMarker(ball.getBallPosition());
      }
      else if(state == 2){
          
          String s;
          String jText;
          
          if(attackingTeam == player.getTeamID()){
                s = "Opponent is restarting play!\n";
                jText = "相手はプレイを再スタートします";
                
          }
          else{
              if(player.equals(SceneCharacterManager.getCharacterInPossession())){
                s = "Take ball back to restart location!\n";
                jText = "ボールをスタート位置に持って行って";
                SceneCreator.setDirectionalArrow(player.getPosition());
              }
              else if(SceneCharacterManager.getCharacterInPossession() == null){
                 s = "Retrieve the ball!\n";
                 jText = "ボールを取って";
                 SceneCreator.setBallMarker(ball.getBallPosition());
              }
              else{
                s = "Team-mate is taking ball to restart location!\n";
                jText = "仲間はスタート位置にボールを持って行っています";
              }
          }

          this.setGUIText(s, guiNode);
          this.setJapaneseText(jText, guiNode);
      }
      else if(state == 3){
          
          String s;
          String jText;
          
          if(attackingTeam == player.getTeamID()){         
              s = "You scored!\n";
              s = s + "Opponent will restart play!";
              jText = "あなたのチームの得点！\n";
              jText = jText + "相手は再スタートします";
          }
          else{
              s = "Opponent scored!\n";
              s = s + "Retrieve the ball!";
              jText = "相手チームの得点\n";
              jText = jText + "ボールを取って";
          }
          this.setGUIText(s, guiNode);
          this.setJapaneseText(jText, guiNode);
          
          SceneCreator.setBallMarker(ball.getBallPosition());
      } 
      
      gameState = state;
  }
  
    private void setGUIText(String text, Node guiNode){
      BitmapText instructionText = new BitmapText(guiFont, false);
      instructionText.setSize(guiFont.getCharSet().getRenderedSize());
      instructionText.setText(text);
      instructionText.setLocalTranslation(resolution[0]/2 - instructionText.getLineWidth()/2, resolution[1] * 0.9f, 0);
      instructionText.setColor(ColorRGBA.Red);
      instructionText.setName("instructions");
      instructionText.setBox(new Rectangle(0, 0, instructionText.getLineWidth(), instructionText.getLineHeight()));
      instructionText.setAlignment(BitmapFont.Align.Center);
      guiNode.attachChild(instructionText);
  }
    
   private void setJapaneseText(String text, Node guiNode){
      BitmapText instructionText = new BitmapText(JFont, false);
      instructionText.setSize(JFont.getCharSet().getRenderedSize());
      instructionText.setText(text);
      instructionText.setLocalTranslation(resolution[0]/2 - instructionText.getLineWidth()/2, resolution[1] * 0.8f, 0);
      instructionText.setColor(ColorRGBA.Red);
      instructionText.setName("JInstructions");
      instructionText.setBox(new Rectangle(0, 0, instructionText.getLineWidth(), instructionText.getLineHeight()));
      instructionText.setAlignment(BitmapFont.Align.Center);
      guiNode.attachChild(instructionText);
  }
    
    public void setScoreText(Node guiNode, int scoreOne, int scoreTwo){
      guiNode.detachChildNamed("score");
      BitmapText scoreText = new BitmapText(guiFont, false);
      scoreText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      scoreText.setText(scoreOne + "-" + scoreTwo);
      scoreText.setLocalTranslation(resolution[0] - scoreText.getLineWidth() - 5, resolution[1], 0);
      scoreText.setColor(ColorRGBA.Red);
      scoreText.setName("score");
      guiNode.attachChild(scoreText);
  }
    
    public static int getGameState(){
        return gameState;
    }
    
    
        
}
