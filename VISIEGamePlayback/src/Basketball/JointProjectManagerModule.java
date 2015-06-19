/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;
import VISIE.BehaviorXMLProcessor;
import VISIE.JointProjectXMLProcessor;
import java.util.ArrayList;
import VISIE.characters.BasketballAgent;
import VISIE.characters.Character;
import VISIE.characters.Player;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;

/**
 *
 * @author Divesh
 */
public class JointProjectManagerModule {
    
    private BasketballAgent parentCharacter;
    private ArrayList<JointProject> jointProjectsList;
    private int behaviorState;
    private boolean waiting = false;
    private long startWaitTime;
    
    public JointProjectManagerModule(BasketballAgent c){
        
        parentCharacter = c;    
        jointProjectsList = new ArrayList<JointProject>();
        behaviorState = 0;
    }
    
    public void updateJointProjects(int newBehaviorState){
                        
        if(newBehaviorState > 100 && newBehaviorState < 200 && behaviorState < 100){ // behavior initiates a new joint project
            if(BehaviorXMLProcessor.checkValidID(newBehaviorState)){
                String JPName = BehaviorXMLProcessor.getJointProjectName(newBehaviorState);
                String targetedCharacter = BehaviorXMLProcessor.getTargetedCharacters(newBehaviorState);
                if(JointProjectXMLProcessor.checkExistence(JPName)){
                    this.createNewJointProject(JPName, targetedCharacter);
                }
            }   
        }  
        
        //parallel/consecutive joint projects
        else if(behaviorState > 100 && newBehaviorState > 100 & newBehaviorState != behaviorState){
                        
            if(newBehaviorState < 200){//initiating state
                if(jointProjectsList.isEmpty()){ //initiate new 
                    if(BehaviorXMLProcessor.checkValidID(newBehaviorState)){
                        String JPName = BehaviorXMLProcessor.getJointProjectName(newBehaviorState);
                        String targetedCharacter = BehaviorXMLProcessor.getTargetedCharacters(newBehaviorState);
                    
                        if(JointProjectXMLProcessor.checkExistence(JPName)){
                            System.out.println("new " + JPName);
                            this.createNewJointProject(JPName, targetedCharacter);
                        }
                    
                    }   
                                    
                }
                else{//parallel initiating JPs
                    
                }
            
            }
            else if(newBehaviorState != 200){ 
                //receiving state: wait for next joint project to be initiated
                //if state is 200, then JP has been proposed - not consecutive state
                 //if none initiated after time limit, return to "normal state"
                if(jointProjectsList.isEmpty()){//consecutive JP
                                                            
                }
                else{//parallel receiving JPs
                    
                }
                
                waiting = true;
                startWaitTime = System.currentTimeMillis();
            }
       }
        
        if(waiting){ // if waiting for JP, set time limit
            if((System.currentTimeMillis() - startWaitTime) > 2000){
                    int abortState = BehaviorXMLProcessor.getAbortState(behaviorState);
                    this.setBehaviorState(abortState);
                    parentCharacter.setBehaviorState(abortState);
                    waiting = false;
            }          
        }
        
       behaviorState = newBehaviorState;
       this.updateOngoingJointProjects();
    }
    
    private void createNewJointProject(String JPName, String targets){
           int role = 0;
           ArrayList<Character> l = this.createJointProjectTargets(targets);
           if(!l.contains(null)){
              this.initialiseJP(JPName, role, l);
           }
           else{
               this.abortJointProject(JPName);
           }
    }
    
    private void abortJointProject(String JPName){
           parentCharacter.setBehaviorState(JointProjectXMLProcessor.getAbortState(JPName, "initiator")); 
           behaviorState = parentCharacter.getActionState();
    }
    
