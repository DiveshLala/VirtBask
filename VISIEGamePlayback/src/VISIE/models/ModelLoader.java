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
    
    
    public static AnimatedModel createAnimatedModel(String type, int id){
        Spatial s;
        
        if(type.equals("A")){
            s = assetManager.loadModel("Models/CuteRobotModel/cuteRobot.mesh.xml");
            CuteModel cm = new CuteModel(s, id);
            return cm;
       }
        else if(type.equals("B")){
            s = assetManager.loadModel("Models/EvilRobotModel/evilRobot.mesh.xml");
            EvilModel em = new EvilModel(s, id);
            return em;
        }
        else if(type.equals("I")){
            s = assetManager.loadModel("Models/Ichigo/ichigo02.mesh.xml");
            IchigoModel im = new IchigoModel(s, id);
            return im;
       }
        else if(type.equals("S")){
            s = assetManager.loadModel("Models/CuteRobotModel/cuteRobot.mesh.xml");
            CuteModel cm = new CuteModel(s, id);
            return cm;
        }
        else if(type.equals("K")){
            s = assetManager.loadModel("Models/KinectUserModel/kinectUser.mesh.xml");
            KinectUserModel km = new KinectUserModel(s, id);
            return km;
        }
        else if(type.equals("D")){
            s = assetManager.loadModel("Models/BasketballPlayer/basketballPlayer.mesh.xml");
            BasketballPlayerModel bm = new BasketballPlayerModel(s, id);
            return bm;
        }
        else {
            System.out.println("Invalid model type! " + type);
            return null;
        }
    }
    
}
