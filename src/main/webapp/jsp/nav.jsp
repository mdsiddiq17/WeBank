<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.enums.Role" %>
<%@ page import="javax.servlet.http.HttpSession" %>

<%
    
    Role role = (Role) request.getSession(false).getAttribute("role");
%>
<style>
	.active {
	    background-color: #3A83F1;
	    border-radius: 7px;
	    padding: 5px;
	    /* You can add other styles to make it more distinct */
	}
</style>
<nav class="sidebar">
    <div class="logo-name">
        <div class="logo-image">
            <img src="<%= request.getContextPath() %>/images/cashflow-blue.png" alt="Fund Flow Logo">
        </div>
        <span class="logo_name">Fund Flow</span>
    </div>
    <div class="menu-items">
        <ul class="nav-links">
            <% if (role == Role.ADMIN) { %>
                    <li ><a href="employee"><img src="<%= request.getContextPath() %>/images/employee.png"><span class="link-name">Employees</span></a></li>
                    <li><a href="customer"><img class="cust" src="<%= request.getContextPath() %>/images/customer-3.png"><span class="link-name">Customers</span></a></li>
                    <li><a href="branch"><img src="<%= request.getContextPath() %>/images/branch.png"><span class="link-name">Branches</span></a></li>
                	<li><a href="account"><img src="<%= request.getContextPath() %>/images/create-account.png"><span class="link-name">Accounts</span></a></li>
                	<li><a href="statement"><img src="<%= request.getContextPath() %>/images/statement.png"><span class="link-name">Statements</span></a></li>
                <% } else if (role == Role.CUSTOMER) { %>
                    <li><a href="account"><img src="<%= request.getContextPath() %>/images/create-account.png"><span class="link-name">Accounts</span></a></li>
                    <li><a href="transaction"><img src="<%= request.getContextPath() %>/images/credit-card.png"><span class="link-name">Transaction</span></a></li>
                    <li><a href="statement"><img src="<%= request.getContextPath() %>/images/statement.png"><span class="link-name">Statements</span></a></li>
                <% } else if (role == Role.EMPLOYEE) { %>
                    <li><a href="customer"><img class="cust" src="<%= request.getContextPath() %>/images/customer-3.png"><span class="link-name">Customers</span></a></li>
                    <li><a href="account"><img src="<%= request.getContextPath() %>/images/create-account.png"><span class="link-name">Accounts</span></a></li>
                    <li><a href="statement"><img src="<%= request.getContextPath() %>/images/statement.png"><span class="link-name">Statements</span></a></li>
                <% } %>
        </ul>
        <ul class="logout-mode">
            
        </ul>
    </div>
</nav>
<script>
    let nav=".nav-links li";
    $(nav-links).on("click", function(){
    	$(nav-links).removeclass("active");
    	$(this).addClass("active;")
    )}
</script>