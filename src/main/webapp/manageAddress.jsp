<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Address" %>
<%
    Address address = (Address) request.getAttribute("editAddress");
    boolean isEdit = (address != null);
    String title = isEdit ? "Edit Address" : "Add New Address";
%>
<html>
    <head>
        <title><%= title%></title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 40px;
            }

            h2 {
                color: #333;
            }

            form {
                margin-top: 20px;
            }

            label {
                display: block;
                margin-bottom: 10px;
            }

            input[type="text"] {
                width: 300px;
                padding: 6px;
                margin-top: 4px;
            }

            input[type="checkbox"] {
                margin-left: 10px;
            }

            button {
                padding: 8px 16px;
                background-color: #1976d2;
                color: white;
                border: none;
                border-radius: 5px;
                margin-top: 10px;
                cursor: pointer;
            }

            button:hover {
                background-color: #1565c0;
            }

            .alert {
                padding: 10px;
                border-radius: 5px;
                margin: 15px 0;
                color: white;
            }

            .alert-success {
                background-color: #4CAF50;
            }

            .alert-error {
                background-color: #f44336;
            }
        </style>
    </head>
    <body>

        <h2><%= title%></h2>

        <% String msg = (String) request.getAttribute("message");
            String type = (String) request.getAttribute("messageType");
            if (msg != null) {%>
        <div class="alert <%= "success".equals(type) ? "alert-success" : "alert-error"%>">
            <%= msg%>
        </div>
        <% } %>

        <form method="post" action="manage-address">
            <% if (isEdit) {%>
            <input type="hidden" name="addressID" value="<%= address.getAddressID()%>" />
            <% }%>

            <label>
                Address Detail:
                <input type="text" name="addressDetail" required value="<%= isEdit ? address.getAddressDetail() : ""%>" />
            </label>

            <label>
                Set as Default:
                <input type="checkbox" name="isDefault" <%= isEdit && address.isDefault() ? "checked" : ""%> />
            </label>

            <button type="submit"><%= isEdit ? "Update Address" : "Add Address"%></button>
        </form>

    </body>
</html>
