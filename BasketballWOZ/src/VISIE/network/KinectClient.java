/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Main;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.rmi.UnknownHostException;
import java.lang.Character;

/**
 *
 * @author huang
 */
public class KinectClient extends Client{
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null; 
    ByteArrayInputStream testIn = null;
    Main parentClass;
    private int connectionState = 0;
    byte[] b = new byte[4096];
    private static String kinectData = "";
    private String currentGesture = "";
    String serverName;
    int portNumber;
    
    public KinectClient(Main bob, String n, int p){
       super(bob, n, p);
   //     parentClass = parent; 
       serverName = n;
       portNumber = p;
    }
    
    public void run(){
        
        while(connectionState != 1){
            attemptConnection();
        }
        
        createIO();
        
        try{
            while(connectionState == 1){
                getKinectSensorInformation();
                Thread.sleep(100);                
            }
        out.close();
        in.close();
        clientSocket.close(); 
        }
        catch(IOException e){
            connectionState = 0;
            System.out.println("Disconnected");
        }   
        catch(InterruptedException e){
            System.out.println("blocked");
        }

    }
    
     public void attemptConnection(){
        //establish connection;
        try{
            System.out.println("Connecting to Kinect sensor on machine " + serverName);
            clientSocket = new Socket(serverName, portNumber);
            System.out.println("Connected to Kinect sensor");
            connectionState = 1;
        }
        catch(UnknownHostException e){
            System.out.println("Can't find host!");
        }
        catch(IOException e){
      //      System.out.println("Can't connect to Kinect sensor! Retrying...");
        }
    }
     
     public void createIO(){
         try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            testIn = new ByteArrayInputStream(b, 0, 4096);
        }
        catch(IOException e){System.out.println("Can't do I/O!");}

     }


    public int getConnectionState(){
        return connectionState;
    }
    
    
    public void getKinectSensorInformation(){
       int fromServer;
        boolean endOfString = false;
        String dataType = "joint";
        try{
            StringBuilder data = new StringBuilder();
            StringBuilder gesture = new StringBuilder();
            int i = 0;
            try{
                out.println(0);
                boolean containsGesture = String.valueOf((char)in.read()).equals("G");
                while(!endOfString){
                    fromServer = in.read();
                    char c  = (char)fromServer;
                    String str = String.valueOf(c);
                    if(str.equals(";") && !containsGesture){
                         endOfString = true;
                    }
                    else if(str.equals("%")){
                        endOfString = true;
                    }
                    else if(str.equals("$")){
                        dataType = "gesture";
                    }
                    else{                     
                        if(dataType.equals("joint")){
                            data.append(str);
                        }
                        else if(dataType.equals("gesture")){
                            gesture.append(str);
                        }
                    }
                }
            }
            catch(IOException e){
                System.out.println("read interrupted" + e);
                connectionState = 0;
            }
            
          kinectData = data.toString();
          currentGesture = gesture.toString(); 
          if(!currentGesture.equals("")){
              System.out.println("message " + currentGesture);
          }
//          else{
//              System.out.println("no gesture " + this.getCurrentGesture());
//          }
        }
        catch(NullPointerException e){
            kinectData = "";
        }
    }
    
    public static String getKinectData(){
        return kinectData;
    }
    
    public String getCurrentGesture(){
        return currentGesture;
    }
    
}
