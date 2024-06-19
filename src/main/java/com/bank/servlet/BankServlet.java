package com.bank.servlet;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.exception.BankException;

import javax.servlet.RequestDispatcher;

public class BankServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestHandlerServlet handler = new RequestHandlerServlet();
		System.out.println("\nIn GET METHOD");
		System.out.println(request.getRequestURI());
		switch (request.getRequestURI()){
		
		case "/WeBank/app/login":
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
			dispatcher.forward(request, response);			
			break;
			 
		case "/WeBank/app/profile":
			try {
				System.out.println("profile");
				handler.getProfile(request, response);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/profile.jsp");
			dispatcher.forward(request, response);			
			break;
		
		case "/WeBank/app/customer":
			try {
				System.out.println("cust");
				handler.setCustomerDetails(request);
				handler.setBranchList(request);
				System.out.println("cus finish");
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/customer.jsp");
			dispatcher.forward(request, response);			
			break;
			
			
		case "/WeBank/app/employee":
			try {
				System.out.println("emp");
				handler.setEmployeeDetails(request);
				handler.setBranchList(request);
				System.out.println("emp finish");
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/employee.jsp");
			dispatcher.forward(request, response);			
			break;
			
		case "/WeBank/app/branch":
			try {
				System.out.println("hi");
				handler.setBranchDetailsPageWise(request);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/branch.jsp");
			dispatcher.forward(request, response);			
			break;
			
		case "/WeBank/app/account":
			try {
				handler.setAccountDetailsPageWise(request);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/account.jsp");
			dispatcher.forward(request, response);			
			break;
			
		case "/WeBank/app/delete-branch":
			   try {
				handler.deleteBranch(request);
			} catch (BankException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   response.sendRedirect("/WeBank/app/branch");
			   break;
			   
		case "/WeBank/app/delete-customer":
			try {
				handler.deleteCustomer(request);
			} catch (BankException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   response.sendRedirect("/WeBank/app/customer");
			break;
			
		case"/WeBank/app/delete-employee":
			   try {
				handler.deleteEmployee(request);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   response.sendRedirect("/WeBank/app/employee");
			   break;
			   
		case"/WeBank/app/delete-account":
			   try {
				handler.deleteAccount(request);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   response.sendRedirect("/WeBank/app/account");
			   break;
			
		
			
		case "/WeBank/app/transaction":
			try {
				handler.setTransactionDetails(request);
			} catch (BankException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispatcher = request.getRequestDispatcher("/jsp/transaction.jsp");
			dispatcher.forward(request, response);			
			break;
			
		case "/WeBank/app/statement":
            try {
                handler.setTransactionStatements(request);
            } catch (BankException e) {
                e.printStackTrace();
            }
            dispatcher = request.getRequestDispatcher("/jsp/statement.jsp");
            dispatcher.forward(request, response);
            break;
            
		case "/WeBank/app/logout":
			request.getSession().removeAttribute("username");
			request.getSession().invalidate();
	        System.out.println(request.getSession(false));
			response.sendRedirect("/WeBank/app/login");
			break;
			
			default:{
				response.sendError(404);
				break;
			}
		}
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)  throws ServletException, IOException {
	   RequestHandlerServlet handler = new RequestHandlerServlet();
	   System.out.println("\nIN POST METHOD");
	   try {
	
	
		   switch (request.getPathInfo()){
	   
			   case "/login":{
				   handler.checkPassword(request,response);	
				   System.out.println("login Entry");
				   break;
			   }
			   
			   case "/add-branch":
				   handler.addBranch(request);
				   response.sendRedirect("/WeBank/app/branch");
				   break;
				   
			   case "/edit-branch":
				   handler.editBranch(request);
				   response.sendRedirect("/WeBank/app/branch");
				   break;
				   
			   case "/add-customer":
				   handler.addCustomer(request);
				   response.sendRedirect("/WeBank/app/customer");
				   break;
				   
			   case "/edit-customer":
				   handler.updateCustomer(request);
				   response.sendRedirect("/WeBank/app/customer");
				   break;
				   
			   case"/add-employee":
				   handler.addEmployee(request);
				   response.sendRedirect("/WeBank/app/employee");
				   break;
				   
			   case"/edit-employee":
				   handler.updateEmployee(request);
				   response.sendRedirect("/WeBank/app/employee");
				   break;
				   
			   case"/add-account":
				   handler.addAccount(request);
				   response.sendRedirect("/WeBank/app/account");
				   break;
				   
			   case"/edit-account":
				   handler.editAccount(request);
				   response.sendRedirect("/WeBank/app/account");
				   break;
			   
				   
			   case"/transaction":
				  
				   boolean transactionSuccess = handler.processTransaction(request);

			        if (transactionSuccess) {
			            request.setAttribute("transactionStatus", "success");
			        } else {
			            request.setAttribute("transactionStatus", "failure");
			        }
			        request.getRequestDispatcher("/jsp/transaction.jsp").forward(request, response);
				   break;
				   
			   case"/account":
				   
				   break;
			   default:
				   System.out.println("...........");
				   break;
	   }
	}catch (BankException e){
		 request.setAttribute("error", e.getMessage());
		 System.out.println("catch srr");
		 e.printStackTrace();
         RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
         dispatcher.forward(request, response);
	   } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
