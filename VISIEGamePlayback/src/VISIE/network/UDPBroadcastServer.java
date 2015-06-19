/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;
import VISIE.Main;
import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;



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
    private Main parentClass;
    private int curr;
    
    public UDPBroadcastServer(Main p, int port){
        
        parentClass = p;
        
        try{
            System.out.println("Broadcast system initialised");
            socket = new DatagramSocket(port);
            socket.setSoTimeout(100);
            connectionState = 1;
        }
        catch(IOException e){
            System.out.println("UDP");
        }
    }
    
    
    public void run(){
        
        while(connectionState == 1){  
            //send broadcast data
            if(!message.isEmpty()){
                try{
                    byte[] buf = new byte[256];
                    String s = message;
                    buf = s.getBytes();

                    InetAddress group = InetAddress.getByName("230.0.0.1");

                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 1237);

                    socket.send(packet);
                    System.out.println("sending broadcast message");
                    message = "";
                }
                catch(IOException e){}
            }
            
            //receives packets from individual clients
            try{
                System.out.println("dfgfdd");
                //receive player position information
                byte[] buf = new byte[5000];
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String reply;
                if(received.startsWith("SKEL")){
                    NetworkMessagingProcessor.parseClientJointData(received);
                    
                    //send back skeleton info
                    reply = NetworkMessagingProcessor.createSkeletonInfoMessage();
                }
                else if(received.startsWith("ball")){
                    reply = "BALL" + ballMessage;
                }
                
                else{
                    NetworkMessagingProcessor.parseNonUserPlayerMessage(received);
                    
                    //send back position info
                    reply = NetworkMessagingProcessor.createCharacterPositionsMessage();
                }
                buf = new byte[256];
                buf = reply.getBytes();
                InetAddress address = receivePacket.getAddress();
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 1237);
                socket.send(sendPacket);
            }
            catch(IOException e){
            //    System.out.println(e);
            }
            
        }
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
}

    
    
