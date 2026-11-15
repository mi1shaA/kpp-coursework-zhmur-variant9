package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/energy_audit_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Запиши свій пароль від MySQL, якщо він встановлений

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Не знайдено MySQL JDBC драйвер: " + e.getMessage());
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}