/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.navigation;

import VISIE.characters.Player;
import VISIE.mathfunctions.Conversions;
import VISIE.network.SensorClient;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;

/**
 *
 * @author DiveshLala
 */
public class UserNavigation {
    
    Player player;
    boolean orientation;
    private Vector3f walkDirection = new Vector3f();
    private Camera cam;
    private int turnCode = 0;
    boolean up, down, left, right;
    static float cameraRot;
    
    public UserNavigation(Player p, boolean o, Camera c){
        player = p;     
        orientation = o;
        cam = c;
      //  cameraRot = (float)Math.toRadians(180);
    }
    
    public static void setInitialCameraRotation(float rot){
        cameraRot = rot;
    }
    
    public void setDirectionKeys(boolean value, int direction){
        if(direction == 0){
            if(value){
                up = true;
            }
            else{
                up = false;
            }
        }
        if(direction == 1){
            if(value){
                down = true;
            }
            else{
                down = false;
            }
        }
        if(direction == 2){
            if(value){
                left = true;
            }
            else{
                left = false;
            }
        }
        if(direction == 3){
            if(value){
                right = true;
            }
            else{
                right = false;
            }
        }
    }
        
    public float executeNavigation(boolean isRunning, float tpf, SensorClient sensorClient){
        
        player.calculateSpeed(up || down);
        Vector3f camDir = cam.getDirection().clone().multLocal(player.getSpeed());
        walkDirection.set(0, 0, 0);

            if(orientation){
                if (up){
                    walkDirection.addLocal(camDir);
                    player.setActionState(1);
                }
                else{
                    player.setActionState(0);
                }
                if (down)  { walkDirection.addLocal(camDir.negate()); }
                if(isRunning){
                    if (left) { walkDirection.addLocal(new Vector3f(0.1f, 0, 0));}
                    if (right) { walkDirection.addLocal(new Vector3f(-0.1f, 0, 0));}
                }
            }
            else{
                if(sensorClient == null || sensorClient.getConnectionState() == 0){
                    this.keyboardNavigation(isRunning, tpf);
                }
                else {
                    this.sensorNavigation(sensorClient, tpf);
                }
            }
            player.walk(walkDirection, turnCode);
            player.setActionState(up);
            return cameraRot;
//        
//        player.calculateSpeed(up || down);
//        Vector3f camDir = cam.getDirection().clone().multLocal(player.getSpeed());
//        walkDirection.set(0, 0, 0);
//
//            if(orientation){
//                if (up){
//                    walkDirection.addLocal(camDir);
//                    player.setActionState(1);
//                }
//                else{
//                    player.setActionState(0);
//                }
//                if (down)  { walkDirection.addLocal(camDir.negate()); }
//                if(isRunning){
//                    if (left) { walkDirection.addLocal(new Vector3f(0.1f, 0, 0));}
//                    if (right) { walkDirection.addLocal(new Vector3f(-0.1f, 0, 0));}
//                }
//            }
//            else{
//                if(sensorClient == null || sensorClient.getConnectionState() == 0){
//                    this.keyboardNavigation(isRunning, tpf);
//                }
//                else {
//                    float newDir = 0;
//                    float s;
//                    Vector3f c = Vector3f.ZERO;
//
//                    if (left) { cameraRot += (tpf);}
//                    if (right) { cameraRot -= (tpf);}
//
//                       sensorClient.getSensorInformation();
//
//                        newDir = sensorClient.getModifiedWalkingDirection();    
//                        Vector3f vec = Conversions.degreesToNormalizedCoordinates(newDir + player.getCharacterRotation());
//                        s = this.calculatePlayerSpeed(sensorClient);
//                        c = new Vector3f(vec.mult(s));              
//                        player.setSpeed(s);
//                        float targetAngle = Conversions.adjustAngleTo360(newDir + player.getCharacterRotation());
//                        float modelRotationSpeed = tpf/2;
//                        float cameraRotationSpeed = tpf/3;
//
//                        if(Conversions.minDistanceBetweenAngles(targetAngle, player.getCharacterRotation()) > 75){
//                            modelRotationSpeed = tpf/3;
//                            cameraRotationSpeed = tpf/4;
//                        }
//                        else{
//                            modelRotationSpeed = tpf/3;
//                            cameraRotationSpeed = tpf/4;
//                        }
//
//
//                        if(s > 0 && Conversions.minDistanceBetweenAngles(targetAngle, player.getCharacterRotation()) > 10){
//                            if(Conversions.calculateSpinDirection(targetAngle, player.getCharacterRotation()) == 1){
//                            //    cameraRot -= tpf/3;
//                                player.setCharacterRotationRadians(player.getCharacterRotationRadians() - (modelRotationSpeed));
//                                player.turnBody();
//
//                            }
//                            else{
//                            //    cameraRot += tpf/3;
//                                player.setCharacterRotationRadians(player.getCharacterRotationRadians() + (modelRotationSpeed));
//                                player.turnBody();
//                            }
//                        }
//                        
//                       float adjustedCam = Conversions.adjustAngleTo360((float)Math.toDegrees(cameraRot));
//
//                       if(Conversions.minDistanceBetweenAngles(adjustedCam, player.getCharacterRotation()) > 1){
//                            if(Conversions.calculateSpinDirection(player.getCharacterRotation(), adjustedCam) == 1){
//                                cameraRot -= cameraRotationSpeed;
//                        //        p.setCharacterRotationRadians(p.getCharacterRotationRadians() - (tpf/3));
//                          //      p.turnBody();
//
//                            }
//                            else{
//                                cameraRot += cameraRotationSpeed;
//                         //       p.setCharacterRotationRadians(cameraRot);
//                         //       p.turnBody();
//                            }
//                        }
//
//                    if(player.getSpeed() == 0){
//                        player.setActionState(0);
//                    }
//                    else{
//                        player.setActionState(1);
//                    }
//
//                    walkDirection.addLocal(c);
//                }
//            }
//
//            player.walk(walkDirection);
//            player.setActionState(up);
//            return cameraRot;
    }
    
