public class TestConnection {

    public static void main(String[] args) {

        if (DatabaseConnection.getConnection() != null) {
            System.out.println("Connected to Don Macchiatos database!");
        } else {
            System.out.println("Connection failed.");
        }
    }
}
