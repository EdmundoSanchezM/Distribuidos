
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

class Actividad1 {

    static Object lock = new Object();
    static String [] hosts; 
    static int [] puertos;
    static int num_nodos;
    static int nodo;
    static long reloj_logico;
    static LinkedList<Integer> cola = new LinkedList<Integer>();

    static void envia_mensaje(long tiempo_logico, String host, int puerto) {
        try {
            Socket conexion = null;
            for (;;)
                try {
                conexion = new Socket(host, puerto);
                break;
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeLong(tiempo_logico);
            salida.close();
            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void envia_peticion(long tiempo_logico, String host, int puerto){


    }

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            System.out.println("Inicio el thread Worker");
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                long tiempo_recibido;
                tiempo_recibido = entrada.readLong();
                synchronized (lock) {
                    if(tiempo_recibido>reloj_logico){
                        reloj_logico = tiempo_recibido;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Servidor extends Thread {
        public void run() {
            try {
                ServerSocket servidor = new ServerSocket(puertos[nodo]);
                while(true){
                    Socket conexion = servidor.accept();
                    new Worker(conexion).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Reloj extends Thread {
        public void run() {
            try {
                while(true){
                    synchronized (lock) {
                        System.out.println("Valor de reloj_logico" + reloj_logico);
                        if (nodo == 0) {
                            reloj_logico += 4;
                        } else if (nodo == 1) {
                            reloj_logico += 5;
                        } else if (nodo == 2) {
                            reloj_logico += 6;
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("No papu :c");
            System.exit(0);
        }
        nodo = Integer.valueOf(args[0]);
        num_nodos = args.length - 1;
        hosts = new String[num_nodos];
        puertos = new int[num_nodos];
        for(int i = 0; i<num_nodos;i++){
            String[] dividido = args[i+1].split(":");
            hosts[i] = dividido[0];
            puertos[i] = Integer.valueOf(dividido[1]);
        }
        Servidor s = new Servidor();
        s.start();
        for(int i = 0; i < num_nodos ; i++){
            if(i != nodo)
                envia_mensaje(-1,hosts[i],puertos[i]);
        }
        Reloj r = new Reloj();
        r.start();
    }
}