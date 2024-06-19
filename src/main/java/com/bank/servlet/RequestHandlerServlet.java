package com.bank.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.enums.AccountType;
import com.bank.enums.Gender;
import com.bank.enums.Role;
import com.bank.enums.Status;
import com.bank.enums.TransactionPeriod;
import com.bank.enums.TransactionType;
import com.bank.exception.BankException;
import com.bank.logic.WeBankLogic;
import com.bank.model.Account;
import com.bank.model.Branch;
import com.bank.model.Customer;
import com.bank.model.Employee;
import com.bank.model.Transaction;
import com.bank.model.User;

public class RequestHandlerServlet {
	ExecutorService service = Executors.newFixedThreadPool(5);	
	int limit = 15;
	WeBankLogic logic=new WeBankLogic();
	public void checkPassword(HttpServletRequest request,HttpServletResponse response) throws BankException, IOException {
		
		int userId = Integer.parseInt(request.getParameter("userId"));
        String password = request.getParameter("password");
        logic.checkPassword(userId, password);
        
        Role role=logic.getUser(userId);
        HttpSession session = request.getSession();
        session.setAttribute("userId", userId);
        session.setAttribute("role",role);
        System.out.println(session);
        
		switch (role) {
				
				case EMPLOYEE:
					System.out.println("employee logged");
					int branchId=logic.getBranchId(userId);
					Employee employee = logic.getEmployeeDetails(userId);
					Branch branch=logic.getBranch(branchId);
					session.setAttribute("details", employee);
					session.setAttribute("branch", branch);
					session.setAttribute("username",employee.getName());
					System.out.println(employee);
					response.sendRedirect("/WeBank/app/customer");
					break;
					
				case CUSTOMER :
					System.out.println("customer logged");
					Customer customer =logic.getCustomerDetail(userId);
					Account account=logic.getAccount(userId);
					Branch branch1=logic.getBranch(account.getBranchId());
					System.out.println("customer logged");
					System.out.println(customer);
					System.out.println(account);
					session.setAttribute("details", customer);
					session.setAttribute("account", account);
					session.setAttribute("branch", branch1);
					session.setAttribute("username",customer.getName());
					response.sendRedirect("/WeBank/app/account");
					break;
					
				case ADMIN:
					System.out.println("admin");
					User admin = logic.getAdminDetail(userId);
					System.out.println(admin);
					session.setAttribute("details",  admin);
					session.setAttribute("username",admin.getName());
					response.sendRedirect("/WeBank/app/employee");
					break;
						
				default:
					break;
					
				}
        if(role == Role.EMPLOYEE ) { 	
			request.getSession(false).setAttribute("branchId",logic.getBranchId(userId) );
		}
        System.out.println("pas");
	}
	
	public void getProfile(HttpServletRequest request, HttpServletResponse response) throws BankException, ServletException, IOException {
		// TODO Auto-generated method stub
		Role user =  (Role) request.getSession().getAttribute("role");
		
		 HttpSession session = request.getSession();
		System.out.println("Profile Handle");
		int userId = (int) request.getSession().getAttribute("userId");
		switch (user) {
		
		case EMPLOYEE:
			System.out.println("employee logged");
			int branchId=logic.getBranchId(userId);
			Employee employee = logic.getEmployeeDetails(userId);
			Branch branch=logic.getBranch(branchId);
			session.setAttribute("details", employee);
			session.setAttribute("branch", branch);
			session.setAttribute("username",employee.getName());
			System.out.println(employee);
			break;
			
		case CUSTOMER :
			System.out.println("customer logged");
			Customer customer =logic.getCustomerDetail(userId);
			Account account=logic.getAccount(userId);
			Branch branch1=logic.getBranch(account.getBranchId());
			System.out.println("customer logged");
			System.out.println(customer);
			System.out.println(account);
			session.setAttribute("details", customer);
			session.setAttribute("account", account);
			session.setAttribute("branch", branch1);
			session.setAttribute("username",customer.getName());
			break;
			
		case ADMIN:
			System.out.println("admin");
			User admin = logic.getAdminDetail(userId);
			System.out.println(admin);
			session.setAttribute("details",  admin);
			session.setAttribute("username",admin.getName());
			break;
				
		default:
			break;
			
		}
		
		
	}
	public void setBranchDetailsPageWise(HttpServletRequest request) throws BankException {
		int page = 1;
	    int pageSize = 15;
	    if (request.getParameter("page") != null) {
	        page = Integer.parseInt(request.getParameter("page"));
	    }

	    WeBankLogic logic = new WeBankLogic();
	    List<Branch> branches = logic.getBranchPageWise(page, pageSize);
	    long totalBranches = logic.getBranchCount();
	    int totalPages = (int) Math.ceil((double) totalBranches / pageSize);
	    request.setAttribute("branches", branches);
	    request.setAttribute("currentPage", page);
	    request.setAttribute("totalPages", totalPages);
	    request.setAttribute("previousPage", page > 1 ? page - 1 : 1);
	    request.setAttribute("nextPage", page < totalPages ? page + 1 : totalPages);
		   
	   }
	   
