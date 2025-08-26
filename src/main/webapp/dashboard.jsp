<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.google.gson.JsonObject"%>
<%
    Gson gsonObj = new Gson();
    Map<Object, Object> map = null;
    List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();

    map = new HashMap<Object, Object>();
    map.put("x", 0);
    map.put("y", 1.293E-03);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 10);
    map.put("y", 3.982E-04);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 20);
    map.put("y", 8.269E-05);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 30);
    map.put("y", 1.554E-05);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 40);
    map.put("y", 3.148E-06);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 50);
    map.put("y", 8.280E-07);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 60);
    map.put("y", 2.328E-07);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 70);
    map.put("y", 5.761E-08);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 80);
    map.put("y", 1.296E-08);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 90);
    map.put("y", 2.702E-09);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 100);
    map.put("y", 4.535E-10);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 110);
    map.put("y", 6.809E-11);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 120);
    map.put("y", 1.604E-11);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 130);
    map.put("y", 6.210E-12);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 140);
    map.put("y", 3.143E-12);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 150);
    map.put("y", 1.825E-12);
    list.add(map);

    String dataPoints1 = gsonObj.toJson(list);

    list = new ArrayList<Map<Object, Object>>();
    map = new HashMap<Object, Object>();
    map.put("x", 0);
    map.put("y", 272.1);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 10);
    map.put("y", 214.9);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 20);
    map.put("y", 206.7);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 30);
    map.put("y", 216.5);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 40);
    map.put("y", 250.6);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 50);
    map.put("y", 258.5);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 60);
    map.put("y", 238.1);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 70);
    map.put("y", 221.7);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 80);
    map.put("y", 210.2);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 90);
    map.put("y", 189);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 100);
    map.put("y", 182.3);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 110);
    map.put("y", 255.4);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 120);
    map.put("y", 400.2);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 130);
    map.put("y", 534.3);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 140);
    map.put("y", 630.4);
    list.add(map);
    map = new HashMap<Object, Object>();
    map.put("x", 150);
    map.put("y", 700.5);
    list.add(map);

    String dataPoints2 = gsonObj.toJson(list);
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                background-color: #f9f9f9;
                display: flex; /* ✅ Đặt flex để sidebar và content nằm ngang */
            }

            /* Sidebar */
            .sidebar {
                width: 240px;
                height: 100vh;
                position: fixed;
                top: 0;
                left: 0;
            }

            /* Nội dung */
            .main-content {
                flex: 1; /* ✅ Chiếm phần còn lại */
                margin-left: 240px; /* Tránh đè lên sidebar */
                padding: 20px;
            }
        </style>
    </head>
    <body>
        <%@ include file="left-sidebar.jsp" %>

        <div class="main-content">
            <div class="container py-4">
                <h2 class="mb-4">📊 Statistic</h2>

                <div class="row g-3">
                    <div class="col-12 col-md-4">
                        <div class="stat-card p-4 bg-white">
                            <div class="stat-title mb-1">Total User</div>
                            <div class="stat-value">
                                <c:out value="${userCount}" default="0" />
                            </div>
                        </div>
                    </div>

                    <div class="col-12 col-md-4">
                        <div class="stat-card p-4 bg-white">
                            <div class="stat-title mb-1">Total Product</div>
                            <div class="stat-value">
                                <c:out value="${productCount}" default="0" />
                            </div>
                        </div>
                    </div>

                    <div class="col-12 col-md-4">
                        <div class="stat-card p-4 bg-white">
                            <div class="stat-title mb-1">Revenue Today</div>
                            <div class="stat-value">
                                <c:out value="${revenueADay}" default="0₫" />₫
                            </div>
                        </div>
                    </div>
                </div>

                <div>
                    <div id="chartContainer" style="height: 300px; width: 40%;"></div>
                </div>
            </div>
        </div>
        <!-- Bootstrap JS (tùy chọn) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.canvasjs.com/canvasjs.min.js"></script>
        <script type="text/javascript">
            window.onload = function () {
                var dataPoints = <%= request.getAttribute("weekRevenueData")%>;
                // ví dụ: [{label:"Mon", y: 300000},{label:"Tue", y:0},...]

                var chart = new CanvasJS.Chart("chartContainer", {
                    animationEnabled: true,
                    theme: "light2",
                    title: {text: "Week Revenue"},
                    axisY: {title: "Money (VND)"},
                    data: [{
                            type: "column",
                            showInLegend: false,
                            dataPoints: dataPoints
                        }]
                });
                chart.render();
            }
        </script>
    </body>
</html>
