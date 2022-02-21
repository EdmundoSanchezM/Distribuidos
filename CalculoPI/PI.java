
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class PI {

    static Object obj = new Object();
    static float pi = 0;

    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            // Algoritmo 1
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                float suma;
                suma = entrada.readFloat();
                synchronized (obj) {
                    pi = suma + pi;
                }
                entrada.close();
                conexion.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Uso:");
            System.err.println("java PI <nodo>");
            System.exit(0);
        }
        int nodo = Integer.valueOf(args[0]);
        if (nodo == 0) {
            // Algoritmo 2
            ServerSocket servidor;
            servidor = new ServerSocket(10000);
            Worker v[] = new Worker[4];
            int i = 0;
            while (i < 4) {
                Socket conexion;
                conexion = servidor.accept();
                v[i] = new Worker(conexion);
                v[i].start();
                i++;
            }
            i = 0;
            while (i < 4) {
                v[i].join();
                i++;
            }
            System.out.println("El valor calculado de PI es: " + pi);

        } else {
            // Algoritmo 3
            Socket conexion = null;
            for (;;)
                try {
                conexion = new Socket("localhost", 10000);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
            float suma = 0;
            int i = 0;
            while (i < 1000000) {
                suma = (float) 4.0 / (8 * i + 2 * (nodo - 2) + 3) + suma;
                i++;
            }
            suma = (nodo % 2 == 0) ? -suma : suma;
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeFloat(suma);

            salida.close();
            conexion.close();

        }
    }
}