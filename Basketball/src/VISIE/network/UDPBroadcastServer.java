/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Games.Game;
import VISIE.Main;
import VISIE.characters.BasketballCharacter;
import VISIE.scenemanager.SceneCharacterManager;
import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;



/**
 *
 * @author DiveshLala
 */
public class UDPBroadcastServer extends Thread implements Runnable{

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    private int connectionState = 0;
    private String message = "";
    private String ballMessage = "";
    private String gameStateMessage = "";
    private long timeOfLastUpdate= System.nanoTime();
    private Game parentClass;
    private int curr;
    private InetAddress add;
    private long timeOfLast = System.nanoTime();
    private long timeSinceLast = 0;
    private boolean hasStarted;
    
    public UDPBroadcastServer(Game p, int port){
        
        parentClass = p;
        
        try{
            System.out.println("UDP system initialised");
            socket = new DatagramSocket(port);
            socket.setSoTimeout(200);
    //        socket.
            connectionState = 1;
        }
        catch(IOException e){
            System.out.println("UDP error");
        }
    }
    
    
    public void run(){
        
        while(connectionState == 1){
            
            this.updateTimer();
            //send broadcast data
//            if(!message.isEmpty()){
//           //    try{
//                    byte[] buf = new byte[256];
//                    System.out.println(message);
//                    String s = message;
//                    buf = s.getBytes();
//                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                    
//              //      socket.send(packet);
//                    
//                    if(message.startsWith("TEAM")){
//                        System.out.println("team info sent");
//                    }
//                    
//              //      System.out.println("sending broadcast message");
//                    message = "";
//          //      }
//           //     catch(IOException e){
//            //        System.out.println(e + " " + message);
//            //    }
//            }
//            else if(hasStarted && parentClass.getRunning() && timeSinceLast > (200 * 1000 * 1000)){
//                this.sendHeartbeatMessage();
//            }
            
            //receives packets from individual clients
            try{
                //receive player position information
                byte[] buf = new byte[5000];
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                add = receivePacket.getAddress();
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String reply;
          //      System.out.println("received " + received);
                
                if(received.startsWith("SKEL")){
                    
                    //parse skeleton data
                    NetworkMessagingProcessor.parseClientJointData(received);
                    
                    //send back own skeleton info + received for other clients
                    String serverSkel = NetworkMessagingProcessor.createPlayerSkeletonInfoMessage();
                    
                   // reply = "";
                    
                    //broadcast the skeleton message (faster)
                    reply = (serverSkel);
                }
                else if(received.startsWith("ball")){
                    reply = "BALL" + ballMessage + "!" + gameStateMessage;
                }
                else if(received.startsWith("GEST")){ //gesture from client
                    NetworkMessagingProcessor.parseClientGestureData(received);
                    reply = "";
                }
                else if(received.startsWith("TEAM")){//request for team info
               //     System.out.println(received.substring(4));
                    int id = Integer.parseInt(received.substring(4));
                    BasketballCharacter bc = (BasketballCharacter)SceneCharacterManager.getCharacterByID(id);
                    reply = "TEAM" + bc.getTeamID() + "," + bc.getID();
                    
                }
                else if(received.startsWith("H")){
                    System.out.println("heartbeat received");
                    String mod = received.replaceFirst("H", "");
                    NetworkMessagingProcessor.parseNonUserPlayerMessage(mod);
                    reply = NetworkMessagingProcessor.createCharacterPositionsMessage();
                }
                else{
                    NetworkMessagingProcessor.parseNonUserPlayerMessage(received);
                    
                    //send back position info
                    reply = NetworkMessagingProcessor.createCharacterPositionsMessage();
                }    
                
        //        System.out.println(reply);
                
                if(!reply.isEmpty()){
         //           System.out.println("sending " + reply);
                    buf = new byte[256];
                    buf = reply.getBytes();
                    InetAddress address = receivePacket.getAddress();
                    DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 1237);
                    socket.send(sendPacket);
                    timeOfLast = System.nanoTime();
                    hasStarted = true;
                }
            }
            catch(SocketTimeoutException e){
                if(parentClass.getClientNumber() > 0){
                    this.sendHeartbeatMessage();
                }
            }
            catch(IOException e){
                System.out.println(e);
//               this.sendHeartbeatMessage();
            }
        }
    }
    
    private void sendHeartbeatMessage (){ 
            try{ 
                    String s = NetworkMessagingProcessor.createCharacterPositionsMessage();
                    String serverSkel = NetworkMessagingProcessor.createPlayerSkeletonInfoMessage();
                    byte[] buf = s.getBytes();

                    InetAddress group = InetAddress.getByName("230.0.0.1");

                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 1237);

                    socket.send(packet);
                    this.setMessage(serverSkel);
                    timeOfLast = System.nanoTime();
                //    System.out.println("heartbeat message " + timeSinceLast/1000000);
                    
                    this.updateTimer();
                }
                catch(IOException e){
                    System.out.println(e + " " + message);
                } 
                catch(NullPointerException e){}
    }
    
    
    public void setMessage(String str){
        message = str;
    }  
    
    public String getMessage(){
        return message;
    }
    
    public void setBallMessage(String str){
        ballMessage = str;
    }
    
    public void setGameStateMessage(String s){
        gameStateMessage = s;    
    }
    
    private void updateTimer(){
        timeSinceLast = System.nanoTime() - timeOfLast;
    }
}

    
    
