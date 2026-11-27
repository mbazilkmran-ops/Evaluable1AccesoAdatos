package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    //Son los datos para conectarnos a la base almacen .
    private static final String DB_URL = "jdbc:mysql://localhost:3306/almacen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Al ser static, las llamadas a getConnection comparten esta conexión.
    private static Connection connection;

    public static Connection getConnection() {
        // Abrimos la conexión la primera vez y reutilizamos la misma en las siguientes llamadas.
        if (connection == null) {
            createNewConnection();
        }
        return connection;
    }

    // DriverManager abre la conexión usando la URL, el usuario y la contraseña.
    private static void createNewConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            // Añadimos un mensaje claro para identificar que el error ocurrió al conectar.
            throw new RuntimeException("Error en la conexión con la base de datos", e);
        }
    }
}
