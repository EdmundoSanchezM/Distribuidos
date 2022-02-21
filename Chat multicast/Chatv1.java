
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.DatagramSocket;
import java.lang.*;
import java.util.Scanner;
import java.math.BigInteger;

class Chat {

    static final int BUFSIZ = 50; //1024;

    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {

        public void run() {
            // En un ciclo infinito se recibirán los mensajes enviados al
            // grupo 230.0.0.0 a través del puerto 10000 y se desplegarán en la pantalla.
            try {
                while (true) {
                    InetAddress grupo = InetAddress.getByName("230.0.0.0");
                    MulticastSocket socket = new MulticastSocket(10000);
                    socket.joinGroup(grupo);
                    byte[] mensaje = recibe_mensaje_multicast(socket, BUFSIZ);
                    //BigInteger bigInteger = new BigInteger(1, mensaje);
                    //String mensajeHex = String.format("%x", bigInteger);
                    //System.out.println("String en Hex: " + mensajeHex);
                    System.out.println(new String(mensaje, "UTF-8"));
                    socket.leaveGroup(grupo);
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Se debe pasar como parametro el nombre de usuario");
            System.exit(1);
        }
        new Worker().start();
        String nombre = args[0];
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el mensaje al
        // grupo 230.0.0.0 a través del puerto 10000.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Ingrese el mensaje a enviar: ");
            String mensaje = scanner.nextLine();
            String salida = nombre + ":" + mensaje;
            //BigInteger bigInteger = new BigInteger(1, mensaje.getBytes("ISO-8859-1"));
            //String mensajeHex = String.format("%x", bigInteger);
            //System.out.println("String en Hex: " + mensajeHex);
            envia_mensaje_multicast(salida.getBytes("UTF-8"), "230.0.0.0", 10000);
        }
    }
}
