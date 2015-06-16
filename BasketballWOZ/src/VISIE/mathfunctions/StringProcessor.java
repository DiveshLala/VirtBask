/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.mathfunctions;
import com.jme3.math.Vector3f;

/**
 *
 * @author huang
 */
public class StringProcessor {
    
  //  public StringProcessor(){}
    
    public static Vector3f processVector3fString(String str){
                
        float x, y, z;
        
        if(str.contains("(")){
            str = str.replace("(", "");
        }
        if(str.contains(")")){
            str = str.replace(")", "");
        }
        
        try{
            x = Float.parseFloat(str.substring(0, str.indexOf(",")));
        }
        catch(Exception e){
            x = 0;
        }
        int posIndex = str.indexOf(",") + 1;

        try{
            y = Float.parseFloat(str.substring(posIndex + 1, str.indexOf(",", posIndex + 1)));
        }
        catch(Exception e){
            y = 0;
        }
        posIndex = str.indexOf(",", posIndex + 1);

        try{
            z = Float.parseFloat(str.substring(posIndex + 1));
        }
        catch(Exception e){
            z = 0;
        }

        Vector3f vec = new Vector3f(x,y,z);
        return vec;
    }



}
