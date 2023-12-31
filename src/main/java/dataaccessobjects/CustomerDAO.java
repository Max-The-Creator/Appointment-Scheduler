package dataaccessobjects;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data access object (DAO) for handling customer-related database operations.
 */
public class CustomerDAO {

    /**
     * Retrieves an observable list of all customers from the database.
     *
     * @return ObservableList containing all customers
     * @throws SQLException if database query fails
     */
    public static ObservableList<Customer> getAllCustomersObservable() throws SQLException {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        String query = "SELECT * FROM customers";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("Customer_ID");
            String name = resultSet.getString("Customer_Name");
            String address = resultSet.getString("Address");
            String postalCode = resultSet.getString("Postal_Code");
            String phoneNumber = resultSet.getString("Phone");
            int divisionId = resultSet.getInt("Division_ID");

            Customer customer = new Customer(id, name, address, postalCode, phoneNumber, divisionId);
            customerList.add(customer);
        }

        return customerList;
    }

    /**
     * Deletes a customer from the database based on the provided customer ID.
     *
     * @param id ID of the customer to be deleted
     * @throws SQLException if database operation fails
     */
    public static void deleteCustomer(int id) throws SQLException {
        String query = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = JDBC.openConnection().prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Updates the provided customer's details in the database.
     *
     * @param customer Customer object containing the updated details
     * @throws SQLException if database operation fails
     */
    public void updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?,  Division_ID = ? WHERE Customer_ID = ?";

        PreparedStatement preparedStatement = JDBC.openConnection().prepareStatement(query);

        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getAddress());
        preparedStatement.setString(3, customer.getPostalCode());
        preparedStatement.setString(4, customer.getPhoneNumber());

        preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(6, "user");

        preparedStatement.setInt(7, customer.getDivisionId());
        preparedStatement.setInt(8, customer.getId());

        preparedStatement.executeUpdate();
    }

    /**
     * Adds a new customer to the database.
     *
     * @param customer Customer object containing the details of the customer to be added
     * @throws SQLException if database operation fails
     */
    public void addCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = JDBC.openConnection().prepareStatement(query);

        preparedStatement.setInt(1, customer.getId());
        preparedStatement.setString(2, customer.getName());
        preparedStatement.setString(3, customer.getAddress());
        preparedStatement.setString(4, customer.getPostalCode());
        preparedStatement.setString(5, customer.getPhoneNumber());

        preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(7, "user");
        preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(9, "user");

        preparedStatement.setInt(10, customer.getDivisionId());

        preparedStatement.executeUpdate();
    }

    /**
     * Retrieves the next available customer ID to be used when adding a new customer to the database.
     *
     * @return the next available customer ID
     * @throws SQLException if database operation fails or no maximum ID is found
     */
    public static int getNextCustomerId() throws SQLException {
        String query = "SELECT MAX(Customer_ID) AS max_id FROM customers";
        PreparedStatement preparedStatement = JDBC.openConnection().prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("max_id") + 1;
        } else {
            throw new SQLException("Unable to fetch maximum Appointment ID from database");
        }
    }
}
