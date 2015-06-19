/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE;
import java.util.List;
/**
 *
 * @author huang
 */
public class Hierarchy {

    List<String> robotType;
    List<Integer> hierarchicalLevel;

    public Hierarchy(List<String> r, List<Integer> h){
        robotType = r;
        hierarchicalLevel = h;
    }

    public int findLevel(String type){
        for(int i = 0; i < robotType.size(); i++){
            if(robotType.get(i).equals(type))
                return hierarchicalLevel.get(i);
        }
        return -1;
    }

}
