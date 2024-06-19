package com.bank.logic;

import java.util.List;
import java.util.Map;

import com.bank.dao.DAO;
import com.bank.enums.CreditDebit;
import com.bank.enums.Role;
import com.bank.enums.TransactionType;
import com.bank.exception.BankException;
import com.bank.model.Account;
import com.bank.model.Branch;
import com.bank.model.Customer;
import com.bank.model.Employee;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.util.SHA256;


public class WeBankLogic {
	private DAO dao = new DAO();
	
	public Role getUser(int userId) throws BankException {
		return dao.getRole(userId);
	}
	
	public void checkPassword(int userId, String password) throws BankException {

			String originalPassword = dao.getPassword(userId);
			String enteredPassword = SHA256.getHash(password);
			System.out.println("original :"+originalPassword);
			System.out.println("entered  :"+enteredPassword);
			boolean isCorrect = originalPassword.equals(enteredPassword);
			if(!isCorrect) {
				throw new BankException("Wrong Password!!");
			}
	}
	
	public List<User> getAllUsers() throws BankException {
        return dao.getAllUsers();
    }
	
	 public List<Branch> getBranchPageWise(int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getAllBranchPageWise(pageSize, offset);
	    }
	 
	 
	 
	 
	 public long getAccountCount() throws BankException {
	        return dao.getAccountCount();
	    }
	 
	 public List<Branch> getAllBranches() throws BankException {
			// TODO Auto-generated method stub
			return dao.getAllBranches();
		}
	 
	 public long getBranchCount() throws BankException {
		   return dao.getBranchCount();
	   }  
	 
	public  Customer getCustomerDetail(int userId) throws BankException {
		return dao.getCustomerDetail(userId);
	}
	
	public Employee getEmployeeDetails(int userId) throws BankException {
		return dao.getEmployeeDetail(userId);
	}
	
	public User getAdminDetail(int userId) throws BankException {
	
		User user = new User();
		System.out.println("logic");	
		dao.getUser(user, userId);
		System.out.println(user);
		return user;
	}
	
	public int getBranchId(int userId) throws BankException {
		 return dao.getBranchId(userId);
	}
	
	public Branch getBranch(int branchId) throws BankException{
		return dao.getBranch(branchId);
	}
	
	public Account getAccount(int userId) throws BankException{
		// TODO Auto-generated method stub
		return dao.getAccount(userId);
	}
	
	public List<Account> getAccountsForUser(int userId) throws BankException {
		System.out.println("In get accounts for user");
        return dao.getAccountsForUser(userId);
    }
	
	public List<Account> getAccountsPageWise(int page, int pageSize) throws BankException {
        int offset = (page - 1) * pageSize;
        return dao.getAccountsPageWise(pageSize, offset);
    }
	
	 public List<Account> getAccountsByBranchPageWise(int branchId, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getAccountsByBranchPageWise(branchId, pageSize, offset);
	    }

	    public List<Account> getAccountsByCustomer(int userId, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getAccountsByCustomer(userId, pageSize, offset);
	    }
	    
	    public long getAccountCountByBranch(int branchId) throws BankException {
	        return dao.getAccountCountByBranch(branchId);
	    }

	    public long getAccountCountByCustomer(int userId) throws BankException {
	        return dao.getAccountCountByCustomer(userId);
	    }
	
	public List<Customer> getCustomersPageWise(int page, int pageSize) throws BankException {
	    int offset = (page - 1) * pageSize;
	    return dao.getCustomersPageWise(pageSize, offset);
	}

	public List<Customer> getCustomersByBranchPageWise(int branchId, int page, int pageSize) throws BankException {
	    int offset = (page - 1) * pageSize;
	    return dao.getCustomersByBranchPageWise(branchId, pageSize, offset);
	}

	public long getCustomerCount() throws BankException {
	    return dao.getCustomerCount();
	}

	public long getCustomerCountByBranch(int branchId) throws BankException {
	    return dao.getCustomerCountByBranch(branchId);
	}

	
	
	 public List<Employee> getEmployeesPageWise(int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getEmployeesPageWise(pageSize, offset);
	    }

	    public long getEmployeeCount() throws BankException {
	        return dao.getEmployeeCount();
	    }
	    
	    public void addBranch(Branch branch) throws BankException {
	        dao.addBranch(branch);
	    }
	    
	    public void updateBranch(Branch branch) throws BankException {
	        dao.updateBranch(branch);
	    }

	    public void deleteBranch(int branchId) throws BankException {
	        dao.deleteBranch(branchId);
	    }
	    
	    
	    
	    public void addCustomer(Customer customer) throws BankException{
	    	dao.addCustomer(customer);
	    }
	    
	    public void addCustomerWithAccount(Customer customer, Account account) throws BankException {
	    	try {
	            dao.addCustomerWithAccount(customer, account);
	        } catch (Exception e) {
	            throw new BankException("Error adding customer with account", e);
	        }
	    }

	    
	    public void updateCustomer(Customer customer) throws BankException {
	        dao.updateCustomer(customer);
	    }
	    
	    public void deleteCustomer(int userId) throws BankException {
	        dao.deleteCustomer(userId);
	    }
	    
	    public void addEmployee(Employee employee) throws BankException {
	        dao.addEmployee(employee);
	    }
	    
	    public void updateEmployee(Employee employee) throws BankException {
	        dao.updateEmployee(employee);
	    }
	    
	    public void deleteEmployee(int employeeId) throws BankException {
	        dao.deleteEmployee(employeeId);
	    }
	    
	    public void addAccount(Account account) throws BankException {
	        dao.addAccount(account);
	    }

