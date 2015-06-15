/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.scenemanager;
import VISIE.mathfunctions.Conversions;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.math.Quaternion;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.RenderManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author huang
 */
public class CameraView {
    
    private Camera cam;
    private Camera[] cameraArray;
    private ArrayList<ViewPort> viewPorts;
    private float cameraFOV;
    private int mainScreen;
    private int numberOfDisplays;
    private int displayWidth;
    private int displayHeight;
    private float angleOffset;
    private float cameraFacingDirection;
    private boolean fixedOrientation;
    
    public CameraView(Camera c, boolean isImmersive, boolean fo){
        cam = c;
        fixedOrientation = fo;
    }
       
    
    //facingDirection in degrees
    public void showViewPoint(Vector3f characterPosition, float rotationAngle, int camState){
        Vector3f offset = Vector3f.ZERO;

        if(camState == 1){
            offset = Conversions.degreesToNormalizedCoordinates(rotationAngle).mult(-5);
        }
        else if(camState == 2){
            offset = Conversions.degreesToNormalizedCoordinates(rotationAngle).mult(-15);
        }
        
        cam.setLocation(characterPosition.add(Vector3f.UNIT_Y.mult(3)).add(offset));
        Quaternion q = cam.getRotation().fromAngleAxis((float)Math.toRadians(rotationAngle), new Vector3f(0,1,0));
        cam.setAxes(q);
    }
    
    //facingDirection in degrees
    public void showImmersiveViewPoint(Vector3f characterPosition, float facingDirection, int camState){
        Vector3f offset = Vector3f.ZERO;
        float characterRotationAngle;
        
        if(fixedOrientation)
            characterRotationAngle = facingDirection;
        else
            characterRotationAngle = 0;
        
        if(camState == 1){
                offset = Conversions.degreesToNormalizedCoordinates(facingDirection).mult(-4.5f);
        }
        
        else if(camState == 2){
                 offset = Conversions.degreesToNormalizedCoordinates(facingDirection).mult(-10);
        }
        
        for(int i = 0; i < cameraArray.length; i++){
                cameraArray[i].setLocation(characterPosition.add(Vector3f.UNIT_Y.mult(0).add(offset)));
         }

        for(int i = 0; i < cameraArray.length; i++){
                Quaternion camQuat = cameraArray[i].getRotation().fromAngleAxis((float)Math.toRadians(facingDirection + ((mainScreen - i) * angleOffset) - characterRotationAngle), new Vector3f(0,1,0));
                cameraArray[i].setAxes(camQuat);       
        }
        
        Quaternion q = cam.getRotation().fromAngleAxis((float)Math.toRadians(facingDirection - characterRotationAngle), new Vector3f(0,1,0));
        cam.setAxes(q);
          
    }
    
    //in degrees
    public void setCameraFacingDirection(float dir){
        cameraFacingDirection = dir;
    }
        
  public void setupImmersiveCameras(RenderManager renderManager, Node rootNode, int numDis, int dh, int dw, float fov, int ms){
      
      cameraFOV = fov; // for 6-display view where 5 displays gives 180-degree field of view (240 degree total)
      mainScreen = ms;
      numberOfDisplays = numDis;
      displayHeight = dh;
      displayWidth = dw;
    
      cam.setViewPort((float)(mainScreen)/numberOfDisplays, (float)(mainScreen + 1)/numberOfDisplays, 0f, 1.0f);
      cam.setFrustumPerspective(cameraFOV, (float)displayWidth/displayHeight, 0.1f, 1000);
      cameraArray = new Camera[numberOfDisplays];
      cameraArray[mainScreen] = cam;
      viewPorts = new ArrayList<ViewPort>();
            
      for(int i = 0; i < cameraArray.length; i++){
          if(i != mainScreen){
              Camera newCam = cam.clone();
              newCam.setViewPort((float)(i)/numberOfDisplays, (float)(i + 1)/numberOfDisplays, 0, 1.0f);
             
//              System.out.println(cameraFOV + " " + displayWidth + " " + displayHeight);
              newCam.setFrustumPerspective(cameraFOV, (float)displayWidth/displayHeight, 0.1f, 1000);

              ViewPort view = renderManager.createMainView("View of Cam " + i, newCam);
              view.setClearFlags(true, true, true);
              view.attachScene(rootNode);
              view.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
              cameraArray[i] = newCam;
              viewPorts.add(view);
          }
       }
      
      angleOffset = 45;
      //  angleOffset = 360/numberOfDisplays; //
  }
  
  public void changeImmersiveFieldOfView(float fov){
            
      for(int i = 0; i < cameraArray.length; i++){
     //     cameraArray[i].resize(displayWidth, displayHeight, false);
          cameraArray[i].setFrustumPerspective(fov, (float)displayWidth/displayHeight, 0.1f, 1000);      
      }
      
      cameraFOV = fov;
  }
  
  public float getCameraFOV(){
      return cameraFOV;
  }
  
  public void blackoutViewPorts(){
      for(ViewPort vp:viewPorts){
          vp.setEnabled(false);
      }
  }
  
    public void enableViewPorts(){
      for(ViewPort vp:viewPorts){
          vp.setEnabled(true);
      }
  }
    
    
    
    
}
