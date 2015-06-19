/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package VISIE.gesturerecognition;
import VISIE.models.AnimatedModel;
import com.jme3.math.Vector3f;
/**
 *
 * @author DiveshLala
 */
public class GestureRecognition {
    
    private Vector3f pointWorldCoord;
    private AnimatedModel model;
    
    
    public GestureRecognition(AnimatedModel am){
        model = am;
    }
    
    public Vector3f getPointTargetWorld(){
        pointWorldCoord = model.calculatePointTarget();
        return pointWorldCoord;
    }

}
