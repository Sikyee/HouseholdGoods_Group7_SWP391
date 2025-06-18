<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User, java.util.List, Model.Address" %>
<%
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) request.getAttribute("user");
    List<Address> addressList = (List<Address>) request.getAttribute("addressList");
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");
%>

<html>
<head>
    <title>User Profile</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        h2, h3 {
            color: #333;
        }

        form {
            margin-bottom: 30px;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            background-color: #f9f9f9;
        }

        label {
            display: block;
            margin-bottom: 12px;
        }

        input[type="text"],
        input[type="email"],
        input[type="date"],
        select {
            width: 300px;
            padding: 8px;
            margin-top: 4px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        button {
            padding: 8px 16px;
            background-color: #1976d2;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
        }

        button:hover {
            background-color: #1565c0;
        }

        .alert {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
            color: white;
        }

        .alert-success {
            background-color: #4CAF50;
        }

        .alert-error {
            background-color: #f44336;
        }

        ul.address-list {
            list-style-type: none;
            padding-left: 0;
        }

        ul.address-list li {
            background: #f0f0f0;
            margin-bottom: 10px;
            padding: 10px;
            border-radius: 6px;
        }
    </style>
</head>
<body>

    <h2>User Profile</h2>

    <!-- Display success/error message -->
    <%
        if (message != null) {
    %>
        <div class="alert <%= "success".equals(messageType) ? "alert-success" : "alert-error" %>">
            <%= message %>
        </div>
    <%
        }
    %>

    <!-- Profile Update Form -->
    <form method="post" action="profile">
        <label>Full Name:
            <input type="text" name="fullName" value="<%= user != null ? user.getFullName() : "" %>" required />
        </label>

        <label>Email:
            <input type="email" name="email" value="<%= user != null ? user.getEmail() : "" %>" required />
        </label>

        <label>Phone:
            <input type="text" name="phone" value="<%= user != null ? user.getPhone() : "" %>" required />
        </label>

        <label>Date of Birth:
            <input type="date" name="dob" value="<%= (user != null && user.getDob() != null) ? user.getDob().toString() : "" %>" />
        </label>

        <label>Gender:
            <select name="gender">
                <option value="">-- Select --</option>
                <option value="Male" <%= user != null && "Male".equals(user.getGender()) ? "selected" : "" %>>Male</option>
                <option value="Female" <%= user != null && "Female".equals(user.getGender()) ? "selected" : "" %>>Female</option>
                <option value="Other" <%= user != null && "Other".equals(user.getGender()) ? "selected" : "" %>>Other</option>
            </select>
        </label>

        <button type="submit">Update Profile</button>
    </form>

    <!-- Display list of user's addresses -->
    <h3>Your Addresses</h3>
    <ul class="address-list">
        <%
            if (addressList != null && !addressList.isEmpty()) {
                for (Address addr : addressList) {
        %>
                    <li>
                        <%= addr.getAddressDetail() %>
                        <%
                            if (addr.isDefault()) {
                        %>
                            <strong> (Default)</strong>
                        <%
                            }
                        %>
                    </li>
        <%
                }
            } else {
        %>
            <li>No addresses found.</li>
        <%
            }
        %>
    </ul>

    <a href="add-address"><button>Add New Address</button></a>

</body>
</html>
