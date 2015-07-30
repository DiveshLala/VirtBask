/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VISIE.network;
import VISIE.Main;
import VISIE.scenemanager.SceneCharacterManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.MulticastSocket;

/**
 *
 * @author huang
 */
public class UDPBroadcastClient extends Thread implements Runnable{
    
    byte[] buf = new byte[256];
    InetAddress broadcastAddress;
    InetAddress datagramAddress;
    DatagramSocket clientSocket;
    int connectionState = 0;
    MulticastSocket broadcastSocket;
    Main parentClass;
    String message;
    String infoString = "";
    String jointInfoString = "";
    String ballPositionString = "";
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int portNumber;


    public UDPBroadcastClient(Main p, String hostname, int port){

        parentClass = p;

        try{
            System.out.println("Connecting to UDP " + port + " " + hostname );
            portNumber = port;
//            clientSocket = new DatagramSocket();
            datagramAddress = InetAddress.getByName(hostname);
       //     broadcastAddress = InetAddress.getByName("230.0.113.0");
            
            if(hostname.equals("localhost")){
                broadcastSocket = new MulticastSocket();
            }
            else{
                broadcastSocket = new MulticastSocket(portNumber);
            }
            
     //       broadcastSocket.setInterface(broadcastAddress);
      //      broadcastSocket.joinGroup(InetAddress.getByName("230.0.0.1"));
            broadcastSocket.setSoTimeout(100);
//            broadcastSocket.setBroadcast(true);
            System.out.println("Connected to broadcast group");
            connectionState = 1;
        }
        catch(IOException e){
            System.out.println("UDP error " + e);
        }


    }
    public void run(){
        
        while(connectionState == 1){
            
            buf = new byte[5000];
            receivePacket = new DatagramPacket(buf, buf.length);

            try{
                broadcastSocket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                
           //     System.out.println("received " + received);
                
                if(received.startsWith("REM")){
                    parentClass.flagCharactersForRemoval(Integer.parseInt(received.replaceAll("REM", "")));
                }
                else if(received.startsWith("POS")){
                    infoString = received;
                }
                else if(received.startsWith("SKEL")){
                   jointInfoString = received;
                }
                else if(received.startsWith("BALL")){
                    ballPositionString = received.substring(received.indexOf("BALL") + 4);
                }
                else if(received.startsWith("TEAM")){
                    System.out.println("NEW TEAM");
                    int[] info = NetworkMessagingProcessor.parseTeamUpdate(received.substring(received.indexOf("TEAM") + 4));
                    parentClass.updateTeamInfo(info);
                }
                else{ //new character created
                    System.out.println("rec " + received);
                    System.out.println(SceneCharacterManager.getCharacterArray().size());
                    String[] props = NetworkMessagingProcessor.parseNewNUPMessage(received);
                    if(SceneCharacterManager.getCharacterArray().size() > 0){
                          System.out.println("new NUP added");
             //             parentClass.flagNewCharacterForCreation(props[0], props[1], SceneCharacterManager.getPlayerID());
                    }
                }
            }
            catch(IOException e){
               // connectionState = 0;
            }
        }
        System.out.println(connectionState);
        broadcastSocket.close();
    }

    public void sendCurrentPosition(String positionString){ 
        try{
                buf = new byte[1024];
                String s = positionString;
                buf = s.getBytes();
                sendPacket = new DatagramPacket(buf, buf.length, datagramAddress, portNumber);
                broadcastSocket.send(sendPacket);
        }
        catch(IOException e){
            System.out.println(e);
        }
        catch(java.lang.IllegalArgumentException e){
            System.out.println(e);
        }
    }
    
    public void askForBall(){
        try{
                buf = new byte[1024];
                String s = "ball";
                buf = s.getBytes();
                sendPacket = new DatagramPacket(buf, buf.length, datagramAddress, portNumber);
                broadcastSocket.send(sendPacket);
        }
        catch(IOException e){
            System.out.println(e);
        }
        catch(java.lang.IllegalArgumentException e){
            System.out.println(e);
        }
        
    }
        
    public void sendSkeletonInformation(String skeletonString){
        try{
                buf = new byte[1024];
                String s = skeletonString;
                buf = s.getBytes();
                sendPacket = new DatagramPacket(buf, buf.length, datagramAddress, portNumber);
                broadcastSocket.send(sendPacket);      
        }
        catch(IOException e){
            System.out.println(e);
        }
        catch(java.lang.IllegalArgumentException e){
            System.out.println(e);
        }
    }
    
    public void requestTeamID(int idString){
        try{
                buf = new byte[1024];
                String s = "TEAM" + idString;
                buf = s.getBytes();
                sendPacket = new DatagramPacket(buf, buf.length, datagramAddress, portNumber);
                broadcastSocket.send(sendPacket);      
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void sendMessage(String message){
            try{
                buf = new byte[1024];
                String s = message;
                buf = s.getBytes();
                sendPacket = new DatagramPacket(buf, buf.length, datagramAddress, portNumber);
                broadcastSocket.send(sendPacket); 
                
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
   

    public String getInfoString(){
        return infoString;
    }
    
    public String getJointInfoString(){
        return jointInfoString;
    }
    
    public String getBallPosString(){
        return ballPositionString;
    }

}
