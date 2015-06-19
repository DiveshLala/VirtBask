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
    
    
    public static AnimatedModel createAnimatedModel(String type, int id, float scale){
        Spatial s;


        if(type.contains("player")){
            s = assetManager.loadModel("Models/BPNew/playeranim.j3o");
            BPNewModel bm = new BPNewModel(s, id, 0.48f);
            bm.setModelFilePath("Models/BPNew/playeranim.j3o");
            return bm;
        }
        else if(type.contains("alan")){
            s = assetManager.loadModel("Models/BPNew/alananim.j3o");
            BPNewModel bm = new BPNewModel(s, id, 0.48f);
            bm.setModelFilePath("Models/BPNew/alananim.j3o");
            return bm;
        }
        else if(type.contains("bob")){
            s = assetManager.loadModel("Models/BPNew/bobanim.j3o");
            BPNewModel bm = new BPNewModel(s, id, 0.4f);
            bm.setModelFilePath("Models/BPNew/bobanim.j3o");
            return bm;
        }
        else if(type.contains("carl")){
            s = assetManager.loadModel("Models/BPNew/carlanim.j3o");
            BPNewModel bm = new BPNewModel(s, id, 0.4f);
            bm.setModelFilePath("Models/BPNew/carlanim.j3o");
            return bm;
        }
        else {
            System.out.println("Invalid model type! " + type);
            return null;
        }
    }
    
}
