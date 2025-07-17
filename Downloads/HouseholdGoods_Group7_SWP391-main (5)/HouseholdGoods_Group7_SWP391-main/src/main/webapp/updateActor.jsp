<%-- 
    Document   : updateActor
    Created on : Jul 12, 2025, 8:49:06 PM
    Author     : Le Quang Giang - CE182527 
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Actor" %>
<%
    Actor actor = (Actor) request.getAttribute("actor");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Update Actor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #ffffff;
            color: #000000;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .card {
            background-color: #f8f9fa;
            border-radius: 12px;
            border: 1px solid #dee2e6;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .card-title {
            font-size: 1.8rem;
            font-weight: bold;
        }

        .btn-dark {
            background-color: #000;
            color: #fff;
        }

        .btn-dark:hover {
            background-color: #333;
        }

        label {
            font-weight: 500;
        }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="card p-4">
        <div class="card-body">
            <% if (actor == null) { %>
            <h4 class="text-danger"> Actor not found!</h4>
            <a href="Actor" class="btn btn-dark mt-3">Back</a>
            <% } else { %>
            <h2 class="card-title mb-4">Update Actor Information</h2>

            <form action="Actor?action=updateSubmit" method="post">
                <input type="hidden" name="actorID" value="<%= actor.getActorID() %>">

                <div class="mb-3">
                    <label for="fullName" class="form-label">Full Name</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" required value="<%= actor.getFullName() %>">
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" required value="<%= actor.getEmail() %>">
                </div>

                <div class="mb-3">
                    <label for="phone" class="form-label">Phone</label>
                    <input type="text" class="form-control" id="phone" name="phone" value="<%= actor.getPhone() != null ? actor.getPhone() : "" %>">
                </div>

                <div class="d-flex justify-content-between mt-4">
                    <a href="Actor" class="btn btn-secondary">← Cancel</a>
                    <button type="submit" class="btn btn-dark">💾 Save Changes</button>
                </div>
            </form>
            <% } %>
        </div>
    </div>
</div>

</body>
</html>