    public void processAbortedProject(Character c, JointProject jp){
           
           if(jp.getInitiator().getID() == parentCharacter.getID()){
                parentCharacter.setBehaviorState(JointProjectXMLProcessor.getAbortState(jp.getProjectName(), "initiator"));
                behaviorState = parentCharacter.getActionState();
           }
           else{
               if(jp.getProjectName().equals("joint project proposal")){ //JP not yet established - do normal activity
                   if(parentCharacter.isInPossession()){
                        parentCharacter.setBehaviorState(0);
                        behaviorState = 0;
                   }
                   else{
                       parentCharacter.setBehaviorState(1);
                       behaviorState = 1;
                   }
               }
               else{
                    parentCharacter.setBehaviorState(JointProjectXMLProcessor.getAbortState(jp.getProjectName(), "receiver"));
                    behaviorState = parentCharacter.getActionState();
               }
           }
           
           System.out.println(parentCharacter.getID() + " - " + jp.getProjectName() + " processing aborted project");
           System.out.println();
           
           //tries to remove JP if known if unknown, remove jp if shared id is same
           if(!jointProjectsList.remove(jp)){
               for(int i = 0; i < jointProjectsList.size(); i++){
                   if(jointProjectsList.get(i).getSharedID() == jp.getSharedID()){
                       jointProjectsList.remove(i);
                       break;
                   }
               }
           }
           parentCharacter.setFocusedCharacter(null);
           parentCharacter.planner.setTargetPosition(parentCharacter.planner.calculateRandomPosition());
    }
    
    private void abortJointProject(JointProject jp){
        parentCharacter.JPMessaging.sendAbortNotification(jp);
    }
    
    private ArrayList<Character> createJointProjectTargets(String targets){
           ArrayList<Character> l = new ArrayList<Character>();
           if(targets.equals("best position")){
               l.add(parentCharacter.planner.getBestPositionedCharacter());
           }
           else if(targets.equals("focused character")){
               l.add(parentCharacter.getFocusedCharacter());           
           }
           else if(targets.equals("in possession")){
               l.add(SceneCharacterManager.getCharacterInPossession());
           }
           return l;
    }
    
    private JointProject initialiseJP(String jpName, int role, ArrayList<Character> l){
           JointProject j = new JointProject(jpName, role, l, parentCharacter); 
           jointProjectsList.add(j);  
           this.executeAnimation(jpName, 0);
           j.setInitialActionTime();
           return j;
    }
    
    private void executeAnimation(String jpName, int animationType){
           String animData = JointProjectXMLProcessor.getJPAnimationDetails(jpName, animationType);
           String[] sep = animData.split(",");
           String animationName = sep[0];
           float speed = Float.parseFloat(sep[1]);
           int channelID = Integer.parseInt(sep[3]);
           LoopMode loop;
           if(sep[2].equals("N")){
               loop = LoopMode.DontLoop;
           }
           else{
               loop = LoopMode.Loop;
           }
           if(animationName.equals("none")){
               
           }
           else{
  //             System.out.println(parentCharacter.getID() + " " + animationName);
               parentCharacter.playAnimation(channelID, animationName, speed, loop);
           }
    }
    
    private void updateOngoingJointProjects(){
               
      for(int i = 0; i < jointProjectsList.size(); i++){
           waiting = false;
           JointProject jp = jointProjectsList.get(i);
           int role = jp.getRole();
           
           //initiator
           if(role == 0){
               this.executeInitiatorBehavior(jp);
           }
           
           //receiver
           else if(role == 1){
               this.executeReceiverBehavior(jp);  
           }  
       }
    }
    
