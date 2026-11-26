package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Producto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ProductoService {

    // Esta clase pide los productos a la web y convierte la respuesta en objetos Producto.
    public List<Producto> obtenerProductos() throws IOException {
        // limit=0 solicita el catálogo completo de DummyJSON.
        URI uri = URI.create("https://dummyjson.com/products?limit=0");
        StringBuilder respuesta = getRespuesta(uri);

        // La respuesta tiene un objeto JSON con una propiedad llamada "products".
        JsonObject json = JsonParser.parseString(respuesta.toString()).getAsJsonObject();
        // Gson convierte cada elemento del array JSON en un objeto de nuestra clase Producto.
        Producto[] productos = new Gson().fromJson(
                json.getAsJsonArray("products"),
                Producto[].class
        );
        // El método devuelve una lista, así que presentamos el array como una List.
        return Arrays.asList(productos);
    }

    // Abre la conexión HTTPS, lee la respuesta completa y la devuelve como texto.
    private StringBuilder getRespuesta(URI uri) throws IOException {
        HttpURLConnection conexion = (HttpURLConnection) uri.toURL().openConnection();
        // GET significa que pedimos información al servidor.
        conexion.setRequestMethod("GET");


        StringBuilder respuesta = new StringBuilder();
        // El lector se cierra automáticamente al salir del try.
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(conexion.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            // La respuesta puede venir en varias líneas; las juntamos en una cadena.
            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }
        } finally {
            // Cerramos la conexión HTTP aunque ocurra un error al leer la respuesta.
            conexion.disconnect();
        }
        return respuesta;
    }
}
