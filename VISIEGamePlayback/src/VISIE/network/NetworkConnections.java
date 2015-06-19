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
    
    public static UDPBroadcastServer createBroadcastServer(int portNumber, Main m){      
            UDPBroadcastServer bs = new UDPBroadcastServer(m, portNumber);
            bs.setDaemon(true);
            bs.start();
            return bs;
    }
    
    public static Server createTCPServer(int portNumber, Main m, UDPBroadcastServer udp){      
            Server TCPServer = new Server(m, portNumber, udp);
            TCPServer.setDaemon(true);
            TCPServer.start();
            return TCPServer;
    }
    
    
    public static Client createClient(int clientType, int portNumber, Main m, String serverAddress){
        Client c;
        
        //TCP Client

        //UDP Client
        
        //Navigation client
        if(clientType == 3){
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
    
}
