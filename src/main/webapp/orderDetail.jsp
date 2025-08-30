<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat, Model.*" %>

<%
    OrderInfo order = (OrderInfo) request.getAttribute("order");
    List<OrderDetail> orderDetailList = (List<OrderDetail>) request.getAttribute("orderDetailList");
    CancelReason cancelReason = (CancelReason) request.getAttribute("cancelReason");
    List<OrderStatus> orderStatusList = (List<OrderStatus>) request.getAttribute("orderStatusList");
    User user = (User) session.getAttribute("user");

    int userRole = (user != null) ? user.getRoleID() : 4;

    int statusID      = order.getOrderStatusID();
    boolean isPending = (statusID == 1);
    boolean isProcessing = (statusID == 2);
    boolean isDelivering = (statusID == 3);
    boolean isDelivered  = (statusID == 4);
    boolean isCompleted  = (statusID == 5);
    boolean isCanceled   = (statusID == 6);
    boolean isPaid       = (statusID == 7); // Paid

    // ETA khi đang Shipping: +3 ngày từ orderDate (mô phỏng)
    String etaText = "";
    if (isDelivering && order.getOrderDate() != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.getOrderDate());
        cal.add(Calendar.DATE, 3);
        Date eta = cal.getTime();
        etaText = new SimpleDateFormat("dd/MM/yyyy").format(eta);
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body { background-color: #f8f9fa; }
        .content { margin-left: 240px; padding: 2rem; }
    </style>
</head>
<body>

<%@include file="left-sidebar.jsp"  %>

<div class="content">
    <div class="container">
        <h2 class="mb-4">Order Detail - #<%= order.getOrderID() %></h2>

        <%-- Flash message --%>
        <%
            String flash = (String) session.getAttribute("flash");
            if (flash != null) {
        %>
            <div class="alert alert-info"><%= flash %></div>
        <%
                session.removeAttribute("flash");
            }
        %>

        <div class="card mb-4">
            <div class="card-body">
                <p><strong>Order Date:</strong> <%= order.getOrderDate() %></p>
                <p><strong>Full Name:</strong> <%= order.getFullName() %></p>
                <p><strong>Phone:</strong> <%= order.getPhone() %></p>
                <p><strong>Address:</strong> <%= order.getDeliveryAddress() %></p>
                <p><strong>Order Status:</strong>
                    <%
                        for (OrderStatus st : orderStatusList) {
                            if (st.getOrderStatusID() == statusID) {
                                out.print(st.getStatusName());
                                break;
                            }
                        }
                    %>
                </p>

                <% if (isDelivering) { %>
                    <div class="alert alert-warning mt-2">
                        Estimated delivery in ~ 3 days (ETA: <%= etaText %>).
                    </div>
                <% } %>

                <% if (isCanceled && cancelReason != null) { %>
                    <div class="alert alert-danger mt-3">
                        <strong>Cancel Reason:</strong> <%= cancelReason.getReason() %>
                    </div>
                <% } %>

                <%-- KHÁCH HÀNG: hủy ngay trên trang (chỉ khi Pending) --%>
                <% if (isPending && userRole == 3) { %>
                    <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#customerCancelModal">
                        Cancel Order
                    </button>

                    <!-- Modal nhập lý do hủy -->
                    <div class="modal fade" id="customerCancelModal" tabindex="-1" aria-hidden="true">
                      <div class="modal-dialog">
                        <form action="cancelOrder" method="post" class="modal-content"><%-- nếu servlet bạn map /cancel-order thì đổi action cho khớp --%>
                          <div class="modal-header">
                            <h5 class="modal-title">Cancel Order #<%= order.getOrderID() %></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
                            <div class="mb-3">
                                <label for="reason" class="form-label">Reason for Cancellation</label>
                                <textarea class="form-control" name="reason" id="reason" rows="4" required>Khách hàng yêu cầu hủy</textarea>
                            </div>
                            <div class="alert alert-warning">
                                Hủy đơn sẽ hoàn số lượng về kho ngay lập tức.
                            </div>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="submit" class="btn btn-danger">Confirm Cancel</button>
                          </div>
                        </form>
                      </div>
                    </div>
                <% } %>

                <%-- ADMIN/STAFF: nút hủy tức thì cho Pending/Paid --%>
                <% if ((userRole == 1 || userRole == 2) && (isPending || isPaid)) { %>
                    <form id="cancelNowForm" action="update-order-status" method="post" class="d-inline">
                        <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
                        <input type="hidden" name="orderStatusID" value="6"/>
                        <input type="hidden" name="cancelReason" id="cancelReasonInstant" value="Out of stock"/>
                        <button type="button" class="btn btn-danger" onclick="confirmCancelNow()">Cancel (restock)</button>
                    </form>
                <% } %>

                <%-- FORM UPDATE CHUNG --%>
                <%
                    // Cho update khi Pending/Paid/Processing (Shipping & các trạng thái khác: khóa)
                    boolean allowUpdate = (isPending || isPaid || isProcessing);
                %>
                <% if (userRole == 1 || userRole == 2) { %>
                    <% if (allowUpdate) { %>
                        <form action="update-order-status" method="post" class="mt-3" onsubmit="return beforeSubmit()">
                            <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>

                            <div class="mb-3">
                                <label for="orderStatus" class="form-label">Update Status:</label>
                                <select name="orderStatusID" id="orderStatus" class="form-select" onchange="handleStatusChange()">
                                    <option value="" disabled selected>-- Update status --</option>
                                    <%
                                        for (OrderStatus st : orderStatusList) {
                                            int id = st.getOrderStatusID();
                                            String label = st.getStatusName();
                                            boolean show = false;

                                            // Pending/Paid -> chỉ Processing (2) ở dropdown (Canceled dùng nút riêng)
                                            if ((isPending || isPaid) && id == 2) {
                                                show = true; label = "Order confirmed (Move to processing)";
                                            }

                                            // Processing -> Shipping (3) | Completed (5: customer return)
                                            if (isProcessing && (id == 3 || id == 5)) {
                                                show = true;
                                                if (id == 3) label = "Move to shipping";
                                                if (id == 5) label = "Customer returned the item (Complete)";
                                            }

                                            if (show) {
                                    %>
                                        <option value="<%= id %>"><%= label %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="mt-3" id="return-reason-box" style="display: none;">
                                <input type="hidden" name="returnReason" value="Người dùng hoàn hàng"/>
                                <div class="alert alert-info mb-0">
                                    Completing due to customer return. Inventory will be restocked.
                                    If prepaid, refund will be processed.
                                </div>
                            </div>

                            <button type="submit" class="btn btn-success mt-2">Update</button>
                        </form>
                    <% } else { %>
                        <div class="mt-3 text-muted">Unable to update the status of this order.</div>
                    <% } %>
                <% } %>
            </div>
        </div>

        <h4>Products</h4>
        <div class="table-responsive">
            <table class="table table-bordered table-striped mt-3">
                <thead class="table-light">
                    <tr>
                        <th>Product Name</th>
                        <th>Quantity</th>
                        <th>Total Price</th>
                    </tr>
                </thead>
                <tbody>
                <% for (OrderDetail od : orderDetailList) { %>
                    <tr>
                        <td><%= od.getOrderName() %></td>
                        <td><%= od.getQuantity() %></td>
                        <td><%= od.getTotalPrice() %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div class="mt-4">
            <h5>Total Price: <%= order.getTotalPrice() %></h5>
            <h5>Final Price (after discount): <%= order.getFinalPrice() %></h5>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function handleStatusChange() {
        const select = document.getElementById('orderStatus');
        const val = select ? select.value : '';
        document.getElementById('return-reason-box').style.display = (val === '5') ? 'block' : 'none';
    }
    function beforeSubmit() {
        const select = document.getElementById('orderStatus');
        if (!select || !select.value) { alert('Please select a status.'); return false; }
        if (select.value === '5') {
            return confirm('Hoàn kho và hoàn tất đơn do khách trả hàng?');
        }
        return true;
    }
    function confirmCancelNow() {
        if (confirm('Hoàn hàng về kho và hủy đơn ngay bây giờ?')) {
            document.getElementById('cancelNowForm').submit();
        }
    }
    window.onload = handleStatusChange;
</script>
</body>
</html>