    private void executeInitiatorBehavior(JointProject jp){
          int level = jp.getCurrentJointActionState();
          Character targetedCharacter;   
                    
          //make focused character
         if(level == 1){
              if(parentCharacter.getFocusedCharacter() != null){
                    targetedCharacter = parentCharacter.getFocusedCharacter();
              }
              else{
                    targetedCharacter = this.selectTargetedCharacter(jp);
                    parentCharacter.setFocusedCharacter(targetedCharacter);
              }
         }
         else{
             targetedCharacter = parentCharacter.getFocusedCharacter();
            }
         
         
          this.doConcurrentMovement(jp, targetedCharacter);
               
               if(level == 1){ 
                   if(parentCharacter.perception.isPerceptionLimitReached(jp.getProjectName(), "initiationAnimation")){
                        if(jp.getElapsedTime() > 2000){
                           System.out.println("initiator abort level 1");
                           this.abortJointProject(jp);
                       }
                       this.sendPerceptionSignals(jp);
                      // this.checkForMutualGaze(jp, targetedCharacter);  
                       this.checkWithinGaze(jp, targetedCharacter, 60);
                    }
               }
               else if(level == 2){//wait for receiver to recognize
                   if(jp.getElapsedPerceptionTime() > 2000){
                       System.out.println("initiator abort level 2");
                       this.abortJointProject(jp);
                   }
                   
                   else if(parentCharacter.isRecognitionLimitReached(jp.getProjectName(), "initiationAnimation")){
                       if(targetedCharacter.getCharacterType().equals("BasketballAgent")){
                           //send movement to receiver
                           BasketballAgent ba = (BasketballAgent)targetedCharacter;
                           ba.receiveSharedProjectInfo(jp.getSharedID(), JointProjectXMLProcessor.getJPMovementToRecognize(jp.getProjectName()), 2);
                       }   
                       else{ //human player
                           Player p = (Player)targetedCharacter;
                           String currentGest = p.getCurrentGesture(1);
                           
                           //if no reaction from user, gesture not recognized                          
                           if(!currentGest.equals("standingPose")){
                               jp.setRecognized(true);
                           }
                       }
                   }

                   if(jp.isRecognized()){
                       this.advanceJointProject(jp);
                   }
               }
               else if(level == 3){//wait for receiver to accept
                   if(targetedCharacter.getCharacterType().equals("BasketballAgent")){
                       if(jp.isAccepted()){
                           this.processSuccessfulProject(jp);
                       } 
                   }
                   else{//human player
                       if(this.successSignalled(jp, (Player)targetedCharacter)){
                            this.processSuccessfulProject(jp);
                       }
                       if(jp.getElapsedTime() > 2000){
                           System.out.println("initiator abort level 3");
                           this.abortJointProject(jp);
                       }
                       
                   }
                }
    }
    
    private void executeReceiverBehavior(JointProject jp){
        int level = jp.getCurrentJointActionState();
        this.doReceiverAction(jp, level);
        
        if(level == 0){ //do perception activity
                   if(jp.isPerceivable()){ // movement can be perceived
                       if(parentCharacter.perception.canSeeCharacter(jp.getInitiator())){                           
                           //    parentCharacter.perception.doGaze(jp.getInitiator());
//                               if(parentCharacter.perception.isMutualGaze(jp.getInitiator())){
//                                       this.advanceJointProject(jp);   
//                                       if(jp.getInitiator() instanceof VISIE.characters.Player){
//                                           jp.setPerceptionTime();
//                                       }
//                               }
                              if(parentCharacter.perception.isWithinGaze(jp.getInitiator().getPosition(), 60f)){
                                       this.advanceJointProject(jp);   
                                       jp.setPerceptionTime();
                               }
                        }
                       else{ // carry on as normal
                           
                       }
                   }
                   //for human
                   if(jp.getInitiator() instanceof VISIE.characters.Player && jp.getElapsedTime() > 2000){
                       this.abortJointProject(jp);
                   }
           }
               
           if(level == 1){//do recognition activity      
               if(jp.isRecognized()){
                    System.out.println("recognized " + jp.getProjectName());
                    parentCharacter.doRecognitionConfirmation(jp);
                    this.advanceJointProject(jp);   
               }
               //for human
              if(jp.getInitiator() instanceof VISIE.characters.Player && jp.getElapsedPerceptionTime() > 2000){
                       this.abortJointProject(jp);
               }
           }
               
           if(level == 2){//do acceptance activity
               int decision = parentCharacter.uptake.actionFeasibility(jp, jp.getInitiator());
               if(decision == 1 && this.arePreconditionsMet(jp, 1, jp.getInitiator())){ //0 = abort, 1 = accept, 2 = wait
                   this.executeAnimation(jp.getProjectName(), 1);
                   if(jp.getInitiator() instanceof VISIE.characters.BasketballAgent){                         
                       if(parentCharacter.perception.isPerceptionLimitReached(jp.getProjectName(), "acceptanceAnimation")){
                            BasketballAgent ba = (BasketballAgent)jp.getInitiator();
                            ba.receiveSharedProjectInfo(jp.getSharedID(), "true", 4);
                            jp.setAccepted(true); 
                       }
                       else{
                           //action executed but not yet perceived
                       }
                   }
                   else{ //human user
                       jp.setAccepted(true);
                   } 
               }
               else if(decision == 0){
                   System.out.println("receiver abort at level 3...");
                   this.abortJointProject(jp);
               }

               if(jp.isAccepted()){
                   this.processSuccessfulProject(jp);
               }
               
               //initiator takes too long
               //should this occur?
               if(jp.getElapsedPerceptionTime() > 2000){
                   if(!arePreconditionsMet(jp, 1, jp.getInitiator())){
                        System.out.println("initiator abort at level 3...");
                   }
                   this.abortJointProject(jp);
               }
           } 
    }
    
