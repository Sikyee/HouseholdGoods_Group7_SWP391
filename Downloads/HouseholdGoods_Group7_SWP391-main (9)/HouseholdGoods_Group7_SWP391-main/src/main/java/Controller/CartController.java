package Controller;

import DAO.CartDAO;
import Model.Cart;
import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author Admin
 */
@WebServlet("/Cart")
public class CartController extends HttpServlet {

    CartDAO dao = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");

            if ("increase".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                int quantity = Integer.parseInt(request.getParameter("quantity")); // form gửi quantity

                Cart cartItem = new Cart();
                cartItem.setCartID(id);
                cartItem.setQuantity(quantity);

                dao.increaseQuantityInCart(cartItem);
                response.sendRedirect(request.getContextPath() + "/Cart"); // reload trang cart
            } else if ("decrease".equals(action)) {
            } else if ("add".equals(action)) {
                HttpSession session = request.getSession(false);
                Model.User user = (Model.User) session.getAttribute("user"); // đảm bảo bạn lưu cả user chứ không chỉ userID
                if (user == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }

                try {
                    int userID = user.getUserID();
                    int productID = Integer.parseInt(request.getParameter("productID"));
                    int quantity = Integer.parseInt(request.getParameter("quantity")); // form gửi quantity

                    Cart cartItem = new Cart();
                    cartItem.setUserID(userID);
                    cartItem.setProductID(productID);
                    cartItem.setQuantity(quantity);

                    dao.addProductToCart(cartItem);
                    response.sendRedirect(request.getContextPath() + "/"); // reload trang cart
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect("error.jsp");
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteProductInCart(id);
                response.sendRedirect(request.getContextPath() + "/Cart");
            } else {
                Object userObj = request.getSession().getAttribute("userID");
                if (userObj == null) {
                    // Chưa đăng nhập, chuyển hướng về login hoặc báo lỗi
                    response.sendRedirect("login.jsp"); // hoặc custom thông báo
                    return;
                }

                int userID = (int) userObj; // An toàn vì đã kiểm tra null
                System.out.println("UserID: " + userID);
                List<Cart> cartList = dao.getProductInCart(userID);
                request.setAttribute("cart", cartList);
                request.getRequestDispatcher("cart.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int cartID = Integer.parseInt(request.getParameter("cartID"));

            Cart cart = new Cart();
            cart.setCartID(cartID);

            if ("increase".equals(action)) {
                dao.increaseQuantityInCart(cart);
            } else if ("decrease".equals(action)) {
                dao.decreaseQuantityByCartID(cartID);
            }

            // Lấy lại giỏ hàng và forward sang JSP fragment (hoặc toàn bộ trang)
            HttpSession session = request.getSession(false);
            Model.User user = (Model.User) session.getAttribute("user");

            if (user != null) {
                List<Cart> cartList = dao.getProductInCart(user.getUserID());
                request.setAttribute("cart", cartList);
            }

            request.getRequestDispatcher("cart.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
