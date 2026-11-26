import Controller.Almacen;
import model.Producto;
import service.ProductoService;

import java.io.IOException;
import java.util.List;

public class main {

    public static void main(String[] args) throws IOException {
        // ProductoService descarga de internet la lista de productos.
        ProductoService servicio = new ProductoService();
        List<Producto> productos = servicio.obtenerProductos();

        // Almacen contiene las operaciones para guardar y consultar datos en MySQL.
        Almacen almacen = new Almacen();
        // Los productos se guardan antes de los pedidos, porque cada pedido referencia un producto.
        almacen.insertarProductos(productos);

        // Creamos las tablas de empleados y pedidos y añadimos datos de ejemplo con Statement.
        almacen.crearTablasEmpleadosYPedidos();
        almacen.insertarEmpleadosYPedidosConStatement();

        // Marcamos como favoritos los productos de más de 1000 y mostramos cuántos se añadieron.
        int favoritosInsertados = almacen.insertarProductosFavoritos();
        System.out.println("Productos favoritos añadidos: " + favoritosInsertados);

        // Ejecutamos las consultas de la actividad y mostramos sus resultados en la consola.
        System.out.println("\n=== Todos los productos ===");
        almacen.mostrarProductos();

        System.out.println("\n=== Productos con precio inferior a 600 ===");
        almacen.mostrarProductosMenoresDe600();

        System.out.println("\n=== Productos favoritos ===");
        almacen.mostrarProductosFavoritos();
    }
}
