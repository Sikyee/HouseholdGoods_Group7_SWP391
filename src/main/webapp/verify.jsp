<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Email Verification</title>
</head>
<body>
    <h2>Email Verification</h2>

    <!-- Verification Form -->
    <form action="${pageContext.request.contextPath}/register" method="post">
        <input type="hidden" name="action" value="verify"/>

        <label for="code">Enter Verification Code:</label><br>
        <input type="text" id="code" name="code" required><br><br>

        <input type="submit" value="Verify">
    </form>

    <!-- Show error message if any -->
    <p style="color: red;">
        <% if (request.getAttribute("error") != null) { %>
            <%= request.getAttribute("error") %>
        <% } %>
    </p>
</body>
</html>
