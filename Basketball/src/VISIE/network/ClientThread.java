/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Games.Game;
import VISIE.Main;
import VISIE.scenemanager.SceneCharacterManager;
import VISIE.scenemanager.SceneCreator;
import VISIE.characters.Character;
import com.jme3.math.Vector3f;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author DiveshLala
 */
public class ClientThread extends Thread implements Runnable {
    
    Socket clientSocket;   
 //   private PrintWriter out;
    private ObjectOutputStream out;
    private ObjectInputStream in;
//    private BufferedReader in;
    private int connectionState = 0;
    private Game parentClass;
    private int clientID;
    private Server server;
    private String newPlayerInfo = "";
    private boolean clientIsReady;
    
    public ClientThread(Socket s, Game m, Server serv){
        clientSocket = s;
        parentClass = m;
        server = serv;
        
        try{
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
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
                        System.out.println("waiting");
                        inputLine = (String)in.readObject();

                        if(inputLine.startsWith("New")){      
                                                                
                            if(parentClass.getGameState() == 1){ //if pre loading
                                while(parentClass.getGameState() != 2){ // wait until game is finished loading
                                    Thread.sleep(100);
                                }
                            } 
                            
                            clientID = SceneCharacterManager.generateNewID();
                                                        
                            boolean kinectState = (Boolean)in.readObject();
                            
                            SceneCharacterManager.flagNewNUP(clientID + "!" + kinectState);                            
                                      
                            //send map model
                            this.sendFileOverNetwork("assets/" + SceneCreator.getSceneName());
                                                                                    
                            //waits until a position has been generated for client
                            while(SceneCharacterManager.getCharacterPosition(clientID) == null){
                                Thread.sleep(100);
                            }
                            
                            //send NUP Player model and info    
                            String zipFileDir = "assets/alananim.zip";
                            this.compressFile(zipFileDir, "assets/Models/BPNew/alananim.j3o");      
                            this.sendFileOverNetwork(zipFileDir);
                            String newPlayerString = "N" + clientID + SceneCharacterManager.getCharacterPosition(clientID) + "!0.48;";
                            out.writeObject(newPlayerString);
                                                        
                            //send character model and info
                            this.sendCharacterFiles();
                            
                            //send ball info
                            out.writeObject(NetworkMessagingProcessor.createBallMessage());
                            
                           
                           System.out.println("New player connected");
                                                      
                        }
                        else if(inputLine.equals("FIN")){
                            parentClass.clientsAreReady();
                        }
                        out.flush();
                        Thread.sleep(5);
                }
                    catch(IOException e){
                        System.out.println("Client " + clientID + " has disconnected");
                        connectionState = 0;
                    }
                    catch(InterruptedException e){}
                    catch(ClassNotFoundException e){
                        System.out.println(e);
                    }
                }
                try{
                    Character c  = SceneCharacterManager.getCharacterByID(clientID);
                    c.cleanUp();
                    System.out.println("CLOSING connection...");
                    server.removeClient(clientID, this.getId());
                    out.close();
                    in.close();
                    clientSocket.close();
                }
                catch(IOException e){}
    
    }   
    
    public void sendMessage(String str){
        try{
            out.writeUTF(str);
            out.flush();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void setNewPlayerInfo(String s){
        newPlayerInfo = s;
    }   
    
    private void sendCharacterFiles(){
        
        try{
        
        ArrayList<Character> characterArray = SceneCharacterManager.getCharacterArray();
        out.writeObject(characterArray.size() - 1);
        
        for(int i = 0; i < characterArray.size(); i++){
            if(characterArray.get(i).getID() != clientID){
                
                String j3oPath = "assets/" + characterArray.get(i).getModelFilePath();
                String zipPath = "assets/" + i + ".zip";
                this.compressFile(zipPath, j3oPath);             
                this.sendFileOverNetwork(zipPath);  
                String info = characterArray.get(i).getCharacterType() + "!" 
                        + characterArray.get(i).getID() + "!" 
                        + characterArray.get(i).getPosition() + "!" 
                        + characterArray.get(i).getModelScale();
                out.writeObject(info);
            }
        }
        }
        catch(IOException e){
            System.out.println(e);
        }
                
    }
    
    private void compressFile(String zipOutput, String j3oInput){
        
        byte[] buffer = new byte[1024];
        
        System.out.println("zipping " + zipOutput);
        
        try{
        
            FileOutputStream fos = new FileOutputStream(zipOutput);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry("player.j3o");
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(j3oInput);
            
            int len;
            while((len = in.read(buffer)) > 0){
                zos.write(buffer, 0, len);
            }
                        
            in.close();
            zos.closeEntry();
            zos.close();
            
            this.cleanUpOnExit(zipOutput);
            
            System.out.println("Done");
        
        }
        catch(FileNotFoundException e){System.out.println(e);}
        catch(IOException e){System.out.println(e);}
              
    }
    
    private void cleanUpOnExit(String fileDir){
        File f = new File(fileDir);
        if(f.exists()){
            f.deleteOnExit();
        }
    }
    
    
    
    private void sendFileOverNetwork(String filePath){
        
        try{
            File file = new File(filePath);
            Path path = Paths.get(file.getPath());
            byte[] b = Files.readAllBytes(path);
                        
            out.writeInt(b.length);
            
            System.out.println("sending file..." + b.length);
            
            if (b.length > 0) {
                out.write(b, 0, b.length);
            }
                        
            out.flush();
            
            System.out.println("file sent");
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
    
}
