/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Main;
import VISIE.scenemanager.SceneCharacterManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.UnknownHostException;
import java.lang.Character;
import java.net.SocketException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author DiveshLala
 */
public class Client extends Thread implements Runnable{
    
    Socket clientSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    Main parentClass;
    private int connectionState = 0;
    byte[] b = new byte[4096];
    int currentDirection = 0;
    boolean isWalking = false;
    String tempDirection = "";
    String serverName;
    String positions = "";
    boolean doingIO;
    int portNumber;
    boolean charactersLoaded = false;
    
    public Client(Main parent, String name, int p){  
        parentClass = parent;
        serverName = name;
        portNumber = p;
    }
    
    public void run(){
//        //establish connection;
        while(connectionState != 1){
            attemptConnection();
        }
        
        createIOStreams();
        
        //retrieves character information from ther server
        try{
            out.writeObject("New");
            out.writeObject(parentClass.isKinect());
            
            this.receiveMapData();
            this.receivePlayerData();
            this.receiveCharacterData();
            this.receiveBallData();
                                    
            charactersLoaded = true;                         
        }
        catch(IOException e){
            System.out.println("Can't load objects! " + e);
        }

        while(connectionState == 1){
            try{
                String received = in.readObject().toString();
                if(received.startsWith("New")){
                    System.out.println(SceneCharacterManager.getCharacterArray().size());
                    String[] props = NetworkMessagingProcessor.parseNewNUPMessage(received);
                    if(SceneCharacterManager.getCharacterArray().size() > 0){
                          System.out.println("new NUP added");
      //                    parentClass.flagNewCharacterForCreation(props[0], props[1], SceneCharacterManager.getPlayerID());
                    }
                }
            }
            catch(ClassNotFoundException e){}
            catch(IOException e){}

        }
            try{
            out.close();
            in.close();
            clientSocket.close();
        }
        catch(IOException e){}
            
    }
    
    public void confirmToServer(){
        try{
            System.out.println("loaded!");
            out.writeObject("FIN");
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
    private void receiveMapData(){
            //receive map
        String mapDirectory = "assets/Scenes/town.j3o";
        this.saveFileFromNetwork(mapDirectory);
        parentClass.loadNetworkEnvironment(mapDirectory.replace("assets/", ""));
        parentClass.cleanUpOnExit(mapDirectory);
            
    }
    
    private void receivePlayerData(){
        
            String playerZipDirectory = "assets/Models/player.zip";  
            String playerModelDirectory = "assets/Models"; 
            String fileName = "player.j3o";
            String playerInfo = "";
                    
            //receive player model + info
            this.saveFileFromNetwork(playerZipDirectory);
            this.deCompressZipFile(playerZipDirectory, playerModelDirectory, fileName);
            
            try{
                playerInfo = in.readObject().toString();
                parentClass.flagPlayerForCreation(playerInfo + fileName);
                parentClass.cleanUpOnExit(playerModelDirectory + File.separator + fileName);
                parentClass.cleanUpOnExit(playerZipDirectory);
                
            }
            catch(IOException e){
                System.out.println("player cant be loaded! " + e);
            }
            catch(ClassNotFoundException e){
                System.out.println("player cant be loaded! " + e);
            }        
    }
    
    private void deCompressZipFile(String zipDir, String outDir, String fileName){
        
        File folder = new File(outDir);
    	if(!folder.exists()){
    		folder.mkdir();
    	}
        
        try{
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDir));
            ZipEntry ze = zis.getNextEntry();
            
            byte[] buffer = new byte[1024];
            
 
    //       String fileName = ze.getName();
           File newFile = new File(outDir + File.separator + fileName);

            System.out.println("file unzip : "+ newFile.getAbsoluteFile());

            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
             new File(newFile.getParent()).mkdirs();

             FileOutputStream fos = new FileOutputStream(newFile);             

            int len;

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

           fos.flush();
           fos.close();   
            
        zis.closeEntry();
        zis.close();
        
                    
        System.out.println("Done");
            
        }
        catch(FileNotFoundException e){System.out.println(e);}
        catch(IOException e){System.out.println(e);}
    
    }
    
    private void receiveCharacterData(){
        
        //receive other character models + info
        try{
            int numChar = (Integer)in.readObject();

            for(int i = 0; i < numChar; i++){
                String characterZipDirectory = "assets/Models/" + i + ".zip";  
                String characterModelDirectory = "assets/Models"; 
                String fileName = i + ".j3o";

                //receive player model + info
                this.saveFileFromNetwork(characterZipDirectory);
                this.deCompressZipFile(characterZipDirectory, characterModelDirectory, fileName);
                                
                parentClass.cleanUpOnExit(characterModelDirectory + File.separator + fileName);
                parentClass.cleanUpOnExit(characterZipDirectory);

                String info = in.readObject().toString();
                parentClass.flagNewCharacterForCreation(info);
            }    
        }
        catch(IOException e){
             System.out.println("characters cant be loaded! " + e);
        }
        catch(ClassNotFoundException e){
            System.out.println("characters cant be loaded! " + e);
        }  
    }
    
    private void receiveBallData(){
        
    //flag ball
        try{
            String ballString = in.readObject().toString();
            parentClass.flagBallForCreation(ballString);
        }
        catch(IOException e){
            System.out.println("ball cant be loaded! " + e);
        }
        catch(ClassNotFoundException e){
            System.out.println("ball cant be loaded! " + e);
        }

    }

    public void attemptConnection(){
        //establish connection;
        try{
            System.out.println("Connecting to server..." + serverName + " " + portNumber);
            clientSocket = new Socket(serverName, portNumber);
            System.out.println("Connected to TCP server");
            connectionState = 1;
        }
        catch(java.net.UnknownHostException e){
            System.out.println("Can't find host!");
        }
        catch(IOException e){
            System.out.println("Can't connect to TCP server! Retrying..." + e);
        }
    }
        
    public void createIOStreams(){
        try{
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        }
        catch(IOException e){System.out.println("Can't do I/O!");}

    }
    public boolean charactersLoaded(){
        return charactersLoaded;
    }
   
    public int getConnectionState(){
        return connectionState;
    }

    public void resetPositionsString(){
        positions = "";
    }

    public void setPositionsString(String str){
        positions = str;
    }

    public String getPositionString(){
        return positions;
    }

    //sends player info and returns positions of everyone else
    public void retrieveWorldInformation(){
        try{
                out.writeUTF(NetworkMessagingProcessor.createPlayerInformationMessage(parentClass.getPlayer()));
                this.setPositionsString(in.readUTF());
        }
        catch(IOException e){}
    
    }
    
    public void saveFileFromNetwork(String dir){
        
        try{
            int len = in.readInt();
            System.out.println("receiving file..." + len);
            byte[] data = new byte[len];
            if (len > 0) {
                in.readFully(data);
            }
            
            System.out.println("file received " + data.length);
            
            FileOutputStream fos = new FileOutputStream(dir);
            fos.write(data);
            fos.close();
            System.out.println("File saved! " + dir);
        }
        catch(java.io.IOException e){
            System.out.println("cant read file! " + e);
        }
    }
    
    
}
