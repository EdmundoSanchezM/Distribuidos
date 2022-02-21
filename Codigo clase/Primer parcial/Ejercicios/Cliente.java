import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author josue
 */
public class Cliente {

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while(longitud > 0){
            int n = f.read(b,posicion,longitud);
            posicion +=n;
            longitud -=n;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Socket conexion = new Socket("localhost", 50000);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        salida.writeInt(123);
        salida.writeDouble(1234567890.1234567890);
        salida.write("UwU ".getBytes());
        
        byte[] buffer = new byte[4];
        read(entrada,buffer,0,4);
        System.out.println(new String(buffer, "UTF-8"));
        
        ByteBuffer b = ByteBuffer.allocate(5*8); //5 doubles * tamaño del double, 8 bytes 
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        byte[] a = b.array();
        salida.write(a);
        
        salida.close();
        entrada.close();
        conexion.close();
    }
    
}
