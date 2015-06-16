/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import Basketball.GameManager;
import Basketball.GameStateManagement;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.network.KinectClient;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Character;
import VISIE.characters.KinectPlayer;
import VISIE.characters.Player;
import VISIE.scenemanager.SceneCreator;
import VISIE.scenemanager.SceneObjectManager;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class NetworkMessagingProcessor {

    
    public NetworkMessagingProcessor(SceneCharacterManager som){
        
    }
    
    public static void parseNonUserPlayerMessage(String positionMessage){
        
      String str = positionMessage;
      int id;
      float x,y,z;
      Vector3f pos;
      float facingDirection;
      int actionState;
      float animSpeed;
      int loopMode;
      int lowerState;
      float lowerAnimSpeed;
      float playerSpeed;
      
      
      try{
          id = Integer.parseInt(str.substring(0, str.indexOf(":")));
          String positionString = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
          x = Float.parseFloat(positionString.substring(0, positionString.indexOf(",")));
          int posIndex = positionString.indexOf(",") + 1;
          y = Float.parseFloat(positionString.substring(posIndex + 1, positionString.indexOf(",", posIndex + 1)));
          posIndex = positionString.indexOf(",", posIndex + 1);
          z = Float.parseFloat(positionString.substring(posIndex + 1));
          pos = new Vector3f(x,y,z);
          
          facingDirection = Float.parseFloat(str.substring(str.indexOf("FD") + 2, str.indexOf("AS")));
          actionState = Integer.parseInt(str.substring(str.indexOf("AS") + 2, str.indexOf("SD")));
          animSpeed = Float.parseFloat(str.substring(str.indexOf("SD") + 2, str.indexOf("LP")));
          loopMode = Integer.parseInt(str.substring(str.indexOf("LP") + 2, str.indexOf("WA")));
          
          lowerState = Integer.parseInt(str.substring(str.indexOf("WA") + 2, str.indexOf("WS")));
          lowerAnimSpeed = Float.parseFloat(str.substring(str.indexOf("WS") + 2, str.indexOf("SP")));
          playerSpeed = Float.parseFloat(str.substring(str.indexOf("SP") + 2, str.indexOf(";")));
          
          SceneCharacterManager.updateNonUserPlayerState(id, pos, facingDirection, actionState, animSpeed, loopMode, lowerState, lowerAnimSpeed, playerSpeed);
      }
      catch(NumberFormatException e){System.out.println(e);}
      catch(NullPointerException e){System.out.println(e);}
      catch(StringIndexOutOfBoundsException e){
        //  System.out.println(e);
      }            
    } 
    
    public static String createCharacterPositionsMessage(){
       ArrayList<Character> a = SceneCharacterManager.getCharacterArray();
       StringBuilder s = new StringBuilder();
       s.append("POS");
       for(int i = 0; i < a.size(); i++){
            s.append(a.get(i).getID() + "!"                       //1
                    + a.get(i).getCharacterType() + "!"         //P:
                    + a.get(i).getPosition() + "!"              //(0,0,0), 
                    + "FD" +(int)(a.get(i).getFacingDirection()) + "!"  //FD75
                    + "AS" + a.get(i).getActionState() + "!"
                    + "SD" + a.get(i).getAnimationSpeed(1) + "!"
                    + "LP" + a.get(i).isLooped(1) + "!"
                    + "WA" + a.get(i).getWalkingState() + "!"
                    + "WS" + a.get(i).getAnimationSpeed(2) + "!"
                    + ";");  
       }
       return s.toString();
    }
    
    
    public static void parseKinectJointMessage(Player p){
        
          String s = KinectClient.getKinectData();
          ArrayList<String> vectorStrings = new ArrayList<String>();
          String[] bob = s.split("!");
          for(int i = 0; i < bob.length; i++){
              if(bob[i].length() > 0){
                    vectorStrings.add(bob[i]);
              }
          }
          
        if(!vectorStrings.isEmpty()){
            if(p.isKinectPlayer()){
              KinectPlayer kp = (KinectPlayer)p;  
              kp.setKinectAnimation(vectorStrings);
            }
         }
    }
    
    public static void parseClientJointData(String jointString){
        
      ArrayList<Character> characterArray = SceneCharacterManager.getCharacterArray();
      StringBuilder sb = new StringBuilder(jointString);
      sb.delete(0, 4);
      int id;
      
      if(sb.length() > 0){ // only parse if skeleton data is valid
        try{
            id = Integer.parseInt(sb.substring(0, sb.indexOf(",")));
            sb.delete(0, sb.indexOf(",") + 1);

            for(int i = 0; i < characterArray.size(); i++){
                if(characterArray.get(i).getID() == id){
                    NonUserPlayer nup = (NonUserPlayer)characterArray.get(i);
                    String str = sb.toString();

                    ArrayList<String> vectorStrings = new ArrayList<String>();
                    String[] b = str.split(";");
                    for(int j = 0; j < b.length; j++){
                        if(b[j].length() > 0){
                              vectorStrings.add(b[j]);
                        }
                    }
                    nup.setSkeletonJoints(vectorStrings);
                    break;
                }     
            }
        }
        catch(NumberFormatException e){System.out.println(e);}
        catch(NullPointerException e){System.out.println(e);}
        catch(StringIndexOutOfBoundsException e){System.out.println(e);}
      }
    
    }
    
    public static void parseClientGestureData(String s){
        
        int id;
        String gesture;
        
        String str = s.replaceFirst("GEST", "");
        String[] info = str.split(",");    
        id = Integer.parseInt(info[0]);
        gesture = info[1];
        
        Character c = SceneCharacterManager.getCharacterByID(id);
        c.doNUPGestureActivity(gesture);
        
//        if(c instanceof VISIE.characters.NonUserPlayer){
//            NonUserPlayer nup = (NonUserPlayer)c;
//            nup.doGestureActivity(gesture);
//        }
    
    }
    
    public static String createPlayerSkeletonInfoMessage(){
        StringBuilder s = new StringBuilder();
        ArrayList<Character> characterArray = SceneCharacterManager.getCharacterArray();
        for(int i = 0; i < characterArray.size(); i++){
            if(characterArray.get(i).isKinectPlayer()){//sending Kinect skeleton info if kinect player
                Character c = characterArray.get(i);
                s.append(c.getID() + "," + c.getSkeletonRotations() + "!");
            }
        } 
        return "SKEL" + s;
    }
    
    public static String createCharacterInitializationMessage(){
       ArrayList<Character> a = SceneCharacterManager.getCharacterArray();
       StringBuilder s = new StringBuilder();
       for(int i = 0; i < a.size(); i++){
            s.append(a.get(i).getID() + "!"                       //1
                    + a.get(i).getCharacterType() + "!"         //R:
                    + a.get(i).getModelType() + "!"         //A:
                    + a.get(i).getPosition() + "!"              //(0,0,0), 
                    + "FD" +(int)(a.get(i).getFacingDirection()) + "!"  //FD75
                    + "AS" + a.get(i).getActionState() + ";");  //AS1;
       }
       return s.toString();
    }
    
    public static String createCharacterInformationMessage(int id){
       Character c = SceneCharacterManager.getCharacterByID(id);
       StringBuilder s = new StringBuilder();
       s.append(c.getID()                       //1
        + c.getModelType() + ":"         //A:
        + c.getPosition() + ","              //(0,0,0), 
        + "FD" +(int)(c.getFacingDirection())  //FD75
        + "AS" + c.getActionState() + ";"   //AS1;
        + "!" + createClientColoursString(id));
       return s.toString();
    }
    
    public static String createClientColoursString(int id){
      Character c = SceneCharacterManager.getCharacterByID(id);
      StringBuilder s = new StringBuilder();
        if(c.getModelTextures() != null){
            ArrayList<String> cols = c.getModelTextures();
            s.append("ID" + c.getID());             //ID1 
            s.append("CN" + cols.size() + ":");            //CN2: 
              for(int j = 0; j < cols.size(); j++){
                s.append(cols.get(j));
              }
            s.append(";");
        }
       return s.toString();
    }
    
    public static String createTextureMessage(){
       ArrayList<Character> a = SceneCharacterManager.getCharacterArray();
       StringBuilder s = new StringBuilder();
        for(int i = 0; i < a.size(); i++){
            if(a.get(i).getModelTextures() != null){
                ArrayList<String> cols = a.get(i).getModelTextures();
                s.append("ID" + a.get(i).getID());             //ID1 
                s.append("CN" + cols.size() + ":");            //CN2: 
                  for(int j = 0; j < cols.size(); j++){
                    s.append(cols.get(j));
                  }
                s.append(";");
            }
       }
//       System.out.println(s.toString());
       return s.toString();
    }
    
    public static String createTextureMessage(int id){
       ArrayList<Character> a = SceneCharacterManager.getCharacterArray();
       StringBuilder s = new StringBuilder();
        for(int i = 0; i < a.size(); i++){
            if(a.get(i).getID() == id && a.get(i).getModelTextures() != null){
                ArrayList<String> cols = a.get(i).getModelTextures();
                s.append("ID" + a.get(i).getID());             //ID1 
                s.append("CN" + cols.size() + ":");            //CN2: 
                for(int j = 0; j < cols.size(); j++){
                    s.append(cols.get(j));
                 }
                break;
            }
       }
//       System.out.println(s.toString());
       return s.toString();
    }
    
    public static String createBallMessage(){
        return "BALL" + SceneObjectManager.getBall().getBallPosition().toString();    
    }
    
    public static String createGameStateMessage(int timeRemaining){
        
        int[] score = GameStateManagement.getScore();
        
        return GameManager.getGameState() + "!" + 
                SceneCreator.getMarkerColor() + "!" + 
                GameManager.getAttackingTeam() + "!" +
                score[0] + "!" +
                score[1] + "!" +
                timeRemaining;
    }
    
    public static ArrayList<String> parseJointData(String jointString){

      try{
            String str = jointString.toString();

            ArrayList<String> vectorStrings = new ArrayList<String>();
            String[] b = str.split(";");
            for(int j = 0; j < b.length; j++){
                if(b[j].length() > 0){
                      vectorStrings.add(b[j]);
                }
            }
            return vectorStrings;
      }
      catch(NumberFormatException e){System.out.println(e);}
      catch(NullPointerException e){System.out.println(e);}
      catch(StringIndexOutOfBoundsException e){System.out.println(e);}
      
        return null;
    }
}
