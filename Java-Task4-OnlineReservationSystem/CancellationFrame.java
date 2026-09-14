import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancellationFrame extends JFrame {

    private JTextField pnrField;
    private JTextArea detailsArea;
    private JButton fetchButton;
    private JButton cancelButton;

    public CancellationFrame() {

        setTitle("Online Reservation System - Cancellation");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout());

        JLabel pnrLabel = new JLabel("Enter PNR:");
        pnrField = new JTextField(15);

        fetchButton = new JButton("FETCH");

        topPanel.add(pnrLabel);
        topPanel.add(pnrField);
        topPanel.add(fetchButton);

        // Booking details
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);

        // Cancel button
        cancelButton = new JButton("CONFIRM CANCELLATION");
        cancelButton.setEnabled(false);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        add(panel);

        // Fetch booking
        fetchButton.addActionListener(e -> fetchBooking());

        // Cancel booking
        cancelButton.addActionListener(e -> cancelBooking());

        setVisible(true);
    }

    // Fetch booking details using PNR
    private void fetchBooking() {

        String pnrText = pnrField.getText().trim();

        if (pnrText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR number."
            );

            return;
        }

        int pnr;

        try {

            pnr = Integer.parseInt(pnrText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR must be numeric."
            );

            return;
        }

        String sql =
                "SELECT * FROM reservations WHERE pnr = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, pnr);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                detailsArea.setText(
                        "PNR: " +
                        result.getInt("pnr") +
                        "\n\nUsername: " +
                        result.getString("username") +
                        "\nPassenger Name: " +
                        result.getString("passenger_name") +
                        "\nTrain Number: " +
                        result.getInt("train_number") +
                        "\nTrain Name: " +
                        result.getString("train_name") +
                        "\nClass: " +
                        result.getString("class_type") +
                        "\nJourney Date: " +
                        result.getString("journey_date") +
                        "\nSource: " +
                        result.getString("source") +
                        "\nDestination: " +
                        result.getString("destination")
                );

                cancelButton.setEnabled(true);

            } else {

                detailsArea.setText("");

                cancelButton.setEnabled(false);

                JOptionPane.showMessageDialog(
                        this,
                        "No booking found for this PNR."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage()
            );
        }
    }

    // Cancel booking
    private void cancelBooking() {

        int pnr;

        try {

            pnr = Integer.parseInt(
                    pnrField.getText().trim()
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid PNR number."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM reservations WHERE pnr = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, pnr);

            int rows = statement.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Booking cancelled successfully."
                );

                detailsArea.setText("");
                pnrField.setText("");
                cancelButton.setEnabled(false);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Booking not found."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed: " + e.getMessage()
            );
        }
    }
}