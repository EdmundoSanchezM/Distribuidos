import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;

public class ServidorRMI{
    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/prueba";
        ClaseRMI obj = new ClaseRMI(); 
        //registra la instancia en el rmiregistry
        Naming.rebind(url,obj);
    }
}