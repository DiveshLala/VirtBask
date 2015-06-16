/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.KinectPlayer;
import VISIE.characters.Player;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.models.BasketballPlayerModel;

/**
 *
 * @author Divesh
 */
public class KinectGesture extends PlayerGesture{
    
    String currentKinectGesture = "";
    boolean isDribbling = false;
    boolean isShooting = false;
    boolean isPassing = false;
    
    public KinectGesture(Player player, AnimatedModel am, Ball b){
        super(player, am, b);
    }
    
    public void initializePlayerGesture(Ball b){

    }
    
    public void setBall(Ball b){
        ball = b;
    }
    
    public void playKinectGestures(){
               
    if(currentKinectGesture.equals("Shoot") && p.isInPossession()){
            System.out.println("current gesture shoot");
            
            this.setShooting(true);
            currentKinectGesture="";
        }
        else if(currentKinectGesture.equals("Shoot") && isPassing){
            
            System.out.println("changed");
            
            this.setPassing(false);
            this.setShooting(true);
            currentKinectGesture="";
            
        }
        else if(currentKinectGesture.equals("Dribble Right") && p.isInPossession()){
            
            this.setDribbling(true);
            
            currentKinectGesture = "";
            
            KinectPlayer kp = (KinectPlayer)p;
            kp.resetUndribbledDistance();
     //       System.out.println(p.getHandPosition(1).distance(ball.getBallPosition()));
            
            //ball is in hand
//            if(p.getHandPosition(1).distance(ball.getBallPosition()) < 1f){
//                isDribbling = true;
//            }
//     //       ball is being dribbled - put back in hand on way up
//            else{
//                isDribbling = false;
//                currentKinectGesture = "";
//            }
        }
        
        else if(currentKinectGesture.equals("Pass") && p.isInPossession()){
            
            System.out.println("current gesture pass");
            isPassing = true;
            currentKinectGesture = "";
        }        
        else{//removes passing and shooting status so that player is free to use gestures again
           if(isPassing && p.getHandPosition(1).distance(ball.getBallPosition()) > 5f){
               this.setPassing(false);
           }            
           if(isShooting && p.getHandPosition(1).distance(ball.getBallPosition()) > 5f){
               this.setShooting(false);
           }
           if(isDribbling && p.getHandPosition(1).distance(ball.getBallPosition()) > 2f){
               this.setDribbling(false);
           }
           if(currentKinectGesture.equals("Pass") && !p.isInPossession()){
               currentKinectGesture = "";
           }
        }
    }
    
     public void setKinectGesture(String animationName){
           currentKinectGesture = animationName;
     }
     
      public String getCurrentKinectGesture(){
         return currentKinectGesture;
     }
      
      public void setShooting(boolean b){
          isShooting = b;
      }
    
     public void setPassing(boolean b){
          isPassing = b;
      }
     
     public void setDribbling(boolean b){
         isDribbling = b;
     }
     
     public boolean getShooting(){
         return isShooting;
     }
     
     public boolean getPassing(){
         return isPassing;
    }
     
     public boolean getDribbling(){
         return isDribbling;
     }
}
