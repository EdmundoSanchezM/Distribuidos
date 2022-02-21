package distro_14_memoriacompartidadistribuida;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class TokenRing {

    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static Object obj = new Object();
    static long arreglo_M [];
    static boolean arreglo_B [];
    static boolean tengo_el_token = false;
    static boolean bloquear = false;

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while(longitud > 0){
            int n = f.read(b,posicion,longitud);
            posicion +=n;
            longitud -=n;
        }
    }
    static void barrera(String host, int puerto) {
        try {
            Socket conexion = null;
            for (;;)
                try {
                conexion = new Socket(host, puerto);
                break;
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void envia_token(String host, int puerto, long token) throws InterruptedException, IOException {
        Socket conexion = null;
        for (;;) {
            try {
                conexion = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeLong(token);
        tengo_el_token = false;
        salida.close();
        conexion.close();
    }

    static int calcularSiguienteNodo() {
        if (nodo + 1 == num_nodos) {
            return 0;
        } else {
            return nodo + 1;
        }
    }

    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                long token;
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                token = entrada.readLong();
                System.out.println("Recibi token" + token );
                synchronized (obj) {
                    tengo_el_token = true;
                }
                int sig_nodo = calcularSiguienteNodo();
                envia_token(hosts[sig_nodo], puertos[sig_nodo], token);
            } catch (Exception e) {

            }

        }
    }

    static class Servidor extends Thread {

        ServerSocket servidor;

        Servidor() throws IOException {
            this.servidor = new ServerSocket(puertos[nodo]);
        }

        public void run() {
            while (true) {
                try {
                    Socket conexion = servidor.accept();
                    Worker w = new Worker(conexion);
                    w.start();
                } catch (Exception ex) {

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.exit(0);
        }
        nodo = Integer.valueOf(args[0]);
        num_nodos = args.length - 1;
        hosts = new String[num_nodos];
        puertos = new int[num_nodos];
        arreglo_M = new long [1];
        arreglo_B = new boolean [1];
        for(int i = 0; i<num_nodos;i++){
            String[] dividido = args[i+1].split(":");
            hosts[i] = dividido[0];
            puertos[i] = Integer.valueOf(dividido[1]);
        }
        Servidor s = new Servidor();
        s.start();
        for(int i = 0; i < num_nodos ; i++){
            if(i != nodo)
                barrera(hosts[i],puertos[i]);
        }
        

        System.out.println("Nodo: " + nodo + " Host: " + hosts[nodo] + " IP: " + puertos[nodo]);

        Servidor w = new Servidor();
        w.start();

        if (nodo == 0) {
            envia_token(hosts[1], puertos[1], 1);
            Thread.sleep(3000);
        }
        while (true) {
            synchronized (obj) {
                if (!tengo_el_token) {

                } else {
                    /* Critical Section */
                    bloquear = true;
                    System.out.println("Adquiri el bloqueo");
                    Thread.sleep(3000);
                    bloquear = false;
                    System.out.println("Desbloquié el bloqueo");
                    /* Critical Section */
                    int sig_nodo = calcularSiguienteNodo();
                    envia_token(hosts[sig_nodo], puertos[sig_nodo], 1);
                }

            }
        }
    }

    public static void lock(){
        bloquear = true;
        for(int i = 0 ; i<arreglo_B.length;i++)
            arreglo_B[i] = false;
    }

    
    public static void unlock(){
        for(int i = 0, i < num_nodos ;i++){
            if(i != nodo)
                actualizarArray(hosts[i],puertos[i]);
        }
        bloquear = false;
    }

    public static void read(int n){
        return arreglo_M[n];
    }
    
    public static void write(int n, int valor){
        arreglo_M[n] = valor;
        arreglo_B[n] = true;
    }

    public static void actualizarArray(String host, int puerto){
        Socket conexion = null;
        for (;;) {
            try {
                conexion = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

        ByteBuffer b = ByteBuffer.allocate(arreglo_M.length*64); //5 doubles * tamaño del double, 8 bytes 
        for(int i = 0; i < arreglo_M.length; i++)b.putLong(arreglo_M[i]);
        byte[] a = b.array();
        salida.write(a);
    }

    public static void obtenerCambios(){
        byte[] a = new byte[arreglo_M.length*64];
        read(entrada,a,0,arreglo_M.length*64);
        ByteBuffer b = ByteBuffer.wrap(a);
        LongBuffer longBuffer = b.asLongBuffer();
        long l[] = new long[longBuffer.capacity()];
        longBuffer.get(l);
    }

}