    private boolean successSignalled(JointProject jp, Player p){
   
        String uptakeSignal = JointProjectXMLProcessor.getSuccessSignal(jp.getProjectName());
        
        if(uptakeSignal.equals("animation")){
            String currentGest =  p.getCurrentGesture(1);
            String uptakeGest = JointProjectXMLProcessor.getJPAnimation(jp.getProjectName(), 1);
            if(currentGest.equals(uptakeGest)){
                return true;
            }
            return false;
        }
        else if(uptakeSignal.equals("ball passed")){
            if(p.isInPossession()){
                return true;
            }        
            else{
                if(jp.getElapsedTime() > 3000){
                    this.abortJointProject(jp);
                }
                return false;
            }
        }
        else{
            return true;
        }
    }
    
    
    private void sendPerceptionSignals(JointProject jp){
           for(int j = 0; j < SceneCharacterManager.getCharacterArray().size(); j++){
                  Character c = SceneCharacterManager.getCharacterArray().get(j);
                  if(c.getID() != parentCharacter.getID()){
                       if(c.getCharacterType().equals("BasketballAgent")){
                         BasketballAgent ba = (BasketballAgent)c;
                         parentCharacter.sendJPPerceptionSignal(ba, jp);
                       }
                       else{ // human player
                       }
                  }
           }
    }
    
    private void checkForMutualGaze(JointProject jp, Character target){ 
       if(parentCharacter.perception.isMutualGaze(target)){
               jp.setPerceived(true);
               jp.setPerceptionTime();
               this.advanceJointProject(jp);
        }
    }
    
    private void checkWithinGaze(JointProject jp, Character target, float angle){
      if(parentCharacter.perception.isWithinGaze(target.getPosition(), angle)){
               jp.setPerceived(true);
               jp.setPerceptionTime();
               this.advanceJointProject(jp);
        }
    
    }
    
    private void doConcurrentMovement(JointProject jp, Character target){
        //choose character to target
       String s = JointProjectXMLProcessor.getConcurrentMovement(jp.getProjectName(), 0);
     //  System.out.println(s);
        if(s.equals("turn to target")){  
           parentCharacter.abo.turnBodyToTarget(target.getPosition());
           parentCharacter.playAnimation(2, "standingPose", 1, LoopMode.Loop);
           parentCharacter.setSpeed(0);
       }
    }
    
    private void doReceiverAction(JointProject jp, int level){
        String s = JointProjectXMLProcessor.getReceiverAction(jp.getProjectName(), level); 
        
        if(s.equals("NA")){ //joint project has not yet been decided (stand still)
               parentCharacter.abo.turnBodyToTarget(jp.getInitiator().getPosition());
               parentCharacter.playAnimation(2, "standingPose", 1, LoopMode.Loop);
               parentCharacter.setSpeed(0); 
       //     }
        }
        else if(s.equals("turn to initiator")){
               parentCharacter.abo.turnBodyToTarget(jp.getInitiator().getPosition());
               parentCharacter.playAnimation(2, "standingPose", 1, LoopMode.Loop);
               parentCharacter.setSpeed(0); 
        }
        else{
            System.out.println(s + " " + level + " " + jp.getProjectName());
        }
    }
    
    private Character selectTargetedCharacter(JointProject jp){
        if(jp.getTargetedCharacters().size() == 1){
            return jp.getTargetedCharacters().get(0);
        }
        else{
            return null;
        }
    }
    
