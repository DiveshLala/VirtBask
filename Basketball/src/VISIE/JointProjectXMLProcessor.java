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

/**
 *
 * @author DiveshLala
 */
public class JointProjectXMLProcessor {
    
    public static String filePath;
    public static Document doc;
    
    public JointProjectXMLProcessor(){
        
       filePath = System.getProperty("user.dir") + "\\src\\XMLFiles\\JointProjectsXML.xml";
       
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(filePath);
            System.out.println(doc);

        }catch(Exception e) {
                e.printStackTrace();
        }       
    }
    
    public static String getJPAnimation(String jpName, int animationType){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String tagName;
        
        if(animationType == 0){
            tagName = "initiationAnimation";
        }
        else if(animationType == 1){
            tagName = "acceptanceAnimation";
        }
        else{
            tagName = "";
        }
        
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                Element f = (Element)(e.getElementsByTagName(tagName).item(0));
                return f.getAttribute("movement");
            }
        }
        return "";
    
    }
    
    public static String getJPMovementToRecognize(String jpName){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                NodeList n = e.getElementsByTagName("state");
                for(int j = 0; j < n.getLength(); j++){
                    Element f = (Element)n.item(j);
                    if(f.getAttribute("level").equals("recognition")){
                        return f.getAttribute("movement");
                    }
                }
            }
        }
        return "";
    
    }
    
    
    public static String getJPAnimationDetails(String jpName,  int animationType){
        StringBuilder data = new StringBuilder();
        //root element
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String movementName;
        
        if(animationType == 0){
            movementName = "initiationAnimation";
        }
        else if(animationType == 1){
            movementName= "acceptanceAnimation";
        }
        else{
            movementName = "";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                Element f = (Element)e.getElementsByTagName(movementName).item(0);
                data.append(f.getAttribute("movement") + ",");
                data.append(f.getElementsByTagName("speed").item(0).getTextContent() + ",");
                data.append(f.getElementsByTagName("looping").item(0).getTextContent() + ",");
                data.append(f.getElementsByTagName("channelID").item(0).getTextContent() + ",");
                return data.toString();
            }
        }
        return "";
    }
    
    //returns limit and channel ID
    public static float[] getMovementLimit(String jpName, String movementName, int activityType){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        float[] data = new float[2];
        String attributeName;
        if(activityType == 0){
            attributeName = "timeToPerception";
        } 
        else{
            attributeName = "timeToRecognition";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                Element f = (Element)(e.getElementsByTagName(movementName).item(0));
                data[0] = Float.parseFloat(f.getElementsByTagName(attributeName).item(0).getTextContent());
                data[1] = Float.parseFloat(f.getElementsByTagName("channelID").item(0).getTextContent());
                return data;
            }
        }
        return data;
    }
    
    public static String getActivity(String JPName, String level, String role){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        NodeList stateNode = null;
        NodeList levelNode = null;
        
        //find node of states
        for(int i = 0; i < jpNode.getLength(); i++){
            Element project = (Element)jpNode.item(i);
            if(project.getAttribute("name").equals(JPName)){    
                stateNode = project.getElementsByTagName("state");
            }
        }
        
        //find node of levels
        for(int i = 0; i < stateNode.getLength(); i++){
             Element e = (Element)stateNode.item(i);
             if(e.getAttribute("level").equals(level)){    
                levelNode = e.getElementsByTagName("participant");
            }
        }
        
        for(int i = 0; i < levelNode.getLength(); i++){
             Element e = (Element)levelNode.item(i);
             if(e.getAttribute("role").equals(role)){    
                return e.getElementsByTagName("activity").item(0).getTextContent();
            }
        }
        
        return ";";
    }
    
    public static String getJPFromRecognizedMovement(String movement){ //could be more than one
        StringBuilder s = new StringBuilder();
        Element rootElement = doc.getDocumentElement();
        NodeList recNodes = rootElement.getElementsByTagName("state");
        for(int i = 0; i < recNodes.getLength(); i++){
            Element e = (Element)recNodes.item(i);
            if(e.getAttribute("level").equals("recognition") && e.getAttribute("movement").equals(movement)){
                Node f = e.getParentNode();
                Element g = (Element)f;
                s.append(g.getAttribute("name") + ",");
            }
        }
        
        return s.toString();   
    }
    
    public static String getRecognitionConfirmationAction(String jpName){        
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        Element projectNode = null;
        Element levelNode = null;
        Element actionNode = null;
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                projectNode = (Element)jpNode.item(i);
                break;
                }
        }
        
        NodeList nodes = projectNode.getElementsByTagName("state");
        for(int i = 0; i < nodes.getLength(); i++){
            Element e = (Element)nodes.item(i);
            if(e.getAttribute("level").equals("recognition")){
                levelNode = (Element)nodes.item(i);
                break;
            }
        }
        
        nodes = levelNode.getElementsByTagName("participant");
        for(int i = 0; i < nodes.getLength(); i++){
            Element e = (Element)nodes.item(i);
            if(e.getAttribute("role").equals("receiver")){
                return e.getElementsByTagName("success").item(0).getTextContent();
            }
        }
        
        return "";
               
    }
    
    public static int getEndBehaviorState(String jpName, int role){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String tagName;
        
        if(role == 0){
            tagName = "initiatorEndState";
        }
        else{
            tagName = "receiverEndState";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                return Integer.parseInt(e.getElementsByTagName(tagName).item(0).getTextContent());
            }
        }
        return -1;   
    }
    
    public static boolean checkExistence(String jpName){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                return true;
            }
        }
        
        return false;
    
    }
    
    public static String getConcurrentMovement(String jpName, int type){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String tagName = "";
        
        if(type == 0){
            tagName = "initiationAnimation";
        }
        else if(type == 1){
            tagName = "acceptanceAnimation";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                Element f = (Element)(e.getElementsByTagName(tagName).item(0));
                return f.getElementsByTagName("concurrentMovement").item(0).getTextContent();
            }
        }
        
        return "";  
    }
    
    public static String getPreConditions(String jpName, int type){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String animationTag;
        
        if(type == 0){
            animationTag = "initiationAnimation";
        }
        else{
            animationTag = "acceptanceAnimation";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                Element f =  (Element)e.getElementsByTagName(animationTag).item(0);
                return f.getAttribute("precondition");
            }
        }
        
        return "";  
    }
    
    public static String getContinuedFocus(String jpName){
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                return e.getElementsByTagName("continueFocus").item(0).getTextContent();
            }
        }     
        return "";  
    }
    
    public static int getAbortState(String jpName, String role){
        String tagName = "";
        if(role.equals("initiator")){
            tagName = "initiatorAbortState";
        }
        else if(role.equals("receiver")){
            tagName = "receiverAbortState";
        }
        
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                return Integer.parseInt(e.getElementsByTagName(tagName).item(0).getTextContent());
            }
        }     
        return -1; 
    }
    
    public static String getSuccessSignal(String jpName){
                
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                return e.getElementsByTagName("successSignal").item(0).getTextContent();
            }
        }     
        return ";";
            
    }
    
    public static String getReceiverAction(String jpName, int level){
        Element rootElement = doc.getDocumentElement();
        NodeList jpNode = rootElement.getElementsByTagName("JointProject");
        String levelTag = "";
        
        if(level == 0){
            levelTag = "perception";
        }
        else if(level == 1){
            levelTag = "recognition";
        }
        else if(level == 2){
            levelTag = "uptake";
        }
        
        for(int i = 0; i < jpNode.getLength(); i++){
            Element e = (Element)jpNode.item(i);
            if(e.getAttribute("name").equals(jpName)){
                NodeList n = e.getElementsByTagName("state");
                for(int j = 0; j < n.getLength(); j++){
                    Element f = (Element)n.item(j);
                    if(f.getAttribute("level").equals(levelTag)){
                        NodeList o = f.getElementsByTagName("participant");
                        for(int k = 0; k < o.getLength(); k++){
                            Element g = (Element)o.item(k);
                            if(g.getAttribute("role").equals("receiver")){
                                return g.getElementsByTagName("concurrentAction").item(0).getTextContent();
                            }
                        }
                    }
                }     
            }
        }
        return "NA";
    }
    
}
