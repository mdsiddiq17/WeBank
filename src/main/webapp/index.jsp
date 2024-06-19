<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fund Flow Login</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
</head>
<body>
    <div class="container">
        <div class="left-section">
            <img src="<%= request.getContextPath() %>/images/login.png" alt="Phone Image">
            <h2>Fund Flow is personal finance, made simple.</h2>
            <p>All your accounts, cards, savings, and investments in one place.</p>
        </div>
        <div class="right-section">
            <div class="bank">
                <div class="logo"><img src="<%= request.getContextPath() %>/images/cashflow-blue.png"></div>
                <div class="name">
                    <h1>Fund Flow </h1></Fund>
                    <span>Online Banking</span></h1>
                </div>
            </div>
                <form action="/WeBank/app/login" method="get">
             
                	<p style="font-size:30px;"><strong>Welcome To Fund Flow</strong></p>
                	<img style="width:400px;" src="<%= request.getContextPath() %>/images/welcome1.png">
                    <button>Get Started</button>
                </form>
            </div>
    </div>
    <script src="<%= request.getContextPath() %>/javascript/script.js"></script>
</body>
</html>
    