package Controller;

import database.DBConnection;
import model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Almacen {

    // Guarda un producto en MySQL usando PreparedStatement para enviar sus valores.
    public void insertarProducto(Producto producto) {
        Connection connection = DBConnection.getConnection();
        // Los ? son huecos que se completan con los datos del objeto.
        // Si el id ya existe, actualizamos la fila en vez de duplicarla.
        String query = """
                INSERT INTO productos (id, nombre, descripcion, cantidad, precio)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    nombre = VALUES(nombre),
                    descripcion = VALUES(descripcion),
                    cantidad = VALUES(cantidad),
                    precio = VALUES(precio)
                """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // Cada número indica a cuál ? se asigna el valor.
            preparedStatement.setInt(1, producto.getId());
            preparedStatement.setString(2, producto.getTitle());
            preparedStatement.setString(3, producto.getDescription());
            preparedStatement.setInt(4, producto.getStock());
            preparedStatement.setDouble(5, producto.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo guardar el producto " + producto.getId(), e);
        }
    }

    // Recorre la lista y reutiliza insertarProducto para guardar cada elemento.
    public void insertarProductos(List<Producto> productos) {
        for (Producto producto : productos) {
            insertarProducto(producto);
        }
    }

    // Crea las tablas que necesita esta parte si todavía no existen.
    public void crearTablasEmpleadosYPedidos() {
        Connection connection = DBConnection.getConnection();
        String crearEmpleados = """
                CREATE TABLE IF NOT EXISTS empleados (
                    id INT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL
                ) ENGINE=InnoDB
                """;
        String crearPedidos = """
                CREATE TABLE IF NOT EXISTS pedidos (
                    id INT PRIMARY KEY,
                    id_producto INT NOT NULL,
                    cantidad INT NOT NULL,
                    CONSTRAINT pedidos_producto_fk
                        FOREIGN KEY (id_producto) REFERENCES productos(id)
                ) ENGINE=InnoDB
                """;

        try {
            // Estas consultas no llevan valores variables, por eso usamos Statement.
            Statement statement = connection.createStatement();
            statement.executeUpdate(crearEmpleados);
            statement.executeUpdate(crearPedidos);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron crear empleados y pedidos", e);
        }
    }

    // Inserta datos de ejemplo en empleados y pedidos mediante Statement, como pide la actividad.
    public void insertarEmpleadosYPedidosConStatement() {
        Connection connection = DBConnection.getConnection();
        // INSERT IGNORE no vuelve a insertar una fila si su id ya existe.
        String empleados = """
                INSERT IGNORE INTO empleados (id, nombre) VALUES
                    (1, 'Ana Garcia'),
                    (2, 'Luis Martin')
                """;
        String pedidos = """
                INSERT IGNORE INTO pedidos (id, id_producto, cantidad) VALUES
                    (1, 1, 2),
                    (2, 2, 1)
                """;

        try {
            // Los valores ya están escritos en el SQL; no hay signos ? que completar.
            Statement statement = connection.createStatement();
            statement.executeUpdate(empleados);
            statement.executeUpdate(pedidos);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron insertar empleados y pedidos", e);
        }
    }

    // Añade a productos_fav los productos cuyo precio es superior a 1000.
    public int insertarProductosFavoritos() {
        Connection connection = DBConnection.getConnection();
        // NOT EXISTS evita añadir de nuevo un producto que ya está marcado como favorito.
        String query = """
                INSERT INTO productos_fav (id_productos)
                SELECT p.id
                FROM productos p
                WHERE p.precio > 1000
                  AND NOT EXISTS (
                      SELECT 1
                      FROM productos_fav pf
                      WHERE pf.id_productos = p.id
                  )
                """;

        try {
            Statement statement = connection.createStatement();
            // executeUpdate devuelve el número de filas insertadas.
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron insertar los productos favoritos", e);
        }
    }

    // Consulta todas las filas de productos y manda cada una a imprimir.
    public void mostrarProductos() {
        String query = "SELECT * FROM productos";
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            // next avanza a una fila; el bucle termina cuando ya no quedan más.
            while (resultSet.next()) {
                imprimirProducto(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los productos", e);
        }
    }

    // WHERE filtra el resultado para dejar solo productos con precio menor que 600.
    public void mostrarProductosMenoresDe600() {
        String query = "SELECT * FROM productos WHERE precio < 600";
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                imprimirProducto(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los productos menores de 600", e);
        }
    }

    // Une productos con productos_fav para obtener los datos completos de cada favorito.
    public void mostrarProductosFavoritos() {
        String query = """
                SELECT p.*
                FROM productos p
                INNER JOIN productos_fav pf ON p.id = pf.id_productos
                """;
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                imprimirProducto(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los productos favoritos", e);
        }
    }

    // Lee las columnas de la fila actual del ResultSet y las muestra en una línea.
    private void imprimirProducto(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nombre = resultSet.getString("nombre");
        String descripcion = resultSet.getString("descripcion");
        int cantidad = resultSet.getInt("cantidad");
        double precio = resultSet.getDouble("precio");
        System.out.printf("%d | %s | %s | cantidad: %d | precio: %.2f%n",
                id, nombre, descripcion, cantidad, precio);
    }
}
