/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basketball;

/**
 *
 * @author Divesh
 */
public class GameManager {
    
    private static String gameType;    
    private static int attackingTeam = 1;//start with attacking team as 1
    private static int gameState = 0;//0 = normal, 1 = out state. 2 = possession swap state, 3 = score state
    
    public static void setInState(){
        gameState = 0;
    }
    
    public static void setOutState(){
        gameState = 1;
    }
    
    public static void setPossessionSwapState(){
        gameState = 2;
    }
    
    public static void setScoreState(){
        gameState = 3;
    }
    
    public static int getGameState(){
        return gameState;
    } 
    
    public static void setAttackingTeam(int i){
        attackingTeam = i;
    }
    
    public static int getAttackingTeam(){
        return attackingTeam;
    }
    
    public static void swapAttackingTeam(){
        if(attackingTeam == 0){
            attackingTeam = 1;
        }
        else if(attackingTeam == 1){
            attackingTeam = 0;
        }
    }
    
    public static String getGameType(){
        return gameType;
    }
    
    public static void setGameType(String s){
        gameType = s;
    }
    
    
    
    
}
