package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.enums.AccountType;
import com.bank.enums.CreditDebit;
import com.bank.enums.Gender;
import com.bank.enums.Role;
import com.bank.enums.Status;
import com.bank.enums.TransactionType;
import com.bank.exception.BankException;
import com.bank.model.Account;
import com.bank.model.Branch;
import com.bank.model.Customer;
import com.bank.model.Employee;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.util.DBConnector;

@SuppressWarnings({ })
public class DAO {

	public String getPassword(int userId) throws BankException {
        String sql = "SELECT password FROM user WHERE user_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             
            // Set the userId parameter
            preparedStatement.setInt(1, userId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                throw new BankException("Invalid User");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage());
        }
    }


	public Role getRole(int userId) throws BankException {
        String sql = "SELECT role FROM user WHERE user_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             
            // Set the userId parameter
            preparedStatement.setInt(1, userId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (resultSet.next()) {
                String roleStr = resultSet.getString("role");
                return Role.valueOf(roleStr.toUpperCase());
            } else {
                throw new BankException("Invalid User");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage());
        }
    }

	public void addUser(User user) throws BankException {
        String query = "INSERT INTO user (password, name, email, mobile, address, birthday, status, gender, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setLong(4, user.getMobile());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setLong(6, user.getDob());
            preparedStatement.setString(7, user.getStatus().toString());
            preparedStatement.setString(8, user.getGender().toString());
            preparedStatement.setString(9, user.getRole().toString());

            preparedStatement.executeUpdate();

            // Get the generated user_id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
            } else {
                throw new BankException("Failed to add user, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new BankException("Error adding user", e);
        }
    }

	 public void addCustomer(Customer customer) throws BankException {
	        String query = "INSERT INTO customer (user_id, aadhaar, pan) VALUES (?, ?, ?)";
	        try (Connection connection = DBConnector.getConnection();
	             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setInt(1, customer.getUserId());
	            preparedStatement.setLong(2, customer.getAadhaar());
	            preparedStatement.setString(3, customer.getPan());
	            preparedStatement.executeUpdate();
	        } catch (SQLException e) {
	            throw new BankException("Error adding customer", e);
	        }
	    }
	
	
	
	 public void addCustomerWithAccount(Customer customer, Account account) throws BankException {
	        Connection connection = null;
	        try {
	            connection = DBConnector.getConnection();
	            connection.setAutoCommit(false); // Begin transaction

	            addUser(customer);
	            addCustomer(customer);
	            account.setUserId(customer.getUserId());
	            addAccount(account);

	            connection.commit(); // Commit transaction
	        } catch (SQLException e) {
	            if (connection != null) {
	                try {
	                    connection.rollback(); // Rollback transaction on error
	                } catch (SQLException ex) {
	                    throw new BankException("Failed to rollback transaction", ex);
	                }
	            }
	            throw new BankException("Failed to add customer with account", e);
	        } finally {
	            if (connection != null) {
	                try {
	                    connection.setAutoCommit(true);
	                    connection.close();
	                } catch (SQLException e) {
	                    throw new BankException("Failed to close connection", e);
	                }
	            }
	        }
	    }
	 
	public void updateUser(User user) throws BankException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            String query = "UPDATE user SET name = ?, email = ?, mobile = ?, address = ?, birthday = ?, gender = ?, status = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setLong(3, user.getMobile());
            preparedStatement.setString(4, user.getAddress());
            preparedStatement.setLong(5, user.getDob());
            preparedStatement.setString(6, user.getGender().toString());
            preparedStatement.setString(7, user.getStatus().toString());
            preparedStatement.setInt(8, user.getUserId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateCustomer(Customer customer) throws BankException {
        updateUser(customer);
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            String query = "UPDATE customer SET aadhaar = ?, pan = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, customer.getAadhaar());
            preparedStatement.setString(2, customer.getPan());
            preparedStatement.setInt(3, customer.getUserId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void deleteCustomer(int userId) throws BankException {
        Connection connection = null;
        PreparedStatement userPreparedStatement = null;
        PreparedStatement customerPreparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            
            //Delete from account
            String accountQuery = "DELETE FROM account WHERE user_id = ?";
            userPreparedStatement = connection.prepareStatement(accountQuery);
            userPreparedStatement.setInt(1, userId);
            userPreparedStatement.executeUpdate();
            
            // Delete from customer table
            String customerQuery = "DELETE FROM customer WHERE user_id = ?";
            customerPreparedStatement = connection.prepareStatement(customerQuery);
            customerPreparedStatement.setInt(1, userId);
            customerPreparedStatement.executeUpdate();

            // Delete from user table
            String userQuery = "DELETE FROM user WHERE user_id = ?";
            userPreparedStatement = connection.prepareStatement(userQuery);
            userPreparedStatement.setInt(1, userId);
            userPreparedStatement.executeUpdate();
            
           
            
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (customerPreparedStatement != null) {
                try {
                    customerPreparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (userPreparedStatement != null) {
                try {
                    userPreparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addBranch(Branch branch) throws BankException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO branch (branch_name, ifsc_code, branch_address) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, branch.getBranchName());
            preparedStatement.setString(2, branch.getIfscCode());
            preparedStatement.setString(3, branch.getBranchAddress());

            preparedStatement.executeUpdate();

            // Get the generated branch_id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                branch.setBranchId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addEmployee(Employee employee) throws BankException {
        addUser(employee);

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO employee (user_id, branch_id) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, employee.getUserId());
            preparedStatement.setInt(2, employee.getBranchId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
    public void getUser(User user, int userId) throws BankException {
        String query = "SELECT * FROM user WHERE user_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            // Set the userId parameter
            preparedStatement.setInt(1, userId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (resultSet.next()) {
                user.setUserId(resultSet.getInt("user_id"));
                user.setPassword(resultSet.getString("password"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setMobile(resultSet.getLong("mobile"));
                user.setAddress(resultSet.getString("address"));
                user.setDob(resultSet.getLong("birthday"));
                user.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
                user.setGender(Gender.valueOf(resultSet.getString("gender").toUpperCase()));
                user.setRole(Role.valueOf(resultSet.getString("role").toUpperCase()));
            } else {
                throw new BankException("Invalid User ID");
            }
            
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public List<Object> getUserDetails(User user) {
    	List<Object> customer = new ArrayList<Object>();
    		
    		customer.add(user.getUserId());
    		customer.add(user.getPassword());
    		customer.add(user.getName());
    		customer.add(user.getEmail());
    		customer.add(user.getMobile());
    		customer.add(user.getAddress());
    		customer.add(user.getDob());
    		customer.add(user.getGender().ordinal());
    		customer.add(user.getStatus().ordinal());
    		customer.add(user.getRole().ordinal());
    		return customer; 
    		
    	}
    
    public List<User> getAllUsers() throws BankException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setMobile(rs.getLong("mobile"));
                user.setAddress(rs.getString("address"));
                user.setDob(rs.getLong("birthday"));
                user.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
                user.setGender(Gender.valueOf(rs.getString("gender").toUpperCase()));
                user.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching users", e);
        }
        return users;
    }

    
    public Customer getCustomerDetail(int userId) throws BankException {
        Customer customer = new Customer();
        String customerQuery = "SELECT * FROM customer WHERE user_id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement customerPreparedStatement = connection.prepareStatement(customerQuery)) {

            // Set the userId parameter for customer query
            customerPreparedStatement.setInt(1, userId);
            ResultSet customerResultSet = customerPreparedStatement.executeQuery();

            // Check if customer result was returned
            if (!customerResultSet.next()) {
                throw new BankException("Invalid Customer");
            }

            // Set customer details
            customer.setUserId(customerResultSet.getInt("user_id"));
            customer.setAadhaar(customerResultSet.getLong("aadhaar"));
            customer.setPan(customerResultSet.getString("pan"));
            

            // Get and set user details
            getUser(customer, userId);

            return customer;

        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public Employee getEmployeeDetail(int userId) throws BankException {
        Employee employee = new Employee();
        String query = "SELECT * FROM employee WHERE user_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            // Set the userId parameter
            preparedStatement.setInt(1, userId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (!resultSet.next()) {
                throw new BankException("Invalid Employee");
            }
            
            // Set employee details
            employee.setUserId(resultSet.getInt("user_id"));
            employee.setBranchId(resultSet.getInt("branch_id"));
            
            // Fetch and set user details
            getUser(employee, userId);
            
            return employee;
            
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public int getBranchId(int userId) throws BankException {
        String query = "SELECT branch_id FROM employee WHERE user_id = ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            // Set the userId parameter
            preparedStatement.setInt(1, userId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (resultSet.next()) {
                return resultSet.getInt("branch_id");
            } else {
                throw new BankException("No branch found for the given userId");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
    
    public Account getAccount(int userId) throws BankException {
        String sql = "SELECT * FROM account WHERE user_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                Account account = new Account();
                account.setUserId(resultSet.getInt("user_id"));
                account.setAccountNo(resultSet.getLong("account_no"));
                account.setAccountType(AccountType.valueOf(resultSet.getString("account_type").toUpperCase()));
                account.setBalance(resultSet.getLong("balance"));
                account.setBranchId(resultSet.getInt("branch_id"));
                account.setAccountStatus(Status.valueOf(resultSet.getString("account_status").toUpperCase()));
                return account;
            } else {
                throw new BankException("Account not found");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage());
        }
    }
    
    public List<Account> getAccountsForUser(int userId) throws BankException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account WHERE user_id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Account account = new Account();
                account.setAccountNo(rs.getLong("account_no"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountType(AccountType.valueOf(rs.getString("account_type").toUpperCase()));
                account.setBalance(rs.getLong("balance"));
                account.setBranchId(rs.getInt("branch_id"));
                account.setAccountStatus(Status.valueOf(rs.getString("account_status").toUpperCase()));
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching accounts for user", e);
        }

        return accounts;
    }

    public Branch getBranch(int branchId) throws BankException {
        String query = "SELECT * FROM branch WHERE branch_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            // Set the branchId parameter
            preparedStatement.setInt(1, branchId);
            
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Check if a result was returned
            if (!resultSet.next()) {
                throw new BankException("Invalid Branch");
            }
            
            // Set branch details
            Branch branch = new Branch();
            branch.setBranchId(resultSet.getInt("branch_id"));
            branch.setBranchName(resultSet.getString("branch_name"));
            branch.setIfscCode(resultSet.getString("ifsc_code"));
            branch.setBranchAddress(resultSet.getString("branch_address"));
            
            return branch;
            
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
  
    public List<Customer> getCustomersPageWise(int limit, int offset) throws BankException {
        List<Customer> customerList = new ArrayList<>();
        String query = "SELECT * FROM customer LIMIT ? OFFSET ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setUserId(resultSet.getInt("user_id"));
                customer.setAadhaar(resultSet.getLong("aadhaar"));
                customer.setPan(resultSet.getString("pan"));
                
                // Retrieve User details
                getUser(customer, customer.getUserId());
                customerList.add(customer);
            }

        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }

        return customerList;
    }
    
    public long getCustomerCount() throws BankException {
        String query = "SELECT COUNT(*) FROM customer";

        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new BankException("Failed to count customers.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }   
	
    public List<Customer> getCustomersByBranchPageWise(int branchId, int pageSize, int offset) throws BankException {
        String query = "SELECT c.* FROM customer c JOIN account a ON c.user_id = a.user_id WHERE a.branch_id = ? LIMIT ? OFFSET ?";
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, branchId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setUserId(rs.getInt("user_id"));
                customer.setAadhaar(rs.getLong("aadhaar"));
                customer.setPan(rs.getString("pan"));
                
                // Retrieve User details
                getUser(customer, customer.getUserId());
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching customers by branch", e);
        }
        return customers;
    }
    
    public long getCustomerCountByBranch(int branchId) throws BankException {
        String query = "SELECT COUNT(*) FROM customer c JOIN account a ON c.user_id = a.user_id WHERE a.branch_id = ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, branchId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new BankException("Error counting customers by branch", e);
        }
        return 0;
    }
    public List<Branch> getAllBranchPageWise(int limit, int offset) throws BankException {
        List<Branch> branchList = new ArrayList<>();
        String query = "SELECT * FROM branch LIMIT ? OFFSET ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Branch branch = new Branch();
                branch.setBranchId(resultSet.getInt("branch_id"));
                branch.setBranchName(resultSet.getString("branch_name"));
                branch.setIfscCode(resultSet.getString("ifsc_code"));
                branch.setBranchAddress(resultSet.getString("branch_address"));
                branchList.add(branch);
            }

        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }

        return branchList;
    }
    
    
    public List<Branch> getAllBranches() throws BankException {
        List<Branch> branches = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = DBConnector.getConnection();
            String query = "SELECT branch_id, branch_name FROM branch";
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getInt("branch_id"));
                branch.setBranchName(rs.getString("branch_name"));
                branches.add(branch);
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return branches;
    }

    public long getBranchCount() throws BankException {
        String query = "SELECT COUNT(*) FROM branch";

        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new BankException("Failed to count branches.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
    
    public List<Account> getAccountsPageWise(int pageSize, int offset) throws BankException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account LIMIT ? OFFSET ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Account account = new Account();
                account.setAccountNo(rs.getLong("account_no"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountType(AccountType.valueOf(rs.getString("account_type").toUpperCase()));
                account.setBalance(rs.getLong("balance"));
                account.setBranchId(rs.getInt("branch_id"));
                account.setAccountStatus(Status.valueOf(rs.getString("account_status").toUpperCase()));
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching accounts", e);
        }
        return accounts;
    }
    
    public long getAccountCount() throws BankException {
        String query = "SELECT COUNT(*) FROM account";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new BankException("Error counting accounts");
            }
        } catch (SQLException e) {
            throw new BankException("Error counting accounts", e);
        }
    }
    
    public List<Account> getAccountsByBranchPageWise(int branchId, int limit, int offset) throws BankException {
        List<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM account WHERE branch_id = ? LIMIT ? OFFSET ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, branchId);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Account account = new Account();
                account.setAccountNo(resultSet.getLong("account_no"));
                account.setUserId(resultSet.getInt("user_id"));
                account.setAccountType(AccountType.valueOf(resultSet.getString("account_type").toUpperCase()));
                account.setBalance(resultSet.getLong("balance"));
                account.setBranchId(resultSet.getInt("branch_id"));
                account.setAccountStatus(Status.valueOf(resultSet.getString("account_status").toUpperCase()));
                accountList.add(account);
            }

        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }

        return accountList;
    }

    public List<Account> getAccountsByCustomer(int userId, int limit, int offset) throws BankException {
        List<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM account WHERE user_id = ? LIMIT ? OFFSET ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Account account = new Account();
                account.setAccountNo(resultSet.getLong("account_no"));
                account.setUserId(resultSet.getInt("user_id"));
                account.setAccountType(AccountType.valueOf(resultSet.getString("account_type").toUpperCase()));
                account.setBalance(resultSet.getLong("balance"));
                account.setBranchId(resultSet.getInt("branch_id"));
                account.setAccountStatus(Status.valueOf(resultSet.getString("account_status").toUpperCase()));
                accountList.add(account);
            }

        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }

        return accountList;
    }
    
    public long getAccountCountByBranch(int branchId) throws BankException {
        String query = "SELECT COUNT(*) FROM account WHERE branch_id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, branchId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new BankException("Failed to count accounts for the branch.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
    
    
    public long getAccountCountByCustomer(int userId) throws BankException {
        String query = "SELECT COUNT(*) FROM account WHERE user_id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new BankException("Failed to count accounts for the customer.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public Map<Integer, String> getAllBranchesMap() throws BankException {
        String query = "SELECT branch_id, branch_name FROM branch";
        Map<Integer, String> branches = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                branches.put(rs.getInt("branch_id"), rs.getString("branch_name"));
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching branches", e);
        }
        return branches;
    }

    public Map<Integer, String> getBranchesMapByBranchId(int branchId) throws BankException {
        String query = "SELECT branch_id, branch_name FROM branch WHERE branch_id = ?";
        Map<Integer, String> branches = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, branchId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    branches.put(rs.getInt("branch_id"), rs.getString("branch_name"));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching branch", e);
        }
        return branches;
    }

    public Map<Integer, String> getBranchesMapByUserId(int userId) throws BankException {
        String query = "SELECT b.branch_id, b.branch_name FROM branch b JOIN account a ON b.branch_id = a.branch_id WHERE a.user_id = ?";
        Map<Integer, String> branches = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    branches.put(rs.getInt("branch_id"), rs.getString("branch_name"));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching branches for user", e);
        }
        return branches;
    }

    public Map<Integer, String> getUserMapByRole(String role) throws BankException {
        String query = "SELECT user_id, role FROM user WHERE role = ?";
        Map<Integer, String> users = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, role);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    users.put(rs.getInt("user_id"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching users by role", e);
        }
        return users;
    }

    public Map<Integer, String> getUserMapByBranchAndRole(int branchId, String role) throws BankException {
        String query = "SELECT u.user_id, u.role FROM user u JOIN account a ON u.user_id = a.user_id WHERE a.branch_id = ? AND u.role = ?";
        Map<Integer, String> users = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, branchId);
            preparedStatement.setString(2, role);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    users.put(rs.getInt("user_id"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching users by branch and role", e);
        }
        return users;
    }

    public Map<Integer, String> getUserMapByUserId(int userId) throws BankException {
        String query = "SELECT user_id, name FROM user WHERE user_id = ?";
        Map<Integer, String> users = new HashMap<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    users.put(rs.getInt("user_id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching user by user ID", e);
        }
        return users;
    }


    public void updateBranch(Branch branch) throws BankException {
        String query = "UPDATE branch SET branch_name = ?, ifsc_code = ?, branch_address = ? WHERE branch_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, branch.getBranchName());
            preparedStatement.setString(2, branch.getIfscCode());
            preparedStatement.setString(3, branch.getBranchAddress());
            preparedStatement.setInt(4, branch.getBranchId());
            
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new BankException("No branch found with the given ID.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public void deleteBranch(int branchId) throws BankException {
        String query = "DELETE FROM branch WHERE branch_id = ?";
        System.out.println("dao br del");
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, branchId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new BankException("No branch found with the given ID.");
            }
        } catch (SQLException e) {
            throw new BankException("Error deleting branch: " + e.getMessage(), e);
        }
    }
    
    public void addAccount(Account account) throws BankException {
        String query = "INSERT INTO account (user_id, account_type, balance, branch_id, account_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, account.getUserId());
            preparedStatement.setString(2, account.getAccountType().toString());
            preparedStatement.setLong(3, account.getBalance());
            preparedStatement.setInt(4, account.getBranchId());
            preparedStatement.setString(5, account.getAccountStatus().toString());
            System.out.println("in DAO Account");
            preparedStatement.executeUpdate();

            // Get the generated account number
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                account.setAccountNo(rs.getLong(1));
            } else {
                throw new BankException("Failed to add account, no account number obtained.");
            }
        } catch (SQLException e) {
            throw new BankException("Error adding account", e);
        }
    }
    
    public void editAccount(Account account) throws BankException {
        String query = "UPDATE account SET account_type = ?, balance = ?, branch_id = ?, account_status = ? WHERE account_no = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, account.getAccountType().toString());
            preparedStatement.setLong(2, account.getBalance());
            preparedStatement.setInt(3, account.getBranchId());
            preparedStatement.setString(4, account.getAccountStatus().toString());
            preparedStatement.setLong(5, account.getAccountNo());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }

    public void deleteAccount(long accountNo) throws BankException {
        String query = "DELETE FROM account WHERE account_no = ?";
        System.out.println("In account delete");
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, accountNo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
    
    public List<Employee> getEmployeesPageWise(int limit, long offset) throws BankException {
        List<Employee> employeeList = new ArrayList<>();
        String query = "SELECT * FROM user JOIN employee ON user.user_id = employee.user_id LIMIT ? OFFSET ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setLong(2, offset);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setUserId(resultSet.getInt("user_id"));
                employee.setName(resultSet.getString("name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setMobile(resultSet.getLong("mobile"));
                employee.setAddress(resultSet.getString("address"));
                employee.setGender(Gender.valueOf(resultSet.getString("gender").toUpperCase()));
                employee.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
                employee.setBranchId(resultSet.getInt("branch_id"));
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }

        return employeeList;
    }
    
    public long getEmployeeCount() throws BankException {
        String query = "SELECT COUNT(*) FROM employee";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new BankException("Failed to count employees.");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        }
    }
    
    public void updateEmployee(Employee employee) throws BankException {
        updateUser(employee);

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            String query = "UPDATE employee SET branch_id = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, employee.getBranchId());
            preparedStatement.setInt(2, employee.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } 
    }
    
    public void deleteEmployee(int employeeId) throws BankException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnector.getConnection();
            
            // Delete from employee table
            String query = "DELETE FROM employee WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, employeeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            // Delete from user table
            query = "DELETE FROM user WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, employeeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } finally {
            closeResources(connection, preparedStatement);
        }
    }

    public int getBranchIdByName(String branchName) throws BankException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int branchId = -1;

        try {
            connection = DBConnector.getConnection();
            String query = "SELECT branch_id FROM branch WHERE branch_name = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, branchName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                branchId = resultSet.getInt("branch_id");
            } else {
                throw new BankException("Branch not found");
            }
        } catch (SQLException e) {
            throw new BankException(e.getMessage(), e);
        } 

        return branchId;
    }
    
    public long getAccountBalance(long accountNo) throws BankException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long balance = 0;

        try {
            connection = DBConnector.getConnection();
            String query = "SELECT balance FROM account WHERE account_no = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accountNo);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                balance = resultSet.getLong("balance");
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching account balance", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return balance;
    }
    
    public void executeTransaction(Transaction transaction) throws BankException {
        Connection connection = null;
        PreparedStatement accountStatement = null;
        PreparedStatement transactionStatement = null;

        try {
            connection = DBConnector.getConnection();
            connection.setAutoCommit(false);

            // Update account balance
            String updateAccountQuery = "UPDATE account SET balance = ? WHERE account_no = ?";
            accountStatement = connection.prepareStatement(updateAccountQuery);
            accountStatement.setLong(1, transaction.getClosingBalance());
            accountStatement.setLong(2, transaction.getAccountNo());
            accountStatement.executeUpdate();

            // Insert transaction record
            String insertTransactionQuery = "INSERT INTO transaction (transaction_id, user_id, account_no, payee_acc_no, credit_debit, transaction_type, amount, closing_balance, dateTime, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            transactionStatement = connection.prepareStatement(insertTransactionQuery);
            transactionStatement.setString(1, transaction.getTransactionId());
            transactionStatement.setInt(2, transaction.getUserId());
            transactionStatement.setLong(3, transaction.getAccountNo());
            transactionStatement.setLong(4, transaction.getPayeeAccNo());
            transactionStatement.setString(5, transaction.getCreditDebit().toString());
            transactionStatement.setString(6, transaction.getTransType().toString());
            transactionStatement.setLong(7, transaction.getAmount());
            transactionStatement.setLong(8, transaction.getClosingBalance());
            transactionStatement.setLong(9, transaction.getTransDateTime());
            transactionStatement.setString(10, transaction.getRemarks());
            transactionStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new BankException("Failed to rollback transaction", ex);
                }
            }
            throw new BankException("Failed to process transaction", e);
        } 
    }

    public void interBankTransfer(Transaction transaction) throws BankException {
        Connection connection = null;
        PreparedStatement transactionStatement = null;

        try {
            connection = DBConnector.getConnection();
            connection.setAutoCommit(false);

            // Update sender's account balance
            String updateSenderAccountQuery = "UPDATE account SET balance = ? WHERE account_no = ?";
            try (PreparedStatement senderAccountStatement = connection.prepareStatement(updateSenderAccountQuery)) {
                senderAccountStatement.setLong(1, transaction.getClosingBalance());
                senderAccountStatement.setLong(2, transaction.getAccountNo());
                senderAccountStatement.executeUpdate();
            }

            // Insert transaction record for sender
            String insertTransactionQuery = "INSERT INTO transaction (transaction_id, user_id, account_no, payee_acc_no, credit_debit, transaction_type, amount, closing_balance, dateTime, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            transactionStatement = connection.prepareStatement(insertTransactionQuery);
            transactionStatement.setString(1, transaction.getTransactionId());
            transactionStatement.setInt(2, transaction.getUserId());
            transactionStatement.setLong(3, transaction.getAccountNo());
            transactionStatement.setLong(4, transaction.getPayeeAccNo());
            transactionStatement.setString(5, transaction.getCreditDebit().toString());
            transactionStatement.setString(6, transaction.getTransType().toString());
            transactionStatement.setLong(7, transaction.getAmount());
            transactionStatement.setLong(8, transaction.getClosingBalance());
            transactionStatement.setLong(9, transaction.getTransDateTime());
            transactionStatement.setString(10, transaction.getRemarks());
            transactionStatement.executeUpdate();
            
            //Insert 
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new BankException("Failed to rollback transaction", ex);
                }
            }
            throw new BankException("Failed to process interbank transfer", e);
        }
    }
    
    public void intraBankTransfer(Transaction transaction) throws BankException {
        Connection connection = null;
        PreparedStatement senderAccountStatement = null;
        PreparedStatement receiverAccountStatement = null;
        PreparedStatement transactionStatement = null;

        try {
            connection = DBConnector.getConnection();
            connection.setAutoCommit(false);
            System.out.println("in intra bank transfer");
            // Update sender's account balance
            String updateSenderAccountQuery = "UPDATE account SET balance = ? WHERE account_no = ?";
            senderAccountStatement = connection.prepareStatement(updateSenderAccountQuery);
            senderAccountStatement.setLong(1, transaction.getClosingBalance());
            senderAccountStatement.setLong(2, transaction.getAccountNo());
            senderAccountStatement.executeUpdate();
            
            System.out.println("in intra bank transfer updated sender");
            // Insert transaction record for sender
            String insertTransactionQuery = "INSERT INTO transaction (transaction_id, user_id, account_no, payee_acc_no, credit_debit, transaction_type, amount, closing_balance, dateTime, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            transactionStatement = connection.prepareStatement(insertTransactionQuery);
            transactionStatement.setString(1, transaction.getTransactionId());
            transactionStatement.setInt(2, transaction.getUserId());
            transactionStatement.setLong(3, transaction.getAccountNo());
            transactionStatement.setLong(4, transaction.getPayeeAccNo());
            transactionStatement.setString(5, transaction.getCreditDebit().toString());
            transactionStatement.setString(6, transaction.getTransType().toString());
            transactionStatement.setLong(7, transaction.getAmount());
            transactionStatement.setLong(8, transaction.getClosingBalance());
            transactionStatement.setLong(9, transaction.getTransDateTime());
            transactionStatement.setString(10, transaction.getRemarks());
            transactionStatement.executeUpdate();
            
            System.out.println("in intra bank transfer updated sender transaction");
            // Update receiver's account balance
            long receiverClosingBalance = getAccountBalance(transaction.getPayeeAccNo()) + transaction.getAmount();
            String updateReceiverAccountQuery = "UPDATE account SET balance = ? WHERE account_no = ?";
            receiverAccountStatement = connection.prepareStatement(updateReceiverAccountQuery);
            receiverAccountStatement.setLong(1, receiverClosingBalance);
            receiverAccountStatement.setLong(2, transaction.getPayeeAccNo());
            receiverAccountStatement.executeUpdate();
            System.out.println("in intra bank transfer updated receiver");
            // Insert transaction record for receiver
            transactionStatement = connection.prepareStatement(insertTransactionQuery);
            transactionStatement.setString(1, transaction.getTransactionId());
            transactionStatement.setInt(2, getUserId(transaction.getPayeeAccNo()));
            transactionStatement.setLong(3, transaction.getPayeeAccNo());
            transactionStatement.setLong(4, transaction.getAccountNo());
            transactionStatement.setString(5, CreditDebit.CREDIT.toString());
            transactionStatement.setString(6, transaction.getTransType().toString());
            transactionStatement.setLong(7, transaction.getAmount());
            transactionStatement.setLong(8, receiverClosingBalance);
            transactionStatement.setLong(9, transaction.getTransDateTime());
            transactionStatement.setString(10, transaction.getRemarks());
            transactionStatement.executeUpdate();
            System.out.println("in intra bank transfer updated  transaction");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new BankException("Failed to rollback transaction", ex);
                }
            }
            throw new BankException("Failed to process intra-bank transfer", e);
        } finally {
            // Close all resources
            try {
                if (senderAccountStatement != null) senderAccountStatement.close();
                if (receiverAccountStatement != null) receiverAccountStatement.close();
                if (transactionStatement != null) transactionStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public int getTotalTransactionCount(long startTime) {
        String query = "SELECT COUNT(*) FROM transaction WHERE dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTransactionCountByBranch(int branchId, long startTime) {
        String query = "SELECT COUNT(*) FROM transaction t JOIN account a ON t.account_no = a.account_no WHERE a.branch_id = ? AND t.dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, branchId);
            ps.setLong(2, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTransactionCountByUserId(int userId, long startTime) {
        String query = "SELECT COUNT(*) FROM transaction WHERE user_id = ? AND dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setLong(2, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTransactionCountByAccount(long accountNo, long startTime) {
        String query = "SELECT COUNT(*) FROM transaction WHERE account_no = ? AND dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, accountNo);
            ps.setLong(2, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTransactionCountByBranchAndAccountNo(int branchId, long accountNo, long startTime) {
        String query = "SELECT COUNT(*) FROM transaction t JOIN account a ON t.account_no = a.account_no WHERE a.branch_id = ? AND t.account_no = ? AND t.dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, branchId);
            ps.setLong(2, accountNo);
            ps.setLong(3, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTransactionCountByUserIdAndAccountNo(int userId, long accountNo, long startTime) {
        String query = "SELECT COUNT(*) FROM transaction WHERE user_id = ? AND account_no = ? AND dateTime >= ?";
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setLong(2, accountNo);
            ps.setLong(3, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Transaction> getAllTransactionsPageWise(long startTime, int pageSize, int offset) {
        String query = "SELECT * FROM transaction WHERE dateTime >= ? ORDER BY dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, startTime);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByBranchPageWise(int branchId, long startTime, int pageSize, int offset) {
        String query = "SELECT t.* FROM transaction t JOIN account a ON t.account_no = a.account_no WHERE a.branch_id = ? AND t.dateTime >= ? ORDER BY t.dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, branchId);
            ps.setLong(2, startTime);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByUserIdPageWise(int userId, long startTime, int pageSize, int offset) {
        String query = "SELECT * FROM transaction WHERE user_id = ? AND dateTime >= ? ORDER BY dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setLong(2, startTime);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByAccountPageWise(long accountNo, long startTime, int pageSize, int offset) {
        String query = "SELECT * FROM transaction WHERE account_no = ? AND dateTime >= ? ORDER BY dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, accountNo);
            ps.setLong(2, startTime);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByBranchAndAccountNoPageWise(int branchId, long accountNo, long startTime, int pageSize, int offset) {
        String query = "SELECT t.* FROM transaction t JOIN account a ON t.account_no = a.account_no WHERE a.branch_id = ? AND t.account_no = ? AND t.dateTime >= ? ORDER BY t.dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, branchId);
            ps.setLong(2, accountNo);
            ps.setLong(3, startTime);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByUserIdAndAccountNoPageWise(int userId, long accountNo, long startTime, int pageSize, int offset) {
        String query = "SELECT * FROM transaction WHERE user_id = ? AND account_no = ? AND dateTime >= ? ORDER BY dateTime DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setLong(2, accountNo);
            ps.setLong(3, startTime);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getString("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setAccountNo(rs.getLong("account_no"));
        transaction.setPayeeAccNo(rs.getLong("payee_acc_no"));
        transaction.setTransType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()));
        transaction.setAmount(rs.getLong("amount"));
        transaction.setClosingBalance(rs.getLong("closing_balance"));
        transaction.setTransDateTime(rs.getLong("dateTime"));
        transaction.setCreditDebit(CreditDebit.valueOf(rs.getString("credit_debit").toUpperCase()));
        transaction.setRemarks(rs.getString("remarks"));
        return transaction;
    }
    
    public Map<Long, String> getAccountMapByRole(Role role, int userId) throws BankException {
        String query = "";
        Map<Long, String> accountMap = new HashMap<>();
        
        if (role == Role.ADMIN) {
            query = "SELECT account_no, name FROM account a JOIN user u ON a.user_id = u.user_id";
        } else if (role == Role.EMPLOYEE) {
            query = "SELECT account_no, name FROM account a JOIN user u ON a.user_id = u.user_id WHERE a.branch_id = ?";
        } else if (role == Role.CUSTOMER) {
            query = "SELECT account_no, name FROM account a JOIN user u ON a.user_id = u.user_id WHERE a.user_id = ?";
        }

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            if (role == Role.EMPLOYEE) {
                ps.setInt(1, getBranchId(userId));
            } else if (role == Role.CUSTOMER) {
                ps.setInt(1, userId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accountMap.put(rs.getLong("account_no"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountMap;
    }

    private void closeResources(Connection connection, PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAccountActive(long accountNumber) throws BankException {
        String query = "SELECT account_status FROM account WHERE account_no = ?";
        boolean isActive = false;

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameter for the account number
            preparedStatement.setLong(1, accountNumber);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String status = resultSet.getString("account_status");
                    // Check if the account status is active
                    isActive = "ACTIVE".equalsIgnoreCase(status);
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error checking account status", e);
        }

        return isActive;
    }
    
    public int getUserId(long accountNumber) throws BankException {
        String query = "SELECT user_id FROM account WHERE account_no = ?";
        int userId = 0;
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameter for the account number
            preparedStatement.setLong(1, accountNumber);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            throw new BankException("Error fetching user ID", e);
        }

        return userId;
    }
}
