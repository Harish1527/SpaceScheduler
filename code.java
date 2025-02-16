import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class BookPortal extends JFrame {

    private Connection connection;
    private JComboBox<String> locationComboBox;
    private JComboBox<String> houseComboBox;
    private JButton selectHouseButton;

    private String selectedLocation;
    private String selectedHouse;

    public BookPortal() {
        JLabel locationLabel = new JLabel("Location:");
        JLabel houseLabel = new JLabel("House:");
        locationComboBox = new JComboBox<>();
        houseComboBox = new JComboBox<>();
        selectHouseButton = new JButton("Select House");

        setLayout(null);
        locationLabel.setBounds(20, 20, 120, 20);
        locationComboBox.setBounds(140, 20, 200, 20);
        houseLabel.setBounds(20, 50, 120, 20);
        houseComboBox.setBounds(140, 50, 200, 20);
        selectHouseButton.setBounds(140, 80, 200, 30);

        add(locationLabel);
        add(locationComboBox);
        add(houseLabel);
        add(houseComboBox);
        add(selectHouseButton);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "root", "harish");
            PreparedStatement getLocationStmt = connection.prepareStatement("SELECT DISTINCT location FROM houses");
            ResultSet locationResultSet = getLocationStmt.executeQuery();
            while (locationResultSet.next()) {
                locationComboBox.addItem(locationResultSet.getString("location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to database.");
        }

        locationComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedLocation = (String) locationComboBox.getSelectedItem();
                populateHouseComboBox(selectedLocation);
            }
        });

        selectHouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedHouse = (String) houseComboBox.getSelectedItem();
                if (selectedHouse != null) {
                    JOptionPane.showMessageDialog(null, "House selected: " + selectedHouse);
                    showBookingDetails();
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a house.");
                }
            }
        });

        setSize(400, 200);
        setTitle("Book Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void populateHouseComboBox(String location) {
        houseComboBox.removeAllItems();
        try {
            PreparedStatement getHousesStmt = connection.prepareStatement("SELECT housename FROM houses WHERE location = ?");
            getHousesStmt.setString(1, location);
            ResultSet houseResultSet = getHousesStmt.executeQuery();
            while (houseResultSet.next()) {
                houseComboBox.addItem(houseResultSet.getString("housename"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch houses.");
        }
    }

    private void showBookingDetails() {
        JLabel checkInDateLabel = new JLabel("Check-in Date (YYYY-MM-DD):");
        JTextField checkInDateField = new JTextField();
        JLabel checkOutDateLabel = new JLabel("Check-out Date (YYYY-MM-DD):");
        JTextField checkOutDateField = new JTextField();
        JLabel numGuestsLabel = new JLabel("Number of Guests:");
        JTextField numGuestsField = new JTextField();
        JLabel customerIDLabel = new JLabel("Customer ID:");
        JTextField customerIDField = new JTextField();
        JLabel fnameLabel = new JLabel("First Name:");
        JTextField fnameField = new JTextField();
        JLabel lnameLabel = new JLabel("Last Name:");
        JTextField lnameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JButton bookButton = new JButton("Book House");

        checkInDateLabel.setBounds(20, 120, 200, 20);
        checkInDateField.setBounds(220, 120, 120, 20);
        checkOutDateLabel.setBounds(20, 150, 200, 20);
        checkOutDateField.setBounds(220, 150, 120, 20);
        numGuestsLabel.setBounds(20, 180, 120, 20);
        numGuestsField.setBounds(220, 180, 120, 20);
        customerIDLabel.setBounds(20, 210, 120, 20);
        customerIDField.setBounds(220, 210, 120, 20);
        fnameLabel.setBounds(20, 240, 120, 20);
        fnameField.setBounds(220, 240, 120, 20);
        lnameLabel.setBounds(20, 270, 120, 20);
        lnameField.setBounds(220, 270, 120, 20);
        emailLabel.setBounds(20, 300, 120, 20);
        emailField.setBounds(220, 300, 120, 20);
        phoneLabel.setBounds(20, 330, 120, 20);
        phoneField.setBounds(220, 330, 120, 20);
        bookButton.setBounds(140, 370, 200, 30);

        add(checkInDateLabel);
        add(checkInDateField);
        add(checkOutDateLabel);
        add(checkOutDateField);
        add(numGuestsLabel);
        add(numGuestsField);
        add(customerIDLabel);
        add(customerIDField);
        add(fnameLabel);
        add(fnameField);
        add(lnameLabel);
        add(lnameField);
        add(emailLabel);
        add(emailField);
        add(phoneLabel);
        add(phoneField);
        add(bookButton);

        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String checkInDate = checkInDateField.getText();
                String checkOutDate = checkOutDateField.getText();
                String numGuestsText = numGuestsField.getText();
                String customerID = customerIDField.getText();
                String fname = fnameField.getText();
                String lname = lnameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();

                if (checkInDate.isEmpty() || checkOutDate.isEmpty() || numGuestsText.isEmpty() || 
                        customerID.isEmpty() || fname.isEmpty() || lname.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }
                
                int numGuests;
                try {
                    numGuests = Integer.parseInt(numGuestsText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input for the number of guests.");
                    return;
                }
                
                double totalPrice = calculateTotalPrice(selectedHouse, checkInDate, checkOutDate);
                
                if (totalPrice == -1) {
                    JOptionPane.showMessageDialog(null, "Failed to calculate total price.");
                    return;
                }

                bookHouse(selectedHouse, customerID, checkInDate, checkOutDate, numGuests, totalPrice, fname, lname, email, phone);
            }
        });

        setSize(400, 450);
    }

    private double calculateTotalPrice(String selectedHouse, String checkInDate, String checkOutDate) {
        double totalPrice = 0.0;
        try {
            PreparedStatement getPriceStmt = connection.prepareStatement("SELECT pricepernight FROM houses WHERE housename = ?");
            getPriceStmt.setString(1, selectedHouse);
            ResultSet priceResultSet = getPriceStmt.executeQuery();
            if (priceResultSet.next()) {
                double pricePerNight = priceResultSet.getDouble("pricepernight");
                long diffInMillies = java.sql.Date.valueOf(checkOutDate).getTime() - java.sql.Date.valueOf(checkInDate).getTime();
                int numNights = (int) (diffInMillies / (1000 * 60 * 60 * 24));
                totalPrice = numNights * pricePerNight;
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
        return totalPrice;
    }

    private void bookHouse(String selectedHouse, String customerID, String checkInDate, String checkOutDate,
            int numGuests, double totalPrice, String fname, String lname, String email, String phone) {
        try {
            PreparedStatement insertCustomerStmt = connection.prepareStatement(
                    "INSERT INTO customers (customerid, fname, lname, email, phone) VALUES (?, ?, ?, ?, ?)");
            insertCustomerStmt.setString(1, customerID);
            insertCustomerStmt.setString(2, fname);
            insertCustomerStmt.setString(3, lname);
            insertCustomerStmt.setString(4, email);
            insertCustomerStmt.setString(5, phone);
            insertCustomerStmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to insert customer.");
            return;
        }

        try {
            PreparedStatement insertBookingStmt = connection.prepareStatement(
                    "INSERT INTO bookings (houseid, customerid, checkindate, checkoutdate, numguests, totalprice) VALUES (?, ?, ?, ?, ?, ?)");
            insertBookingStmt.setInt(1, 1);
            insertBookingStmt.setString(2, customerID);
            insertBookingStmt.setString(3, checkInDate);
            insertBookingStmt.setString(4, checkOutDate);
            insertBookingStmt.setInt(5, numGuests);
            insertBookingStmt.setDouble(6, totalPrice);
            insertBookingStmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Booking completed successfully! Total Price: $" + totalPrice);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to book the house.");
        }
    }

    public static void main(String[] args) {
        new BookPortal();
    }
}
