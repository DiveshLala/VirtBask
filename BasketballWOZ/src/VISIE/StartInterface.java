/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.controls.RadioButtonGroup;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.controls.dynamic.PanelCreator;
import de.lessvoid.nifty.controls.radiobutton.builder.RadioGroupBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;

/**
 *
 * @author Divesh
 */
public class StartInterface extends AbstractAppState implements ScreenController{
    
  private Nifty nifty;
  private Screen screen;
  private SimpleApplication app;
  private Main parent;
  private int[] diagramArray;
  private ArrayList<String> loadedFile;
  
 
  /** custom methods */ 
 
  public StartInterface(Main m){ 
      parent = m;
      diagramArray = new int[8];
    /** Your custom constructor, can accept arguments */ 
  }
  
  @NiftyEventSubscriber(id = "flatRes")
  public void resChanged(String id, DropDownSelectionChangedEvent e){
      DropDown flatRes = screen.findNiftyControl("flatRes", DropDown.class);
      Element elem = screen.findElementByName("customResPanel");
      if(flatRes.getSelection().equals("Custom")){
          elem.setVisible(true);
      }
      else{
          elem.setVisible(false);
      }
  }
  
  @NiftyEventSubscriber(id = "displayNum")
  public void displayNumChanged(String id, TextFieldChangedEvent e){
     TextField displayNum = screen.findNiftyControl("displayNum", TextField.class);
     String s = displayNum.getText();
     Element[] panels = new Element[8];
     for(int i = 0; i < panels.length; i++){
         panels[i] = screen.findElementByName("disp" + i);
     }
      
      try{
         int input = Integer.parseInt(s);
         if(input >= 4 && input <= 8){
             for(int j = 0; j < panels.length; j++){
                 if(j < input){
                     panels[j].setVisible(true);
                 }
                 else{
                     int state = diagramArray[j];
                     if(panels[j].getElements().size() > 0){
                        this.removeChildFromElement(panels[j].getId());
                     }
                     diagramArray[j] = 0;
                     panels[j].setVisible(false);
                     
                     if(state == 1){
                         for(int k = 0; k < panels.length; k++){
                             if(panels[k].getElements().isEmpty()){
                                 this.addPanel(panels[k].getId(), "#ff0000ff");
                                 diagramArray[k] = 1;
                                 break;
                             }
                        }
                     }
                     else if(state == 2){
                        for(int k = 0; k < panels.length; k++){
                             if(panels[k].getElements().isEmpty()){
                                 this.addPanel(panels[k].getId(), "#0000ffff");
                                 diagramArray[k] = 2;
                                 break;
                             }
                        }
                     }
                     else if(state == 3){
                         this.addPanel("disp0", "#ff0000ff");  
                         this.addPanel("disp1", "#0000ffff");  
                         diagramArray[0] = 1;
                         diagramArray[1] = 2;
                     }
                 }
             }
         }  
      }
      catch(NumberFormatException err){
          displayNum.setText("8");
          for(int i = 0 ; i < panels.length; i++){
              panels[i].setVisible(true);
          }
      }
  }
 
  /** Nifty GUI ScreenControl methods */ 
 
  public void bind(Nifty nifty, Screen screen){
    this.nifty = nifty;
    this.screen = screen;
    this.loadPreviousSettings();
    this.populateDDBoxes();
    this.populateTextBoxes();
    this.displayLabels();
    this.initializeDisplayDiagram();
    Element e = screen.findElementByName("startButton");
    e.setFocus();
  }
  
  private void loadPreviousSettings(){
      
      loadedFile = new ArrayList<String>();
  
      try{
          BufferedReader reader = new BufferedReader(new FileReader("config/gamesettings.txt"));
          String line;
          
          while((line = reader.readLine()) != null){
              loadedFile.add(line);
          } 
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          
        }
      
  }
  
  private void populateDDBoxes(){

    DropDown flatRes = screen.findNiftyControl("flatRes", DropDown.class);
            
        flatRes.addItem("Full Screen");
        flatRes.addItem("800 x 600");
        flatRes.addItem("1024 x 768");
        flatRes.addItem("1280 x 720");
        flatRes.addItem("Custom");
        flatRes.selectItem("800 x 600");        
  }
  
