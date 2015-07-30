/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;
import VISIE.models.AnimatedModel;
import VISIE.mathfunctions.CollisionMath;
import VISIE.mathfunctions.Conversions;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.control.CharacterControl;
import Basketball.Ball;

/**
 *
 * @author DiveshLala
 */
public class BasketballPlayer extends Player{
    
    public BasketballPlayer(CharacterControl p, float rad, int i, AnimatedModel model){
        
       super(p, rad, i, model);  
    }
    
    
}
