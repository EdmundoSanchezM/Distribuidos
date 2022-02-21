import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class TokenExclusion {

    static String hosts[];
    static int puertos[];
    static int num_nodos;
    static int nodo;
    static boolean bloquear;
    static boolean tengo_token;

    static void envia_mensaje(long token, String host, int puerto) throws Exception {
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
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        salida.writeLong(token);
        conexion.close();

    }

    static class Worker extends Thread {
        Socket conexion;
        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            long token;
            System.out.println("Inició el thread Worker");
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                token = entrada.readLong();
                tengo_token = true;
                System.out.println("La variable token es:" + token);
                token++;
                if (!bloquear) {
                    envia_mensaje(token, hosts[((20000 + (nodo + 2)) % 3)], puertos[((20000 + (nodo + 2)) % 3)]);
                    tengo_token = false;
                    conexion.close();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static class Servidor extends Thread {
        public void run() {
            try {

                ServerSocket servidor = new ServerSocket(puertos[nodo]);
                for (;;) {
                    Socket conexion = servidor.accept();
                    Worker w = new Worker(conexion);
                    w.start();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Se debe pasar los parámetros correctos");
            System.exit(1);
        }
        nodo = Integer.valueOf(args[0]);
        num_nodos = args.length - 1;

        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.valueOf(args[i + 1].split(":")[1]);
        }
        Servidor s = new Servidor();
        s.start();

        if (nodo == 0) {
            envia_mensaje(1, hosts[nodo + 1], puertos[nodo + 1]);
        }

        while (!tengo_token) {
            bloquear = true;
            Thread.sleep(3000);
        }
        if ((tengo_token == true) && (bloquear == true)) {
            System.out.println("Tengo el bloqueo");
            Thread.sleep(3000);
            bloquear = false;
            System.out.println("Libero el bloqueo");
            envia_mensaje(token, hosts[((20000 + (nodo + 2)) % 3)], puertos[((20000 + (nodo + 2)) % 3)]);
            tengo_token = false;
        }
    }
}
