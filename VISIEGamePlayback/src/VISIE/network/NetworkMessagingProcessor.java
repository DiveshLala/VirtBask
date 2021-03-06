/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Character;
import VISIE.characters.Player;
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
      float walkingSpeed;

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
          actionState = Integer.parseInt(str.substring(str.indexOf("AS") + 2, str.indexOf("WS")));
          walkingSpeed = Float.parseFloat(str.substring(str.indexOf("WS") + 2, str.indexOf(";")));
          
          SceneCharacterManager.updateNonUserPlayerState(id, pos, facingDirection, actionState, walkingSpeed);
      }
      catch(NumberFormatException e){System.out.println(e);}
      catch(NullPointerException e){System.out.println(e);}
      catch(StringIndexOutOfBoundsException e){System.out.println(e);}            
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
                    + "AS" + a.get(i).getActionState() + ";");  //AS1;
       }
       return s.toString();
    }
    
    
    
    public static void parseClientJointData(String jointString){
      
      ArrayList<Character> characterArray = SceneCharacterManager.getCharacterArray();
      StringBuilder sb = new StringBuilder(jointString);
      sb.delete(0, 4);
      int id;

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
    
//    public static String createSkeletonInfoMessage(){
//        StringBuilder s = new StringBuilder();
//        ArrayList<Character> characterArray = SceneCharacterManager.getCharacterArray();
//        for(int i = 0; i < characterArray.size(); i++){
//            if(characterArray.get(i).getCharacterType() == "P"){
//                Character c = characterArray.get(i);
//                s.append(c.getID() + "," + c.getSkeletonRotations() + "!");
//            }
//        } 
//        return "SKEL" + s;
//    }
    
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
       System.out.println(s.toString());
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
       System.out.println(s.toString());
       return s.toString();
    }
    
    public static String createBallMessage(){
        System.out.println(SceneObjectManager.getBall().getBallPosition().toString());
        return "BALL" + SceneObjectManager.getBall().getBallPosition().toString();    
    }
    
    //string in format (x, y, z) to Vector3f
    public static Vector3f stringToVector(String s){
        String[] dim = s.split(",");
        return new Vector3f(Float.parseFloat(dim[0].substring(1)), Float.parseFloat(dim[1]), Float.parseFloat(dim[2].substring(0, dim[2].length() - 1)));
    }
}
