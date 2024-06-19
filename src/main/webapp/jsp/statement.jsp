	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.bank.model.Transaction" %>
	<%@ page import="com.bank.model.User" %>
	<%@ page import="com.bank.enums.Role" %>
	<%@ page import="com.bank.enums.TransactionPeriod" %>
	<%@ page import="com.bank.enums.CreditDebit" %>
	<%@ page import="java.text.SimpleDateFormat" %>
	
	<%
	    response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");//HTTP 1.1
	    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	    response.setHeader("Expires", "0"); //Proxies
	
	    if (session.getAttribute("username") == null) {
	        response.sendRedirect("/WeBank/app/login");
	    }
	
	    User user = (User) session.getAttribute("details");
	    Role role = (Role) session.getAttribute("role");
	    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
	    Map<Long, String> accountMap = (Map<Long, String>) request.getAttribute("accountMap");
	    int currentPage = (int) request.getAttribute("currentPage");
	    int totalPages = (int) request.getAttribute("totalPages");
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	%>
	
	<!DOCTYPE html>
	<html lang="en">
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>Transaction Statements</title>
	    <link rel="preconnect" href="https://fonts.googleapis.com">
	    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
	    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
	    <style>.amount-credit { color: green; }
        .amount-debit { color: red; }</style>
	</head>
	<body>
	    <jsp:include page="nav.jsp" />
	    <div class="main-content">
	        <div class="header">
	            <h1>Transaction Statements</h1>
	            <div class="user-info">
	                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
	                <span><%= session.getAttribute("username") %></span>
	            </div>
	        </div>
	        <div class="filters">
	            <form method="get" action="statement">
	                <label for="accountNo">Account Number:</label>
	                <select id="accountNo" name="accountNo" class="select2">
	                    <option value="">Select Account Number</option>
	                    <%
	                        if (accountMap != null) {
	                            for (Map.Entry<Long, String> entry : accountMap.entrySet()) {
	                    %>
	                    <option value="<%= entry.getKey() %>"><%= entry.getKey() %> - <%= entry.getValue() %></option>
	                    <%
	                            }
	                        }
	                    %>
	                </select>
	                <label for="period">Transaction Period:</label>
	                <select id="period" name="period">
	                    <option value="LAST_10_DAYS">Last 10 Days</option>
	                    <option value="LAST_1_MONTH">Last 1 Month</option>
	                    <option value="LAST_3_MONTHS">Last 3 Months</option>
	                    <option value="LAST_6_MONTHS">Last 6 Months</option>
	                    <option value="LAST_1_YEAR">Last 1 Year</option>
	                </select>
	                <button type="submit">Filter</button>
	            </form>
	        </div>
	        <div class="table-container">
	            <table>
	                <thead>
	                    <tr>
	                        <th>Transaction ID</th>
	                        <th>Date & Time</th>
	                        <th>Account Number</th>
	                        <th>Transaction Type</th>
	                        <th>Credit/Debit</th>
	                        <th>Amount</th>
	                        <th>Closing Balance</th>
	                        <th>Remarks</th>
	                    </tr>
	                </thead>
	                <tbody>
	                    <%
	                        if (transactions != null) {
	                            for (Transaction transaction : transactions) {
	                                String dateTime = sdf.format(new Date(transaction.getTransDateTime()));
	                                boolean isCredit = transaction.getCreditDebit() == CreditDebit.CREDIT;
	                    %>
	                    <tr>
	                        <td><%= transaction.getTransactionId() %></td>
	                        <td><%= dateTime %></td>
	                        <td><%= transaction.getAccountNo() %></td>
	                        <td><%= transaction.getTransType() %></td>
	                        <td><%= transaction.getCreditDebit() %></td>
	                        <td class="<%= isCredit ? "amount-credit" : "amount-debit" %>">
   							 <%= isCredit ? "+" : "-" %><%= transaction.getAmount() %>
							</td>
	                        <td><%= transaction.getClosingBalance() %></td>
	                        <td><%= transaction.getRemarks() %></td>
	                    </tr>
	                    <%
	                            }
	                        }
	                    %>
	                </tbody>
	            </table>
	        </div>
	        <div class="pagination">
	            <form method="get" action="statement">
	                <input type="hidden" name="accountNo" value="<%= request.getParameter("accountNo") %>">
	                <input type="hidden" name="period" value="<%= request.getParameter("period") %>">
	                <button type="submit" name="page" value="<%= currentPage - 1 %>" <%= (currentPage == 1) ? "disabled" : "" %>>&larr; </button>
	                <span>Page <%= currentPage %> of <%= totalPages %></span>
	                <button type="submit" name="page" value="<%= currentPage + 1 %>" <%= (currentPage == totalPages) ? "disabled" : "" %>> &rarr;</button>
	            </form>
	        </div>
	    </div>
	    <script>
	        $(document).ready(function() {
	            $('.select2').select2();
	        });
	    </script>
	</body>
	</html>
