/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 *
 * @author josue
 */
public class ClienteMulticast {

    static byte[] recibe_mensaje( MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        InetAddress ip_grupo = InetAddress.getByName("230.0.0");
        MulticastSocket socket = new MulticastSocket(50000);
        socket.joinGroup(ip_grupo);
        
        byte[] a = recibe_mensaje(socket, 4);
        System.out.println(new String(a,"UTF-8"));
        
        byte[] buffer = recibe_mensaje(socket, 5*8);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        for(int i = 0; i<5;i++) System.out.println(b.getDouble());
        
        socket.leaveGroup(ip_grupo);
        socket.close();
                
             
    }
    
}
