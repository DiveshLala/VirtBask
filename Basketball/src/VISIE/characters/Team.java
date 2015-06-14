/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author DiveshLala
 */
public abstract class Team {
    
    int numMembers;
    ArrayList<Character> players;
    int teamState;
    int teamID;
    Vector3f teamColor;
    
    public void setDefending(){
        teamState = 0;
    }
    
    public void setAttacking(){
        teamState = 1;
    }
    
    public boolean isMember(Character c){
        for(int i = 0; i < players.size(); i++){
            if(c.getID() == players.get(i).getID()){
                return true;
            }
        }
        return false;
    }
    
    public int getTeamID(){
        return teamID;
    }
    
    public void addToTeam(Character c){
        players.add(c);
    }
    
    public Character getRandomTeamMate(){
        return players.get((int)Math.random() * players.size()); 
    }
    
    public void getTeamMate(){}
    
    public ArrayList<Character> getMembers(){
        return players;
    }
    
    public void setTeamColor(Vector3f colVec){
        teamColor = colVec;
    }
    
    public Vector3f getTeamColor(){
        return teamColor;
    }
    
}
