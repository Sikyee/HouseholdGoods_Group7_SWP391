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
                            <div class="stat-title mb-1"><i class="fa fa-users" aria-hidden="true"></i> Total User</div>
                            <div class="stat-value">
                                <c:out value="${userCount}" default="0" />
                            </div>
                        </div>
                    </div>

                    <div class="col-12 col-md-4">
                        <div class="stat-card p-4 bg-white">
                            <div class="stat-title mb-1"><i class="fa fa-archive" aria-hidden="true"></i> Total Product</div>
                            <div class="stat-value">
                                <c:out value="${productCount}" default="0" />
                            </div>
                        </div>
                    </div>

                    <div class="col-12 col-md-4">
                        <div class="stat-card p-4 bg-white">
                            <div class="stat-title mb-1"><i class="fa fa-line-chart" aria-hidden="true"></i> Revenue Today</div>
                            <div class="stat-value">
                                <c:out value="${revenueADay}" default="0₫" />₫
                            </div>
                        </div>
                    </div>

                    <!-- Bộ lọc thời gian -->
                    <div class="card p-3 my-3">
                        <form class="row g-3 align-items-end" action="dashboard" method="get">
                            <div class="col-sm-4">
                                <label class="form-label">Start</label>
                                <input type="date" class="form-control" name="start" value="${start != null ? start : ''}">
                            </div>
                            <div class="col-sm-4">
                                <label class="form-label">End</label>
                                <input type="date" class="form-control" name="end" value="${end != null ? end : ''}">
                            </div>
                            <div class="col-sm-2">
                                <label class="form-label">Status</label>
                                <select class="form-select" name="status">
                                    <option value="5" ${status == 5 ? 'selected' : ''}>Completed</option>
                                    <option value="1" ${status == 1 ? 'selected' : ''}>Pending</option>
                                    <option value="2" ${status == 2 ? 'selected' : ''}>Proccessing</option>
                                    <option value="3" ${status == 3 ? 'selected' : ''}>Shipping</option>
                                    <option value="4" ${status == 4 ? 'selected' : ''}>Delivered</option>
                                    <!-- Thêm nếu bạn có nhiều trạng thái hơn -->
                                </select>
                            </div>
                            <div class="col-sm-2">
                                <button class="btn btn-primary w-100" type="submit">Filter</button>
                            </div>
                            <!--                            <div class="col-12">
                                                            <small class="text-muted">* Revenue <b>orderStatusID = ${status}</b> trong khoảng thời gian đã chọn.</small>
                                                        </div>-->
                        </form>
                    </div>

                    <div class="row g-3">
                        <div class="col-12 col-lg-7">
                            <div class="bg-white p-3">
                                <h5 class="mb-3">Weekly Revenue</h5>
                                <div id="weekChart" style="height: 360px;"></div>
                            </div>
                        </div>
                        <div class="col-12 col-lg-5">
                            <div class="bg-white p-3">
                                <h5 class="mb-3">Orders by Status</h5>
                                <div id="pieChart" style="height: 360px;"></div>
                            </div>
                        </div>
                    </div>

                    <div class="bg-white p-3 my-4">
                        <h5 class="mb-3">Monthly Revenue</h5>
                        <div id="monthlyChart" style="height: 360px;"></div>
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
        <script>
            window.onload = function () {
                var weekData = <%= request.getAttribute("weekRevenueData")%> || [];
                var monthlyData = <%= request.getAttribute("monthlyRevenueData")%> || [];
                var pieData = <%= request.getAttribute("orderStatusPieData")%> || [];

                // Tuần
                new CanvasJS.Chart("weekChart", {
                    animationEnabled: true, theme: "light2",
                    axisY: {title: "Money (VND)"},
                    data: [{type: "column", dataPoints: weekData}]
                }).render();

                // Tháng
                new CanvasJS.Chart("monthlyChart", {
                    animationEnabled: true, theme: "light2",
                    axisY: {title: "Money (VND)"},
                    data: [{type: "column", dataPoints: monthlyData}]
                }).render();

                // Pie
                new CanvasJS.Chart("pieChart", {
                    animationEnabled: true, theme: "light2",
                    legend: {verticalAlign: "center", horizontalAlign: "right"},
                    data: [{
                            type: "pie",
                            showInLegend: true,
                            legendText: "{name}",
                            indexLabel: "{name} - {y}",
                            dataPoints: pieData
                        }]
                }).render();
            }
        </script>
    </body>
</html>
