/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import Basketball.Ball;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import java.util.ArrayList;
import  com.jme3.scene.Node;

/**
 *
 * @author DiveshLala
 */
public class SceneObjectManager {
    
    private static Ball ball;
    
    public SceneObjectManager(Ball b){
        ball = b;
    }
    
    public static Ball getBall(){
        return ball;
    }
    
    public void updateBallPhysics(){
//        if(SceneCharacterManager.getCharacterInPossession() instanceof VISIE.characters.Player){
//            ball
//        }
//        
    }
    
}
