/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author josue
 */
public class ServidorHilo {

    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
            while (longitud > 0) {
                int n = f.read(b, posicion, longitud);
                posicion += n;
                longitud -= n;
            }
        }
        
        static void escribe_archivo(String archivo, byte[] buffer) throws Exception{
            FileOutputStream f = new FileOutputStream(archivo);
            try{
                f.write(buffer);
            }finally{
                f.close();
            }
        }
      
        public void run() {
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                String nombre = entrada.readUTF();
                long tam = entrada.readLong();
                byte[] buffer = new byte[(int)tam];
                read(entrada, buffer, 0, (int)tam);
                System.out.println("\nSe recibe el archivo " + nombre + " con " + tam + "bytes");
                escribe_archivo(nombre, buffer);
                salida.close();
                entrada.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
         System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "150900");
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket servidor = socket_factory.createServerSocket(50000);
        for (;;) {
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }
    }

}
