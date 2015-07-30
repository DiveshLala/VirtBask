/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;

import VISIE.Main;

/**
 *
 * @author DiveshLala
 */
public class NetworkConnections {
       
    public NetworkConnections(){
    
    }
       
    public static Client createClient(int clientType, int portNumber, Main m, String serverAddress){
        Client c;
        
        //TCP Client
        if(clientType == 1){
            c = new Client(m, serverAddress, portNumber); 
        }
               
       
        //Navigation client
        else if(clientType == 3){
            c = new SensorClient(m, serverAddress, portNumber);
        }
        
        //Kinect client
        else if(clientType == 4){
           c = new KinectClient(m, serverAddress, portNumber);
        }
        
        else{
            c = null;
        }
        
        c.setDaemon(true);
        c.start();
        return c;
            
    }
    
    
    public static Client createSensorClient(int portNumber, Main m, String serverAddress){      
            Client client = new SensorClient(m, serverAddress, portNumber);
            client.setDaemon(true);
            client.start();
            return client;
    }
    
    public static UDPBroadcastClient createUDPBroadcastClient(int portNumber, Main m, String serverAddress){
        UDPBroadcastClient udp = new UDPBroadcastClient(m, serverAddress, portNumber);
        udp.setDaemon(true);
        udp.start();
        return udp;
    }
    
    
}
