/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Main;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import VISIE.characters.Character;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import com.jme3.math.Vector3f;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.OutputStream;

/**
 *
 * @author DiveshLala
 */
public class ClientThread extends Thread implements Runnable {
    
    Socket clientSocket;   
    private PrintWriter out;
    private BufferedReader in;
    private int connectionState = 0;
    private Main parentClass;
    private int clientID;
    private Server server;
    private String newPlayerInfo = "";
    
    public ClientThread(Socket s, Main m, Server serv){
        clientSocket = s;
        parentClass = m;
        server = serv;
        
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connectionState = 1;
            clientSocket.setTcpNoDelay(true);
        }
        catch(IOException e){
            System.out.println("Cant do I/O!");
        }
    }
    
    public void run(){
        String inputLine;
                while(connectionState == 1){
                    try{
                        inputLine = in.readLine();
                        if(inputLine.startsWith("New")){
                            String modelType;
                            modelType = inputLine.replace("New", "");
                            clientID = SceneCharacterManager.generateNewID();
                            SceneCharacterManager.flagNewNUP(clientID + "!" + modelType);
                            
                            out.println(SceneCreator.getSceneName());
                            out.println(NetworkMessagingProcessor.createCharacterInitializationMessage());
                            out.println(NetworkMessagingProcessor.createTextureMessage()); 
                            
                            //waits until a position has been generated for client
                            while(SceneCharacterManager.getCharacterPosition(clientID) == null){
                            }
                            //send information to broadcast server so that clients may update
                           String newPlayerString = "N" + clientID + SceneCharacterManager.getCharacterPosition(clientID) + NetworkMessagingProcessor.createTextureMessage(clientID) +";";
                           //send this info back to the TCP connection
                           out.println(newPlayerString);
                           out.println(clientID);
                           out.println(NetworkMessagingProcessor.createBallMessage());
                           
                          // newPlayerString = this.getClientInformation(clientID);
                           newPlayerString = NetworkMessagingProcessor.createCharacterInformationMessage(clientID);
                           //broadcast the new message so other clients can create the player in their system
                           server.broadcastNewPlayerMessage("New" + newPlayerString);
                           
                           System.out.println("New player connected");
                        }

                        else if(inputLine.startsWith("C")){
                            out.println(NetworkMessagingProcessor.createTextureMessage());                    
                        }
                        else if(inputLine.startsWith("SKEL")){
                       //     System.out.println("hello");
                        }
                        
                        else{
                             //message is an update of player position
                             NetworkMessagingProcessor.parseNonUserPlayerMessage(inputLine);
                             out.println(NetworkMessagingProcessor.createCharacterPositionsMessage());            
                        }
                        out.flush();
                        Thread.sleep(5);
                }
                    catch(IOException e){
                        System.out.println("Client " + clientID + " has disconnected");
                        connectionState = 0;
                    }
                    catch(InterruptedException e){}
                }
                try{
                    System.out.println("CLOSING connection...");
                    server.removeClient(clientID, this.getId());
                    out.close();
                    in.close();
                    clientSocket.close();
                }
                catch(IOException e){}
    
    }   
    
    public void sendMessage(String str){
        out.println(str);
    }
    
    public void setNewPlayerInfo(String s){
        newPlayerInfo = s;
    }
    
    
    
}
