
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Actividad1 {

    static Object obj = new Object();
    static String [] hosts; 
    static int [] puertos;
    static int num_nodos;
    static int nodo;
    static long reloj_logico;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            System.out.println("Inicio el thread Worker");
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


    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
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
    }
}