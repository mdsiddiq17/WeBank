<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.bank.model.Account" %>
<%
	
	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");//HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 	
	response.setHeader("Expires","0"); //Proxies
	if(session.getAttribute("username")==null){
		response.sendRedirect("/Webank/app/login");
	}
%>
<%
    List<Account> accounts = (List<Account>) session.getAttribute("useraccounts");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
    <style>.message {
            cursor: pointer;
        }</style>
</head>
<body>
    <jsp:include page="nav.jsp" />
    <div class="main-content">
        <div class="header">
            <h1>Transaction</h1>
            <div class="user-info">
                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
                <span><%= session.getAttribute("username") %></span>
            </div>
        </div>
        <div class="content">
            <form action="transaction" method="post">
                <div class="form-group">
                    <label for="transaction-type">Transaction Type</label>
                    <select id="transaction-type" name="transaction-type">
                        <option value="deposit">Deposit</option>
                        <option value="withdraw">Withdraw</option>
                        <option value="interbank">Interbank Transfer</option>
                        <option value="intrabank">Intra-bank Transfer</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="account-number">Account Number</label>
                    <select id="account-number" name="account-number" class="select2">
                        <%
                            if (accounts != null && !accounts.isEmpty()) {
                                for (Account account : accounts) {
                        %>
                        <option value="<%= account.getAccountNo() %>"><%= account.getAccountNo() %></option>
                        <%
                                }
                            } else {
                        %>
                        <option value="">No accounts available</option>
                        <%
                            }
                        %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="amount">Amount</label>
                    <input type="number" id="amount" name="amount" min="1" required>
                </div>
                <div id="receiver-account-group" class="form-group" style="display: none;">
                    <label for="receiver-account">Payee Account Number</label>
                    <input type="text" id="receiver-account" name="receiver-account">
                </div>
                <div class="form-group">
                    <label for="remarks">Remarks</label>
                    <input type="text" id="remarks" name="remarks">
                </div>
                <button type="submit" id="submit-button">Submit</button>
            </form>
        </div>
        <div style="font-size:24px; margin-left:100px; margin-top:100px;">
	        <%
	            String transactionStatus = (String) request.getAttribute("transactionStatus");
	            if ("success".equals(transactionStatus)) {
	        %>
	            <p style="color:green;" class="message success">Transaction Successful</p>
	        <%
	            } else if ("failure".equals(transactionStatus)) {
	        %>
	            <p style="color:red;" class="message failure">Transaction Failed</p>
	        <%
	            }
	        %>
	    </div>
    </div>
    <script>
    	
    $(document).ready(function() {
        $('.select2').select2();
    });	
    
        document.getElementById('transaction-type').addEventListener('change', function() {
            var transactionType = this.value;
            var receiverAccountGroup = document.getElementById('receiver-account-group');
            
            if (transactionType === 'interbank' || transactionType === 'intrabank') {
                receiverAccountGroup.style.display = 'block';
            } else {
                receiverAccountGroup.style.display = 'none';
            }
        });
        
        document.addEventListener('click', function() {
            var message = document.querySelector('.message');
            if (message) {
                message.style.display = 'none';
            }
        });
    </script>
</body>
</html>
