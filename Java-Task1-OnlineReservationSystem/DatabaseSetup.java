import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Users table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL)"
            );

            // Trains table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trains (" +
                "train_number INTEGER PRIMARY KEY, " +
                "train_name TEXT NOT NULL)"
            );

            // Reservations table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr INTEGER PRIMARY KEY, " +
                "username TEXT NOT NULL, " +
                "passenger_name TEXT NOT NULL, " +
                "train_number INTEGER NOT NULL, " +
                "train_name TEXT NOT NULL, " +
                "class_type TEXT NOT NULL, " +
                "journey_date TEXT NOT NULL, " +
                "source TEXT NOT NULL, " +
                "destination TEXT NOT NULL)"
            );

            // Sample users
            statement.executeUpdate(
                "INSERT OR IGNORE INTO users VALUES ('admin', '1234')"
            );

            statement.executeUpdate(
                "INSERT OR IGNORE INTO users VALUES ('user1', '1111')"
            );

            // Sample trains
            statement.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12674, 'Cheran Express')"
            );

            statement.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12675, 'Kovai Express')"
            );

            statement.executeUpdate(
                "INSERT OR IGNORE INTO trains VALUES (12637, 'Pandian Express')"
            );

            System.out.println("Database tables created successfully!");
            System.out.println("Sample data inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}