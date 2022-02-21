/*
  Sanchez Mendez Edmundo Josue, Nov 2021
*/

package negocio;

import com.google.gson.*;
import java.math.BigDecimal;

public class Articulo
{
  String nombre;
  String descripcion;
  String UPC;
  String precio;
  String cantidad;
  byte[] foto;

  // @FormParam necesita un metodo que convierta una String al objeto de tipo Articulo
  public static Articulo valueOf(String s) throws Exception
  {
    Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
    return (Articulo)j.fromJson(s,Articulo.class);
  }
}
