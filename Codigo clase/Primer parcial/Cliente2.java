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
    public static void main(String[] args) throws Exception{
        System.out.println("Cliente iniciado \n");
        Socket conexion = new Socket("sisdis.sytes.net", 20010);
    
        System.out.println("conexion exitosa\n");
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
         
        salida.writeDouble(86.0);
        salida.writeInt(52);
        salida.writeDouble(35.0);
        salida.writeInt(4);
        double xd = entrada.readDouble();
        System.out.println("Nuevo tiempo es: "+ xd);
        /*
        long T1 = 1632744934;

        long T2 = entrada.readLong();
        long T3 = entrada.readLong();
        System.out.println(T2);
        long T4 = T3 + 3;


        long T_res = ((T4-T1) - (T3-T2))/2; 
        System.out.println("Nuevo tiempo es: "+ (T3+T_res));*/

    }
}
