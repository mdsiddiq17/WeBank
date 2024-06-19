package com.bank.dao;

import java.util.List;
import java.util.Map;
import com.bank.enums.*;
import com.bank.exception.BankException;
import com.bank.model.*;

public interface Connector {

	public String getPassword(int userId) throws  BankException;
	
	public Role getRole(int userId) throws BankException ;
	
	public void addEmployee(Employee employee) throws BankException;
	
	public void addBranch(Branch branch) throws BankException;
	
	public void addCustomer(Customer customer) throws BankException;
	
	public void addAccount(Account account) throws BankException  ;
	
	public int getUserId(long accNo) throws BankException;
	
	public List<Long> getAccountNumbers(int userId) throws BankException;
	
	public long getBalance(long accountNumber) throws BankException ;
	
	public void changePassword(int userId,String password) throws BankException;
	
	public Map<Integer,Branch> getAllBranches(int limit,long offset)  throws BankException;
	
	public Customer getCustomerDetails(int userId) throws BankException;
	
	public  Map<Long,Account> getAccountDetails(int userId) throws BankException;
	
	public Map<Integer,Map<Long,Account>> getAllAccounts(int branchId,int limit, long offset2) throws BankException;
	
	public boolean isActive(long accountNumber) throws BankException ;
	
	 //public List<Transaction> getTransactionDetail(TransactionReq requirement) throws BankException;
	
	public void updateTransaction(Transaction transaction) throws BankException;
	
	public void setAccountStatus(long accountNumber,Status status) throws BankException ;
	
	public void getUser(User user, int userId) throws BankException ;
	
	public void setUserStatus(int userId, Status status) throws BankException;
	
	public void verifyAccount(int userId,long accountNumber) throws BankException ;

	public long getOverAllbalance(int userId) throws BankException;

	public Employee getEmployeeDetails(int userId) throws BankException;

	public int getBranchId(int userId) throws BankException; 

	public List<Customer> getAllCustomer(int limit,long offset) throws  BankException;
	
	public List<Employee> getAllEmployee(int limit,long offset) throws BankException;
	
	public long getBranchCount() throws BankException;
	
	public long getCustomerCount(int branchId) throws BankException ;
	
	public long getAllCustomerCount() throws BankException ;
	
	public long getEmployeeCount() throws BankException;
	
	public String getBranchName(int branchId) throws BankException;
	
	public Account getAccount(long accountNumber) throws BankException;
	
	public long getAccountCount(int branchId) throws BankException ;
	
	public Branch getBranch(int branchId) throws BankException ;
	
	public void updateCustomer(Customer customer) throws BankException;
	
	public void updateUser(User user) throws BankException ;
	
	public int getUsersId(long mobile) throws BankException ;
	
}

