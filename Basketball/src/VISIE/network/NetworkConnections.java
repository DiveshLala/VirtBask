/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VISIE.network;

import VISIE.Games.Game;
import VISIE.Main;

/**
 *
 * @author DiveshLala
 */
public class NetworkConnections {
       
    public NetworkConnections(){
    
    }
    
    public static UDPBroadcastServer createBroadcastServer(int portNumber, Game g){      
            UDPBroadcastServer bs = new UDPBroadcastServer(g, portNumber);
            bs.setDaemon(true);
            bs.start();
            return bs;
    }
    
    public static Server createTCPServer(int portNumber, Game g, UDPBroadcastServer udp){      
            Server TCPServer = new Server(g, portNumber, udp);
            TCPServer.setDaemon(true);
            TCPServer.start();
            return TCPServer;
    }
    
    
    public static Client createClient(int clientType, int portNumber, Game g, String serverAddress){
        Client c;
        
        //TCP Client

        //UDP Client
        
        //Navigation client
        if(clientType == 3){
            c = new SensorClient(g, serverAddress, portNumber);
        }
        
        //Kinect client
        else if(clientType == 4){
           c = new KinectClient(g, serverAddress, portNumber);
        }
        
        else{
            c = null;
        }
        
        c.setDaemon(true);
        c.start();
        return c;
            
    }
    
    
    public static Client createSensorClient(int portNumber, Game g, String serverAddress){      
            Client client = new SensorClient(g, serverAddress, portNumber);
            client.setDaemon(true);
            client.start();
            return client;
    }
    
}
