/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Divesh
 */
public class VISIEFileReader {
    
    public static String[][] readAgentConfigurations(int i, int numMembers){
        String filename = "config/playerConfig.txt";
        String[][] configArray = new String[numMembers][6];
        int counter = 0;

        try{
          BufferedReader reader = new BufferedReader(new FileReader(filename));
          String line;
          
          while((line = reader.readLine()) != null){
            if(line.startsWith(Integer.toString(i))){
                System.out.println(line);
                String[] s = line.split(", ");
                String[] a = Arrays.copyOfRange(s, 1, 7);
                configArray[counter] = a;
                counter++;
            }
            if(counter > numMembers - 1){
                break;
            }
          } 
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        
        return configArray;
    }
    
    public static String[] readPlayerConfiguration(String teamID){
        
        String filename = "config/playerConfig.txt";
        String[] config = new String[6];
        int counter = 0;

        try{
          BufferedReader reader = new BufferedReader(new FileReader(filename));
          String line;
          
          while((line = reader.readLine()) != null){
            if(line.startsWith("P" + teamID)){
                String[] s = line.split(", ");
                String[] a = Arrays.copyOfRange(s, 2, 7);
                config = a.clone();
            }
          } 
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        return config;
    }
    
    public static String[] readNUPConfiguration(int teamID, int NUPNum){
        
        String filename = "config/playerConfig.txt";
        String[] config = new String[6];
        int counter = 0;

        try{
          BufferedReader reader = new BufferedReader(new FileReader(filename));
          String line;
          
          while((line = reader.readLine()) != null){
            if(line.startsWith("NP" + teamID + ""+ NUPNum)){
                String[] s = line.split(", ");
                String[] a = Arrays.copyOfRange(s, 2, 7);
                config = a.clone();
            }
          } 
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        return config;
    }
}
