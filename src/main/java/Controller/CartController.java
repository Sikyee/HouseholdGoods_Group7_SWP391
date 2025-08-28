package Controller;

import DAO.CartDAO;
import DAO.PromotionDAO;
import DAO.VoucherDAO;
import Model.Cart;
import Model.Product;
import Model.Promotion;
import Model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.NumberFormat;
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
            String ajax = request.getParameter("ajax");
            boolean isAjax = "1".equals(ajax);

            int cartID = Integer.parseInt(request.getParameter("cartID"));
            Cart cart = new Cart();
            cart.setCartID(cartID);

            if ("increase".equals(action)) {
                dao.increaseQuantityInCart(cart);
            } else if ("decrease".equals(action)) {
                dao.decreaseQuantityByCartID(cartID); // không xóa nếu =1
            }

            Cart updated = dao.getCartItemById(cartID);

            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();

                if (updated == null) {
                    // (hiếm khi) item bị xóa bằng thao tác khác — cứ báo deleted
                    out.write("{\"ok\":true,\"deleted\":true}");
                    return;
                }

                Product p = updated.getProduct();
                long baseUnit = (p != null ? p.getPrice() : 0L);
                int quantity = updated.getQuantity();

                // === ÁP DỤNG PROMOTION THEO BRAND ===
                long effectiveUnit = baseUnit;
                try {
                    PromotionDAO promoDao = new PromotionDAO();
                    List<Promotion> promos = promoDao.getAllPromotions(); // isActive=1
                    Integer brandId = p != null ? p.getBrandID() : null;

                    if (brandId != null) {
                        long best = baseUnit;
                        for (Promotion pr : promos) {
                            if (brandId.equals(pr.getBrandID())) {
                                long discounted = baseUnit;
                                if ("percentage".equalsIgnoreCase(pr.getDiscountType())) {
                                    discounted = Math.round(baseUnit * (100.0 - pr.getDiscountValue()) / 100.0);
                                } else if ("fixed".equalsIgnoreCase(pr.getDiscountType())) {
                                    discounted = Math.max(0L, baseUnit - pr.getDiscountValue());
                                }
                                if (discounted < best) {
                                    best = discounted;
                                }
                            }
                        }
                        effectiveUnit = best;
                    }
                } catch (Exception ignore) {
                    // nếu lỗi khuyến mãi thì dùng giá gốc
                    effectiveUnit = baseUnit;
                }

                long itemTotal = effectiveUnit * quantity;

                NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                String itemTotalFmt = nf.format(itemTotal) + "₫";
                String unitFmt = nf.format(effectiveUnit) + "₫";

                boolean canDecrease = quantity > 1;

                out.write("{\"ok\":true,"
                        + "\"quantity\":" + quantity + ","
                        + "\"unit\":" + effectiveUnit + ","
                        + "\"unitFmt\":\"" + unitFmt.replace("\"", "\\\"") + "\","
                        + "\"itemTotal\":" + itemTotal + ","
                        + "\"itemTotalFmt\":\"" + itemTotalFmt.replace("\"", "\\\"") + "\","
                        + "\"canDecrease\":" + canDecrease
                        + "}");
                return;
            }

            // Non-AJAX: render lại trang
            HttpSession session = request.getSession(false);
            Model.User user = (Model.User) (session != null ? session.getAttribute("user") : null);
            if (user != null) {
                List<Cart> cartList = dao.getProductInCart(user.getUserID());
                request.setAttribute("cart", cartList);
                session.setAttribute("cartQuantity", cartList.size());
            }
            request.getRequestDispatcher("cart.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