  private void populateTextBoxes(){
      TextField tcp = screen.findNiftyControl("TCPPortText", TextField.class);
      TextField udp = screen.findNiftyControl("UDPPortText", TextField.class);
      TextField server = screen.findNiftyControl("AddressText", TextField.class);
      TextField kinAdd = screen.findNiftyControl("kinectAddress", TextField.class);
      TextField kinPort = screen.findNiftyControl("kinectPort", TextField.class);
      TextField psAdd = screen.findNiftyControl("psAddress", TextField.class);
      TextField psPort = screen.findNiftyControl("psPort", TextField.class);
      TextField immHeight = screen.findNiftyControl("immersiveHeight", TextField.class);
      TextField immWidth = screen.findNiftyControl("immersiveWidth", TextField.class);
      TextField FOV = screen.findNiftyControl("fov", TextField.class);
      
      String kinectAdd;
      
      try{
          if(this.getDataOfInput("kinectAddress").equals("host")){
            kinectAdd =  InetAddress.getLocalHost().getHostAddress();
          }
          else{
              kinectAdd = this.getDataOfInput("kinectAddress");
          }
      }
      catch(Exception e){
            kinectAdd = e.toString();
      }
      
      String isKin = this.getDataOfInput("hasKinect");
      String isPS = this.getDataOfInput("hasPressure");
      String isImm = this.getDataOfInput("isImmersive");

      if(isKin.equals("true")){
          screen.findNiftyControl("kinectCheck", CheckBox.class).setChecked(true);
      }
      
      if(isPS.equals("true")){
          screen.findNiftyControl("psCheck", CheckBox.class).setChecked(true);
      }
      
      if(isImm.equals("true")){
          this.selectImmersiveDisplay();
      }
      
      server.setText(this.getDataOfInput("serverAddress"));
      tcp.setText(this.getDataOfInput("TCPPort"));
      udp.setText(this.getDataOfInput("UDPPort"));
      kinAdd.setText(kinectAdd);
      kinPort.setText(this.getDataOfInput("kinectPort"));
      psAdd.setText(this.getDataOfInput("pressureSensorAddress"));
      psPort.setText(this.getDataOfInput("pressureSensorPort"));
      immHeight.setText(this.getDataOfInput("immersiveHeight"));
      immWidth.setText(this.getDataOfInput("immersiveWidth"));
      FOV.setText(this.getDataOfInput("FOV"));
      
  }
  
  private void displayLabels(){
      screen.findElementByName("portPanel").setVisible(false);
      screen.findElementByName("psPanel").setVisible(false);
      screen.findElementByName("customResPanel").setVisible(false);
      screen.findElementByName("immersivePanel").setVisible(false);
      screen.findElementByName("numDisplays").setVisible(false);
      screen.findElementByName("displayDiagram").setVisible(false);
      screen.findElementByName("diagExplanation").setVisible(false);
      screen.findElementByName("cameraInfo").setVisible(false);
      screen.findElementByName("portPanel").setVisible(true);
      screen.findElementByName("kinectPanel").setVisible(false);
      
      String isKin = this.getDataOfInput("hasKinect");
      String isPS = this.getDataOfInput("hasPressure");
      
      if(isKin.equals("true")){
          screen.findElementByName("kinectPanel").setVisible(true);
      }
      
      if(isPS.equals("true")){
          screen.findElementByName("psPanel").setVisible(true);
      }
  }

  
  private void initializeDisplayDiagram(){
      
        diagramArray[0] = 1;
        diagramArray[1] = 2;
        
        Element e = screen.findElementByName("disp0");
        PanelCreator pc = new PanelCreator();
        pc.setChildLayout("center");
        pc.setBackgroundColor("#ff0000ff");
        pc.setHeight("100%");
        pc.setWidth("100%");
        pc.create(nifty, screen, e);
        
        e = screen.findElementByName("disp1");
        pc = new PanelCreator();
        pc.setChildLayout("center");
        pc.setBackgroundColor("#0000ffff");
        pc.setHeight("100%");
        pc.setWidth("100%");
        pc.create(nifty, screen, e);
  }
 
