
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
class Token {

    static DataInputStream entrada;
    static DataOutputStream salida;
    static boolean inicio = true;
    static String ip;
    static int nodo;
    static long token;
    static class Worker extends Thread {

        public void run() {
            //Algoritmo 1
            try {
                SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                ServerSocket servidor;
                servidor =  socket_factory.createServerSocket(20000 + nodo); //new ServerSocket(20000 + nodo);
                Socket conexion;
                conexion = servidor.accept();
                entrada = new DataInputStream(conexion.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
            System.exit(1);
        }
        
        nodo = Integer.valueOf(args[0]);
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "150000");
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "150900");
        ip = args[1];
        //Algoritmo 2
        Worker w;
        w = new Worker();
        w.start();
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = null;
        while (true) {
            try {
                conexion = cliente.createSocket(ip, 20000 + (nodo + 1) % 4);//new Socket(ip, 20000 + (nodo + 1) % 4);
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        salida = new DataOutputStream(conexion.getOutputStream());
        w.join();
        while (true) {
            if (nodo == 0) {
                if (inicio == true) {
                    inicio = false;
                    token = 1;
                } else {
                    token = entrada.readLong();
                    token++;
                    System.out.println("nodo = "+ nodo +" token = " +token);
                }
            } else {
                token = entrada.readLong();
                token++;
                System.out.println("nodo = "+ nodo +" token = " +token);
            }
            if (nodo == 0 && token >= 1000) {
                break;
            }
            salida.writeLong(token);
        }
        salida.close();
        entrada.close();
        conexion.close();
    }
}
