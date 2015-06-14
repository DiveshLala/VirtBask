/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballCharacter;
import VISIE.characters.KinectPlayer;
import VISIE.characters.Player;
import VISIE.mathfunctions.Conversions;
import VISIE.models.AnimatedModel;
import VISIE.characters.Character;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.SceneCharacterManager;

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
 //       playerGesture = new PlayerGesture(this, characterModel, b);
    }
    
    public void playKinectGestures(){
           
        if(currentKinectGesture.equals("Shoot") && p.isInPossession()){
            System.out.println("current gesture shoot");
            
            if(ball.isValidShoot()){
                p.removePossession();
                p.recordShootContexts();
                this.setShooting(true);
            }
            else{
                System.out.println("cant shoot");
            }
            currentKinectGesture="";
        }
        else if(currentKinectGesture.equals("Shoot") && isPassing){
            System.out.println("changing to shoot...");
            
            this.setPassing(false);
            
            if(ball.isValidShoot()){
                p.removePossession();
                this.setShooting(true);
            }
            else{
                System.out.println("cant shoot");
            }
            currentKinectGesture="";
            
        }
        else if(currentKinectGesture.equals("Dribble Right") && p.isInPossession()){
            KinectPlayer kp = (KinectPlayer)p;
            kp.resetUndribbledDistance();
            
//              if(ball.isBallInSpace(p.getHandPosition(1)) && !isDribbling){
//                  isDribbling = true;
//              }          
              
              
              
//            System.out.println("dribb");
//            
//            //ball is in hand
//            if(p.getHandPosition(1).distance(ball.getBallPosition()) < 2f && !isDribbling){
//                ball.bounceBall(Conversions.degreesToNormalizedCoordinates(p.getFacingDirection()).mult(0));
//                isDribbling = true;
//                KinectPlayer kp = (KinectPlayer)p;
//                kp.resetUndribbledDistance();
//                System.out.println("bounce");
//            }
//            
//   //         System.out.println(isDribbling + " " + ball.isBouncingUp() + " " + (p.getHandPosition(1).y - ball.getBallPosition().y));
//     //       ball is being dribbled - put back in hand on way up
//            if(isDribbling && ball.isBouncingUp() && ball.getBallPosition().y > 2f){
//                isDribbling = false;
//                currentKinectGesture = "";
//                System.out.println("get bounce");
//            }
        }
        else if(currentKinectGesture.equals("Pass") && p.isInPossession()){
            System.out.println("current gesture pass");
            this.throwPass();
            currentKinectGesture = "";
        }        
        else{//removes passing and shooting status so that player is free to use gestures again
           if(isPassing && p.getHandPosition(1).distance(ball.getBallPosition()) > 7.5f){
               this.setPassing(false);
               this.setDribbling(false);
           }            
           if(isShooting && p.getHandPosition(1).distance(ball.getBallPosition()) > 5f){
               this.setShooting(false);
               this.setDribbling(false);
           }
           if(currentKinectGesture.equals("Pass") && !p.isInPossession()){
               currentKinectGesture = "";
           }
           //ensure that ball is taken on way back up
//           if(isDribbling && currentKinectGesture.equals("")){
//                if(ball.isBouncingUp() && ball.getBallPosition().y > 1f){
//                     isDribbling = false;
//                     currentKinectGesture = "";
//                 }
//           }
           //get attention gesture
//           if(!p.isInPossession()){               
//           
//           }
        }
    }
    
     public void setKinectGesture(String animationName){
           currentKinectGesture = animationName;
     }
     
      public String getCurrentKinectGesture(){
         return currentKinectGesture;
     }
            
      private void setShooting(boolean b){
          isShooting = b;
      }

      private void setPassing(boolean b){
          isPassing  =b;
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
      
      public boolean isDribbling(){
          return isDribbling;
      }
      
      public void throwPass(){
            Character c = super.getPassTarget();
            
            if(c != null){
                if(p.canPass((BasketballCharacter)c)){
                        ball.passBall(p, c);
                }
                else{
                    ball.passBall(p, c);
                }
            }
            else{
                ball.passBall(p.getFacingDirection());
            }
            isPassing = true;
            p.removePossession();
      }
}
