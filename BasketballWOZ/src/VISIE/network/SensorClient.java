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
import java.net.InetAddress;
/**
 *
 * @author DiveshLala
 */
public class SensorClient extends Client{
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null; 
    ByteArrayInputStream testIn = null;
    Main parentClass;
    private int connectionState = 0;
    byte[] b = new byte[4096];
    int currentDirection = 0;
    float currentSpeed = 0;
    boolean isWalking = false;
    String tempDirection = "";
        String turning = "";
    String serverName;
    int portNumber;
    
    public SensorClient(Main parent, String n, int p){
       super(parent, n, p);
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
                
            }
        out.close();
        in.close();
        clientSocket.close(); 
        }
        catch(IOException e){
            connectionState = 0;
            System.out.println("Disconnected");
        }   
//        catch(InterruptedException e){
//            System.out.println("blocked");
//        }

    }
    
     public void attemptConnection(){
        //establish connection;
        try{
            System.out.println("Connecting to pressure sensor..." + serverName + " " + portNumber);
            clientSocket = new Socket(serverName, portNumber);
            System.out.println("Connected to pressure sensor");
            connectionState = 1;
        }
        catch(java.net.UnknownHostException e){
            System.out.println("Can't find host!");
        }
        catch(IOException e){
            System.out.println("Can't connect to pressure sensor! Retrying..." + e);
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
    
    public boolean getWalking(){
        return isWalking;
    }
    
    public int getSensorWalkingDirection(){
        return currentDirection;
    }
    
    public int getModifiedWalkingDirection(){
        if(currentDirection > 120 && currentDirection < 240){
            if(currentDirection > 180){
                return currentDirection - 180;
            }
            else
                return currentDirection + 180;
                    
        }
        else
            return currentDirection;
    }
    
    public float getWalkingSpeed(){
        return currentSpeed;
    }

    public int getConnectionState(){
        return connectionState;
    }
    
        public void getSensorInformation(){
        int fromServer;
        boolean endOfString = false;
        try{
            StringBuilder direction = new StringBuilder();
            StringBuilder speed = new StringBuilder();
            int i = 0;
                        try{
                            out.println(0);
                            while(!endOfString){
                                fromServer = in.read();
                                char c  = (char)fromServer;
                                String str = String.valueOf(c);
                                if(str.equals(";")){
                                     endOfString = true;
                                }
                                else if(str.equals("D")) {
                                    i = 1;
                                  //  isWalking = true;
                                }
                                else if(str.equals("S")) {
                                    i = 2;
                                 //   isWalking = false;
                                }
                                else if(Character.isDigit(c)){
                                    if(i == 1)
                                         direction.append(str);
                                    else if(i == 2)
                                         speed.append(str);
                                }
                                else if(str.equals("L")){
                                    turning = "L";
                                }
                                else if(str.equals("R")){
                                    turning = "R";
                                }
                                else if(str.equals("X")){
                                    turning = "";
                                }
                                else if(str.equals("B")){
                                    turning = "B";
                                }
                            }
                            if(!direction.toString().isEmpty()){
                                 currentDirection = Integer.parseInt(direction.toString());
                            }
                            if(!speed.toString().isEmpty()){
                                 currentSpeed = Integer.parseInt(speed.toString());
                            }
                            
                        //    System.out.println("Direction: " + currentDirection + " Speed: " + currentSpeed);

                        }
                        catch(IOException e){
                            System.out.println("read interrupted" + e);
                            connectionState = 0;
                        }
        }
        catch(NullPointerException e){
            System.out.println("I/O not yet created!");
        }
    }
    
//    public void getSensorInformation(){
//        int fromServer;
//        boolean endOfString = false;
//        try{
//            StringBuilder direction = new StringBuilder();
//            StringBuilder speed = new StringBuilder();
//            int i = 0;
//                        try{
//                            out.println(0);
//                            while(!endOfString){
//                                fromServer = in.read();
//                                char c  = (char)fromServer;
//                                String str = String.valueOf(c);
//                                if(str.equals(";")){
//                                     endOfString = true;
//                                }
//                                else if(str.equals("D")) {
//                                    i = 1;
//                                  //  isWalking = true;
//                                }
//                                else if(str.equals("S")) {
//                                    i = 2;
//                                 //   isWalking = false;
//                                }
//                                else if(Character.isDigit(c)){
//                                    if(i == 1)
//                                         direction.append(str);
//                                    else if(i == 2)
//                                         speed.append(str);
//                                }
//                            }
//                            if(!direction.toString().isEmpty()){
//                                 currentDirection = Integer.parseInt(direction.toString());
//                            }
//                            if(!speed.toString().isEmpty()){
//                                 currentSpeed = Integer.parseInt(speed.toString());
//                            }
//                            
//                        //    System.out.println("Direction: " + currentDirection + " Speed: " + currentSpeed);
//
//                        }
//                        catch(IOException e){
//                            System.out.println("read interrupted" + e);
//                            connectionState = 0;
//                        }
//        }
//        catch(NullPointerException e){
//            System.out.println("I/O not yet created!");
//        }
//    }
    
        public String getTurning(){
        return turning;
    }
    
}

