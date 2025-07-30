<%-- 
    Document   : viewActor
    Created on : Jul 12, 2025, 8:49:32 PM
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
        <title>View Actor</title>
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

            .table th, .table td {
                vertical-align: middle;
            }

            .btn-back {
                background-color: #000;
                color: #fff;
            }

            .btn-back:hover {
                background-color: #333;
            }
        </style>
    </head>
    <body>

        <div class="container py-5">
            <div class="card p-4">
                <div class="card-body">
                    <% if (actor == null) { %>
                    <h4 class="text-danger"> Actor not found!</h4>
                    <a href="Actor" class="btn btn-back mt-3">Back</a>
                    <% } else { %>
                    <h2 class="card-title mb-4">View Actor Detail</h2>

                    <table class="table table-bordered">
                        <tr><th>ID</th><td><%= actor.getActorID() %></td></tr>
                        <tr><th>Full Name</th><td><%= actor.getFullName() %></td></tr>
                        <tr><th>Email</th><td><%= actor.getEmail() %></td></tr>
                        <tr><th>Phone</th><td><%= actor.getPhone() != null ? actor.getPhone() : "N/A" %></td></tr>
                        <tr><th>Status</th><td><%= actor.getStatus() == 1 ? "Active" : "Banned" %></td></tr>
                    </table>

                    <a href="Actor" class="btn btn-back mt-3">← Back to Actor List</a>
                    <% } %>
                </div>
            </div>
        </div>

    </body>
</html>