	public void setBranchList(HttpServletRequest request) throws BankException {
	    List<Branch> branches = logic.getAllBranches();
	    request.setAttribute("branches", branches);
	}
	
	public List<Account> getAccountsForUser(int userId) throws BankException {
        return logic.getAccountsForUser(userId);
    }
	
	public void setCustomerDetails(HttpServletRequest request) throws BankException {
		
		HttpSession session = request.getSession();
		
		int userId = (int) request.getSession().getAttribute("userId");
	    
	    Role role = (Role) session.getAttribute("role");

	    int page = 1;
	    int pageSize = 15;
	    if (request.getParameter("page") != null) {
	        page = Integer.parseInt(request.getParameter("page"));
	    }

	    List<Customer> customers;
	    long totalCustomers;

	    if (role == Role.ADMIN) {
	        customers = logic.getCustomersPageWise(page, pageSize);
	        totalCustomers = logic.getCustomerCount();
	    } else if (role == Role.EMPLOYEE) {
	        int branchId = logic.getBranchId(userId);
	        customers = logic.getCustomersByBranchPageWise(branchId, page, pageSize);
	        totalCustomers = logic.getCustomerCountByBranch(branchId);
	    } else {
	        throw new BankException("Unauthorized access");
	    }

	    int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);

	    request.setAttribute("customers", customers);
	    request.setAttribute("currentPage", page);
	    request.setAttribute("totalPages", totalPages);
	    request.setAttribute("previousPage", page > 1 ? page - 1 : 1);
	    request.setAttribute("nextPage", page < totalPages ? page + 1 : totalPages);
	}
	
	public void setAccountDetailsPageWise(HttpServletRequest request) throws BankException {
		HttpSession session = request.getSession();
        Role role = (Role) session.getAttribute("role");
        int userId = (int) session.getAttribute("userId");
        int page = 1;
        int pageSize = 10;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<Account> accounts;
        long totalAccounts;

        if (role == Role.ADMIN) {
            accounts = logic.getAccountsPageWise(page, pageSize);
            totalAccounts = logic.getAccountCount();
        } else if (role == Role.EMPLOYEE) {
            int branchId = logic.getBranchId(userId);
            accounts = logic.getAccountsByBranchPageWise(branchId, page, pageSize);
            totalAccounts = logic.getAccountCountByBranch(branchId);
        } else if (role == Role.CUSTOMER) {
            accounts = logic.getAccountsByCustomer(userId, page, pageSize);
            totalAccounts = logic.getAccountCountByCustomer(userId);
        } else {
            throw new BankException("Unauthorized access");
        }
        
        setBranchList(request);
        int totalPages = (int) Math.ceil((double) totalAccounts / pageSize);

        List<Branch> branches = logic.getAllBranches();
        List<User> users = logic.getAllUsers();
        Map<Integer, String> userMap = new HashMap<>();

        for (User user : users) {
            if (user.getRole() == Role.CUSTOMER) {
                userMap.put(user.getUserId(), user.getName());
            }
        }

        Map<Integer, String> branchMap = new HashMap<>();
        for (Branch branch : branches) {
            branchMap.put(branch.getBranchId(), branch.getBranchName());
        }

        request.setAttribute("accounts", accounts);
        request.setAttribute("branchMap", branchMap);
        request.setAttribute("userMap", userMap);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("previousPage", page > 1 ? page - 1 : 1);
        request.setAttribute("nextPage", page < totalPages ? page + 1 : totalPages);
    }
	
	public void addAccount(HttpServletRequest request) throws BankException {
		String accountTypeStr=request.getParameter("accountType");
		AccountType accountType = AccountType.valueOf(accountTypeStr.toUpperCase());
        Account account = new Account();
        account.setUserId(Integer.parseInt(request.getParameter("userId")));
        account.setAccountType(accountType);
        account.setBalance(Long.parseLong(request.getParameter("balance")));
        account.setBranchId(Integer.parseInt(request.getParameter("branchId")));
        account.setAccountStatus(Status.ACTIVE);
        logic.addAccount(account);
    }

    public void editAccount(HttpServletRequest request) throws BankException {
    	String accountTypeStr=request.getParameter("accountType");
		AccountType accountType = AccountType.valueOf(accountTypeStr.toUpperCase());
		String accountStatusStr=request.getParameter("accountStatus");
		Status accountStatus = Status.valueOf(accountStatusStr.toUpperCase());
        Account account = new Account();
        account.setAccountNo(Long.parseLong(request.getParameter("accountNo")));
        account.setAccountType(accountType);
        account.setBalance(Long.parseLong(request.getParameter("balance")));
        account.setBranchId(Integer.parseInt(request.getParameter("branchId")));
        account.setAccountStatus(accountStatus);
        logic.editAccount(account);
    }

    public void deleteAccount(HttpServletRequest request) throws BankException {
        long accountNo = Long.parseLong(request.getParameter("accountNo"));
        logic.deleteAccount(accountNo);
    }
    
	public void setEmployeeDetails(HttpServletRequest request) throws BankException{
		
		int page = 1;
        int pageSize = 15;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<Employee> employees = logic.getEmployeesPageWise(page, pageSize);
        long totalEmployees = logic.getEmployeeCount();
        int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);
  
        request.setAttribute("employees", employees);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("previousPage", page > 1 ? page - 1 : 1);
        request.setAttribute("nextPage", page < totalPages ? page + 1 : totalPages);

	}
	
	public void addBranch(HttpServletRequest request)throws BankException{
		
		 String branchName = request.getParameter("branchName");
	        String ifscCode = request.getParameter("ifscCode");
	        String branchAddress = request.getParameter("branchAddress");

	        Branch branch = new Branch();
	        branch.setBranchName(branchName);
	        branch.setIfscCode(ifscCode);
	        branch.setBranchAddress(branchAddress);

	        try {
	            logic.addBranch(branch);
	            request.setAttribute("successMessage", "Branch added successfully.");
	        } catch (BankException e) {
	            request.setAttribute("errorMessage", "Error adding branch: " + e.getMessage());
	        }
	}
	
	public void editBranch(HttpServletRequest request)throws BankException, ServletException{
		 	int branchId = Integer.parseInt(request.getParameter("branchId"));
	        String branchName = request.getParameter("branchName");
	        String ifscCode = request.getParameter("ifscCode");
	        String branchAddress = request.getParameter("branchAddress");

	        Branch branch = new Branch();
	        branch.setBranchId(branchId);
	        branch.setBranchName(branchName);
	        branch.setIfscCode(ifscCode);
	        branch.setBranchAddress(branchAddress);

	        try {
	            logic.updateBranch(branch);
	            request.setAttribute("successMessage", "Branch updated successfully");
	        } catch (BankException e) {
	            throw new ServletException("Error updating branch", e);
	        }
	}
	
	public void deleteBranch(HttpServletRequest request)throws BankException, ServletException{
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		System.out.println("del sta");
        try {
        	System.out.println("Before Delete Branch");
            logic.deleteBranch(branchId);
            System.out.println("Branch");
            request.setAttribute("successMessage", "Branch deleted successfully");
          
        } catch (BankException e) {
            request.setAttribute("errorMessage", "Failed to delete branch: " + e.getMessage());
            
        }
	}
	
	public void addCustomer(HttpServletRequest request) throws BankException, ParseException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");
        String aadhaar = request.getParameter("aadhaar");
        String pan = request.getParameter("pan");
        String password = request.getParameter("password");
        String birthdayStr = request.getParameter("birthday");
        String genderStr = request.getParameter("gender");
        String accountTypeStr = request.getParameter("accountType");
        String branchIdStr = request.getParameter("branchId");
        String initialBalanceStr = request.getParameter("initialBalance");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long birthday = 0;
        try {
            Date parsedDate = dateFormat.parse(birthdayStr);
            birthday = parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Gender gender = Gender.valueOf(genderStr.toUpperCase());
        AccountType accountType = AccountType.valueOf(accountTypeStr.toUpperCase());
        int branchId = Integer.parseInt(branchIdStr);
        long initialBalance = Long.parseLong(initialBalanceStr);

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setMobile(Long.parseLong(mobile));
        customer.setAddress(address);
        customer.setAadhaar(Long.parseLong(aadhaar));
        customer.setPan(pan);
        customer.setPassword(password);
        customer.setDob(birthday);
        customer.setGender(gender);
        customer.setStatus(Status.ACTIVE);
        customer.setRole(Role.CUSTOMER);

        Account account = new Account();
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        account.setBranchId(branchId);
        account.setAccountStatus(Status.ACTIVE);

        logic.addCustomerWithAccount(customer, account);
    }

	
	
	public void updateCustomer(HttpServletRequest request)throws BankException, ServletException{
		
		int userId = Integer.parseInt(request.getParameter("userId"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");
        String aadhaar = request.getParameter("aadhaar");
        String pan = request.getParameter("pan");
     
        Customer customer = logic.getCustomerDetail(userId);
        customer.setUserId(userId);
        customer.setName(name);
        customer.setEmail(email);
        customer.setMobile(Long.parseLong(mobile));
        customer.setAddress(address);
        customer.setAadhaar(Long.parseLong(aadhaar));
        customer.setPan(pan);
        
        // Assuming the status is active

        try {
            logic.updateCustomer(customer);	
            request.setAttribute("successMessage", "Customer updated successfully!");
            
        } catch (BankException e) {
            request.setAttribute("errorMessage", e.getMessage());
            
        }
	}
	
	public void deleteCustomer(HttpServletRequest request)throws BankException, ServletException{
		 int userId = Integer.parseInt(request.getParameter("userId"));

	        try {
	            logic.deleteCustomer(userId);
	            request.setAttribute("successMessage", "Customer deleted successfully!");
	            
	        } catch (BankException e) {
	            request.setAttribute("errorMessage", e.getMessage());
	            	
	        }
	}

	public void addEmployee(HttpServletRequest request) throws BankException{
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String birthdayStr = request.getParameter("birthday");
        String genderStr = request.getParameter("gender");
        String branchIdStr = request.getParameter("branchId");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long birthday = 0;
        try {
            Date parsedDate = dateFormat.parse(birthdayStr);
            birthday = parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Gender gender = Gender.valueOf(genderStr.toUpperCase());
        int branchId = Integer.parseInt(branchIdStr);
        
        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setMobile(Long.parseLong(mobile));
        employee.setAddress(address);
        employee.setPassword(password);
        employee.setDob(birthday);
        employee.setGender(gender);
        employee.setStatus(Status.ACTIVE); // Assuming the status is active
        employee.setRole(Role.EMPLOYEE); // Assuming the role is employee
        employee.setBranchId(branchId);
        try {
            logic.addEmployee(employee);
            request.setAttribute("successMessage", "employee updated successfully!");
            
        } catch (BankException e) {
            request.setAttribute("errorMessage", e.getMessage());
            
        }
		
	}
	
	 public void updateEmployee(HttpServletRequest request) throws BankException {
		    
		    
	        int employeeId = Integer.parseInt(request.getParameter("employeeId"));
	        Employee employee = logic.getEmployeeDetails(employeeId);
	        String name = request.getParameter("name");
	        String email = request.getParameter("email");
	        String mobile = request.getParameter("mobile");
	        String address = request.getParameter("address");
	        System.out.println("update empl");
	        String branchName = request.getParameter("branchId");
	        
	        
	        // Get branch ID from branch name
	        int branchId = Integer.parseInt(branchName);
	        
	        
	        employee.setUserId(employeeId);
	        employee.setName(name);
	        employee.setEmail(email);
	        employee.setMobile(Long.parseLong(mobile));
	        employee.setAddress(address);

	        employee.setBranchId(branchId);

	        try {
	            logic.updateEmployee(employee);
	            request.setAttribute("successMessage", "Employee updated successfully!");
	        } catch (BankException e) {
	            request.setAttribute("errorMessage", e.getMessage());
	        }
	    }
	 
	 public void deleteEmployee(HttpServletRequest request) throws BankException {
	        int employeeId = Integer.parseInt(request.getParameter("employeeId"));
	        try {
	            logic.deleteEmployee(employeeId);
	            request.setAttribute("successMessage", "Employee deleted successfully!");
	        } catch (BankException e) {
	            request.setAttribute("errorMessage", e.getMessage());
	        }
	    }
	
	 public void setTransactionDetails(HttpServletRequest request) throws BankException {
		 	HttpSession session = request.getSession();
		 	int userId = (int) request.getSession().getAttribute("userId");
		    System.out.println(userId);
		    List<Account> accounts = getAccountsForUser(userId);
			session.setAttribute("useraccounts", accounts);
			
	 }
	 
	 public boolean processTransaction(HttpServletRequest request) throws BankException{
		 	System.out.println("In process Transaction");
		 	int userId = (int) request.getSession().getAttribute("userId");
	        String transactionType = request.getParameter("transaction-type");
	        long accountNumber = Long.parseLong(request.getParameter("account-number"));
	        long amount = Long.parseLong(request.getParameter("amount"));
	        String remarks = request.getParameter("remarks");
	        long receiverAccountNumber = transactionType.equals("interbank") || transactionType.equals("intrabank")
	                ? Long.parseLong(request.getParameter("receiver-account"))
	                : 0;
	        if(logic.isAccountActive(accountNumber)) {
		        
		        Transaction transaction = new Transaction();
		        transaction.setUserId(userId);
		        transaction.setAccountNo(accountNumber);
		        transaction.setAmount(amount);
		        transaction.setRemarks(remarks);
		        transaction.setTransDateTime(System.currentTimeMillis());
		        transaction.setTransType(TransactionType.valueOf(transactionType.toUpperCase()));
		        transaction.setPayeeAccNo(receiverAccountNumber);
		        System.out.println(transaction);
		        try {
		            logic.processTransaction(transaction);
		            return true;
		            
		        } catch (BankException e) {
		        	
		            e.printStackTrace();
		            request.setAttribute("errorMessage", e.getMessage());
		        }
	        }
	        else {
	        	throw new BankException("Inactive Account");
	        }
	        return false;
	 }
	 
	 public void setTransactionStatements(HttpServletRequest request) throws BankException {
		    int userId = (int) request.getSession().getAttribute("userId");
		    Role role = (Role) request.getSession().getAttribute("role");

		    int page = 1;
		    int pageSize = 15; // Number of transactions per page

		    if (request.getParameter("page") != null) {
		        page = Integer.parseInt(request.getParameter("page"));
		    }

		    long accountNo = request.getParameter("accountNo") != null && !request.getParameter("accountNo").isEmpty() ? Long.parseLong(request.getParameter("accountNo")) : -1;
		    String periodParam = request.getParameter("period");
		    TransactionPeriod period = periodParam != null ? TransactionPeriod.valueOf(periodParam) : TransactionPeriod.LAST_10_DAYS;

		    Instant startTime = getStartTime(period);

		    List<Transaction> transactions = new ArrayList<>();
		    int totalTransactions = 0;

		    switch (role) {
		        case ADMIN:
		            if (accountNo != -1) {
		                totalTransactions = logic.getTransactionCountByAccount(accountNo, startTime.toEpochMilli());
		                transactions = logic.getTransactionsByAccountPageWise(accountNo, startTime.toEpochMilli(), page, pageSize);
		            } else {
		                totalTransactions = logic.getTotalTransactionCount(startTime.toEpochMilli());
		                transactions = logic.getAllTransactionsPageWise(startTime.toEpochMilli(), page, pageSize);
		            }
		            break;
		        case EMPLOYEE:
		            int branchId = logic.getBranchId(userId);
		            if (accountNo != -1) {
		                totalTransactions = logic.getTransactionCountByBranchAndAccountNo(branchId, accountNo, startTime.toEpochMilli());
		                transactions = logic.getTransactionsByBranchAndAccountNoPageWise(branchId, accountNo, startTime.toEpochMilli(), page, pageSize);
		            } else {
		                totalTransactions = logic.getTransactionCountByBranch(branchId, startTime.toEpochMilli());
		                transactions = logic.getTransactionsByBranchPageWise(branchId, startTime.toEpochMilli(), page, pageSize);
		            }
		            break;
		        case CUSTOMER:
		            if (accountNo != -1) {
		                totalTransactions = logic.getTransactionCountByUserIdAndAccountNo(userId, accountNo, startTime.toEpochMilli());
		                transactions = logic.getTransactionsByUserIdAndAccountNoPageWise(userId, accountNo, startTime.toEpochMilli(), page, pageSize);
		            } else {
		                totalTransactions = logic.getTransactionCountByUserId(userId, startTime.toEpochMilli());
		                transactions = logic.getTransactionsByUserIdPageWise(userId, startTime.toEpochMilli(), page, pageSize);
		            }
		            break;
		    }

		    int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
		    Map<Long, String> accountMap = logic.getAccountMapByRole(role, userId);

		    request.setAttribute("accountMap", accountMap);
		    request.setAttribute("transactions", transactions);
		    request.setAttribute("currentPage", page);
		    request.setAttribute("totalPages", totalPages);
		}

	 private Instant getStartTime(TransactionPeriod period) {
		    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
		    switch (period) {
		        case LAST_10_DAYS:
		            return now.minusDays(10).toInstant();
		        case LAST_1_MONTH:
		            return now.minusMonths(1).toInstant();
		        case LAST_3_MONTHS:
		            return now.minusMonths(3).toInstant();
		        case LAST_6_MONTHS:
		            return now.minusMonths(6).toInstant();
		        case LAST_1_YEAR:
		            return now.minusYears(1).toInstant();
		        default:
		            return now.minusDays(10).toInstant();
		    }
		}



	
}