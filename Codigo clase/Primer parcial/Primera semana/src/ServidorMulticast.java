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
public class ServidorMulticast {

    static void envia_mensaje(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress ip_grupo = InetAddress.getByName(ip);
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, ip_grupo, puerto);
        socket.send(paquete);
        socket.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        envia_mensaje("hola???????".getBytes(), "230.0.0.0", 50000);
        ByteBuffer b = ByteBuffer.allocate(5 * 8);
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        envia_mensaje(b.array(), "230.0.0.0", 50000);
    }

}
