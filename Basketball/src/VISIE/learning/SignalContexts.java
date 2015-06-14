/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.learning;

import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class SignalContexts {
    
    private String signalName;
    private ArrayList<Context> signalContexts;
    private boolean hasBall;
    private String outputAction;
    
    public SignalContexts(String s, ArrayList<Context> sc, boolean b, String o){
        signalName = s;
        signalContexts = sc;
        hasBall = b;
        outputAction = o;
    }
    
    public String getSignalName(){
        return signalName;
    }
    
    public boolean isSignalWithBall(){
        return hasBall;
    }
    
    public ArrayList<Context> getSignalContexts(){
        return signalContexts;
    }
    
    public void setOutputAction(String s){
        outputAction = s;
    }
    
    public String getOutput(){
        return outputAction;
    }
    
    @Override
    public String toString(){
        return signalContexts.get(signalContexts.size()-1).toString();
    }
    
}
