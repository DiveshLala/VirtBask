/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.models;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author DiveshLala
 */
public class ModelLoader {
    
    private static AssetManager assetManager;
    
    public ModelLoader(AssetManager am){
        assetManager = am;
    }
    
    public static Spatial loadModel(String modelpath){
        Spatial s = assetManager.loadModel(modelpath);
        return s;
    }
    
    
    public static AnimatedModel createAnimatedModel(String filename, int id, float modelScale){
        Spatial s;
        
        s = assetManager.loadModel("Models/" + filename);
        BPNewModel model = new BPNewModel(s, id, modelScale);
        return model;
        
    }
    
}
