<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.bank.enums.*"%>
<%@ page import="com.bank.model.*"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	
	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");//HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 	
	response.setHeader("Expires","0"); //Proxies
	if(session.getAttribute("username")==null){
		response.sendRedirect("/Webank/app/login");
	}
%>
<%
    User user = (User) session.getAttribute("details");
    Role role = (Role) session.getAttribute("role");
    Account account = (Account) request.getSession().getAttribute("account");
    Branch branch = (Branch) request.getSession().getAttribute("branch");
    Date dobDate = new Date(user.getDob());

    // Format the Date
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String dobFormatted = sdf.format(dobDate);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Banking - Profile</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    <style>
         .profile-display {
            background-color: #f9f9f9; /* Light grey background */
            border-radius: 8px; /* Rounded corners */
            padding: 20px; /* Padding inside the box */
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
            margin-bottom: 20px; /* Space below the box */
            font-family: 'Inter', sans-serif; /* Modern font */
        }

        .profile-display div {
            margin-bottom: 15px; /* Space between items */
            font-size: 16px; /* Font size */
        }

        .profile-display strong {
            color: #333; /* Darker color for labels */
            font-weight: 800; /* Slightly bolder text */
        }

        .edit-icon {
            width: 20px; /* Size of the edit icon */
            height: 20px;
            cursor: pointer; /* Pointer cursor on hover */
            margin-left: 10px; /* Space between text and icon */
            vertical-align: middle; /* Align icon with text */
        }

        .edit-icon:hover {
            transform: scale(1.1); /* Slightly enlarge on hover */
            transition: transform 0.2s; /* Smooth transition */
        }

        .edit-form {
            display: none; /* Hide edit form by default */
            background-color: #fff; /* White background */
            border-radius: 8px; /* Rounded corners */
            padding: 20px; /* Padding inside the box */
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
            margin-bottom: 20px; /* Space below the box */
        }

/* Popup form styles */
.popup-overlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999;
}

.popup-logout {
    display: none;
    position: fixed;
    left: 50%;
    top: 35%;
    transform: translate(-50%, -50%);
    background-color: white;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    text-align: center;
    z-index: 1000;
}

.popup-logout h2 {
    margin-bottom: 20px;
    font-size: 20px;
    color: #333;
}

.popup-logout button {
    padding: 10px 20px;
    margin: 10px;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s;
    
}

.logout{
    background-color: rgb(176, 0, 0);
    color: white;
    border: none;
}

.logout {
    background-color: #a50000;
}

.cancel{
    background-color: white;
    color: black;
    border: 1px solid black;
}




    </style>
