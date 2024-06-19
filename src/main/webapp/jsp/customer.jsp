<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.bank.model.Customer" %>
<%@ page import="com.bank.model.Branch" %>
<%@ page import="com.bank.enums.Role" %>
<%
	
	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");//HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 	
	response.setHeader("Expires","0"); //Proxies
	if(session.getAttribute("username")==null){
		response.sendRedirect("/Webank/app/login");
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
    <title>Customer Details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    <style>
                /* Pop-up Form */
.popup-form {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); /* Shadow effect */
    z-index: 1001; /* Ensure the form appears above the overlay */
    display: none; /* Initially hidden */
    width: 400px; /* Adjust the width as needed */
}

.popup-form h2 {
    margin-top: 0;
    font-size: 24px;
    color: #333;
}

.popup-form p {
    margin-bottom: 20px;
    color: #666;
}

.popup-form input[type="text"],
.popup-form input[type="email"],
.popup-form input[type="tel"],
.popup-form input[type="date"],
.popup-form select,
.popup-form input[type="password"],
.popup-form input[type="number"] {
    width: 100%;
    padding: 10px;
    margin-bottom: 15px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

/* Button Styles */
.popup-form button[type="submit"],
.popup-form button[type="button"] {
    padding: 10px 20px;
    border-radius: 4px;
    background-color: #007bff; /* Blue color */
    font-size:16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.popup-form button[type="submit"]:hover,
.popup-form button[type="button"]:hover {
    background-color: #0056b3; /* Darker shade of blue on hover */
}

.popup-form button[type="button"] {
    color:black;
    background-color: white; /* Red color */
    border-color:black;
    baroder-type:solid;
    border-width:0.1px;
}

.popup-form button[type="button"]:hover {
    background-color: #d1d9e0; /* Darker shade of red on hover */
}

.popup-form button[type="submit"],
.popup-form button[type="button"] {
    width: calc(50% - 5px); /* Adjust width to fit in the same line with margin */
    display: inline-block;
    margin-top: 10px; /* Add margin between buttons */
}
/* Popup Overlay Styles */
.popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent black background */
    z-index: 1000; /* Ensure the overlay appears above other content */
    display: none; /* Hide by default */
}

.popup-active .popup-overlay {
    display: block; /* Show overlay when popup is active */
}

        
    </style>
</head>
<body>
    <jsp:include page="nav.jsp" />
<div class="main-content">
    <div class="header">
        <h1>List of Customers</h1>
        <div class="user-info">
            <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
            <span><%= session.getAttribute("username") %></span>
        </div>
    </div>
    <div class="search-bar">
        <input type="text" id="searchInput" placeholder="Search Customer..." onkeyup="searchCustomers()">
        <button onclick="openAddForm()">Add New Customer</button>
    </div>
    <% if (request.getAttribute("successMessage") != null) { %>
        <p class="success-message"><%= request.getAttribute("successMessage") %></p>
    <% } %>
    <div class="table-container">
        <table id="customerTable">
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Mobile</th>
                    <th>Address</th>
                    <th>Aadhaar</th>
                    <th>PAN</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                	List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                	Role role = (Role) session.getAttribute("role");
                	Branch userBranch = (Branch) session.getAttribute("branch");
                    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
                    if (customers != null) {
                        for (Customer customer : customers) {
                %>
                <tr>
                    <td><%= customer.getUserId() %></td>
                    <td><%= customer.getName() %></td>
                    <td><%= customer.getEmail() %></td>
                    <td><%= customer.getMobile() %></td>
                    <td><%= customer.getAddress() %></td>
                    <td><%= customer.getAadhaar() %></td>
                    <td><%= customer.getPan() %></td>
                    <td>
                        <div class="table-actions">
                            <button onclick="openEditForm(<%= customer.getUserId() %>, '<%= customer.getName() %>', '<%= customer.getEmail() %>', '<%= customer.getMobile() %>', '<%= customer.getAddress() %>', '<%= customer.getAadhaar() %>', '<%= customer.getPan() %>')">
                                <img src="<%= request.getContextPath() %>/images/edit.png" alt="Edit">
                            </button>
                            <button onclick="openDeleteForm(<%= customer.getUserId() %>)">
                                <img src="<%= request.getContextPath() %>/images/delete.png" alt="Delete">
                            </button>
                        </div>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
    <div class="pagination">
        <form method="get" action="customer">
            <button type="submit" name="page" value="<%= (Integer) request.getAttribute("previousPage") %>">&larr;</button>
            <span>Page <%= request.getAttribute("currentPage") %> of <%= request.getAttribute("totalPages") %></span>
            <button type="submit" name="page" value="<%= (Integer) request.getAttribute("nextPage") %>">&rarr;</button>
        </form>
    </div>
</div>

<!-- Add Popup Form -->
<div class="popup-overlay" id="addPopupOverlay"></div>
<div class="popup-form" id="addPopupForm">
    <h2>Add New Customer</h2>
    <form id="addCustomerForm" action="add-customer" method="post">
        <input type="text" name="name" placeholder="Name" pattern="[A-Za-z ]+" required>
        <input type="email" name="email" placeholder="Email" required>
        <input type="number" name="mobile" placeholder="Mobile" pattern="[6-9]{1}[0-9]{9}" maxlength="10" min="6000000000" max="9999999999" required>
        <input type="text" name="address" placeholder="Address" pattern="{1,100}" required>
        <input type="text" name="aadhaar" placeholder="Aadhaar" pattern="[0-9]{12}" maxlength="12" required>
        <input type="text" name="pan" placeholder="PAN" pattern="[A-Z]{5}[0-9]{4}[A-Z]{1}" maxlength="10" required>
        <input type="date" name="birthday" placeholder="Birthday" min="1920-01-01" max="2024-01-01" required>
        <select name="gender" required>
            <option value="male">MALE</option>
            <option value="female">FEMALE</option>
        </select>
        <select name="accountType" required>
            <option value="" disabled selected>Select Account Type</option>
            <option value="savings">SAVINGS</option>
            <option value="current">CURRENT</option>
            <option value="salary">SALARY</option>
        </select>
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
        <input type="number" name="initialBalance" placeholder="Initial Balance" min="0" max="100000" required>
        <input type="password" name="password" placeholder="Temporary Password" required>
        <button type="submit">Add</button>
        <button type="button" onclick="closeAddForm()">Cancel</button>
    </form>
</div>

<!-- Edit Popup Form -->
<div class="popup-overlay" id="editPopupOverlay"></div>
<div class="popup-form" id="editPopupForm">
    <h2>Edit Customer</h2>
    <form id="editCustomerForm" action="edit-customer" method="post">
        <input type="hidden" name="userId" id="editUserId">
        <input type="text" name="name" id="editName" placeholder="Name" required>
        <input type="email" name="email" id="editEmail" placeholder="Email" required>
        <input type="text" name="mobile" id="editMobile" placeholder="Mobile" required>
        <input type="text" name="address" id="editAddress" placeholder="Address" required>
        <input type="text" name="aadhaar" id="editAadhaar" placeholder="Aadhaar" required>
        <input type="text" name="pan" id="editPan" placeholder="PAN" required>
        <button type="submit">Update</button>
        <button type="button" onclick="closeEditForm()">Cancel</button>
    </form>
</div>

<!-- Delete Popup Form -->
<div class="popup-overlay" id="deletePopupOverlay"></div>
<div class="popup-form" id="deletePopupForm">
    <h2>Delete Customer</h2>
    <p>Are you sure you want to delete this customer?</p>
    <form id="deleteCustomerForm" action="delete-customer" method="get">
        <input type="hidden" name="userId" id="deleteUserId">
        <button type="submit">Delete</button>
        <button type="button" onclick="closeDeleteForm()">Cancel</button>
    </form>
</div>

<script>
    function searchCustomers() {
        var input, filter, table, tr, td, i, txtValue;
        input = document.getElementById("searchInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("customerTable");
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

    function openEditForm(userId, name, email, mobile, address, aadhaar, pan) {
        document.getElementById("editUserId").value = userId;
        document.getElementById("editName").value = name;
        document.getElementById("editEmail").value = email;
        document.getElementById("editMobile").value = mobile;
        document.getElementById("editAddress").value = address;
        document.getElementById("editAadhaar").value = aadhaar;
        document.getElementById("editPan").value = pan;
        document.getElementById("editPopupForm").style.display = "block";
        document.getElementById("editPopupOverlay").style.display = "block";
    }

    function closeEditForm() {
        document.getElementById("editPopupForm").style.display = "none";
        document.getElementById("editPopupOverlay").style.display = "none";
    }

    function openDeleteForm(userId) {
        document.getElementById("deleteUserId").value = userId;
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

