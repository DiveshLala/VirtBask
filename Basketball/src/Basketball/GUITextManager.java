/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import static Basketball.GameStateManagement.score;
import VISIE.characters.KinectPlayer;
import VISIE.characters.Player;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 *
 * @author Divesh
 */
public class GUITextManager {
    
    private Player player;
    private Ball ball;
    BitmapFont guiFont;
    BitmapFont JFont;
    int[] resolution;
    int mainScreen = 0;
    
    public GUITextManager(Player p, Ball b, BitmapFont f, BitmapFont jf, int[] r){
        ball = b;
        player = p;
        guiFont = f;
        JFont = jf;
        resolution = r;
    }
    
    public void setMainScreen(int i){
        mainScreen = i;
    }
    
    public void displayInstructions(Node guiNode){
      
      int state = GameManager.getGameState();
      guiNode.detachChildNamed("instructions");
      guiNode.detachChildNamed("JInstructions");
      SceneCreator.removeBallMarker();
      SceneCreator.removeDirectionalArrow();
      SceneCreator.removeDribbleArrow();
      
      if(state == 0 && player.isKinectPlayer()){
          this.remindDribble();
      }
      else if(state == 1){
          this.ballOut(guiNode);
      }
      else if(state == 2){
          this.transportBallToRestart(guiNode);
      }
      else if(state == 3){
          this.goalScored(guiNode);
      }   
  }
      
    private void remindDribble(){
        
        KinectPlayer kp = (KinectPlayer)player;
          if(kp.isTravelling()){
                SceneCreator.setDribbleArrow(player.getFacingDirection(), ball.getBallPosition());
          }
    }
    
    private void ballOut(Node guiNode){
        
          int attackingTeam = GameManager.getAttackingTeam();
        
          String s = "Ball is out!\n";
          String jText = "ボールはアウト\n";
          
          if(player.getTeamID() != attackingTeam){
              s = s + "Retrieve the ball from the restart location!";
          }
          else{
              s = s + "Opponent will restart play!\n";
          }
          this.setGUIText(s, guiNode);   
          
          if(player.getTeamID() != attackingTeam){
              jText = jText + "スタートの位置にはボールを取って";
          }
          else{
              jText = jText + "相手はプレイを再スタートします";
          }
          this.setJapaneseText(jText, guiNode);
          
          SceneCreator.setBallMarker(ball.getBallPosition());
    }
    
    private void transportBallToRestart(Node guiNode){
        
          String s;
          String jText;
          
          int attackingTeam = GameManager.getAttackingTeam();
          
          if(attackingTeam == player.getTeamID()){
                s = "Opponent is restarting play!\n";
                jText = "相手はプレイを再スタートします\n";
          }
          else{
              if(player.equals(SceneCharacterManager.getCharacterInPossession())){
                s = "Take ball back to restart location!\n";
                jText = "ボールをスタート位置に持って行って";
                SceneCreator.setDirectionalArrow(player.getPosition());
              }
              else if(SceneCharacterManager.getCharacterInPossession() == null){
                 s = "Retrieve the ball!\n";
                 jText = "ボールを取って\n";
                 SceneCreator.setBallMarker(ball.getBallPosition());
              }
              else{
                s = "Teammate is taking ball to restart location!\n";
                jText = "仲間はスタート位置にボールを持って行っています\n";
              }
          }

          this.setGUIText(s, guiNode);
          this.setJapaneseText(jText, guiNode);
    }
    
    private void goalScored(Node guiNode){
        
          int attackingTeam = GameManager.getAttackingTeam();
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
              jText = "相手チームの得点！\n";
              jText = jText + "ボールを取って";
          }
          this.setGUIText(s, guiNode);
          this.setJapaneseText(jText, guiNode);
          
          SceneCreator.setBallMarker(ball.getBallPosition());
    }
      
    private void setGUIText(String text, Node guiNode){
      BitmapText instructionText = new BitmapText(guiFont, false);
      instructionText.setSize(guiFont.getCharSet().getRenderedSize());
      instructionText.setText(text);
      instructionText.setLocalTranslation(resolution[0] * mainScreen + resolution[0]/2 - instructionText.getLineWidth()/2, resolution[1] * 0.9f, 0);
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
      instructionText.setLocalTranslation(resolution[0] * mainScreen + resolution[0]/2 - instructionText.getLineWidth()/2, resolution[1] * 0.8f, 0);
      instructionText.setColor(ColorRGBA.Red);
      instructionText.setName("JInstructions");
      instructionText.setBox(new Rectangle(0, 0, instructionText.getLineWidth(), instructionText.getLineHeight()));
      instructionText.setAlignment(BitmapFont.Align.Center);
      guiNode.attachChild(instructionText);
    } 
    
    public void setScoreText(Node guiNode){
      guiNode.detachChildNamed("score");
      BitmapText scoreText = new BitmapText(guiFont, false);
      scoreText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      scoreText.setText(score[0] + "-" + score[1]);
      scoreText.setLocalTranslation((resolution[0] * (mainScreen + 1))  - scoreText.getLineWidth() - 5, resolution[1], 0);
      scoreText.setColor(ColorRGBA.Red);
      scoreText.setName("score");
      guiNode.attachChild(scoreText);
  }
 
    public void setTimeText(String timeString, Node guiNode){
      guiNode.detachChildNamed("time");
      BitmapText timeText = new BitmapText(guiFont, false);
      timeText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      timeText.setText(timeString);
      timeText.setLocalTranslation((resolution[0] * (mainScreen)) , resolution[1], 0);
      timeText.setColor(ColorRGBA.Red);
      timeText.setName("time");
      guiNode.attachChild(timeText);
    }
    
}
