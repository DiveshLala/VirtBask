/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.mathfunctions.Conversions;
import VISIE.Main;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.network.NetworkMessagingProcessor;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Player;
import VISIE.characters.Character;
import com.jme3.animation.LoopMode;
import java.util.ArrayList;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.BulletAppState;

/**
 *
 * @author DiveshLala
 */
public class SceneCharacterManager {
    
    private static ArrayList<Character> characterArray;
    private static ArrayList<Integer> charactersToRemove;
    private static ArrayList<String> NUPsToAdd;
    private static ArrayList<String> playerToAdd;
    private static ArrayList<String> agentsToAdd;
    private static String colourString;
    private static String textureStrings;
    private Main parentClass;
    private Node root;
    private CharacterCreator characterCreator;
    private BulletAppState bulletAppState;
    public static int IDCounter; 
    
    
    public SceneCharacterManager(Main m, ArrayList<Character> ca, CharacterCreator cc, Node n, BulletAppState bas){
        parentClass = m;
        characterArray = ca;
        NUPsToAdd = new ArrayList<String>();
        charactersToRemove = new ArrayList<Integer>();
        playerToAdd = new ArrayList<String>();
        agentsToAdd = new ArrayList<String>();
        characterCreator = cc;
        root = n;
        bulletAppState = bas;
        IDCounter = 0;
    }
    
    public static void flagNewNUP(String str){
        NUPsToAdd.add(str);
    }
        
    public static void flagDeletedCharacter(int id){
        charactersToRemove.add(id);
    }
    
    public static void flagNewPlayer(String str){
        playerToAdd.add(str);
    }
    
    public static void flagNewAgents(String str){
        agentsToAdd.add(str);
    }
    
    public static void setColourString(String str){
        colourString = str;
    }
    
    public static void setTextureString(String str){
        textureStrings = str;
    }
   
    private void addNewNonUserPlayer(String str){
        int id;
        Vector3f startPosition;
        String modelType;
        float modelScale;
        
        ArrayList<Object> properties = NetworkMessagingProcessor.parseCharacterCreationMessage(str);
        
        id = (Integer)properties.get(0);
        startPosition = (Vector3f)properties.get(1);
        modelScale = Float.parseFloat(properties.get(2).toString());
        
        NonUserPlayer nup = characterCreator.addNonUserPlayerCharacter(id, id + ".j3o", root, startPosition, modelScale);
        characterArray.add(nup);
  //      nup.setBall(SceneObjectManager.getBall());
         
    }
    
    private void addNewAgent(String str){
        
        System.out.println("adding agent...");
        
        int id;
        Vector3f startPosition;
        String modelType;
        float modelScale;
        ArrayList<Object> properties = NetworkMessagingProcessor.parseCharacterCreationMessage(str);
        
        id = (Integer)properties.get(0);
        startPosition = (Vector3f)properties.get(1);
        modelScale = Float.parseFloat(properties.get(2).toString());
        
        BasketballAgent ba = characterCreator.addBasketballCharacter(id, id + ".j3o", startPosition, modelScale);
        characterArray.add(ba);
        
    }
    
    
    private void removeCharacter(int id){
       for(int i = characterArray.size() - 1; i >= 0; i--){
           if(characterArray.get(i).getID() == id){
               characterArray.get(i).removeCharacter();
               bulletAppState.getPhysicsSpace().remove(characterArray.get(i).getPhysicsNode());
               characterArray.remove(i);
               System.out.println("character " + id + " removed");
               break;
           }       
       }
    }
    
    public void updateSceneObjects(){
                     
          if(!playerToAdd.isEmpty()){
              this.createPlayer();
           //   NetworkMessagingProcessor.parseCharacterTextureMessage(textureStrings, characterArray);
          }
          
          if(NUPsToAdd.size() > 0){
              for(int i = 0; i < NUPsToAdd.size(); i++){
                  addNewNonUserPlayer(NUPsToAdd.get(i));
              }
              NUPsToAdd.clear();
          } 
          
          if(!agentsToAdd.isEmpty()){
              for(int i = 0 ; i < agentsToAdd.size(); i++){
                  addNewAgent(agentsToAdd.get(i));
              }
              agentsToAdd.clear();  
          }
          
          if(charactersToRemove.size() > 0){
              for(int i = 0; i < charactersToRemove.size(); i++){
                  removeCharacter(charactersToRemove.get(i));
              }
              charactersToRemove.clear();
          } 
    }  
    
