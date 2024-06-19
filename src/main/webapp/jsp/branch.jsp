<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
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
    <title>Branch Details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    <style>
        /* Popup form styles */
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
    background-color:#d1d9e0; /* Darker shade of red on hover */
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
            <h1>List of Branches</h1>
            <div class="user-info">
                <a href="profile"><img src="<%= request.getContextPath() %>/images/profile-user.png" alt="User Avatar" class="avatar"></a>
                <span><%= session.getAttribute("username") %></span>
            </div>
        </div>
        <div class="search-bar">
            <input type="text" id="searchInput" placeholder="Search Branch..." onkeyup="searchBranches()">
            <button onclick="openForm()">Add New Branch</button>
        </div>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="success-message"><%= request.getAttribute("successMessage") %></p>
        <% } %>
        <div class="table-container">
            <table id="branchTable">
                <thead>
                    <tr>
                        <th>Branch ID</th>
                        <th>Branch Name</th>
                        <th>IFSC Code</th>
                        <th>Branch Address</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                        if (branches != null) {
                            for (Branch branch : branches) {
                    %>
                    <tr>
                        <td><%= branch.getBranchId() %></td>
                        <td><%= branch.getBranchName() %></td>
                        <td><%= branch.getIfscCode() %></td>
                        <td><%= branch.getBranchAddress() %></td>
                        <td>
                            <div class="table-actions">
                                <button onclick="openEditForm(<%= branch.getBranchId() %>, '<%= branch.getBranchName() %>', '<%= branch.getIfscCode() %>', '<%= branch.getBranchAddress() %>')">
                                    <img src="<%= request.getContextPath() %>/images/edit.png" alt="Edit">
                                </button>
                                <button onclick="openDeleteForm(<%= branch.getBranchId() %>)">
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
            <form method="get" action="branch">
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("previousPage") %>">&larr;</button>
                <span>Page <%= request.getAttribute("currentPage") %> of <%= request.getAttribute("totalPages") %></span>
                <button type="submit" name="page" value="<%= (Integer) request.getAttribute("nextPage") %>">&rarr;</button>
            </form>
        </div>
    </div>

    <!-- Popup Form -->
    <div class="popup-overlay" id="popupOverlay"></div>
    <div class="popup-form" id="popupForm">
        <h2>Add New Branch</h2>
        <form id="addBranchForm" action="add-branch" method="post">
            <input type="text" name="branchName" placeholder="Branch Name" required>
            <input type="text" name="ifscCode" placeholder="IFSC Code" required>
            <input type="text" name="branchAddress" placeholder="Branch Address" required>
            <button type="submit">Add</button>
            <button type="button" onclick="closeForm()">Cancel</button>
        </form>
    </div>

    <!-- Edit Popup Form -->
    <div class="popup-overlay" id="editPopupOverlay"></div>
    <div class="popup-form" id="editPopupForm">
        <h2>Edit Branch</h2>
        <form id="editBranchForm" action="edit-branch" method="post">
            <input type="hidden" name="branchId" id="editBranchId">
            <input type="text" name="branchName" id="editBranchName" placeholder="Branch Name" required>
            <input type="text" name="ifscCode" id="editIfscCode" placeholder="IFSC Code" required>
            <input type="text" name="branchAddress" id="editBranchAddress" placeholder="Branch Address" required>
            <button type="submit">Update</button>
            <button type="button" onclick="closeEditForm()">Cancel</button>
        </form>
    </div>

    <!-- Delete Popup Form -->
    <div class="popup-overlay" id="deletePopupOverlay"></div>
    <div class="popup-form" id="deletePopupForm">
        <h2>Delete Branch</h2>
        <p>Are you sure you want to delete this branch?</p>
        <form id="deleteBranchForm" action="delete-branch" method="get">
            <input type="hidden" name="branchId" id="deleteBranchId">
            <button type="submit">Delete</button>
            <button type="button" onclick="closeDeleteForm()">Cancel</button>
        </form>
    </div>

    <script>
        function searchBranches() {
            var input, filter, table, tr, td, i, txtValue;
            input = document.getElementById("searchInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("branchTable");
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

        function openEditForm(branchId, branchName, ifscCode, branchAddress) {
            document.getElementById("editBranchId").value = branchId;
            document.getElementById("editBranchName").value = branchName;
            document.getElementById("editIfscCode").value = ifscCode;
            document.getElementById("editBranchAddress").value = branchAddress;
            document.getElementById("editPopupForm").style.display = "block";
            document.getElementById("editPopupOverlay").style.display = "block";
        }

        function closeEditForm() {
            document.getElementById("editPopupForm").style.display = "none";
            document.getElementById("editPopupOverlay").style.display = "none";
        }

        function openForm() {
            document.getElementById("popupForm").style.display = "block";
            document.getElementById("popupOverlay").style.display = "block";
        }

        function closeForm() {
            document.getElementById("popupForm").style.display = "none";
            document.getElementById("popupOverlay").style.display = "none";
        }

        function openDeleteForm(branchId) {
            document.getElementById("deleteBranchId").value = branchId;
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