    private boolean arePreconditionsMet(JointProject jp, int role, Character c){
        String s = JointProjectXMLProcessor.getPreConditions(jp.getProjectName(), role);
        if(s.equals("none")){
            return true;
        }
        else if(s.equals("mutual gaze")){
            if(parentCharacter.perception.isMutualGaze(c)){
                return true;
            }
        }
        else if(s.equals("in possession")){
            return parentCharacter.isInPossession();
        }
        
        return false;
        
    
    }
    
    
    public void addJointProject(JointProject jp){
        jointProjectsList.add(jp);
    }
    
    public void setBehaviorState(int i){
        behaviorState = i;
    }
    
    private void advanceJointProject(JointProject jp){
        jp.advanceJointAction();  
    }
    
    public void updateJointProjectStatus(int id, String attribute, int updateType){
                
        JointProject j = null;
        
        //get joint project
        for(int i = 0; i< jointProjectsList.size(); i++){
            if(id == jointProjectsList.get(i).getSharedID()){
                j = jointProjectsList.get(i);
            }
        }
        //set value
        
        if(updateType == 1 && j.getCurrentJointActionState() == 0){ //movement has been perceived
            j.setPerceivable(true);
        }
                
        if(updateType == 2 && j != null && j.getCurrentJointActionState() == 1
           && parentCharacter.perception.isWithinGaze(j.getInitiator().getPosition(), 30)){ //movement has been recognized from receiver, try to map animation name
            
            //get candidates for movement
            String movement = attribute;
            String s = JointProjectXMLProcessor.getJPFromRecognizedMovement(movement);
            String[] candidates = s.split(",");
            
            //only update if recognized movement is legitimate
            if(candidates.length == 1){
                if(parentCharacter.recognition.doRecognitionActivity(j, movement, behaviorState)){
                    j.updateProjectName(candidates[0]);
                }
            }
            else if(candidates.length == 2){ // movement could be either of 2 or more projects
                    j.updateProjectName(candidates[0]);
            }
            
            if(!j.getProjectName().equals("")){
                    j.setRecognized(true);
            }
            else{
                System.out.println("receiver aborting at level 2... " + this.parentCharacter.getID());
                this.abortJointProject(j);
            }
        }
        
        if(updateType == 3){ // recognition has been confirmed by receiver, update joint project\\
            String confirmation = attribute;
            if(confirmation.equals(JointProjectXMLProcessor.getRecognitionConfirmationAction(j.getProjectName()))){
                 j.setRecognized(true);   
            }
        }
        if(updateType == 4){
            j.setAccepted(true);        
        }
    }
    
    private void processSuccessfulProject(JointProject jp){
        
            System.out.println(jp.getProjectName() + " Joint project successful " + parentCharacter.getID()); 
            for(int i = 0; i < jointProjectsList.size(); i++){
                if(jp.getSharedID() == jointProjectsList.get(i).getSharedID()){
                    jointProjectsList.remove(jp);
                }
            }
            System.out.println("Joint project removed");
            System.out.println();
            
            if(jp.getInitiator().getID() == parentCharacter.getID()){
                parentCharacter.setBehaviorState(JointProjectXMLProcessor.getEndBehaviorState(jp.getProjectName(), 0));
                if(JointProjectXMLProcessor.getContinuedFocus(jp.getProjectName()).equals("N")){
                    parentCharacter.setFocusedCharacter(null);
                }
            }
            else{
                parentCharacter.setBehaviorState(JointProjectXMLProcessor.getEndBehaviorState(jp.getProjectName(), 1));
                if(JointProjectXMLProcessor.getContinuedFocus(jp.getProjectName()).equals("N")){
                    parentCharacter.setFocusedCharacter(null);
                }
            }
    }
    
    public boolean jointProjectExists(int JPID){
        for(int i = 0; i < jointProjectsList.size(); i++){
            if(jointProjectsList.get(i).getSharedID() == JPID){
                return true;
            }
        }
        return false;
    }
    
    public boolean isEngagedInJPWithUser(){
        for(int i = 0; i < jointProjectsList.size(); i++){
            if(jointProjectsList.get(i).getInitiator() instanceof VISIE.characters.Player){
                return true;
            }
        }  
        
        return false;
    }
    
    //agent waiting for joint project
    public boolean isWaiting(){
        return waiting;
    }
    
    public void removeWaiting(){
        waiting = false;
    }
    
}
