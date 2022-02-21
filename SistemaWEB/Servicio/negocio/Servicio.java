/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero, Octubre 2021
 */
package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import com.google.gson.*;
import java.math.BigDecimal;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio
@Path("ws")
public class Servicio {

    static DataSource pool = null;

    static {
        try {
            Context ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Gson j = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
            .create();

    @POST
    @Path("alta_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alta(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();

        if (articulo.nombre == null || articulo.nombre.equals("")) {
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();
        }

        if (articulo.descripcion == null || articulo.descripcion.equals("")) {
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripcion"))).build();
        }

        if (articulo.UPC == null || articulo.UPC.equals("")) {
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el UPC"))).build();
        }

        if (articulo.precio == null || articulo.precio.equals("")) {
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el precio"))).build();
        }

        if (articulo.cantidad == null || articulo.cantidad.equals("")) {
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la cantidad en almacen"))).build();
        }

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_articulo FROM articulos WHERE UPC=?");
            try {
                stmt_1.setString(1, articulo.UPC);

                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        return Response.status(400).entity(j.toJson(new Error("El UPC ya existe"))).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
            conexion.setAutoCommit(false);
            PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO articulos(id_articulo,nombre,descripcion,UPC,precio,cantidad_almacen) VALUES (0,?,?,?,?,?)");
            try {
                stmt_2.setString(1, articulo.nombre);
                stmt_2.setString(2, articulo.descripcion);
                stmt_2.setString(3, articulo.UPC);
                stmt_2.setBigDecimal(4, new BigDecimal(articulo.precio));
                stmt_2.setInt(5, Integer.parseInt(articulo.cantidad));
                stmt_2.executeUpdate();
            } finally {
                stmt_2.close();
            }

            if (articulo.foto != null) {
                PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_articulos VALUES (0,?,(SELECT id_articulo FROM articulos WHERE UPC=?))");
                try {
                    stmt_3.setBytes(1, articulo.foto);
                    stmt_3.setString(2, articulo.UPC);
                    stmt_3.executeUpdate();
                } finally {
                    stmt_3.close();
                }
            }
            conexion.commit();
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.ok().build();
    }

    @POST
    @Path("consulta_todo_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulta_todo() throws Exception {
        Connection conexion = pool.getConnection();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.nombre, a.descripcion, a.precio, b.foto, a.UPC FROM articulos a LEFT OUTER JOIN fotos_articulos b ON a.id_articulo=b.id_articulo");
            try {

                ResultSet rs = stmt_1.executeQuery();
                try {
                    ArrayList<Articulo> articulos = new ArrayList<Articulo>();
                    while (rs.next()) {
                        Articulo r = new Articulo();
                        r.nombre = rs.getString(1);
                        r.descripcion = rs.getString(2);
                        r.precio = rs.getString(3);
                        r.foto = rs.getBytes(4);
                        r.UPC = rs.getString(5);
                        articulos.add(r);
                    }
                    return Response.ok().entity(j.toJson(articulos)).build();
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("busqueda_articulos")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response busqueda_articulos(@FormParam("busqueda") String palabra) throws Exception {
        Connection conexion = pool.getConnection();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.nombre, a.descripcion, a.precio, b.foto, a.UPC FROM articulos a LEFT OUTER JOIN fotos_articulos b ON a.id_articulo=b.id_articulo WHERE descripcion LIKE ?");
            try {
                stmt_1.setString(1, "%" + palabra + "%");
                ResultSet rs = stmt_1.executeQuery();
                try {
                    ArrayList<Articulo> articulos = new ArrayList<Articulo>();
                    while (rs.next()) {
                        Articulo r = new Articulo();
                        r.nombre = rs.getString(1);
                        r.descripcion = rs.getString(2);
                        r.precio = rs.getString(3);
                        r.foto = rs.getBytes(4);
                        r.UPC = rs.getString(5);
                        articulos.add(r);
                    }
                    return Response.ok().entity(j.toJson(articulos)).build();

                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("comprar_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response comprar_articulo(@FormParam("UPC") String UPC, @FormParam("cantidad") String cantidad) throws Exception {
        Connection conexion = pool.getConnection();
        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_articulo,cantidad_almacen FROM articulos WHERE UPC =?");
            String cantidad_actual;
            String id_articulo;
            BigDecimal cantidad_compra = new BigDecimal(cantidad);
            try {
                stmt_1.setString(1, UPC);
                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        id_articulo = rs.getString(1);
                        cantidad_actual = rs.getString(2);
                        if (cantidad_compra.compareTo(new BigDecimal(cantidad_actual)) == 1) {
                            return Response.status(400).entity(j.toJson(new Error("Cantidad disponible del articulo: " + cantidad_actual))).build();
                        }
                    } else {
                        return Response.status(400).entity(j.toJson(new Error("El UPC no existe"))).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
            conexion.setAutoCommit(false);
            PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO carrito_compra(id_compra,cantidad,id_articulo) VALUES (0,?,?)");
            try {
                stmt_2.setString(1, cantidad);
                stmt_2.setString(2, id_articulo);
                stmt_2.executeUpdate();
            } finally {
                stmt_2.close();
            }
            PreparedStatement stmt_3 = conexion.prepareStatement("UPDATE articulos SET cantidad_almacen = ? WHERE UPC=?");
            try {
                stmt_3.setString(1, new BigDecimal(cantidad_actual).subtract(cantidad_compra).toString());
                stmt_3.setString(2, UPC);
                stmt_3.executeUpdate();
            } finally {
                stmt_3.close();
            }
            conexion.commit();
        } catch (Exception e) {
            conexion.rollback();
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.ok().build();
    }
   
    @POST
    @Path("consulta_todo_carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulta_todo_carrito() throws Exception {
        Connection conexion = pool.getConnection();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("SELECT b.nombre, b.descripcion, b.precio, c.foto, a.cantidad, a.id_articulo FROM carrito_compra a LEFT OUTER JOIN articulos b ON a.id_articulo=b.id_articulo LEFT OUTER JOIN fotos_articulos c ON a.id_articulo=c.id_articulo");
            try {

                ResultSet rs = stmt_1.executeQuery();
                try {
                    ArrayList<Articulo> articulos = new ArrayList<Articulo>();
                    ArrayList<String> id_articulos = new ArrayList<String>(); 
                    while (rs.next()) {
                        Articulo r = new Articulo();
                        r.nombre = rs.getString(1);
                        r.descripcion = rs.getString(2);
                        r.precio = rs.getString(3);
                        r.foto = rs.getBytes(4);
                        r.cantidad = rs.getString(5);
                        if(id_articulos.contains(rs.getString(6))){
                          int index = id_articulos.indexOf(rs.getString(6));
                          Articulo nuevo_r = articulos.get(index);
                          r.cantidad = Integer.toString(Integer.parseInt(nuevo_r.cantidad) + Integer.parseInt(r.cantidad));
                          articulos.set(index,r);
                        }else{
                          id_articulos.add(rs.getString(6));
                          articulos.add(r);
                        }
                    }
                    return Response.ok().entity(j.toJson(articulos)).build();
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }
}
