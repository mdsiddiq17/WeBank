<div class="mode-toggle"><span class="switch"></span></div>
            <li class="mode"><a href="/logout"><img src="<%= request.getContextPath() %>/images/dark.png"><span class="link-name">Dark Mode</span></a></li>
            
            
            
 public void setTransactionStatements(HttpServletRequest request) throws BankException {
		 int userId = (int) request.getSession().getAttribute("userId");
		    Role role = (Role) request.getSession().getAttribute("role");
		    

		    int page = 1;
		    int pageSize = 15; // Number of transactions per page

		    if (request.getParameter("page") != null) {
		        page = Integer.parseInt(request.getParameter("page"));
		    }

		    List<Transaction> transactions = new ArrayList<>();
		    int totalTransactions = 0;

		    switch (role) {
		        case ADMIN:
		            totalTransactions = logic.getTotalTransactionCount();
		            transactions = logic.getAllTransactionsPageWise(page, pageSize);
		            break;
		        case EMPLOYEE:
		            int branchId = logic.getBranchId(userId);
		            totalTransactions = logic.getTransactionCountByBranch(branchId);
		            transactions = logic.getTransactionsByBranchPageWise(branchId, page, pageSize);
		            break;
		        case CUSTOMER:
		            totalTransactions = logic.getTransactionCountByUserId(userId);
		            transactions = logic.getTransactionsByUserIdPageWise(userId, page, pageSize);
		            break;
		    }
		    System.out.println("before setting");
		    int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
		    Map<Long, String> accountMap = logic.getAccountMapByRole(role, userId);
		    System.out.println("beforeaccountMap");
		    request.setAttribute("accountMap", accountMap);
		    request.setAttribute("transactions", transactions);
		    request.setAttribute("currentPage", page);
		    request.setAttribute("totalPages", totalPages);
		}
            