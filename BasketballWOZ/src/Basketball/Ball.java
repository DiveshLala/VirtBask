/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;
import VISIE.characters.BasketballAgent;
import VISIE.characters.BasketballCharacter;
import VISIE.mathfunctions.Conversions;
import VISIE.models.BasketballPlayerModel;
import VISIE.scenemanager.Scene;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import java.util.ArrayList;
import VISIE.characters.Character;
import VISIE.scenemanager.Court;
import VISIE.scenemanager.SceneCreator;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;

/**
 *
 * @author DiveshLala
 */
public class Ball {
    
    Node parentNode;
    static RigidBodyControl ballPhysicsNode;
    Vector3f ballPosition;
    int playerInPossession;
    int lastTouched;
    float radius;
    
    public Ball(Vector3f initialPos, RigidBodyControl n, float r){
        
        ballPosition = initialPos;
        ballPhysicsNode = n;  
        radius = r;
        ballPhysicsNode.setDamping(0.0f, 0.00f);
        ballPhysicsNode.setFriction(0.01f);
        ballPhysicsNode.setRestitution(0.75f);
        ballPhysicsNode.setCollisionGroup(2);
    }
    
    public void setMainNode(Node n){
        parentNode = n;
    }
    
    public Vector3f getBallPosition(){
        return ballPhysicsNode.getPhysicsLocation();
    }
    
    public static void setBallPosition(Vector3f pos){
        ballPhysicsNode.setPhysicsLocation(pos);
    }
    
    public void setLastTouched(int i){
        lastTouched = i;
    }
    
    public int getLastTouched(){
        return lastTouched;
    }
     
    public void removeDamping(){
        ballPhysicsNode.setDamping(0, 0);
    }
    public void reduceSpin(){
        ballPhysicsNode.setAngularFactor(0.02f);
    }
    
    public boolean isBouncingUp(){
       return ballPhysicsNode.getLinearVelocity().getY() > 0;
    }
    
    public void updateBallInPossession(){
         Vector3f vec = SceneCharacterManager.getCharacterByID(playerInPossession).getHandPosition(1);
         vec.subtractLocal(0, radius, 0);
         this.setBallPosition(vec);              
    }
    
    public void updateBallWithoutPossession(){
        this.removeSpin();
        ArrayList<Character> clist = SceneCharacterManager.getCharacterArray();
        for(int i = 0; i < clist.size(); i++){
            Character c = clist.get(i);
            CollisionResults results = new CollisionResults();
            parentNode.collideWith(c.getCharacterMesh().getWorldBound(), results);
            if(results.size() > 0){
                    BasketballCharacter bc = (BasketballCharacter)c;
                    if(!bc.isShooting() && !bc.isPassing()){
                        bc.setPossession();
                        bc.setActionState(0);
                        if(bc.getCharacterType().equals("BasketballAgent")){
                            BasketballAgent ba = (BasketballAgent)bc;
                            System.out.println("agent " + ba.getID() + " captured ball");
                        }
                        System.out.println(playerInPossession + " is now in possession");   
                    }
            }
        }
    }
    
    public void setPossession(int i){
        playerInPossession = i;
    }
    
    public void passBall(float angle){
        float yForce = 20;
        this.applyForceToBall(angle, yForce);
        this.removeSpin();
        playerInPossession = -1;
    }
    
    public void bounceBall(Vector3f movementVector){
        Vector3f yForce = Vector3f.UNIT_Y.mult(-15f);
        this.applyForceToBall(movementVector.add(yForce));
    }
    
    public boolean shootBall(){
        Vector3f ballPos = this.getBallPosition();
        float releaseAngle = (float)Math.toRadians(50);
        Vector3f goalPos = Court.getHoopLocation();
        float h = goalPos.y - ballPos.y;
        double l = Math.sqrt(Math.pow(goalPos.x - ballPos.x, 2) + Math.pow(goalPos.z - ballPos.z, 2));
        double a = l/Math.cos(releaseAngle);
        double b = 9.8/(2 * ((l * Math.tan(releaseAngle)) - h));
        double initialVelocity = a * Math.sqrt(b);
        Vector3f targetVector = Conversions.degreesToNormalizedCoordinates(Conversions.originToTargetAngle(this.getBallPosition(), goalPos));

        float heightForce = (float)initialVelocity * (float)Math.sin(releaseAngle);
        float lengthForce = (float)initialVelocity * (float)Math.cos(releaseAngle);  
        
        Vector3f forceVector = new Vector3f(targetVector.x * lengthForce, heightForce, targetVector.z * lengthForce);
        
        if(Vector3f.isValidVector(forceVector)){
            this.applyForceToBall(forceVector);
            this.removeSpin();
            return true;
        }
        else{
            return false;
        }

    }
    
    private void applyForceToBall(float angle, float yForce){
        ballPhysicsNode.setLinearVelocity(Conversions.degreesToNormalizedCoordinates(angle).mult(yForce).add(0, 5, 0));
    }
    
    private void applyForceToBall(Vector3f force){
        ballPhysicsNode.setLinearVelocity(force);
    }
    
    public boolean isBallInSpace(Vector3f pos){
        Vector3f ballPos = this.getBallPosition();
        boolean x = ((Math.pow(ballPos.x - pos.x, 2) + Math.pow(ballPos.y - pos.y, 2) + Math.pow(ballPos.z - pos.z, 2)) <= radius);
        return x;
    }
    
    public void removeSpin(){
        ballPhysicsNode.setPhysicsRotation(Quaternion.ZERO);
    }
    
}
