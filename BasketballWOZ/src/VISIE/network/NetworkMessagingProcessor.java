/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import Basketball.Ball;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.CharacterCreator;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.mathfunctions.StringProcessor;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.characters.Character;
import VISIE.characters.KinectPlayer;
import VISIE.scenemanager.ObjectCreator;
import VISIE.scenemanager.SceneCreator;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class NetworkMessagingProcessor {

    
    public NetworkMessagingProcessor(SceneCharacterManager som){
           
    }
    
    public static ArrayList<Object> parsePlayerCreationMessage(String str){
        ArrayList<Object> initProp = new ArrayList<Object>();
        int id, indexPointer;
        float x, y, z;
        Vector3f pos;
        String modelPath;
        float modelScale;
        String s;

        id = Integer.parseInt(str.substring(str.indexOf("N") + 1, str.indexOf("(")));
        s = str.substring(str.indexOf("(") + 1, str.indexOf(","));
        indexPointer = str.indexOf(s) + s.length();
        x = Float.parseFloat(s);
        s = str.substring(indexPointer + 1, str.indexOf(",", indexPointer + 1));
        indexPointer = str.indexOf(s) + s.length();
        y = Float.parseFloat(s);
        s = str.substring(indexPointer + 1, str.indexOf(")", indexPointer + 1));
        z = Float.parseFloat(s);

        pos = new Vector3f(x, y, z);
        
        modelPath = str.substring(str.indexOf(";") + 1, str.length());
        modelScale = Float.parseFloat(str.substring(str.indexOf("!") + 1, str.indexOf(";")));
                
        initProp.add(id);
        initProp.add(pos);
        initProp.add(modelPath);
        initProp.add(modelScale);
        return initProp;
    }
    
    public static void characterCreationMessageSplitting(String str){
       
      int indexPointer = 0;

      System.out.println(str);
      
              
        String propArray[] = str.split("!");

        String characterType = propArray[0];

          if(!characterType.equals("P")){
              System.out.println("agent");
              SceneCharacterManager.flagNewAgents(str);
          }

          else{
              System.out.println("player");
              SceneCharacterManager.flagNewNUP(str);
          }
    }
    
    public static ArrayList<Object> parseCharacterCreationMessage(String infoString){
          ArrayList<Object> properties = new ArrayList<Object>();
          
          String propArray[] = infoString.split("!");
          
          int id = Integer.parseInt(propArray[1]);
          Vector3f pos = new Vector3f(StringProcessor.processVector3fString(propArray[2]));
          float modelScale = Float.parseFloat(propArray[3]);
          
          properties.add(id);
          properties.add(pos);
          properties.add(modelScale);
          
          return properties;

    }
    
   public static void parseCharacterColoringMessage(String colStr, ArrayList<Character> characterArray){
      int indexPointer = 0;
          while(indexPointer < colStr.length()){
              ArrayList<Vector3f> colourList = new ArrayList<Vector3f>();
              int id;
              int numCol = 0;
              String infoString = colStr.substring(indexPointer, colStr.indexOf(";", indexPointer) + 1);
              id = Integer.parseInt(infoString.substring(infoString.indexOf("ID") + 2, infoString.indexOf("CN")));
              numCol = Integer.parseInt(infoString.substring(infoString.indexOf("CN") + 2, infoString.indexOf(":")));

              StringBuilder s = new StringBuilder(infoString);
              String colVector;

              for(int i = 0; i < numCol; i++){
                   colVector = s.substring(s.indexOf("(") + 1, s.indexOf(")") - 1);
                   Vector3f col = StringProcessor.processVector3fString(colVector);
                   colourList.add(col);
                   s.replace(s.indexOf("("), s.indexOf(")") + 1, "*");
              }

              for(int i = 0; i < characterArray.size(); i++){
                    if(id == characterArray.get(i).getID()){
                        characterArray.get(i).setCharacterColours(colourList);
                        break;
                    }
              }
              indexPointer = colStr.indexOf(";", indexPointer + 1) + 1;
           }
    }
   
    public static void parseCharacterTextureMessage(String texStr, ArrayList<Character> characterArray){
      int indexPointer = 0;
      System.out.println(texStr);
          while(indexPointer < texStr.length()){
              ArrayList<String> texList = new ArrayList<String>();
              int id;
              int numTex = 0;
              String infoString = texStr.substring(indexPointer, texStr.indexOf(";", indexPointer) + 1);
              id = Integer.parseInt(infoString.substring(infoString.indexOf("ID") + 2, infoString.indexOf("CN")));
              numTex = Integer.parseInt(infoString.substring(infoString.indexOf("CN") + 2, infoString.indexOf(":")));
              Character c = SceneCharacterManager.getCharacterByID(id);
              StringBuilder s = new StringBuilder(infoString);
              String colVector;
              
              s.replace(0, s.indexOf(":") + 1, "");
              
              String[] textColl = s.toString().split("!");
              
              for(int i = 0; i < numTex; i++){
                  texList.add(textColl[i]);
              }      
              
              indexPointer = texStr.indexOf(";", indexPointer + 1) + 1;
           }
    }
   
   public static void parseUpdateFromServer(String str, ArrayList<Character> characterArray, int playerID){
        try{
          str = str.replaceFirst("POS", "");
          int indexPointer = 0;
          while(indexPointer < str.length()){
              String infoString = str.substring(indexPointer, str.indexOf(";", indexPointer) + 1);
              String propArray[] = infoString.split("!");
              propArray[propArray.length - 1] = propArray[propArray.length - 1].replace(";", "");
              
              Vector3f pos;
              int facingDirection;
              int actionState;
              float animationSpeed;
              int loopMode;
              int walkState;
              float walkSpeed;
              
              int id = Integer.parseInt(propArray[0]);
              String characterType = propArray[1];
              String positionsString = propArray[2];
              pos = new Vector3f(StringProcessor.processVector3fString(positionsString));
              
              facingDirection = Integer.parseInt(propArray[3].replace("FD", ""));
              actionState = Integer.parseInt(propArray[4].replace("AS", ""));
              animationSpeed = Float.parseFloat(propArray[5].replace("SD", ""));
              loopMode = Integer.parseInt(propArray[6].replace("LP", ""));
              walkState = Integer.parseInt(propArray[7].replace("WA", ""));
              walkSpeed = Float.parseFloat(propArray[8].replace("WS", ""));
             
              for(int i = 0; i < characterArray.size(); i++){
                if(characterArray.get(i).getID() == id){
                    
                    if(id != playerID){
                        if(!characterType.contains("P")){
                            BasketballAgent ba = (BasketballAgent)characterArray.get(i);
                            SceneCharacterManager.updateAgentState(ba, pos, facingDirection,actionState, animationSpeed, loopMode, walkState, walkSpeed);
                            break;
                        }
                        else{
                            NonUserPlayer nup = (NonUserPlayer)characterArray.get(i);
                            SceneCharacterManager.updateNonUserPlayerState(nup, pos, facingDirection,actionState, animationSpeed, loopMode, walkState, walkSpeed);
                            break;
                        }
                    }
                }

              }
                indexPointer = str.indexOf(";", indexPointer + 1) + 1;
              }
        }
        catch(NumberFormatException e){}
        catch(NullPointerException e){}
        catch(ArrayIndexOutOfBoundsException e){}
        catch(StringIndexOutOfBoundsException e){}
   }
    
    public static String createCharacterPositionsMessage(){
       ArrayList<Character> a = SceneCharacterManager.getCharacterArray();
       StringBuilder s = new StringBuilder();
       s.append("POS");
       for(int i = 0; i < a.size(); i++){
            s.append(a.get(i).getID()                       //1
                    + a.get(i).getCharacterType() + ":"         //A:
                    + a.get(i).getPosition() + ","              //(0,0,0), 
                    + "FD" +(int)(a.get(i).getFacingDirection())  //FD75
                    + "AS" + a.get(i).getActionState() + ";");  //AS1;
       }
       return s.toString();
    }
    
    public static String createPlayerInformationMessage(Player p){
      //  System.out.println(p.getCharacterRotation());
        
        return p.getID() + ":" +            //1:
        p.getPosition() + "," +     //(0,0,0),
        "FD" + p.getFacingDirection() +     //75;
        "AS" + p.getActionState() + 
        "SD" + p.getAnimationSpeed(1) +
        "LP"  + p.isLooped(1) + 
        "WA" + p.getLowerBodyState() +    
        "WS" + p.getLowerAnimSpeed(2) + 
         "SP" + p.getSpeed()       
        + ";";
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
              p.setKinectAnimation(vectorStrings);
         }
    }
    
    public static int[] parseBallAndStateMessage(String str){
        String[] s = str.split("!");
        int gameState = 0;
        int attacking;
        int scoreOne, scoreTwo;
        
        if(!str.equals("")){
            Ball.setBallPosition(StringProcessor.processVector3fString(s[0]));
            BasketballCharacter curPos = (BasketballCharacter)SceneCharacterManager.getCharacterInPossession();
            
            try{
                if(!s[1].equals("null")){ //ball is in possession

                    int i = Integer.parseInt(s[1]);

                    if(curPos != null && curPos.getID() != i){ // if ball is old -> new possessor
                            curPos.removePossession();
                            BasketballCharacter newPos = (BasketballCharacter)SceneCharacterManager.getCharacter(i);
                            newPos.setPossession();
                    }  
                    else if(curPos == null){ // if ball is null -> new possessor
                            BasketballCharacter newPos = (BasketballCharacter)SceneCharacterManager.getCharacter(i);
                            System.out.println(newPos);
                            newPos.setPossession();
                    }

                }
                else{ //ball not in possession
                    if(curPos != null){
                        curPos.removePossession();
                    }
                }

                gameState = Integer.parseInt(s[2]);  
                }
            catch(Exception e){}
            
        }
        
        if(gameState == 0){
            SceneCreator.hideMarker();
        }
        else{
            String[] c = s[3].split(",");
            ColorRGBA col = new ColorRGBA(Float.parseFloat(c[0]), Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3]));
            SceneCreator.displayMarker(col);
        }
        
        try{
            attacking = Integer.parseInt(s[4]);
            scoreOne = Integer.parseInt(s[5]);
            scoreTwo = Integer.parseInt(s[6]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            attacking = 0;
            scoreOne = 0;
            scoreTwo = 0;
        }
        
        int[] info = {gameState,attacking, scoreOne, scoreTwo};
        return info;
    }
    
    public static int[] parseTeamUpdate(String str){
        String[] s = str.split(",");
        int[] teamInfo = new int[2];
        teamInfo[0] = Integer.parseInt(s[0]);
        teamInfo[1] = Integer.parseInt(s[1]);
        return teamInfo;
    }
    
    public static String createPlayerSkeletonInfoMessage(Player p){
        return p.getSkeletonMessage();
//        if(p instanceof KinectPlayer){
//            return "SKEL" + p.getID() + "," + p.getSkeletonRotations();
//        }
//        else{
//            return "SKEL";
//        }
    }
    
    public static void parseServerJointData(String jointString, ArrayList<Character> characterArray, int playerID){
      jointString = jointString.replace("SKEL", "");
      String[] separate = jointString.split("!");
      for(int i = 0; i < separate.length; i ++){
          String current = separate[i];
          
          if(current.isEmpty()){
              break;
          }
          
          StringBuilder sb = new StringBuilder(current);
          int id;
          try{
              id = Integer.parseInt(sb.substring(0, sb.indexOf(",")));
              sb.delete(0, sb.indexOf(",") + 1);

              for(int j = 0; j < characterArray.size(); j++){
                  if(id != playerID && characterArray.get(j).getID() == id){
                      NonUserPlayer nup = (NonUserPlayer)characterArray.get(j);
                      String str = sb.toString();

                      ArrayList<String> vectorStrings = new ArrayList<String>();
                      String[] b = str.split(";");
                      for(int k = 0; k < b.length; k++){
                          if(b[k].length() > 0){
                                vectorStrings.add(b[k]);
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
    
    public static String[] parseNewNUPMessage(String s){
        StringBuilder builder = new StringBuilder(s);
        String[] props = new String[3];
        builder.replace(0, 3, "");
        
        int endIndex = 0;
        
        for(int i = 1; i < builder.length(); i++){
            try{
                Integer.parseInt(builder.substring(0, i));
            }
            catch(NumberFormatException e){
                endIndex = i - 1;
                break;
            }
        }
        
        int id = Integer.parseInt(builder.substring(0, endIndex));
        String modelType = builder.substring(endIndex, endIndex + 1);
        
        int vecStart = builder.indexOf("(");
        int vecEnd = builder.indexOf(")");
        String vecString = builder.substring(vecStart, vecEnd + 1);
        String fd = builder.substring(builder.indexOf("FD"), builder.indexOf("AS"));
        String as = builder.substring(builder.indexOf("AS"), builder.indexOf(";"));
        int startTex = builder.indexOf(";", builder.indexOf("AS"));
        

        props[0] = id + "!P!" + modelType + "!" + vecString + "!" + fd + "!" + as + ";";
        props[1] = builder.substring(startTex + 2);
        props[2] = id + "";
//        System.out.println(props[0]);
//        System.out.println(props[1]);
//        System.out.println(props[2]);
        return props;
    }
    }   
    

