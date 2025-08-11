package Controller;

import DAO.CartDAO;
import DAO.VoucherDAO;
import Model.Cart;
import Model.Product;
import Model.Voucher;
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
    VoucherDAO voucherDAO;

    public CartController() {
        try {
            this.voucherDAO = new VoucherDAO();
        } catch (Exception e) {
        }
    }

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
                HttpSession session = request.getSession();
                Object userObj = request.getSession().getAttribute("userID");
                if (userObj == null) {
                    // Chưa đăng nhập, chuyển hướng về login hoặc báo lỗi
                    response.sendRedirect("login.jsp"); // hoặc custom thông báo
                    return;
                }

                int userID = (int) userObj;
                System.out.println("UserID: " + userID);
                List<Cart> cartList = dao.getProductInCart(userID);
                request.setAttribute("cart", cartList);
                session.setAttribute("cartQuantity", cartList.size());
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

            HttpSession session = request.getSession(false);
            Model.User user = (Model.User) session.getAttribute("user");

            if (user != null) {
                List<Cart> cartList = dao.getProductInCart(user.getUserID());
                request.setAttribute("cart", cartList);
                session.setAttribute("cartQuantity", cartList.size());

                String promoIDStr = request.getParameter("promotionID");
                if (promoIDStr != null) {
                    try {
                        int promoID = Integer.parseInt(promoIDStr);
                        Voucher selected = voucherDAO.getVoucherById(promoID);
                        if (selected != null && selected.isIsActive()) {
                            request.setAttribute("selectedPromotion", selected);
                            request.setAttribute("selectedPromotionID", promoID); // để đánh dấu lại <option selected>
                        }
                    } catch (NumberFormatException ex) {
                        // optional: log lỗi
                    }
                }

                List<Voucher> allPromotions = voucherDAO.getAllVouchers();
                List<Voucher> availablePromotions = new ArrayList<>();
                for (Voucher voucher : allPromotions) {
                    if (voucher.isIsActive() && voucher.getUsedCount() < voucher.getMaxUsage()) {
                        availablePromotions.add(voucher);
                    }
                }
                request.setAttribute("promotions", availablePromotions);
            }

            request.getRequestDispatcher("cart.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
