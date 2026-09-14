import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReservationFrame extends JFrame {

    private JTextField passengerNameField;
    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JComboBox<String> classBox;
    private JTextField dateField;
    private JTextField sourceField;
    private JTextField destinationField;

    private String username;

    public ReservationFrame(String username) {

        this.username = username;

        setTitle("Online Reservation System - Reservation");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Labels
        JLabel passengerLabel = new JLabel("Passenger Name:");
        JLabel trainNumberLabel = new JLabel("Train Number:");
        JLabel trainNameLabel = new JLabel("Train Name:");
        JLabel classLabel = new JLabel("Class Type:");
        JLabel dateLabel = new JLabel("Date of Journey:");
        JLabel sourceLabel = new JLabel("Source Station:");
        JLabel destinationLabel = new JLabel("Destination Station:");

        // Text fields
        passengerNameField = new JTextField();
        trainNumberField = new JTextField();

        trainNameField = new JTextField();
        trainNameField.setEditable(false);

        classBox = new JComboBox<>(
                new String[]{"AC", "Sleeper", "First Class", "Second Class"}
        );

        dateField = new JTextField();
        sourceField = new JTextField();
        destinationField = new JTextField();

        // Buttons
        JButton bookButton = new JButton("BOOK TICKET");
        JButton cancelButton = new JButton("CANCEL BOOKING");

        // Add components
        panel.add(passengerLabel);
        panel.add(passengerNameField);

        panel.add(trainNumberLabel);
        panel.add(trainNumberField);

        panel.add(trainNameLabel);
        panel.add(trainNameField);

        panel.add(classLabel);
        panel.add(classBox);

        panel.add(dateLabel);
        panel.add(dateField);

        panel.add(sourceLabel);
        panel.add(sourceField);

        panel.add(destinationLabel);
        panel.add(destinationField);

        panel.add(bookButton);
        panel.add(cancelButton);

        add(panel);

        // Find train name when Enter is pressed
        trainNumberField.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusLost(java.awt.event.FocusEvent e)
            {
                findTrain();
            }
        });

        // Book ticket
        bookButton.addActionListener(e -> bookTicket());

        // Open cancellation window
        cancelButton.addActionListener(e -> {
            new CancellationFrame();
        });

        setVisible(true);
    }

    // Find train name from train number
    private void findTrain() {

        String trainNumberText = trainNumberField.getText().trim();

        if (trainNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter train number."
            );
            return;
        }

        try {

            int trainNumber = Integer.parseInt(trainNumberText);

            String sql =
                    "SELECT train_name FROM trains WHERE train_number = ?";

            try (
                    Connection connection =
                            DatabaseConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(1, trainNumber);

                ResultSet result = statement.executeQuery();

                if (result.next()) {

                    trainNameField.setText(
                            result.getString("train_name")
                    );

                } else {

                    trainNameField.setText("");

                    JOptionPane.showMessageDialog(
                            this,
                            "Train number not found."
                    );
                }
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric."
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage()
            );
        }
    }

    // Book ticket
    private void bookTicket() {

        String passengerName =
                passengerNameField.getText().trim();

        String trainNumberText =
                trainNumberField.getText().trim();

        String trainName =
                trainNameField.getText().trim();

        String classType =
                (String) classBox.getSelectedItem();

        String journeyDate =
                dateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        // Check empty fields
        if (passengerName.isEmpty()
                || trainNumberText.isEmpty()
                || trainName.isEmpty()
                || journeyDate.isEmpty()
                || source.isEmpty()
                || destination.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields."
            );

            return;
        }
        if(!journeyDate.matches("\\d{2}-\\d{2}-\\d{4}"))
        {
            JOptionPane.showMessageDialog(
            this,"Invalid date format.Use DD-MM-YYYY.");
            return;
        }

        // Check train number
        int trainNumber;

        try {

            trainNumber =
                    Integer.parseInt(trainNumberText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric."
            );

            return;
        }

        // Generate PNR
        int pnr =
                (int) (System.currentTimeMillis() % 1000000);

        String sql =
                "INSERT INTO reservations " +
                "(pnr, username, passenger_name, train_number, " +
                "train_name, class_type, journey_date, source, destination) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, pnr);
            statement.setString(2, username);
            statement.setString(3, passengerName);
            statement.setInt(4, trainNumber);
            statement.setString(5, trainName);
            statement.setString(6, classType);
            statement.setString(7, journeyDate);
            statement.setString(8, source);
            statement.setString(9, destination);

            statement.executeUpdate();

            // Confirmation
            JOptionPane.showMessageDialog(
                    this,
                    "Booking Successful!\n\n"
                    + "PNR: " + pnr + "\n"
                    + "Passenger: " + passengerName + "\n"
                    + "Train: " + trainName + "\n"
                    + "Class: " + classType + "\n"
                    + "Date: " + journeyDate + "\n"
                    + "From: " + source + "\n"
                    + "To: " + destination,
                    "Booking Confirmation",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking failed: " + e.getMessage()
            );
        }
    }

    // Clear form
    private void clearFields() {

        passengerNameField.setText("");
        trainNumberField.setText("");
        trainNameField.setText("");
        dateField.setText("");
        sourceField.setText("");
        destinationField.setText("");

        classBox.setSelectedIndex(0);
    }
}