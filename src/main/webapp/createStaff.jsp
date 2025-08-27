<%-- 
    Document   : createStaff
    Created on : Aug 19, 2025, 9:55:07 PM
    Author     : pts03
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Create Staff</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: #f4f6f9;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .card-header {
            background: #0d6efd;
            color: white;
            border-top-left-radius: 15px;
            border-top-right-radius: 15px;
        }
        .btn-primary {
            border-radius: 8px;
        }
        .btn-secondary {
            border-radius: 8px;
        }
    </style>
    </head>
    <body class="container mt-5">
       <div class="container d-flex justify-content-center align-items-center vh-100">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header text-center">
                    <h4>Create Staff Account</h4>
                </div>
                <div class="card-body">
                    <form action="User?action=create&type=staff" method="post">
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" name="fullName" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" name="username" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Phone</label>
                            <input type="text" name="phone" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Gender</label>
                            <select name="gender" class="form-select">
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Date of Birth</label>
                            <input type="date" name="dob" class="form-control">
                        </div>
                        <div class="d-flex justify-content-between">
                            <a href="User?action=list&type=staff" class="btn btn-secondary">Cancel</a>
                            <button type="submit" class="btn btn-primary">Create</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
         <!-- Hiển thị popup -->
   <%
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage   = (String) request.getAttribute("errorMessage");

    if (successMessage != null || errorMessage != null) {
        String type   = (successMessage != null) ? "success" : "error";
        String message = (successMessage != null) ? successMessage : errorMessage;
%>
<style>
    #overlay {
        position: fixed;
        top: 0; left: 0;
        width: 100%; height: 100%;
        background: rgba(0,0,0,0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    }

    #messageBox {
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.25);
        overflow: hidden;
        animation: fadeIn 0.3s ease;
        min-width: 320px;
    }

    #messageBox table {
        width: 100%;
        border-collapse: collapse;
        text-align: center;
    }

    /* Success header */
    #messageBox th.success {
        background: #28a745;
        color: #fff;
        padding: 12px;
        font-size: 20px;
    }

    /* Error header */
    #messageBox th.error {
        background: #dc3545;
        color: #fff;
        padding: 12px;
        font-size: 20px;
    }

    #messageBox td {
        padding: 16px;
        font-size: 16px;
        color: #333;
    }

    #messageBox button {
        padding: 8px 20px;
        margin-top: 10px;
        border: none;
        border-radius: 6px;
        font-size: 16px;
        cursor: pointer;
        transition: background 0.3s ease;
        color: #fff;
    }

    /* Nút success */
    #messageBox button.success {
        background: #28a745;
    }
    #messageBox button.success:hover {
        background: #218838;
    }

    /* Nút error */
    #messageBox button.error {
        background: #dc3545;
    }
    #messageBox button.error:hover {
        background: #c82333;
    }

    @keyframes fadeIn {
        from { transform: scale(0.8); opacity: 0; }
        to { transform: scale(1); opacity: 1; }
    }
</style>

<div id="overlay">
    <div id="messageBox">
        <table>
            <tr>
                <th class="<%= type %>">Thông báo</th>
            </tr>
            <tr>
                <td><%= message %></td>
            </tr>
            <tr>
                <td>
                    <button class="<%= type %>" onclick="document.getElementById('overlay').style.display='none'">
                        OK
                    </button>
                </td>
            </tr>
        </table>
    </div>
</div>
<% } %>


    </body>
</html>