  public void onStartScreen() {
    System.out.println("start");
  }
 
  public void onEndScreen() { 
    System.out.println("end");
  }
 
  /** jME3 AppState methods */ 
 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
  }
 
  @Override
  public void update(float tpf) { 
    /** jME update loop! */ 
  }
  
  public void gameButtonClick(){
      Element element = nifty.getCurrentScreen().findElementByName("activityPanel"); 
      RadioButton trainingButton = screen.findNiftyControl("trainingButton", RadioButton.class);
      Element offDefPanel = nifty.getCurrentScreen().findElementByName("offDef");
      List<Element> children = element.getElements();
      
      for(int i = 0; i < children.size(); i++){
          Element e = children.get(i);
          e.setVisible(false);
      }
      
      Element visibleElement = nifty.getCurrentScreen().findElementByName("gameSettingPanel");
      visibleElement.setVisible(true);
      
            
      if(trainingButton.isActivated()){
          offDefPanel.setVisible(false);            
      }
  }
  
  public void networkButtonClick(){
      Element element = nifty.getCurrentScreen().findElementByName("activityPanel");
      List<Element> children = element.getElements();
      CheckBox mpCheck = nifty.getCurrentScreen().findNiftyControl("mpCheck", CheckBox.class);
      CheckBox kinectCheck = nifty.getCurrentScreen().findNiftyControl("kinectCheck", CheckBox.class);
      CheckBox psCheck = nifty.getCurrentScreen().findNiftyControl("psCheck", CheckBox.class);
      Element portPanel = nifty.getCurrentScreen().findElementByName("portPanel");
      Element kinectPanel = nifty.getCurrentScreen().findElementByName("kinectPanel");
      Element psPanel = nifty.getCurrentScreen().findElementByName("psPanel");
      
      for(int i = 0; i < children.size(); i++){
          Element e = children.get(i);
          e.setVisible(false);
      }
      
      Element visibleElement = nifty.getCurrentScreen().findElementByName("networkSettingPanel");
      visibleElement.setVisible(true);
      
      if(!kinectCheck.isChecked()){
          kinectPanel.setVisible(false);
      }
      if(!psCheck.isChecked()){
          psPanel.setVisible(false);
      }
  }
  
  public void displayButtonClick(){
      Element element = nifty.getCurrentScreen().findElementByName("activityPanel");
      List<Element> children = element.getElements();
      
      Element visibleElement = nifty.getCurrentScreen().findElementByName("displaySettingsPanel");
      visibleElement.setVisible(true);
      
      DropDown flatRes = screen.findNiftyControl("flatRes", DropDown.class);
      RadioButton flatButton = screen.findNiftyControl("flatButton", RadioButton.class);
      
      if(flatButton.isActivated()){
          
          screen.findElementByName("immersivePanel").setVisible(false);
          screen.findElementByName("numDisplays").setVisible(false);
          screen.findElementByName("displayDiagram").setVisible(false);
          screen.findElementByName("diagExplanation").setVisible(false);
          screen.findElementByName("cameraInfo").setVisible(false);
          
          if(!flatRes.getSelection().equals("Custom")){
              screen.findElementByName("customResPanel").setVisible(false);
          }      
      }
      else{
          screen.findElementByName("flatPanel").setVisible(false);
          screen.findElementByName("customResPanel").setVisible(false);                
      }
      
  }
  
  public void startGame(){

      if(!hasInputErrors()){
          InitialSettings settings = this.getSettings();
          this.updateGameSettings();
          parent.finishLoading(settings);
      }
  }
  
  private void updateGameSettings(){
      StringBuilder s = new StringBuilder();
 
      s.append("hasKinect," + screen.findNiftyControl("kinectCheck", CheckBox.class).isChecked()+ System.getProperty("line.separator"));
      s.append("hasPressureSensor," + screen.findNiftyControl("psCheck", CheckBox.class).isChecked()+ System.getProperty("line.separator"));
      s.append("serverAddress," + screen.findNiftyControl("AddressText", TextField.class).getText() + System.getProperty("line.separator") );
      s.append("TCPPort," + screen.findNiftyControl("TCPPortText", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("UDPPort," + screen.findNiftyControl("UDPPortText", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("kinectAddress,host" + System.getProperty("line.separator"));
//      try{
//            String add =  InetAddress.getLocalHost().getHostAddress();
//            if(add.equals(screen.findNiftyControl("kinectAddress", TextField.class).getText())){
//                s.append("kinectAddress,host");
//            }
//            else{
//                s.append("kinectAddress," + screen.findNiftyControl("kinectAddress", TextField.class).getText());
//            }
//      }
//      catch(Exception e){
//      }
      
      s.append("kinectPort," + screen.findNiftyControl("kinectPort", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("pressureSensorAddress," + screen.findNiftyControl("psAddress", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("pressureSensorPort," + screen.findNiftyControl("psPort", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("immersiveHeight," + screen.findNiftyControl("immersiveHeight", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("immersiveWidth," + screen.findNiftyControl("immersiveWidth", TextField.class).getText()+ System.getProperty("line.separator"));
      s.append("isImmersive," + screen.findNiftyControl("immersiveButton", RadioButton.class).isActivated()+ System.getProperty("line.separator"));
      s.append("FOV," + screen.findNiftyControl("fov", TextField.class).getText()+ System.getProperty("line.separator"));
      
      File file = new File("config/gamesettings.txt");
      
      try{
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(s.toString());
        bw.close();
      }
      catch(IOException e){
          System.out.println(e);
      } 

  }
  
  public void run(){
      while(true){
          System.out.println("dddd");
      }
  }
  
//  public void omnom() {
//    new Thread(this, "My Thread").start();
//  }
//  
//  
  public void setLoadingMessage(){
    Label errorMessage = screen.findNiftyControl("errorMessage", Label.class);
    errorMessage.setText("hello");
  }
  
  private boolean hasInputErrors(){
      
      Label errorMessage = screen.findNiftyControl("errorMessage", Label.class);
 //     errorMessage.setText("DSDDD");  
      
        if(screen.findNiftyControl("AddressText", TextField.class).getText().isEmpty()){
            errorMessage.setText("Please specify a server address!");
            return true;
        }
              
        if(!this.isValidNumericInput("TCPPortText", 0, 65535)){
            errorMessage.setText("Please specify a valid TCP port!");
            return true;
        }
        if(!this.isValidNumericInput("UDPPortText",0, 65535)){
            errorMessage.setText("Please specify a valid UDP broadcast port!");
            return true;
        }

      
      if(screen.findNiftyControl("kinectCheck", CheckBox.class).isChecked()){
          if(!this.isValidNumericInput("kinectPort", 0, 65535)){
             errorMessage.setText("Please specify a valid Kinect program port!");
              return true;   
          }      
      }
      
      if(screen.findNiftyControl("psCheck", CheckBox.class).isChecked()){
           if(!this.isValidNumericInput("psPort", 0, 65535)){
             errorMessage.setText("Please specify a valid pressure sensor port!");
              return true;   
          }   
      }
      
      if(screen.findNiftyControl("flatButton", RadioButton.class).isActivated() && screen.findNiftyControl("flatRes", DropDown.class).getSelection().equals("Custom")){
          if(!this.isValidNumericInput("flatHeight", 10, 100000)){
              errorMessage.setText("Please specify a valid display height!");
              return true;
          }
          
          if(!this.isValidNumericInput("flatWidth", 10, 100000)){
              errorMessage.setText("Please specify a valid display width!");
              return true;
          }
      }
      
      else if(screen.findNiftyControl("immersiveButton", RadioButton.class).isActivated()){
           if(!this.isValidNumericInput("immersiveHeight", 10, 100000)){
              errorMessage.setText("Please specify a valid immersive display height!");
              return true;
          }
          
          if(!this.isValidNumericInput("immersiveWidth", 10, 100000)){
              errorMessage.setText("Please specify a valid immersive display width!");
              return true;
          }
          
          if(!this.isValidNumericInput("displayNum", 4, 8)){
              errorMessage.setText("Number of immersive displays must be between 4 and 8!");
              return true;       
          }    
          if(!this.isValidNumericInput("fov", 20, 150)){
              errorMessage.setText("Display field of view must be between 20 and 150 degrees!");
              return true;          
          } 
      }
      errorMessage.setText("LOADING...");
      return false;  
  }
  
  private boolean isValidNumericInput(String id, int lowerLim, int upperLim){
      
      try{
          int testChar = Integer.parseInt(screen.findNiftyControl(id, TextField.class).getText());
          if(testChar < lowerLim  || testChar > upperLim){
                  return false;
            }
      }  
      catch(NumberFormatException e){
          return false;
      }
      
      return true;
  
  
  }
  
//  public void selectMP(){
//        CheckBox cb = screen.findNiftyControl("mpCheck", CheckBox.class);
//        Element e = screen.findElementByName("portPanel");
//        
//        cb.setChecked(!cb.isChecked());
//        
//        if(cb.isChecked()){
//            e.setVisible(true);
//        }  
//        else{
//            e.setVisible(false);
//        }
//  }
  
    public void selectKinect(){
        CheckBox cb = screen.findNiftyControl("kinectCheck", CheckBox.class);
        Element e = screen.findElementByName("kinectPanel");
        
        cb.setChecked(!cb.isChecked());
    
        if(cb.isChecked()){
            e.setVisible(true);
        }  
        else{
            e.setVisible(false);
        }
  }
    
    public void selectPS(){
        
        CheckBox cb = screen.findNiftyControl("psCheck", CheckBox.class);
        Element e = screen.findElementByName("psPanel");
        
         cb.setChecked(!cb.isChecked());
         
        if(cb.isChecked()){
            e.setVisible(true);
        }  
        else{
            e.setVisible(false);
        }
    }
    
    public void selectTraining(){
        RadioButton trainingButton = screen.findNiftyControl("trainingButton", RadioButton.class);
        Element f = screen.findElementByName("offDef");
        
        trainingButton.select();
        f.setVisible(false);
    }
    
    public void selectTeam(){
        RadioButton teamButton = screen.findNiftyControl("teamButton", RadioButton.class);
        RadioButton offButton = screen.findNiftyControl("offButton", RadioButton.class);
        RadioButton defButton = screen.findNiftyControl("defButton", RadioButton.class);
        Element f = screen.findElementByName("offDef");
        
        teamButton.select();
        f.setVisible(true);
        
        if(offButton.isActivated()){
            offButton.select();
        }
        else{
            defButton.select();
        }
    }
    
    public void selectFlatDisplay(){
        RadioButton flatButton = screen.findNiftyControl("flatButton", RadioButton.class);
        DropDown flatRes = screen.findNiftyControl("flatRes", DropDown.class);
        screen.findElementByName("flatPanel").setVisible(true);
        
        screen.findElementByName("immersivePanel").setVisible(false);
        screen.findElementByName("numDisplays").setVisible(false);
        screen.findElementByName("displayDiagram").setVisible(false);
        screen.findElementByName("diagExplanation").setVisible(false);
        screen.findElementByName("cameraInfo").setVisible(false);
                
        Element e = screen.findElementByName("customResPanel");
        if(flatRes.getSelection().equals("Custom")){
            e.setVisible(true);
        }
        flatButton.select(); 
    }
    
    public void selectImmersiveDisplay(){
        RadioButton immersiveButton = screen.findNiftyControl("immersiveButton", RadioButton.class);
        screen.findElementByName("immersivePanel").setVisible(true);
        screen.findElementByName("numDisplays").setVisible(true);
        screen.findElementByName("displayDiagram").setVisible(true);
        screen.findElementByName("diagExplanation").setVisible(true);
        screen.findElementByName("cameraInfo").setVisible(true);
        
        screen.findElementByName("flatPanel").setVisible(false);
        screen.findElementByName("customResPanel").setVisible(false);

        immersiveButton.select(); 
    }
    
    public void selectActivityDisplay(String panelID){
        
        int currentDisplay = -1;
        int currentValue = -1;
        int newDisplay = Integer.parseInt(panelID);
        int newValue = diagramArray[newDisplay];
        
        for(int i = 0; i < diagramArray.length; i++){
            if(diagramArray[i] == 1 || diagramArray[i] == 3){
                currentDisplay = i;
                currentValue = diagramArray[i];
                break;
            }
        }
        
        if(currentValue != newValue){
        
            if(currentValue == 1){

                 this.removeChildFromElement("disp" + currentDisplay);
                 diagramArray[currentDisplay] = 0;
                
                if(newValue == 0){
                    this.addPanel("disp" + panelID, "#ff0000ff");
                    diagramArray[newDisplay] = 1;                                      
                }
                else if(newValue == 2){
                    this.removeChildFromElement("disp" + newDisplay);
                    this.addPanel("disp" + newDisplay, "#ff00ffff");
                    diagramArray[newDisplay] = 3;      
                }
            
            }
            else if(currentValue == 3){
                
                this.removeChildFromElement("disp" + currentDisplay);
                this.addPanel("disp" + currentDisplay, "#0000ffff");             
                diagramArray[currentDisplay] = 2;
                
            
                if(newValue == 0){
                    this.addPanel("disp" + panelID, "#ff0000ff");
                    diagramArray[newDisplay] = 1;               
                }
                else if(newValue == 2){
                    
                    this.removeChildFromElement("disp" + currentDisplay);
                    this.addPanel("disp" + newDisplay, "#ff00ffff");
                    diagramArray[newDisplay] = 3;       
                }        
            }
        }
    }
    
    private void removeChildFromElement(String elementID){
        Element e = screen.findElementByName(elementID);
        Element child = e.getElements().get(0);
        child.markForRemoval();
    }
    
    private void addPanel(String parentID, String color){
        Element e = screen.findElementByName(parentID);
        PanelCreator pc = new PanelCreator();
        pc.setChildLayout("center");
        pc.setBackgroundColor(color); //red
        pc.setHeight("100%");
        pc.setWidth("100%");
        pc.create(nifty, screen, e);
    }
    
    public void selectMainScreen(String panelID){
        int currentDisplay = -1;
        int currentValue = -1;
        int newDisplay = Integer.parseInt(panelID);
        int newValue = diagramArray[newDisplay];
        
        for(int i = 0; i < diagramArray.length; i++){
            if(diagramArray[i] == 2 || diagramArray[i] == 3){
                currentDisplay = i;
                currentValue = diagramArray[i];
                break;
            }
        }
        
        if(currentValue != newValue){
        
            if(currentValue == 2){

                 this.removeChildFromElement("disp" + currentDisplay);
                 diagramArray[currentDisplay] = 0;
                
                if(newValue == 0){
                    this.addPanel("disp" + panelID, "#0000ffff");
                    diagramArray[newDisplay] = 2;                                      
                }
                else if(newValue == 1){
                    this.removeChildFromElement("disp" + newDisplay);
                    this.addPanel("disp" + newDisplay, "#ff00ffff");
                    diagramArray[newDisplay] = 3;      
                }
            
            }
            else if(currentValue == 3){
                
                this.removeChildFromElement("disp" + currentDisplay);
                this.addPanel("disp" + currentDisplay, "#ff0000ff");             
                diagramArray[currentDisplay] = 1;
                
            
                if(newValue == 0){
                    this.addPanel("disp" + panelID, "#0000ffff");
                    diagramArray[newDisplay] = 2;               
                }
                else if(newValue == 1){
                    
                    this.removeChildFromElement("disp" + currentDisplay);
                    this.addPanel("disp" + newDisplay, "#ff00ffff");
                    diagramArray[newDisplay] = 3;       
                }        
            }
        }
    }
  
  private InitialSettings getSettings(){   
      InitialSettings settings = new InitialSettings();
      
//      RadioButton training = screen.findNiftyControl("trainingButton", RadioButton.class);

      CheckBox kinectCheck = screen.findNiftyControl("kinectCheck", CheckBox.class);
      CheckBox pressureSensorCheck = screen.findNiftyControl("psCheck", CheckBox.class);
      RadioButton flatButton = screen.findNiftyControl("flatButton", RadioButton.class);
      CheckBox recordCheck = screen.findNiftyControl("recordCheck", CheckBox.class);
            
      settings.setKinect(kinectCheck.isChecked());
      settings.setPressureSensor(pressureSensorCheck.isChecked());
      
      int[] resolution = new int[2];

        TextField server = screen.findNiftyControl("AddressText", TextField.class);
        TextField tcp = screen.findNiftyControl("TCPPortText", TextField.class);
        TextField udp = screen.findNiftyControl("UDPPortText", TextField.class);
        settings.setServerAddress(server.getText());
        settings.setTCPPort(Integer.parseInt(tcp.getText()));
        settings.setUDPPort(Integer.parseInt(udp.getText()));


      if(kinectCheck.isChecked()){
          TextField kinAdd = screen.findNiftyControl("kinectAddress", TextField.class);
          TextField kinPort = screen.findNiftyControl("kinectPort", TextField.class);
          settings.setKinectAddress(kinAdd.getText());
          settings.setKinectPort(Integer.parseInt(kinPort.getText()));
      }
      if(pressureSensorCheck.isChecked()){
          TextField psAdd = screen.findNiftyControl("psAddress", TextField.class);
          TextField psPort = screen.findNiftyControl("psPort", TextField.class);
          settings.setPressureAddress(psAdd.getText());
          settings.setPressurePort(Integer.parseInt(psPort.getText()));
      }
      
      if(flatButton.isActivated()){
          
          DropDown flatRes = screen.findNiftyControl("flatRes", DropDown.class);
          
          if(flatRes.getSelection().equals("Full Screen")){
              resolution[0] = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
              resolution[1] = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
          }
          else if(!flatRes.getSelection().equals("Custom")){
              resolution = new int[2];
              String s = flatRes.getSelection().toString();
              String[] t = s.split("x");
              resolution[0] = Integer.parseInt(t[0].trim());
              resolution[1] = Integer.parseInt(t[1].trim());
          }
          else{
              TextField flatHeight = screen.findNiftyControl("flatHeight", TextField.class);
              TextField flatWidth = screen.findNiftyControl("flatWidth", TextField.class);
              resolution[0] = Integer.parseInt(flatWidth.getText());
              resolution[1] = Integer.parseInt(flatHeight.getText());
          }
          
          settings.setResolution(resolution);
          
      }
      else{
          
          TextField immersiveWidth = screen.findNiftyControl("immersiveWidth", TextField.class);
          TextField immersiveHeight = screen.findNiftyControl("immersiveHeight", TextField.class);
          TextField numDisplays = screen.findNiftyControl("displayNum", TextField.class);
          TextField fov = screen.findNiftyControl("fov", TextField.class);
          CheckBox fixedOrientation = screen.findNiftyControl("fixedOrientation", CheckBox.class);
          
          int activityScreen = -1;
          int mainScreen = -1;
                    
          for(int i = 0; i < diagramArray.length; i++){
              if(diagramArray[i] == 1){
                  activityScreen = i;
              }
              else if(diagramArray[i] == 2){
                  mainScreen = i;
              }
              else if(diagramArray[i] == 3){
                  activityScreen = i;
                  mainScreen = i;
              }
          }
          
          resolution[0] = Integer.parseInt(immersiveWidth.getText());
          resolution[1] = Integer.parseInt(immersiveHeight.getText());
          
          settings.setResolution(resolution);
          settings.setNumDisplays(Integer.parseInt(numDisplays.getText()));
          settings.setActivityScreen(activityScreen);
          settings.setMainScreen(mainScreen);
          settings.setFOV(Float.parseFloat(fov.getText()));
          settings.setFixedOrientation(fixedOrientation.isChecked());
          settings.setImmersive(true);
      } 
            
      return settings;
  }
  
  private String getDataOfInput(String s){
  
      for(int i = 0; i < loadedFile.size(); i++){
          String data = loadedFile.get(i);
          if(data.contains(s)){
              String[] a = data.split(",");
              return a[1];
          }
      }
      
      return "";
  }
}
