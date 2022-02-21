
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ServidorMultithread {

    static Object obj = new Object();
    static String [] hosts; 
    static int [] puertos;
    static int num_nodos;
    static int nodo;
    static int coordinador_actual;

    static void eleccion(int nodo){
        System.out.println("En construccion uwu");
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

    static String envia_mensaje_eleccion(String host, int puerto){
        Socket conexion = null;
        try {
            conexion = new Socket(host, puerto);
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("ELECCION");
            String retorno = entrada.readUTF();
            conexion.close();
            return retorno;
        } catch (Exception e) {
            return "";
        }
    }

    static void envia_mensaje_coordinador(String host, int puerto){
        Socket conexion = null;
        try {
            conexion = new Socket(host, puerto);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("COORDINADOR");
            salida.writeInt(nodo);
            conexion.close();
        } catch (Exception e) {
        }
    }

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                System.out.println("Inicio el thread Worker");
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                String mensaje;
                mensaje = entrada.readUTF();
                if(mensaje.equals("ELECCION")){
                    salida.writeUTF("OK");
                    eleccion(nodo);
                }else if(mensaje.equals("COORDINADOR")){
                    coordinador_actual = entrada.readInt();
                }
            } catch (Exception e) {
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


    public static void main(String[] args) throws Exception {
        if (args.length != 9) {
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
                barrera(hosts[i],puertos[i]);
        }
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(nodo == 7)
            System.exit(0);
        else if(nodo == 4)
            eleccion(4);
    }
}