	    public void editAccount(Account account) throws BankException {
	        dao.editAccount(account);
	    }

	    public void deleteAccount(long accountNo) throws BankException {
	        dao.deleteAccount(accountNo);
	    }
	    
	    public int getBranchIdByName(String branchName) throws BankException {
	        return dao.getBranchIdByName(branchName);
	    }	
	    

	    public void processTransaction(Transaction transaction) throws BankException {
	        long closingBalance;
	        String transactionId = generateTransactionId(transaction.getAccountNo(), transaction.getPayeeAccNo(), transaction.getTransType());
	        long accountBalance=dao.getAccountBalance(transaction.getAccountNo());
	        long amount=transaction.getAmount();
	       
	        if(accountBalance>=amount || amount<0) {
		        transaction.setTransactionId(transactionId);
	
		        switch (transaction.getTransType()) {
		            case DEPOSIT:
		                transaction.setCreditDebit(CreditDebit.CREDIT);
		                closingBalance = dao.getAccountBalance(transaction.getAccountNo()) + transaction.getAmount();
		                transaction.setClosingBalance(closingBalance);
		                dao.executeTransaction(transaction);
		                break;
		            case WITHDRAW:
		                transaction.setCreditDebit(CreditDebit.DEBIT);
		                closingBalance = dao.getAccountBalance(transaction.getAccountNo()) - transaction.getAmount();
		                transaction.setClosingBalance(closingBalance);
		                dao.executeTransaction(transaction);
		                break;
		            case INTERBANK:
		                transaction.setCreditDebit(CreditDebit.DEBIT);
		                closingBalance = dao.getAccountBalance(transaction.getAccountNo()) - transaction.getAmount();
		                transaction.setClosingBalance(closingBalance);
		                dao.interBankTransfer(transaction);
		                break;
		            case INTRABANK:
		                transaction.setCreditDebit(CreditDebit.DEBIT);
		                closingBalance = dao.getAccountBalance(transaction.getAccountNo()) - transaction.getAmount();
		                transaction.setClosingBalance(closingBalance);
		                dao.intraBankTransfer(transaction);
		                break;
		            default:
		                throw new BankException("Invalid transaction type");
		        }
	        }else {
	        	throw new BankException("Not Enough Balance");
	        }
	    }

	    private String generateTransactionId(long senderAccountNo, long receiverAccountNo, TransactionType transType) {
	        if (transType == TransactionType.DEPOSIT || transType == TransactionType.WITHDRAW) {
	            String senderStr = Long.toString(senderAccountNo);
	            long currentTimeMillis = System.currentTimeMillis();
	            return "FFT" + senderStr.substring(senderStr.length()-6) + currentTimeMillis;
	        } else {
	            String senderStr = Long.toString(senderAccountNo);
	            String receiverStr = Long.toString(receiverAccountNo);
	            long currentTimeMillis = System.currentTimeMillis();
	            return "FFT" + senderStr.substring(senderStr.length()-3) + receiverStr.substring(senderStr.length()-3) + currentTimeMillis;
	        }
	    }
	    
	    public int getTotalTransactionCount(long startTime) throws BankException {
	        return dao.getTotalTransactionCount(startTime);
	    }

	    public int getTransactionCountByBranch(int branchId, long startTime) throws BankException {
	        return dao.getTransactionCountByBranch(branchId, startTime);
	    }

	    public int getTransactionCountByUserId(int userId, long startTime) throws BankException {
	        return dao.getTransactionCountByUserId(userId, startTime);
	    }

	    public int getTransactionCountByAccount(long accountNo, long startTime) throws BankException {
	        return dao.getTransactionCountByAccount(accountNo, startTime);
	    }

	    public int getTransactionCountByBranchAndAccountNo(int branchId, long accountNo, long startTime) throws BankException {
	        return dao.getTransactionCountByBranchAndAccountNo(branchId, accountNo, startTime);
	    }

	    public int getTransactionCountByUserIdAndAccountNo(int userId, long accountNo, long startTime) throws BankException {
	        return dao.getTransactionCountByUserIdAndAccountNo(userId, accountNo, startTime);
	    }

	    public List<Transaction> getAllTransactionsPageWise(long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getAllTransactionsPageWise(startTime, pageSize, offset);
	    }

	    public List<Transaction> getTransactionsByBranchPageWise(int branchId, long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getTransactionsByBranchPageWise(branchId, startTime, pageSize, offset);
	    }

	    public List<Transaction> getTransactionsByUserIdPageWise(int userId, long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getTransactionsByUserIdPageWise(userId, startTime, pageSize, offset);
	    }

	    public List<Transaction> getTransactionsByAccountPageWise(long accountNo, long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getTransactionsByAccountPageWise(accountNo, startTime, pageSize, offset);
	    }

	    public List<Transaction> getTransactionsByBranchAndAccountNoPageWise(int branchId, long accountNo, long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getTransactionsByBranchAndAccountNoPageWise(branchId, accountNo, startTime, pageSize, offset);
	    }

	    public List<Transaction> getTransactionsByUserIdAndAccountNoPageWise(int userId, long accountNo, long startTime, int page, int pageSize) throws BankException {
	        int offset = (page - 1) * pageSize;
	        return dao.getTransactionsByUserIdAndAccountNoPageWise(userId, accountNo, startTime, pageSize, offset);
	    }

	    public Map<Long, String> getAccountMapByRole(Role role, int userId) throws BankException {
	        return dao.getAccountMapByRole(role, userId);
	    }

		public boolean isAccountActive(long accountNumber) throws BankException {
			return dao.isAccountActive(accountNumber);
			
		}



  
}
