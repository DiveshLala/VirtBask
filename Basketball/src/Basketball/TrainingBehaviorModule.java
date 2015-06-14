/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.animation.LoopMode;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public class TrainingBehaviorModule extends BehaviorModule{
    
    private ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
    private boolean isPlayerTeam;
    
    public TrainingBehaviorModule(BasketballAgent c, int pTeamID){
        super(c);  
        if(pTeamID == parentCharacter.getTeamID()){
            isPlayerTeam = true;
        }
        parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
        positions.add(new Vector3f(-9, 0, -4));
        positions.add(new Vector3f(-9, 0, -16));
        positions.add(new Vector3f(-9, 0, -28));
    }
    

    public void updateBehavior(){
        
        BasketballCharacter possessionCharacter = SceneCharacterManager.getCharacterInPossession();
        
        if(possessionCharacter == null){
            if(!Court.isInsideCourt(parentCharacter.ball.getBallPosition()) && isPlayerTeam){
                parentCharacter.playAnimation(1, "standingPose", 1, LoopMode.Loop);
                parentCharacter.setPossession();
            }
        
        }
        else if(parentCharacter.isInPossession() && isPlayerTeam){
            ArrayList<BasketballCharacter> teamMates = parentCharacter.getTeamMates();
            BasketballCharacter opponent = parentCharacter.getOpponents().get(0);
            
            float maxDist = 0;
            BasketballCharacter bestCandidate = parentCharacter;
            for(BasketballCharacter bc: teamMates){
                float dist = opponent.getPosition().distance(bc.getPosition());
                if(isClearPath(bc, opponent) &&
                    dist > maxDist){
                    bestCandidate = bc;
                    maxDist = dist;
                }
            }
            
   //         System.out.println(bestCandidate.getID());
            
            parentCharacter.abo.turnBodyToTarget(bestCandidate.getPosition());
            
            if(bestCandidate.getActionState() != 1 && this.canPass(bestCandidate, opponent) && parentCharacter.perception.isFacingEachOther(bestCandidate, 10f)){
//                System.out.println("best " + bestCandidate.getID());
                int currentIndex = this.getClosestPosition();
                int newIndex = this.getNewPosition(currentIndex);
                parentCharacter.planner.setTargetPosition(positions.get(newIndex));
                System.out.println(parentCharacter.planner.getTargetPosition());
                parentCharacter.setActionState(-1);
                parentCharacter.playAnimation(1, "passAction", 1, LoopMode.DontLoop);
            }
        }
        else if(isPlayerTeam){
            if(parentCharacter.planner.isTargetReached(0.5f)){
                parentCharacter.setActionState(-1);
                parentCharacter.abo.turnBodyToTarget(possessionCharacter.getPosition());
                
                if(parentCharacter.perception.isFacingEachOther(possessionCharacter, 7.5f)){                       
                    parentCharacter.playAnimation(1, "acceptPass", 1, LoopMode.DontLoop);
                }
                else{
                    parentCharacter.playAnimation(1, "standingPose", 1, LoopMode.DontLoop);
                    parentCharacter.playAnimation(2, "standingPose", 1, LoopMode.DontLoop);
                }
            }
            else{
                parentCharacter.setActionState(1);
            }
        }
        else{
            
            if(parentCharacter.isInPossession()){
                parentCharacter.setActionState(-1);
                parentCharacter.abo.turnBodyToTarget(new Vector3f());
                parentCharacter.planner.setTargetPosition(parentCharacter.getPosition());
                if(parentCharacter.perception.isLookingAtTarget(Vector3f.ZERO)){
                    parentCharacter.playAnimation(1, "passAction", 0.5f, LoopMode.DontLoop);  
                }              
            }
            else{
                parentCharacter.setActionState(1);
                Vector3f newTarget = possessionCharacter.getPosition().add(Conversions.degreesToNormalizedCoordinates(possessionCharacter.getFacingDirection()).mult(15));
                parentCharacter.planner.setTargetPosition(newTarget);
            }
        }   
    }
    
    private boolean canPass(BasketballCharacter bc, BasketballCharacter opponent){
        
        if(parentCharacter.perception.isFacingEachOther(bc, 10f)){
            
            Ray ray = new Ray(parentCharacter.getPosition(), Conversions.degreesToNormalizedCoordinates(parentCharacter.getFacingDirection()));
            CollisionResults results = new CollisionResults();
            ray.collideWith(opponent.getCharacterMesh().getWorldBound(), results);
            if(results.size() == 0){
                return true;
            }
        }
        
        return false; 
    }
    
    private boolean isClearPath(BasketballCharacter bc, BasketballCharacter opponent){
        Ray ray = new Ray(parentCharacter.getPosition(), Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(parentCharacter.getPosition(), bc.getPosition())));
        CollisionResults results = new CollisionResults();
        ray.collideWith(opponent.getCharacterMesh().getWorldBound(), results);
        if(results.size() == 0){
            return true;
        }
        return false;
    }
    
    private int getClosestPosition(){
        float minDist = 1000;
        int index = -1;
        
        for(int i = 0; i < positions.size(); i++){
            float dist = parentCharacter.getPosition().distance(positions.get(i));
            if(minDist > dist){
                minDist = dist;
                index = i;
            } 
        }       
        return index;
        
    }
    
    private int getNewPosition(int current){
        if(current == 0){
            return 0;
        }
        else if(current == 1){
            return 2;
        }
        else{
            return 1;
        }
    }
    
    public boolean isEngagedInJointProject(){
        return false;
    }
    
}