      private void sensorNavigation(SensorClient sensorClient, float tpf){
         
         if(!orientation){
                float newDir = 0;
                float s;
                float turningScale = 0.5f;
                Vector3f c = Vector3f.ZERO;

                 sensorClient.getSensorInformation();
                 newDir = sensorClient.getModifiedWalkingDirection();  
                 Vector3f vec = Conversions.degreesToNormalizedCoordinates(newDir + player.getFacingDirection());
                 s = this.calculatePlayerSpeed(sensorClient);
                 c = new Vector3f(vec.mult(0));
                 String isTurning = sensorClient.getTurning();
                 
                 turnCode = 0;
                 
                 if(isTurning.equals("L")){
                    cameraRot += tpf * player.getTurnSpeed() * turningScale;
                    turnCode = 1;
                 }
                 else if(isTurning.equals("R")){
                    cameraRot -= tpf * player.getTurnSpeed() * turningScale;
                    turnCode = 2;
                 }
                 else if(isTurning.equals("B") && !player.isInPossession()){
                     c = Conversions.degreesToNormalizedCoordinates(player.getFacingDirection()).multLocal(0.015f).negate();
                     turnCode = 3;
                 }
                 else if(s != 0){
                    c = Conversions.degreesToNormalizedCoordinates(player.getFacingDirection()).multLocal(s);   
                 }
                 
                 player.setCharacterRotationRadians(cameraRot);
                 player.setHeadRotationRadians(cameraRot);
                 player.turnBody();
                 player.setSpeed(s);
               
                 
             if(player.getSpeed() == 0){
                 player.setActionState(0);
             }
             else{
                 player.setActionState(1);
             }

             walkDirection.addLocal(c);
         }
                 
 //                Vector3f vec = Conversions.degreesToNormalizedCoordinates(newDir + player.getCharacterRotation());
//                 s = this.calculatePlayerSpeed(sensorClient);
//                 c = new Vector3f(vec.mult(s));              
//                 player.setSpeed(s);
//                 float targetAngle = Conversions.adjustAngleTo360(newDir + player.getCharacterRotation());
//                 float modelRotationSpeed = tpf/2;
//                 float cameraRotationSpeed = tpf/3;
//
//                 if(Conversions.minDistanceBetweenAngles(targetAngle, player.getCharacterRotation()) > 75){
//                     modelRotationSpeed = tpf/3;
//                     cameraRotationSpeed = tpf/4;
//                 }
//                 else{
//                     modelRotationSpeed = tpf/3;
//                     cameraRotationSpeed = tpf/4;
//                 }
//
//
//                 if(s > 0 && Conversions.minDistanceBetweenAngles(targetAngle, player.getCharacterRotation()) > 10){
//                     if(Conversions.calculateSpinDirection(targetAngle, player.getCharacterRotation()) == 1){
//                     //    cameraRot -= tpf/3;
//                         player.setCharacterRotationRadians(player.getCharacterRotationRadians() - (modelRotationSpeed));
//                         player.turnBody();
//
//                     }
//                     else{
//                     //    cameraRot += tpf/3;
//                         player.setCharacterRotationRadians(player.getCharacterRotationRadians() + (modelRotationSpeed));
//                         player.turnBody();
//                     }
//                 }
//
//                float adjustedCam = Conversions.adjustAngleTo360((float)Math.toDegrees(cameraRot));
//
//                if(Conversions.minDistanceBetweenAngles(adjustedCam, player.getCharacterRotation()) > 1){
//                     if(Conversions.calculateSpinDirection(player.getCharacterRotation(), adjustedCam) == 1){
//                         cameraRot -= cameraRotationSpeed;
//                 //        p.setCharacterRotationRadians(p.getCharacterRotationRadians() - (tpf/3));
//                   //      p.turnBody();
//
//                     }
//                     else{
//                         cameraRot += cameraRotationSpeed;
//                  //       p.setCharacterRotationRadians(cameraRot);
//                  //       p.turnBody();
//                     }
//                 }
     }
    
