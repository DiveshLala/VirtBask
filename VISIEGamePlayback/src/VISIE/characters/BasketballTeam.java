/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.characters;

import Basketball.Ball;
import VISIE.scenemanager.CharacterCreator;
import VISIE.VISIEFileReader;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.math.Vector3f;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author DiveshLala
 */
public class BasketballTeam extends Team{
    
    CharacterCreator characterCreator;
    
    public BasketballTeam(int i, CharacterCreator cc, int id){               
        numMembers = i;
        characterCreator = cc;
        teamID = id;
        players = new ArrayList<Character>();
    }
    
         
    
}
