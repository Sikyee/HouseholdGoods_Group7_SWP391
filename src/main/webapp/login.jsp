<%-- 
    Document   : login
    Created on : Jun 17, 2025, 3:36:51 PM
    Author     : thong
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Login - Household Goods</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <style>
            body {
                background: linear-gradient(to right, #e8d4b4, #f9f3ea);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                font-family: 'Segoe UI', sans-serif;
            }

            .login-box {
                background-color: #fff8f2;
                padding: 40px 30px;
                border-radius: 12px;
                box-shadow: 0px 8px 20px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 420px;
            }

            .login-title {
                font-size: 28px;
                font-weight: bold;
                color: #6e4b3a;
                margin-bottom: 20px;
            }

            .btn-login {
                background-color: #a67c52;
                color: white;
                border: none;
            }

            .btn-login:hover {
                background-color: #8b6744;
            }

            .form-label {
                font-weight: 500;
                color: #5e412f;
            }

            .icon {
                font-size: 40px;
                color: #a67c52;
            }
        </style>
    </head>
    <body>

        <div class="login-box">
            <div class="text-center">
                <div class="icon mb-2">🛋️</div>
                <div class="login-title">Household Goods Login</div>
            </div>

            <form action="login" method="post">
                <div class="mb-3">
                    <label class="form-label">Username</label>
                    <input type="text" name="username" class="form-control" required placeholder="Enter your username"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Password</label>
                    <input type="password" name="password" class="form-control" required placeholder="Enter your password"/>
                </div>

                <div class="d-grid mb-3">
                    <button class="btn btn-login">Login</button>
                </div>

                <% String err = (String) request.getAttribute("error");
                    if (err != null) {%>
                <div class="alert alert-danger text-center"><%= err%></div>
                <% }%>

                <div class="text-center mt-3">
                    <small>Don't have an account? <a href="register.jsp">Register</a></small>
                </div>

                <div class="text-center mt-3">
                    <a href="homePage.jsp" class="btn btn-outline-secondary btn-sm">⬅ Back to Home</a>
                </div>
            </form>
        </div>

    </body>
</html>
