/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.Games.Game;
import VISIE.mathfunctions.Conversions;
import VISIE.Main;
import VISIE.VISIEFileReader;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.characters.NonUserPlayer;
import VISIE.characters.Character;
import com.jme3.animation.LoopMode;
import java.util.ArrayList;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author DiveshLala
 */
public class SceneCharacterManager {
    
    private static ArrayList<Character> characterArray;
    private static ArrayList<Integer> charactersToRemove;
    private static ArrayList<String> NUPsToAdd;
    private Game parentClass;
    private Node root;
    private CharacterCreator characterCreator;
    private BulletAppState bulletAppState;
    public static int IDCounter; 
    
    
    public SceneCharacterManager(Game m, ArrayList<Character> ca, CharacterCreator cc, Node n, BulletAppState bas){
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
    
    private void addNewNonUserPlayer(String s){
                
         //get model type info and ID
         String[] info = s.split("!");
         int id = Integer.parseInt(info[0]);
         String kinectState = info[1];
         String modelType = "Alan";
                 
         Vector3f startPosition = CharacterCreator.generateStartingPosition(1);
         startPosition.setY(5);
         NonUserPlayer nup = characterCreator.addNonUserPlayerCharacter(id, modelType, root, startPosition, 0.48f);
         characterArray.add(nup);
         nup.setBall(SceneObjectManager.getBall());
         parentClass.setNUPToTeam(nup);
         if(kinectState.equals("true")){
             nup.setKinectNUP();
         }
         Game.logNewPlayer(nup);
         System.out.println("added " + id);
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
    
    public void updateSceneObjects(){
          if(NUPsToAdd.size() > 0){
              System.out.println(NUPsToAdd.size());
              for(int i = 0; i < NUPsToAdd.size(); i++){
                  addNewNonUserPlayer(NUPsToAdd.get(i));
              }
              NUPsToAdd.clear();
          } 
          
         if(charactersToRemove.size() > 0){
              for(int i = 0; i < charactersToRemove.size(); i++){
                  removeCharacter(charactersToRemove.get(i));
              }
              charactersToRemove.clear();
          }
            
    }
    
    public static void updateNonUserPlayerState(int id, Vector3f position, float facingDirection, int actionState, float animSpeed, int loopMode, int lowerState, float lowerAnimSpeed, float walkingSpeed){
                  
        for(int i = 0; i < characterArray.size(); i++){
              if(characterArray.get(i).getID() == id){
                  NonUserPlayer nup = (NonUserPlayer)characterArray.get(i);
                  
                  String lowerAnim = nup.getModelAnimationName(lowerState);
                  nup.playAnimation(2, lowerAnim, animSpeed, LoopMode.Loop);
                                    
                  //only move if action state is moving
                 if(lowerAnim.equals("walk")){
                    nup.move(Conversions.degreesToNormalizedCoordinates(facingDirection));
                    nup.setPosition(position);
                 }
                 else if(lowerAnim.equals("stepBack")){
                     nup.move(Conversions.degreesToNormalizedCoordinates(facingDirection).negate());
                     nup.setPosition(position);
                 }
                 
                 if(!Float.isNaN(facingDirection)){
                    nup.turnBody(facingDirection);
                    nup.setFacingDirection(facingDirection);
                 }
                  nup.setActionState(actionState);
                  nup.setAnimation(actionState, animSpeed, loopMode);
                  nup.setSpeed(walkingSpeed);
                  if(!nup.isKinectPlayer()){
                      nup.handleNUPPredefinedGestures();
                  }
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
    
    public static int getNumberOfNUPS(){
        
        int nups = 0;
        
        for(int i = 0; i < characterArray.size(); i++){
            if(characterArray.get(i).isNUP()){
                nups++;
            }
        }
        
        return nups;
    }
    
    public void updateCharacterSounds(){
        
       for(Character c:characterArray){
           BasketballCharacter bc = (BasketballCharacter)c;
           bc.playWalkingSound();
       }    
    }
    
    public void showCollisionLines(){
                
        ArrayList<Character> chars = getCharacterArray();
        for(Character c:chars){
            if(c instanceof VISIE.characters.BasketballAgent){
                BasketballAgent ba = (BasketballAgent)c;
                ArrayList<Geometry> geoms = ba.getCollisionLines();
                for(int i = 0; i < geoms.size(); i++){
                   Geometry geo = geoms.get(i);
                   Material orange = new Material(SceneCreator.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                   orange.setColor("Color", ColorRGBA.Blue);
                   geo.setMaterial(orange);
                   root.detachChildNamed("collLine" + ba.getID() + " " + i);
                   geo.setName("collLine" + ba.getID() + " " + i);
                   root.attachChild(geo);
                }
            }
        }
    }
    
    public void showAgentTargetPositions(int id){
        
        BasketballAgent ba = (BasketballAgent)SceneCharacterManager.getCharacterByID(id);
        Vector3f target = ba.planner.getTargetPosition();
        
        Sphere s = new Sphere(4,4, 1);
        Geometry g = new Geometry(ba.getID() + " target", s);
        g.setLocalTranslation(target);
        Material mat = new Material(SceneCreator.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        
        ColorRGBA col;
        if(id == 1){
             col = ColorRGBA.Cyan;
        }
        else if(id ==2){
            col = ColorRGBA.Red;
        }
        else if(id ==3){
            col = ColorRGBA.Green;
        }
        else{
            col = ColorRGBA.Pink;
        }
        mat.setColor("Color", col);
        g.setMaterial(mat);
        root.detachChildNamed(ba.getID() + " target");
        root.attachChild(g);
    
    }
        
}
