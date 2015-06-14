/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Games.Game;
import VISIE.Main;
import VISIE.scenemanager.SceneCharacterManager;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;

/**
 *
 * @author DiveshLala
 */
public class Server extends Thread implements Runnable{
    
   
    private ServerSocket server;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream oos;
    private Vector3f clientPos;
    private int connectionState = 0;
    private Game parentClass;
    private String playerPos = "";
    private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    private String newPlayerInfo = "";
    private UDPBroadcastServer broadcastServer;
      
    public Server(Game parent, int port, UDPBroadcastServer udp){
        
        clientPos = null;
        parentClass = parent;
        broadcastServer = udp;
                
        try{
            server = new ServerSocket(port);
            System.out.println("TCP Server initialised");
            connectionState = 1;
            
        }
        catch(IOException e){
            System.out.println("Unavailable port");
        }                    
    }
    
    public void run(){
        
        while(true){
            try{
                 System.out.println("TCP server accepting");
                 Socket s = server.accept(); 
                 ClientThread c = new ClientThread(s, parentClass, this);
                 c.start();
                 clients.add(c);
                 System.out.println("Player added");
                 for(int i = 0; i < clients.size(); i++){
                     System.out.println(clients.get(i).getId());
                 }
            }
            catch(IOException e){
            }
        }
                
//            try{
//                System.out.println("accepting");
//                clientSocket = server.accept();
//                System.out.println("accepted");
//                connectionState = 1;
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                oos = new ObjectOutputStream(clientSocket.getOutputStream());
//                
//                String inputLine;
//                while(connectionState == 1){
//                    inputLine = in.readLine();
//                    System.out.println(inputLine);
//                    if(inputLine.equals("New")){
//                        System.out.println("New player connected");
//                        int clientID = parentClass.getRobotPositions().size();
//                        Vector3f initPos = new Vector3f(-15f, 3f, -15f);
//                        out.println("N" + clientID + initPos + ";");
//                        parentClass.notifyNewPlayerArrival(clientID, initPos);
//                        out.println(getPositionsString());
//                        out.println(robotColoursString());                       
//                    }
//                            
//                    else if(inputLine.startsWith("C")){
//                        out.println(robotColoursString());                    
//                    }
//                    else{//message is an update of player position
//                         parentClass.updatePlayerPositions(inputLine);
//                   //      playerPos = inputLine;
//                         out.println(getPositionsString());                 
//              //           System.out.println(in.readLine());                
//                    }
//  //                  out.println("Robot Positions are " + getRobotPositions());
////                    inputLine = in.readLine();
////                    System.out.println(inputLine); 
////                    processMessage(inputLine);
//                    Thread.sleep(5);
//                }
//             //   out.close();
////                in.close();
////                clientSocket.close();
////                server.close();
//            }
//            catch(IOException e){
//                clientPos = Vector3f.ZERO;
//                connectionState = 0;
//                System.out.println("Connection error " + e);
//            }  
//            catch(InterruptedException e){
//                System.out.println("Cant accept");
//            }
//            
//            //attempts to start new connection
//            run();    
    }
    
    
    public void removeClient(int clientID, long threadID){
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getId() == (int)threadID){
                clients.remove(i);
                break;
            }
        }
        SceneCharacterManager.flagDeletedCharacter(clientID);
        broadcastServer.setMessage("REM" + clientID);
    }
    
    public void broadcastNewPlayerMessage(String s){
        //use TCP (important)
        System.out.println("NEW PLAYER BROADCAST " + s);
            for(int i = 0; i < clients.size(); i++){
                clients.get(i).sendMessage(s);            
            }
    }
    
    public void broadcastTeamMessage(String s){
        System.out.println("TEAM BROADCAST " + s);
            for(int i = 0; i < clients.size(); i++){
                clients.get(i).sendMessage(s);            
            }
    }
    
    public int getNumberOfUsers(){
        return clients.size();
    }
    
     
}
