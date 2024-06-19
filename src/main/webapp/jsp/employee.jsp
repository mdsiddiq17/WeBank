<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.bank.model.Employee" %>
<%@ page import="com.bank.model.Branch" %>
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
    <title>Employee Details</title>
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
            <h1>List of Employees</h1>
            <div class="user-info">
                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
                <span><%= session.getAttribute("username") %></span>
            </div>
        </div>
        <div class="search-bar">
            <input type="text" id="searchInput" placeholder="Search Employee..." onkeyup="searchEmployees()">
            <button onclick="openAddForm()">Add New Employee</button>
        </div>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="success-message"><%= request.getAttribute("successMessage") %></p>
        <% } %>
        <div class="table-container">
            <table id="employeeTable">
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Mobile</th>
                        <th>Address</th>
                        <th>Branch</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                        List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                        Map<Integer, String> branchMap = new HashMap<>();
                        if (branches != null) {
                            for (Branch branch : branches) {
                                branchMap.put(branch.getBranchId(), branch.getBranchName());
                            }
                        }
                        if (employees != null) {
                            for (Employee employee : employees) {
                    %>
                    <tr>
                        <td><%= employee.getUserId() %></td>
                        <td><%= employee.getName() %></td>
                        <td><%= employee.getEmail() %></td>
                        <td><%= employee.getMobile() %></td>
                        <td><%= employee.getAddress() %></td>
                        <td><%= branchMap.get(employee.getBranchId()) %></td>
                        <td>
                            <div class="table-actions">
                                <button onclick="openEditForm(<%= employee.getUserId() %>, '<%= employee.getName() %>', '<%= employee.getEmail() %>', '<%= employee.getMobile() %>', '<%= employee.getAddress() %>', <%= employee.getBranchId() %>)">
                                    <img src="<%= request.getContextPath() %>/images/edit.png" alt="Edit">
                                </button>
                                <button onclick="openDeleteForm(<%= employee.getUserId() %>)">
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
            <form method="get" action="employee">
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("previousPage") %>">&larr;</button>
                <span>Page <%= request.getAttribute("currentPage") %> of <%= request.getAttribute("totalPages") %></span>
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("nextPage") %>">&rarr;</button>
            </form>
        </div>
    </div>
    
    <!-- Add Popup Form -->
<div class="popup-overlay" id="addPopupOverlay"></div>
<div class="popup-form" id="addPopupForm">
    <h2>Add New Employee</h2>
    <form id="addEmployeeForm" action="add-employee" method="post">
        <input type="text" name="name" placeholder="Name" pattern="[A-Za-z ]+" required>
        <input type="email" name="email" placeholder="Email" required>
        <input type="text" name="mobile" placeholder="Mobile" pattern="[6-9]{1}[0-9]{9}" maxlength="10" required>
        <input type="text" name="address" placeholder="Address" required>
        <input type="date" name="birthday" placeholder="Birthday" required>
        <select name="gender" required>
            <option value="male">Male</option>
            <option value="female">Female</option>
        </select>
        <select name="branchId" required>
            <option value="" disabled selected>Select Branch</option>
            <% 
                if (branches != null) {
                    for (Branch branch : branches) {
            %>
            <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
            <% 
                    }
                }
            %>
        </select>
        <input type="password" name="password" placeholder="Temporary Password" required>
        <button type="submit">Add</button>
        <button type="button" onclick="closeAddForm()">Cancel</button>
    </form>
</div>

<!-- Edit Popup Form -->
<div class="popup-overlay" id="editPopupOverlay"></div>
<div class="popup-form" id="editPopupForm">
    <h2>Edit Employee</h2>
    <form id="editEmployeeForm" action="edit-employee" method="post">
        <input type="hidden" name="employeeId" id="editEmployeeId">
        <input type="text" name="name" id="editName" placeholder="Name" pattern="[A-Za-z ]+" required>
        <input type="email" name="email" id="editEmail" placeholder="Email" required>
        <input type="text" name="mobile" id="editMobile" placeholder="Mobile" pattern="[6-9]{1}[0-9]{9}" maxlength="10"required>
        <input type="text" name="address" id="editAddress" placeholder="Address" required>
        <select name="branchId" id="editBranchId" required>
            <option value="" disabled selected>Select Branch</option>
            <% 
                if (branches != null) {
                    for (Branch branch : branches) {
            %>
            <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
            <% 
                    }
                }
            %>
        </select>
        <button type="submit">Update</button>
        <button type="button" onclick="closeEditForm()">Cancel</button>
    </form>
</div>

<!-- Delete Popup Form -->
<div class="popup-overlay" id="deletePopupOverlay"></div>
<div class="popup-form" id="deletePopupForm">
    <h2>Delete Employee</h2>
    <p>Are you sure you want to delete this employee?</p>
    <form id="deleteEmployeeForm" action="delete-employee" method="get">
        <input type="hidden" name="employeeId" id="deleteEmployeeId">
        <button type="submit">Delete</button>
        <button type="button" onclick="closeDeleteForm()">Cancel</button>
    </form>
</div>

<script>
    function searchEmployees() {
        var input, filter, table, tr, td, i, txtValue;
        input = document.getElementById("searchInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("employeeTable");
        tr = table.getElementsByTagName("tr");

        for (i = 1; i < tr.length; i++) {
            tr[i].style.display = "none";
            td = tr[i].getElementsByTagName("td");
            if (td) {
                for (j = 0; j < td.length; j++) {
                    if (td[j]) {
                        txtValue = td[j].textContent || td[j].innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            tr[i].style.display = "";
                            break;
                        }
                    }
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

    function openEditForm(userId, name, email, mobile, address, branchId) {
        document.getElementById("editEmployeeId").value = userId;
        document.getElementById("editName").value = name;
        document.getElementById("editEmail").value = email;
        document.getElementById("editMobile").value = mobile;
        document.getElementById("editAddress").value = address;
        document.getElementById("editBranchId").value = branchId;
        document.getElementById("editPopupForm").style.display = "block";
        document.getElementById("editPopupOverlay").style.display = "block";
    }

    function closeEditForm() {
        document.getElementById("editPopupForm").style.display = "none";
        document.getElementById("editPopupOverlay").style.display = "none";
    }

    function openDeleteForm(employeeId) {
        document.getElementById("deleteEmployeeId").value = employeeId;
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
    