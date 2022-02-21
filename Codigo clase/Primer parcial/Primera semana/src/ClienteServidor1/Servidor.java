/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteServidor1;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 *
 * @author josue
 */
public class Servidor {

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while(longitud > 0){
            int n = f.read(b,posicion,longitud);
            posicion +=n;
            longitud -=n;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(50000);
        Socket conexion = servidor.accept();
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        
        int n = entrada.readInt();
        System.out.println(n);
        
        double x = entrada.readDouble();
        System.out.println(x);
        
        byte [] buffer = new byte[4];
        read(entrada,buffer,0,4);
        System.out.println(new String(buffer, "UTF-8"));
        
        salida.write("HOLA".getBytes());
        
        //writeDouble
        int numDoubles = 10000;
        long iniciowD = System.currentTimeMillis(); 
        for(int i = 0; i < numDoubles; i++) entrada.readDouble();
        long finwD = System.currentTimeMillis();
        System.out.println("Se tardo en recibir " + (finwD-iniciowD) + " milisegundos");
        //Fin writeDouble
        
        //ByteBuffer
        byte[] a = new byte[numDoubles*8];
        long inicio = System.currentTimeMillis(); 
        read(entrada,a,0,numDoubles*8);
        long fin = System.currentTimeMillis();
        System.out.println("Se tardo en recibir " + (fin-inicio) + " milisegundos");
        //Fin ByteBuffer
        
        
        //ByteBuffer b = ByteBuffer.wrap(a);
        
        //for(int i=0;i<numDoubles;i++) System.out.println(b.getDouble());
        
        salida.close();
        entrada.close();
        conexion.close();
    }
    
}
