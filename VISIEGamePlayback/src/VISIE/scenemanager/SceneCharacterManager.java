/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.mathfunctions.Conversions;
import VISIE.Main;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Character;
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
        characterCreator = cc;
        root = n;
        bulletAppState = bas;
        IDCounter = 0;
    }
    
    public static void flagNewNUP(String s){
        NUPsToAdd.add(s);
    }
    
    public static void flagDeletedCharacter(int id){
        charactersToRemove.add(id);
    }
    
    
    private void removeCharacter(int id){
       for(int i = characterArray.size() - 1; i >= 0; i--){
           if(characterArray.get(i).getID() == id){
      //         characterArray.get(i).removeCharacter();
               bulletAppState.getPhysicsSpace().remove(characterArray.get(i).getPhysicsNode());
               characterArray.get(i).removeExistence();
               characterArray.remove(i);
               System.out.println("character " + id + " removed");
               break;
           }       
       }
    }
        
    public static void updateNonUserPlayerState(int id, Vector3f position, float facingDirection, int actionState, float walkingSpeed){
          
        for(int i = 0; i < characterArray.size(); i++){
              if(characterArray.get(i).getID() == id){
                  NonUserPlayer nup = (NonUserPlayer)characterArray.get(i);
                  
                  //only move if action state is moving
                 if(actionState != 0){
                    nup.move(Conversions.degreesToNormalizedCoordinates(facingDirection));
                    nup.setPosition(position);
                  }
                  nup.turnBody(facingDirection);
                  nup.setFacingDirection(facingDirection);
                  nup.setActionState(actionState);
                  nup.setAnimation(actionState);
                  nup.setSpeed(walkingSpeed);
                  break;
              }     
          }
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
              col = characterArray.get(i).getModelColours();
          }
      }
      return col;
  }
  
    public ArrayList<String> getCharacterTexture(int id){
      ArrayList<String> col = null; 
        for(int i = 0; i < characterArray.size(); i++){
          if(characterArray.get(i).getID() == id){
              col = characterArray.get(i).getModelTextures();
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
    
    public static BasketballCharacter getCharacterInPossession(){
                   
        for(int i = 0; i < characterArray.size(); i++){
                BasketballCharacter bc = (BasketballCharacter)characterArray.get(i);
                if(bc.isInPossession()){
                    return bc;
                } 
        }
    
        return null;
    }    
        
}
