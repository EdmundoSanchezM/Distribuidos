/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SocketsSeguros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author josue
 */
public class ClienteSSL {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "150900");
        
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = cliente.createSocket("localhost",50000);
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        
        salida.writeDouble(123456789.123456789);
        
        salida.close();
        entrada.close();
        conexion.close();
    }
    
}