</head>
<body>
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
                    <li><a href="employee"><img src="<%= request.getContextPath() %>/images/employee.png"><span class="link-name">Employees</span></a></li>
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
                
                <li></li>
            </ul>
            
        </div>
    </nav>
    <div class="main-content">
        <div class="header">
            <h1>Profile Settings</h1>
            <div class="user-info">
                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
                <span><%= user.getName() %></span>
            </div>
        </div>
        <div class="lg"><a onclick="openLogoutPopup()"><img src="<%= request.getContextPath() %>/images/logout-red.png" class="lgi"><span style="color:rgb(176,0,0);" class="link-name">Log Out</span></a></div>
        <div class="content">
            <div class="tabs">
                <button class="tab active" data-tab="personal-info">Personal Details</button>
                <% if (role == Role.CUSTOMER) { %>
                    <button class="tab" data-tab="account-details">Account Details</button>
                    <button class="tab" data-tab="branch-details">Branch Details</button>
                <% } %>
                <% if (role == Role.EMPLOYEE) { %>
                    <button class="tab" data-tab="branch-details">Branch Details</button>
                <% } %>
            </div>
            <div class="tab-content active" id="personal-info">
                <div class="profile-display">
                    <div><strong>User Id:</strong> <%= user.getUserId() %></div>
                    <div><strong>Name:</strong> <%= user.getName() %> </div>
                    <div><strong>Email:</strong> <%= user.getEmail() %></div>
                    <div><strong>Phone number:</strong> <%= user.getMobile() %></div>
                    <div><strong>User Address:</strong> <%= user.getAddress() %></div>
                    <div><strong>Date of Birth:</strong> <%= dobFormatted %></div>
                </div>
                <form id="editPersonalForm" action="update-profile" method="post" class="edit-form">
                    <div class="form-group">
                        <label for="user-id">User Id</label>
                        <input type="text" id="user-id" name="user-id" value="<%= user.getUserId() %>" readonly>
                    </div>
                                        <div class="form-group">
                        <label for="name">Name</label>
                        <input type="text" id="name" name="name" value="<%= user.getName() %>">
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<%= user.getEmail() %>">
                    </div>
                    <div class="form-group">
                        <label for="phone">Phone number</label>
                        <input type="tel" id="phone" name="phone" pattern="[6-9]{1}[0-9]{9}" maxlength="10" value="<%= user.getMobile() %>">
                    </div>
                    <div class="form-group">
                        <label for="address">User Address</label>
                        <input type="text" id="address" name="address" value="<%= user.getAddress() %>">
                    </div>
                    <div class="form-group">
                        <label for="dob">Date of Birth</label>
                        <input type="text" id="dob" name="dob" value="<%= dobFormatted %>">
                    </div>
                    <button type="submit">Save changes</button>
                </form>
            </div>

            <% if (role == Role.CUSTOMER) { %>
                <div class="tab-content" id="account-details">
                    <div class="profile-display">
                        <div><strong>Account Number:</strong> <%= account.getAccountNo() %></div>
                        <div><strong>Account Type:</strong> <%= account.getAccountType() %></div>
                        <div><strong>Balance:</strong> <%= account.getBalance() %></div>
                    </div>
                    <form id="editAccountForm" action="/update-account" method="post" class="edit-form">
                        <div class="form-group">
                            <label for="account-number">Account Number</label>
                            <input type="text" id="account-number" name="account-number" value="<%= account.getAccountNo() %>" readonly>
                        </div>
                        <div class="form-group">
                            <label for="account-type">Account Type</label>
                            <input type="text" id="account-type" name="account-type" value="<%= account.getAccountType() %>">
                        </div>
                        <div class="form-group">
                            <label for="balance">Balance</label>
                            <input type="text" id="balance" name="balance" value="<%= account.getBalance() %>">
                        </div>
                        <button type="submit">Save changes</button>
                    </form>
                </div>
            <% } %>

            <% if (role == Role.EMPLOYEE) { %>
                <div class="tab-content" id="branch-details">
                    <div class="profile-display">
                        <div><strong>Branch ID:</strong> <%= branch.getBranchId() %></div>
                        <div><strong>Branch Name:</strong> <%= branch.getBranchName() %></div>
                        <div><strong>IFSC Code:</strong> <%= branch.getIfscCode() %></div>
                        <div><strong>Branch Address:</strong> <%= branch.getBranchAddress() %></div>
                    </div>
                    <form id="editBranchForm" action="/update-branch" method="post" class="edit-form">
                        <div class="form-group">
                            <label for="branch-id">Branch ID</label>
                            <input type="text" id="branch-id" name="branch-id" value="<%= branch.getBranchId() %>" readonly>
                        </div>
                        <div class="form-group">
                            <label for="branch-name">Branch Name</label>
                            <input type="text" id="branch-name" name="branch-name" value="<%= branch.getBranchName() %>">
                        </div>
                        <div class="form-group">
                            <label for="ifsc">IFSC Code</label>
                            <input type="text" id="ifsc" name="ifsc" value="<%= branch.getIfscCode() %>">
                        </div>
                        <div class="form-group">
                            <label for="branch-address">Branch Address</label>
                            <input type="text" id="branch-address" name="branch-address" value="<%= branch.getBranchAddress() %>">
                        </div>
                        <button type="submit">Save changes</button>
                    </form>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Logout Confirmation Popup -->
    <div class="popup-overlay" id="logoutPopupOverlay"></div>
    <div class="popup-logout" id="logoutPopupForm">
        <h2>Are you sure you want to log out?</h2>
        <button class="logout" onclick="confirmLogout()">Log Out</button>
        <button class="cancel" onclick="closeLogoutPopup()">Cancel</button>
    </div>

    <script>
        function toggleEditForm(formId) {
            var form = document.getElementById(formId);
            if (form.style.display === "none" || form.style.display === "") {
                form.style.display = "block";
            } else {
                form.style.display = "none";
            }
        }

        function openLogoutPopup() {
            document.getElementById("logoutPopupForm").style.display = "block";
            document.getElementById("logoutPopupOverlay").style.display = "block";
        }

        function closeLogoutPopup() {
            document.getElementById("logoutPopupForm").style.display = "none";
            document.getElementById("logoutPopupOverlay").style.display = "none";
        }

        function confirmLogout() {
            window.location.href = 'logout';
        }

        document.querySelectorAll('.tab').forEach(button => {
            button.addEventListener('click', () => {
                const tabContents = document.querySelectorAll('.tab-content');
                const target = button.getAttribute('data-tab');
                
                tabContents.forEach(content => {
                    if (content.id === target) {
                        content.classList.add('active');
                    } else {
                        content.classList.remove('active');
                    }
                });

                document.querySelectorAll('.tab').forEach(btn => {
                    btn.classList.remove('active');
                });

                button.classList.add('active');
            });
        });
        
    </script>
</body>
</html>