    public void createPlayer(){
        int id;
        Vector3f startPosition;
        String modelPath;
        float modelScale;
        Player p;
        
        ArrayList<Object> initProp = NetworkMessagingProcessor.parsePlayerCreationMessage(playerToAdd.get(0));
        id = (Integer)initProp.get(0);
        startPosition = (Vector3f)initProp.get(1);
        modelPath = initProp.get(2).toString();
        modelScale = Float.parseFloat(initProp.get(3).toString());
        
        p = characterCreator.addPlayerCharacter(id, modelPath, root, startPosition, parentClass.isKinect(), modelScale);
        characterArray.add(p);
        parentClass.setPlayer(p);
        playerToAdd.clear();
    }
    
    public static void updateNonUserPlayerState(NonUserPlayer nup, Vector3f position, int facingDirection, int actionState, float animationSpeed, int loopMode, int walkState, float walkSpeed){
        
        if(walkState == 1){
            nup.move(Conversions.degreesToNormalizedCoordinates(facingDirection), walkSpeed);
         }
        else{
            String legAnim = nup.getCharacterModel().getLegAnimationName(walkState);
            nup.playAnimation(2, legAnim, walkSpeed, LoopMode.Loop);
        }
   
        nup.setPosition(position);

        if(!Float.isNaN(facingDirection)){
          nup.turnBody(facingDirection);
          nup.setFacingDirection(facingDirection);
        }
        nup.setActionState(actionState, animationSpeed, loopMode);
       //       nup.setSpeed(walkingSpeed);
  
    }
    
    public static void updateAgentState(BasketballAgent ba, Vector3f pos, int facingDirection, int actionState, float animationSpeed, int loopMode, int walkState, float walkSpeed){
        ba.setPosition(pos);
        ba.setActionState(actionState);
        ba.playActionState(actionState, animationSpeed, loopMode, walkState, walkSpeed);
        ba.abo.setFacingDirection(facingDirection, facingDirection);
    }
    
    
   public static Character getCharacter(int id){
      Character c = null;
      for(int i = 0; i < characterArray.size(); i++){
          if(characterArray.get(i).getID() == id){
             return characterArray.get(i);
          }
      }
      return c;
  }
  
  public static Vector3f getCharacterPosition(int id){
      Vector3f pos = null;
      for(int i = 0; i < characterArray.size(); i++){
          if(characterArray.get(i).getID() == id){
              pos = characterArray.get(i).getPosition();
          }
      }
      return pos;
  }
  
  public ArrayList<Vector3f> getCharacterColour(int id){
      ArrayList<Vector3f> col = null; 
        for(int i = 0; i < characterArray.size(); i++){
          if(characterArray.get(i).getID() == id){
      //        col = characterArray.get(i).getModelColours();
          }
      }
      return col;
  }
  
    public static int generateNewID(){
      int temp = IDCounter;
      IDCounter++;
      return temp;
  }
    
    public static ArrayList<Character> getCharacterArray(){
        return characterArray;
    }
    
    public static Character getCharacterByID(int id){
      Character c = null;
      for(int i = 0; i < characterArray.size(); i++){
          if(characterArray.get(i).getID() == id){
             return characterArray.get(i);
          }
      }
      return c;
  }
    
    public static BasketballCharacter getCharacterInPossession(){
                   
        for(int i = 0; i < characterArray.size(); i++){
                BasketballCharacter bc = (BasketballCharacter)characterArray.get(i);
                if(bc.isInPossession()){
                    return bc;
                } 
        }
    
        return null;
    }
    
    public static int getPlayerID(){
        
        for(int i = 0; i < characterArray.size(); i++){
            if(characterArray.get(i) instanceof VISIE.characters.Player){
                return characterArray.get(i).getID();
            }
        } 
            return -1;
    }
        
}
