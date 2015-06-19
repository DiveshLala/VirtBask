/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE;
import Basketball.JointProject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Divesh
 */
public class BehaviorXMLProcessor {
    
    public static String filePath;
    public static Document doc;
    
   public BehaviorXMLProcessor(){
        
       filePath = System.getProperty("user.dir") + "\\src\\XMLFiles\\BehaviorDescriptorXML.xml";
       
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(filePath);
            System.out.println(doc);

        }catch(Exception e) {
                e.printStackTrace();
        }       
    }
   
   public static String getJointProjectName(int id){
        //root element
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            int x = Integer.parseInt(e.getAttribute("id"));
            if(id == x){
                return e.getAttribute("jointProject");
            }
        }
        return "";
    }
   
   public static ArrayList<Integer> getRelatedBehaviorIDs(String jpName){
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        ArrayList<Integer> behaviorIDList = new ArrayList<Integer>();
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            String s = e.getAttribute("jointProject");
            if(s.equals(jpName)){
                int x = Integer.parseInt(e.getAttribute("id"));
                behaviorIDList.add(x);
              //  return null;
            }
        }
        return behaviorIDList;
   }
   
    public static int getBehaviorPairID(int id){
        //root element
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            int x = Integer.parseInt(e.getAttribute("id"));
            if(id == x){
                Element f = (Element)e.getElementsByTagName("behaviorPair").item(0);
                return Integer.parseInt(f.getTextContent());
            }
        }
        return -1;
    }
    
    public static boolean checkValidID(int x){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            int j = Integer.parseInt(e.getAttribute("id"));
            if(x == j){
                return true;
            }
        }
        return false;
    
    }
    
    public static String getTargetedCharacters(int x){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            int j = Integer.parseInt(e.getAttribute("id"));
            if(x == j){
                return e.getElementsByTagName("targetedCharacter").item(0).getTextContent();
            }
        }
        return "";
    
    }
    
    public static int getAbortState(int behaviorID){
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JPBehavior");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            int j = Integer.parseInt(e.getAttribute("id"));
            if(behaviorID == j){
                return Integer.parseInt(e.getElementsByTagName("abortState").item(0).getTextContent());
            }
        }
        return -1;
    }
    
    //parameter is the name of the joint project and the end behavior state
    //finds a list of joint pehaviors related to the joint project name
    //finds a list of transitions that precedes the end behavior
    //returens true if intersection in list (i.e. a state with the same name is found which precedes the end state)
    
    public static boolean isPrecedingBehavior(String JPname, int endBehaviorState){
        ArrayList<Integer> precedingIDCandidates = getRelatedBehaviorIDs(JPname);
        ArrayList<Integer> possibleStartStates = getStartStateTransitions(endBehaviorState);
        for(int i = 0; i < precedingIDCandidates.size(); i++){
            for(int j = 0; j < possibleStartStates.size(); j++){
                int a = precedingIDCandidates.get(i);
                int b = possibleStartStates.get(j);
                if(a == b){
                    return true;
                }
            }
        } 
        return false;    
    }
    
    //set desired 
    public static ArrayList<Integer> getStartStateTransitions(int endState){
        ArrayList<Integer> startStateList = new ArrayList<Integer>();
        Element rootElement = doc.getDocumentElement();
        Element e = (Element)rootElement.getElementsByTagName("StateTransitions").item(0);
        NodeList connections = e.getElementsByTagName("connection");

        for(int i = 0; i < connections.getLength(); i++){
            Element connection = (Element)connections.item(i);
            if((Integer.parseInt(connection.getAttribute("end")) == endState)){
                startStateList.add(Integer.parseInt(connection.getAttribute("start")));
            }
        }
        return startStateList;
    }
    
}
