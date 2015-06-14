/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.Games;

import VISIE.VISIEFileReader;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballTeam;
import VISIE.characters.Character;
import VISIE.characters.ImitationAgent;
import VISIE.characters.KinectPlayer;
import VISIE.characters.PassingGameAgent;
import VISIE.characters.Team;
import VISIE.navigation.UserNavigation;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;
import com.jme3.math.Vector3f;
import java.util.Arrays;

/**
 *
 * @author Divesh
 */
public class ImitationGame extends Game{
    
    private ArrayList<String> playerRecord = new ArrayList<String>();
    private int recordWindow = 300;
    private int delayMilli = 2000;
    private ImitationAgent imAgent;
    
    public ImitationGame(){
    
    }
   
    @Override
    public void setupKeys(){
        super.setupKeys();
        inputManager.addMapping("PassTrigger", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addListener(this, "PassTrigger");
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf){
        if (binding.equals("PassTrigger") && !value) { 
            imAgent.triggerRecording(playerRecord, "Pass", recordWindow);
            KinectPlayer kp = (KinectPlayer)p;
            kp.throwPass();
        }
    }
    
    public void createTeamsAndAgents(){
        
           int agentsInTeam = 1;
            
           BasketballTeam team = new BasketballTeam(agentsInTeam, characterCreator, 0);
           teams.add(team);
           p.setTeam(team);
           team.addToTeam(p);
           
           p.setPossession();
           p.setPosition(new Vector3f(10, 5, -15));
           UserNavigation.setInitialCameraRotation((float)Math.toRadians(-90));
           
           double d = Math.random();
           String[] tex = new String[6];
           
           if(d < 0.5f){
               tex[0] = "shirtblueBob.JPG";
               tex[1] = "Bob.jpg";
               tex[2] = "skinBob.jpg";
               tex[3] = "templateshirt.JPG";
               tex[4] = "Carl.jpg";
               tex[5] = "skinCarl.jpg";
           }
           else{
               tex[0] = "templateshirt.JPG";
               tex[1] = "Carl.jpg";
               tex[2] = "skinCarl.jpg";
               tex[3] = "shirtblueBob.JPG";
               tex[4] = "Bob.jpg";
               tex[5] = "skinBob.jpg";
           }
           
           imAgent = (ImitationAgent)this.createAgent(team, true);
           imAgent.setShirtTexture(tex[0]);
           imAgent.setFaceAndSkinTexture(tex[1], tex[2]);
           imAgent.planner.setTargetPosition(imAgent.get2DPosition());
           imAgent.setPlayer(p);
           BasketballAgent bp = this.createAgent(team, false);
           bp.setShirtTexture(tex[3]);
           bp.setFaceAndSkinTexture(tex[4], tex[5]);
           
           
    }
    
    private BasketballAgent createAgent(BasketballTeam team, boolean isImit){
           
            BasketballAgent bp;
            String[][] config = VISIEFileReader.readAgentConfigurations(0, 1);
           
            String[] textureArray = config[0];
            String modelType = textureArray[0];
            Vector3f startPosition = new Vector3f(10, 5, 0); 
            String[] bpTex = Arrays.copyOfRange(textureArray, 1, 6);
            if(isImit){
                bp = (ImitationAgent)characterCreator.addAgentCharacter("Imitation",SceneCharacterManager.IDCounter, 1, modelType, startPosition, 0.48f);
                bp.setPosition(new Vector3f(-10, 5, -15));
                bp.abo.setFacingDirection(90);
            }
            else{
                bp = characterCreator.addAgentCharacter("Passing", SceneCharacterManager.IDCounter, 1, modelType, startPosition, 0.48f);
                bp.setPosition(new Vector3f(-10, 5, -25));
                bp.abo.setFacingDirection(90);
            }
            characterArray.add(bp); 
            SceneCharacterManager.IDCounter++;
            bp.setBehaviorState(1);
            bp.setTeam(team);
            bp.setBall(ball);
    
            Game.logNewPlayer(bp);   
            
            return bp;
    }
    
    public void initializeGlobalSettings(){
          membersPerTeam = 2;
          numberOfTeams = 1;
    
    }
    public void customizeEnvironment(){
    
    }
    
//    @Override
//    public void updatePlayerSkeleton(){
//        super.updatePlayerSkeleton();
//        
//    }
    
    public void updateGame(float tpf){
            super.playerMovement(tpf);
            p.updateGestures();
            agentThread.updateAgents(tpf);
            agentThread.updateBall();
            sceneCharacterManager.updateSceneObjects(); 
            sceneObjectManager.updateBallPhysics();
            
            if(p.isKinectPlayer()){
                this.updatePlayerSkeleton();
                this.recordPlayerMovement();
                KinectPlayer kp = (KinectPlayer)p;
                if(kp.getCurrentGestureName().equals("Pass") && playerRecord.size() >= recordWindow){
                    imAgent.triggerRecording(playerRecord, "Pass", recordWindow);
                }
                imAgent.recordGesture(playerRecord.get(playerRecord.size() - 1));  
            }
    }
    
    private void recordPlayerMovement(){
        String pose = p.getSkeletonRotations();
        
        if(playerRecord.size() >= recordWindow){
            playerRecord.remove(0);
        }
        playerRecord.add(pose);
    }  
}
