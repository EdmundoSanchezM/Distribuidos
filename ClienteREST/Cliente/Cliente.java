import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.Scanner;

public class Cliente {
    static class Usuario {
        int id_usuario;
        String email;
        String nombre;
        String apellido_paterno;
        String apellido_materno;
        String fecha_nacimiento;
        String telefono;
        String genero;
        byte[] foto;
    }
    
    static String ip_puerto = "20.121.196.40:8080";
    
    public static void main(String[] args) throws Exception {
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\nMENU\n");
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borra usuario");
            System.out.println("d. Salir");
            System.out.print("Opcion: ");
            char opc = br.readLine().charAt(0);
            switch (opc) {
                case 'a':
                    System.out.println("Alta usuario");
                    Usuario usuario = new Usuario();

                    System.out.print("Email: ");
                    usuario.email = br.readLine();

                    System.out.print("Nombre: ");
                    usuario.nombre = br.readLine();

                    System.out.print("Apellido Paterno: ");
                    usuario.apellido_paterno = br.readLine();

                    System.out.print("Apellido Materno: ");
                    usuario.apellido_materno = br.readLine();
                    
                    System.out.print("Fecha de nacimiento: ");
                    usuario.fecha_nacimiento = br.readLine();

                    System.out.print("Telefono: ");
                    usuario.telefono = br.readLine();

                    System.out.print("Genero (M/F): ");
                    usuario.genero = br.readLine();
                    alta_usuario(usuario);
                    break;
                case 'b':
                    System.out.println("Consulta usuario");
                    System.out.println("Ingresa el ID de usuario:");
                    consulta_usuario(Integer.parseInt(br.readLine()));
                    break;
                case 'c':
                    System.out.println("Borrar usuario");
                    System.out.println("Ingresa el ID de usuario:");
                    borrar_usuario(Integer.parseInt(br.readLine()));
                    break;
                case 'd':
                    br.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        }
    }

    public static void alta_usuario(Usuario usuario) throws IOException {
        URL url = new URL("http://"+ip_puerto+"/Servicio/rest/ws/alta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        Gson gson = builder.create();
        String body = gson.toJson(usuario);
        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta = br.readLine();
            if(respuesta != null)
                System.out.println("Usuario dado de alta con el ID:  " + respuesta);
        } else { 
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta = br.readLine();
            if(respuesta != null)
                System.out.println(respuesta);
            System.out.println("Error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void consulta_usuario(int id_usuario) throws IOException {
        URL url = new URL("http://"+ip_puerto+"/Servicio/rest/ws/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            BufferedReader brinput = new BufferedReader(new InputStreamReader(System.in));
            String respuesta = br.readLine();
            Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            if(respuesta != null){
                Usuario usuario_consulta = (Usuario) j.fromJson(respuesta, Usuario.class);
                System.out.println("Email   : " + usuario_consulta.email);
                System.out.println("Nombre: " + usuario_consulta.nombre);
                System.out.println("Apellido Paterno: " + usuario_consulta.apellido_paterno);
                System.out.println("Apellido Materno: " + usuario_consulta.apellido_materno);
                System.out.println("Telefono: " + usuario_consulta.telefono);
                System.out.println("Fecha: " + usuario_consulta.fecha_nacimiento);
                System.out.println("Genero: " + usuario_consulta.genero);
                System.out.println("Desea modificar los datos del usuario (s/n)?");
                char res = brinput.readLine().charAt(0);
                if(res=='s'){
                    Usuario usuario = new Usuario();
                    usuario.id_usuario=usuario_consulta.id_usuario;
                    System.out.println("Email:");
                    usuario.email = brinput.readLine();
                    System.out.println("Nombre:");
                    usuario.nombre = brinput.readLine();
                    System.out.println("Apellido Paterno:");
                    usuario.apellido_paterno = brinput.readLine();
                    System.out.println("Apellido Materno:");
                    usuario.apellido_materno = brinput.readLine();
                    System.out.println("Fecha de nacimiento:");
                    usuario.fecha_nacimiento = brinput.readLine();
                    System.out.println("Telefono:");
                    usuario.telefono = brinput.readLine();
                    System.out.println("Genero (M/F):");
                    usuario.genero = brinput.readLine();
                    if (usuario.email.isEmpty()){
                        usuario.email=usuario_consulta.email;
                    }
                    if (usuario.nombre.isEmpty()){
                        usuario.nombre=usuario_consulta.nombre;
                    }
                    if (usuario.apellido_paterno.isEmpty()){
                        usuario.apellido_paterno=usuario_consulta.apellido_paterno;
                    }
                    if (usuario.apellido_materno.isEmpty()){
                        usuario.apellido_materno=usuario_consulta.apellido_materno;
                    }
                    if (usuario.fecha_nacimiento.isEmpty()){
                        usuario.fecha_nacimiento=usuario_consulta.fecha_nacimiento;
                    }
                    if (usuario.telefono.isEmpty()){
                        usuario.telefono=usuario_consulta.telefono;
                    }
                    if (usuario.genero.isEmpty()){
                        usuario.genero=usuario_consulta.genero;
                    }
                    actualiza_usuario(usuario);
                }
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta = br.readLine();
            if(respuesta != null)
                System.out.println(respuesta);
            System.out.println("Codigo de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void actualiza_usuario(Usuario usuario) throws IOException {
        URL url = new URL("http://"+ip_puerto+"/Servicio/rest/ws/modifica_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        String body = gson.toJson(usuario);
        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        if (conexion.getResponseCode() == 200) { 
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta=br.readLine();
            System.out.println("El usuario ha sido modificado");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            respuesta = br.readLine();
            System.out.println("Error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }

    public static void borrar_usuario(int id_usuario) throws IOException {
        URL url = new URL("http://"+ip_puerto+"/Servicio/rest/ws/borra_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        if (conexion.getResponseCode() == 200) {
            System.out.println("El usuario ha sido borrado");
        } else { 
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta= br.readLine();
            if(respuesta != null)
                System.out.println(respuesta);
            System.out.println("Codigo de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }
}