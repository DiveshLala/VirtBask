/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE;

/**
 *
 * @author Divesh
 */
public class InitialSettings {
    
    private int numPlayers;
    private boolean isOffense;
    private boolean isMultiPlayer;
    private boolean hasKinect;
    private boolean hasPressureSensor;
    private int TCPPort;
    private int UDPPort;
    private String kinectAddress;
    private int kinectPort;
    private String pressureSensorAddress;
    private int pressureSensorPort;
    private String gameType;
    private int[] resolution;
    private boolean isImmersive;
    private int numDisplays;
    private int activityScreenLocation;
    private int mainScreen;
    private float FOV;
    private boolean isFixedOrientation;
    private boolean doRecording;
    private String serverAddress;
    
    public InitialSettings(){
    }
    
    public void setNumPlayers(int n){
        numPlayers = n;
    }
    public void setOffense(boolean b){
        isOffense = b;
    }
    public void setMultiPlayer(boolean b){
        isMultiPlayer = b;
    }
    public void setKinect(boolean b){
        hasKinect = b;
    }
    public void setPressureSensor(boolean b){
        hasPressureSensor = b;
    }
    
    public void setTCPPort(int i){
        TCPPort = i;
    }
    
    public void setUDPPort(int i){
        UDPPort = i;
    }
    
    public void setKinectAddress(String s){
        kinectAddress = s;
    }
    
    public void setKinectPort(int i){
        kinectPort = i;
    }
    
    public void setPressureAddress(String s){
        pressureSensorAddress = s;
    }
    
    public void setPressurePort(int i){
        pressureSensorPort = i;
    }
    
    public void setGameType(String s){
        gameType = s;
    }
    public void setResolution(int[] res){
        resolution = res;
    }
    public void setImmersive(boolean b){
        isImmersive = b;
    }
    
    public void setNumDisplays(int i){
        numDisplays = i;
    }
    
    public void setActivityScreen(int i){
        activityScreenLocation = i;
    }
    
    public void setMainScreen(int i){
        mainScreen = i;
    }
    
    public void setFOV(float f){
        FOV = f;
    }
    
    public void setFixedOrientation(boolean b){
        isFixedOrientation = b;
    }
    
    public void setRecording(boolean b){
        doRecording = b;
    }
    
    public void setServerAddress(String s){
        serverAddress = s;
    }
    
    public int getNumPlayers(){
        return numPlayers;
    }
    public boolean getOffense(){
        return isOffense;
    }
    public boolean getMultiPlayer(){
        return isMultiPlayer;
    }
    public boolean getKinect(){
        return hasKinect;
    }
    public boolean getPressureSensor(){
       return hasPressureSensor;
    }
    
    public int getTCPPort(){
        return TCPPort;
    }
    
    public int getUDPPort(){
        return UDPPort;
    }
    
    public String getKinectAddress(){
        return kinectAddress;
    }
    
    public int getKinectPort(){
        return kinectPort;
    }
    
    public String getPressureAddress(){
        return pressureSensorAddress;
    }
    
    public int getPressurePort(){
        return pressureSensorPort;
    }
    
    public String getGameType(){
        return gameType;
    }
    
    public int[] getResolution(){
        return resolution;
    }
    
    public boolean getImmersive(){
        return isImmersive;
    }
    
    public int getNumDisplays(){
        return numDisplays;
    }
    
    public int getActivityScreen(){
        return activityScreenLocation;
    }
    
    public int getMainScreen(){
        return mainScreen;
    }
    
    public float getFOV(){
        return FOV;
    }
    
    public boolean getFixedOrientation(){
        return isFixedOrientation;
    }
    
    public boolean getRecording(){
        return doRecording;
    }
    
    public String getServerAddress(){
        return serverAddress;
    }
    
    @Override
    public String toString(){
        String newLine = System.getProperty("line.separator");
        
        
        return "no. of players = " + numPlayers + newLine
                + "is offense " + isOffense + newLine
                + "is multiplayer " + isMultiPlayer + newLine
                + "has Kinect " + hasKinect + newLine
                + "has pressure sensor " + hasPressureSensor + newLine
                + "tcp port = " + TCPPort + newLine
                + "udp port = " + UDPPort + newLine
                + "Kinect address " + kinectAddress + newLine
                + "Kinect port = " + kinectPort + newLine
                + "pressure sensor address " + pressureSensorAddress + newLine
                + "pressure sensor port =  " + pressureSensorPort + newLine
                + "is training " + gameType + newLine
                + "resolution width " + resolution[0] + newLine
                + "resolution height " + resolution[1] + newLine
                + "is immersive " + isImmersive + newLine
                + "no. of displays " + numDisplays + newLine
                + "activity screen " + activityScreenLocation + newLine
                + "main screen " + mainScreen + newLine
                + "FOV " + FOV + newLine
                + "is fixed orientation " + isFixedOrientation
                + "do recording " + doRecording
                + "server address " + serverAddress;
    }
    
}