     private float calculatePlayerSpeed(SensorClient sc){
       float f = sc.getWalkingSpeed();
       if(f == 0){
           return 0;
       }
       else if(f <= 100){
           return 0.03f;
       }
       else{
           return(float)(0.03 * (f/100));
       }
   }
     
     private void sensorNavigation(){
     
     }
     
     private void keyboardNavigation(boolean isRunning, float tpf){

         turnCode = 0;
                          
         if (up){
            walkDirection.addLocal(Conversions.degreesToNormalizedCoordinates(player.getFacingDirection()).multLocal(player.getSpeed()));
            
            if(player.hasPossession){
                player.doNavigationStateChange(1);
            }
            else{
                player.doNavigationStateChange(0);
            }
            player.setSpeed(10f);
        }
        else if(down){
            walkDirection.addLocal(Conversions.degreesToNormalizedCoordinates(player.getFacingDirection()).negate().multLocal(player.getSpeed() * 0.5f)); 
            if(player.hasPossession){
                player.doNavigationStateChange(1);
            }
            else{
                player.doNavigationStateChange(0);
            }
            player.setSpeed(5f);
            turnCode = 3;
        }
        else{
            player.doNavigationStateChange(0);
            player.setSpeed(0);
        }
         
        if(isRunning){
            if (left) { 
                cameraRot += (tpf * player.getTurnSpeed());
                turnCode = 1;
            }
            if (right) { 
                cameraRot -= (tpf * player.getTurnSpeed());
                turnCode = 2;
            }
        }
        
        player.setCharacterRotationRadians(cameraRot);  
        player.setHeadRotationRadians(cameraRot);
        player.turnBody();
     }
     
     public void setPlayer(Player p){
         player = p;
     }
     
}
