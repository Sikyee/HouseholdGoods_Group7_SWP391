<%-- 
    Document   : wishlist
    Created on : Aug 27, 2025, 10:54:44 PM
    Author     : thach
--%>
<%-- 
    Document   : wishlist
    Created on : Aug 27, 2025, 10:54:44 PM
    Author     : thach
--%>

<%@ page import="java.util.*, Model.Wishlist" %>
<%@ include file="header.jsp" %>
<div class="container mt-5">
    <h4>Your Wishlist</h4>
    <%
        List<Wishlist> wishlist = (List<Wishlist>) request.getAttribute("wishlist");
        String context = request.getContextPath();
        if (wishlist == null || wishlist.isEmpty()) {
    %>
        <div class="alert alert-info">Your wishlist is empty.</div>
    <% } else { %>
        <ul class="list-group">
            <% for (Wishlist w : wishlist) {
                Model.Product p = w.getProduct();
            %>
<li class="list-group-item d-flex justify-content-between align-items-center align-items-start">
    <img src="/HouseholdGoods_Group7_SWP391/images/<%=p.getImage()%>" 
         style="width:80px;height:80px;object-fit:cover;">
    <div class="ms-3 flex-grow-1">
        <div><%= p.getProductName() %></div>
        <div class="d-flex align-items-center mt-2">
            <!-- Nút gi?m quantity -->
            <form method="post" action="<%= context %>/Wishlist" style="display:inline;">
                <input type="hidden" name="action" value="decreaseQty">
                <input type="hidden" name="wishlistID" value="<%= w.getWishlistID() %>">
                <button type="submit" class="btn btn-sm btn-secondary">-</button>
            </form>

            <span class="mx-2">Qty: <%= w.getQuantity() %></span>

            <!-- Nút t?ng quantity -->
            <form method="post" action="<%= context %>/Wishlist" style="display:inline;">
                <input type="hidden" name="action" value="increaseQty">
                <input type="hidden" name="wishlistID" value="<%= w.getWishlistID() %>">
                <button type="submit" class="btn btn-sm btn-secondary">+</button>
            </form>
        </div>
    </div>
    <div class="d-flex flex-column">
        <!-- Add to Cart -->
        <form method="post" action="<%= context %>/Wishlist" class="mb-1">
            <input type="hidden" name="action" value="toCart">
            <input type="hidden" name="wishlistID" value="<%= w.getWishlistID() %>">
            <button type="submit" class="btn btn-success btn-sm">Add to Cart</button>
        </form>

        <!-- Remove -->
        <form method="post" action="<%= context %>/Wishlist">
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="wishlistID" value="<%= w.getWishlistID() %>">
            <button type="submit" class="btn btn-danger btn-sm"><i class="fas fa-trash"></i></button>
        </form>
    </div>
</li>

            <% } %>
        </ul>
    <% } %>
</div>
<%@ include file="footer.jsp" %>

