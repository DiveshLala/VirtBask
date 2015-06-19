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
 * @author DiveshLala
 */
public class Client extends Thread implements Runnable{
    
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null; 
    ByteArrayInputStream testIn = null;
    Main parentClass;
    private int connectionState = 0;
    byte[] b = new byte[4096];
    int currentDirection = 0;
    boolean isWalking = false;
    String tempDirection = "";
    
    public Client(Main parent){
        
        parentClass = parent;
               
    }
    
    public void run(){
        
//        //establish connection;
//        try{
//            System.out.println("Connecting to server...");
//            clientSocket = new Socket("DiveshLala", 1234);
//            System.out.println("connected"); 
//            connectionState = 1;
//        }
//        catch(UnknownHostException e){System.out.println("Can't find host!");}
//        catch(IOException e){System.out.println("Can't connect!");}
//        
//        //create I/O
//        try{
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            testIn = new ByteArrayInputStream(b, 0, 4096);
//        }
//        catch(IOException e){System.out.println("Can't do I/O!");}
//        
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));   
//        int fromServer;
//        
//        try{
//            while(connectionState == 1){
//                out.println(0);
//                fromServer = in.read();
//                char c  = (char)fromServer;
//                String str = String.valueOf(c);
//              //  System.out.println(str);
//                if(Character.isDigit(c)){
//                    tempDirection = tempDirection.concat(str);                    
//                }
//                else{
//                    if(str.equals("W")){
//                        isWalking = true;
//                    }
//                    else if(str.equals("N")){
//                        isWalking = false;
//                    }
//                    else if(str.equals(";")){
//                       try{
//                        currentDirection = Integer.parseInt(tempDirection); 
//                        tempDirection = "";
//                 //       System.out.println(currentDirection);
//                       }
//                       catch(NumberFormatException e){
//                    // System.out.println("cant parse");
//                       }
//                    }   
//                }
//                //Thread.sleep(20);
//            }        
//        out.close();
//        in.close();
//        stdIn.close();
//        clientSocket.close();
//            
//        }
//        catch(IOException e){
//            System.out.println("Disconnected");
//        }   

    }
    
    public boolean getWalking(){
        return isWalking;
    }
    
    public int getWalkingDirection(){
        return currentDirection;
    }
    
    public int getConnectionState(){
        return connectionState;
    }
    
    
}
