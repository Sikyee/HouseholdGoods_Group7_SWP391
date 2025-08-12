<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <title>User Registration</title>
        <meta charset="UTF-8">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <style>
            body {
                background: linear-gradient(to right, #fce3c3, #fff);
                font-family: 'Segoe UI', sans-serif;
                padding: 40px;
            }
            .register-box {
                background-color: #fff8f2;
                padding: 30px;
                border-radius: 12px;
                max-width: 500px;
                margin: auto;
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            }
            .register-title {
                font-size: 26px;
                font-weight: bold;
                color: #6e4b3a;
                text-align: center;
                margin-bottom: 20px;
            }
        </style>
    </head>
    <body>

        <div class="register-box">
            <div class="register-title">User Registration</div>

            <!-- Registration Form -->
            <form action="${pageContext.request.contextPath}/register" method="post">
                <input type="hidden" name="action" value="register"/>

                <div class="mb-3">
                    <label class="form-label" for="fullName">Full Name:</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" required>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="username">Username:</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="email">Email:</label>
                    <input type="email" class="form-control" id="email" name="email" required>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="password">Password:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-primary">Register</button>
                </div>
            </form>

            <!-- Display error if any -->
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
            <div class="alert alert-danger mt-3 text-center"><%= error%></div>
            <%
                }
            %>

            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-secondary btn-sm">⬅ Back to Login</a>
            </div>
        </div>

    </body>
</html>
