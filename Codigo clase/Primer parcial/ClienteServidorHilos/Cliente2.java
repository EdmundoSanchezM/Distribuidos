/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author josue
 */
public class Cliente2 {

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    /**
     * @param args the command line arguments
     */
     static byte[] lee_archivo(String archivo) throws Exception {
         FileInputStream f = new FileInputStream(archivo);
         byte[] buffer;
         try{
             buffer = new byte[f.available()];
             f.read(buffer);
         }finally{
             f.close();
         }
         return buffer;
     }
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        if(args.length != 1){
            System.exit(1);
        }
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "150900");
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = null;
        for (;;)
            try {
            conexion = cliente.createSocket("localhost",50000);
            break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        File file = new File(args[0]);
        salida.writeUTF(args[0]);
        salida.writeLong(file.length());//mejor long que int
        salida.write(lee_archivo(args[0]));
        salida.close();
        entrada.close();
        conexion.close();
    }

}
