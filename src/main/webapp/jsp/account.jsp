<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.bank.model.Account" %>
<%@ page import="com.bank.model.Branch" %>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.enums.AccountType" %>
<%@ page import="com.bank.enums.Role" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
    <title>Account Details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    <style>
        .popup-form {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
            z-index: 1001;
            display: none;
            width: 400px;
        }
        .popup-form h2 { margin-top: 0; font-size: 24px; color: #333; }
        .popup-form p { margin-bottom: 20px; color: #666; }
        .popup-form input, .popup-form select { width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; }
        .popup-form button[type="submit"],
        .popup-form button[type="button"] {
            padding: 10px 20px;
            border-radius: 4px;
            background-color: #007bff;
            font-size:16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .popup-form button[type="submit"]:hover,
        .popup-form button[type="button"]:hover {
            background-color: #0056b3;
        }
        .popup-form button[type="button"] {
            color:black;
            background-color: white;
            border-color:black;
            border-style:solid;
            border-width:0.1px;
        }
        .popup-form button[type="button"]:hover {
            background-color: #d1d9e0;
        }
        .popup-form button[type="submit"],
        .popup-form button[type="button"] {
            width: calc(50% - 5px);
            display: inline-block;
            margin-top: 10px;
        }
        .popup-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 1000; display: none; }
    </style>
</head>
<body>
    <jsp:include page="nav.jsp" />
    <div class="main-content">
        <div class="header">
            <h1>List of Accounts</h1>
            <div class="user-info">
                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
                <span><%= session.getAttribute("username") %></span>
            </div>
        </div>
        <div class="search-bar">
            <input type="text" id="searchInput" placeholder="Search Account..." onkeyup="searchAccounts()">
             <% if (!"CUSTOMER".equals(session.getAttribute("role").toString())) { %>
                            <button onclick="openAddForm()">Add New Account</button>
                        <% } %>
        </div>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="success-message"><%= request.getAttribute("successMessage") %></p>
        <% } %>
        <div class="table-container">
            <table id="accountTable">
                <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Account Type</th>
                        <th>Name</th>
                        <th>Customer ID</th>
                        <th>Branch Name</th>
                        <th>Balance</th>
                        <th>Account Status</th>
                        <% if (!"CUSTOMER".equals(session.getAttribute("role").toString())) { %>
                            <th>Actions</th>
                        <% } %>
                    </tr>
                </thead>
                <tbody>
                    <%
                    	List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                    	Role role = (Role) session.getAttribute("role");
                    	Branch userBranch = (Branch) session.getAttribute("branch");
                        List<Account> accounts = (List<Account>) request.getAttribute("accounts");
                        Map<Integer, String> branchMap = (Map<Integer, String>) request.getAttribute("branchMap");
                        Map<Integer, String> userMap = (Map<Integer, String>) request.getAttribute("userMap");
                        if (accounts != null) {
                            for (Account account : accounts) {
                    %>
                    <tr>
                        <td><%= account.getAccountNo() %></td>
                        <td><%= account.getAccountType() %></td>
                        <td><%= userMap.get(account.getUserId()) %></td>
                        <td><%= account.getUserId() %></td>
                        <td><%= branchMap.get(account.getBranchId()) %></td>
                        <td><%= account.getBalance() %></td>
                        <td><%= account.getAccountStatus() %></td>
                        <% if (!"CUSTOMER".equals(session.getAttribute("role").toString())) { %>
                            <td>
                                <div class="table-actions">
                                    <button onclick="openEditForm(<%= account.getAccountNo() %>, '<%= account.getAccountType() %>', '<%= account.getBalance() %>', <%= account.getBranchId() %>, '<%= account.getAccountStatus() %>')">
                                        <img src="<%= request.getContextPath() %>/images/edit.png" alt="Edit">
                                    </button>
                                    <button onclick="openDeleteForm(<%= account.getAccountNo() %>)">
                                        <img src="<%= request.getContextPath() %>/images/delete.png" alt="Delete">
                                    </button>
                                </div>
                            </td>
                        <% } %>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>
        <div class="pagination">
            <form method="get" action="AccountServlet?action=list-accounts">
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("previousPage") %>">&larr;</button>
                <span>Page <%= request.getAttribute("currentPage") %> of <%= request.getAttribute("totalPages") %></span>
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("nextPage") %>">&rarr;</button>
            </form>
        </div>
    </div>

    <!-- Add Popup Form -->
    <div class="popup-overlay" id="addPopupOverlay"></div>
    <div class="popup-form" id="addPopupForm">
        <h2>Add New Account</h2>
        <form id="addAccountForm" action="add-account" method="post">
            <select name="userId" required>
                <option value="" disabled selected>Select Customer ID</option>
                <% 
                
                    Map<Integer, String> users = (Map<Integer, String>) request.getAttribute("userMap");
                    if (users != null) {
                        for (Map.Entry<Integer, String> entry : users.entrySet()) {
                            
                %>
                <option value="<%= entry.getKey() %>"><%= entry.getKey() %></option>
                <% 
                            
                        }
                    }
                %>
            </select>
            <select name="accountType" required>
                <option value="" disabled selected>Select Account Type</option>
                <option value="SAVINGS">Savings</option>
                <option value="CURRENT">Current</option>
                <option value="SALARY">Salary</option>
            </select>
            <input type="number" name="balance" placeholder="Initial Balance" min="0" max="1000000" required>
            <select name="branchId" required>
                <option value="" disabled selected>Select Branch</option>
                <% 
                        if (role == Role.ADMIN && branches != null) {
                            for (Branch branch : branches) {
                    %>
                    <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
                    <% 
                            }
                        } else if (role == Role.EMPLOYEE && userBranch != null) {
                    %>
                    <option value="<%= userBranch.getBranchId() %>" selected><%= userBranch.getBranchName() %></option>
                    <% 
                        }
                    %>
            </select>
            <button type="submit">Add</button>
            <button type="button" onclick="closeAddForm()">Cancel</button>
        </form>
    </div>

    <!-- Edit Popup Form -->
    <div class="popup-overlay" id="editPopupOverlay"></div>
    <div class="popup-form" id="editPopupForm">
        <h2>Edit Account</h2>
        <form id="editAccountForm" action="edit-account" method="post">
            <input type="hidden" name="accountNo" id="editAccountNo">
            <select name="accountType" id="editAccountType" required>
                <option value="SAVINGS">Savings</option>
                <option value="CURRENT">Current</option>
                <option value="SALARY">Salary</option>
            </select>
            <input type="number" name="balance" id="editBalance" placeholder="Balance" min="0" max="1000000" required>
            <select name="branchId" id="editBranchId" required>
                <option value="" disabled selected>Select Branch</option>
                <% 
                        if (role == Role.ADMIN && branches != null) {
                            for (Branch branch : branches) {
                    %>
                    <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
                    <% 
                            }
                        } else if (role == Role.EMPLOYEE && userBranch != null) {
                    %>
                    <option value="<%= userBranch.getBranchId() %>" selected><%= userBranch.getBranchName() %></option>
                    <% 
                        }
                    %>	
            </select>
            <select name="accountStatus" id="editAccountStatus" required>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
            </select>
            <button type="submit">Update</button>
            <button type="button" onclick="closeEditForm()">Cancel</button>
        </form>
    </div>

    <!-- Delete Popup Form -->
    <div class="popup-overlay" id="deletePopupOverlay"></div>
    <div class="popup-form" id="deletePopupForm">
        <h2>Delete Account</h2>
        <p>Are you sure you want to delete this account?</p>
        <form id="deleteAccountForm" action="delete-account" method="get">
            <input type="hidden" name="accountNo" id="deleteAccountNo">
            <button type="submit">Delete</button>
            <button type="button" onclick="closeDeleteForm()">Cancel</button>
        </form>
    </div>

    <script>
        function searchAccounts() {
            var input, filter, table, tr, td, i, txtValue;
            input = document.getElementById("searchInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("accountTable");
            tr = table.getElementsByTagName("tr");

            for (i = 1; i < tr.length; i++) {
                td = tr[i].getElementsByTagName("td");
                if (td) {
                    txtValue = "";
                    for (j = 0; j < td.length; j++) {
                        if (td[j]) {
                            txtValue += td[j].textContent || td[j].innerText;
                        }
                    }
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }
        }

        function openAddForm() {
            document.getElementById("addPopupForm").style.display = "block";
            document.getElementById("addPopupOverlay").style.display = "block";
        }

        function closeAddForm() {
            document.getElementById("addPopupForm").style.display = "none";
            document.getElementById("addPopupOverlay").style.display = "none";
        }

        function openEditForm(accountNo, accountType, balance, branchId, accountStatus) {
            document.getElementById("editAccountNo").value = accountNo;
            document.getElementById("editAccountType").value = accountType;
            document.getElementById("editBalance").value = balance;
            document.getElementById("editBranchId").value = branchId;
            document.getElementById("editAccountStatus").value = accountStatus;
            document.getElementById("editAccountStatus").value = accountStatus;
            document.getElementById("editPopupForm").style.display = "block";
            document.getElementById("editPopupOverlay").style.display = "block";
        }

        function closeEditForm() {
            document.getElementById("editPopupForm").style.display = "none";
            document.getElementById("editPopupOverlay").style.display = "none";
        }

        function openDeleteForm(accountNo) {
            document.getElementById("deleteAccountNo").value = accountNo;
            document.getElementById("deletePopupForm").style.display = "block";
            document.getElementById("deletePopupOverlay").style.display = "block";
        }

        function closeDeleteForm() {
            document.getElementById("deletePopupForm").style.display = "none";
            document.getElementById("deletePopupOverlay").style.display = "none";
        }
    </script>
</body>
</html>

                        
