package data;

public class DAOconfig {
    public static final String USERNAME = "restaurante";
    public static final String PASSWORD = "1234";
    private static final String DATABASE = "gestao_restaurante";
    private static final String DRIVER = "jdbc:mysql";
    public static final String URL = DRIVER + "://localhost:3306/" + DATABASE + "?autoReconnect=true&useSSL=false";
}
