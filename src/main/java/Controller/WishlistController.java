package Controller;

import DAO.WishlistDAO;
import DAO.CartDAO;
import Model.Wishlist;
import Model.Cart;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/Wishlist")
public class WishlistController extends HttpServlet {

    private WishlistDAO wishlistDAO = new WishlistDAO();
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) (session != null ? session.getAttribute("user") : null);
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            List<Wishlist> wishlist = wishlistDAO.getWishlistByUser(user.getUserID());
            request.setAttribute("wishlist", wishlist);

            // Cập nhật tổng số lượng sản phẩm wishlist lên session
            int totalQty = 0;
            for (Wishlist w : wishlist) {
                totalQty += w.getQuantity();
            }
            session.setAttribute("wishlistQuantity", totalQty);

            request.getRequestDispatcher("wishlist.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) (session != null ? session.getAttribute("user") : null);
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                int productID = Integer.parseInt(request.getParameter("productID"));

                // Tạo wishlist mới hoặc tăng quantity nếu đã có
                Wishlist w = new Wishlist();
                w.setUserID(user.getUserID());
                w.setProductID(productID);
                w.setQuantity(1);

                wishlistDAO.addToWishlist(w); // DAO xử lý logic tăng quantity nếu sản phẩm đã tồn tại

                // Sau khi add vào wishlist, cập nhật số lượng wishlist trong session
                int newWishlistQty = wishlistDAO.getWishlistByUser(user.getUserID()).size();
                session.setAttribute("wishlistQuantity", newWishlistQty);

            } else if ("toCart".equals(action)) {
                int wishlistID = Integer.parseInt(request.getParameter("wishlistID"));
                Wishlist w = null;

                List<Wishlist> wishlistList = wishlistDAO.getWishlistByUser(user.getUserID());
                for (Wishlist item : wishlistList) {
                    if (item.getWishlistID() == wishlistID) {
                        w = item;
                        break;
                    }
                }

                if (w != null) {
                    // Thêm vào Cart
                    Cart c = new Cart();
                    c.setUserID(user.getUserID());
                    c.setProductID(w.getProductID());
                    c.setQuantity(w.getQuantity());
                    cartDAO.addProductToCart(c);

                    // Xóa khỏi Wishlist
                    wishlistDAO.removeFromWishlist(wishlistID);

                    // Cập nhật số lượng wishlist trong session
                    int newWishlistQty = wishlistDAO.getWishlistByUser(user.getUserID()).size();
                    session.setAttribute("wishlistQuantity", newWishlistQty);
                }

            } else if ("remove".equals(action)) {
                int wishlistID = Integer.parseInt(request.getParameter("wishlistID"));
                wishlistDAO.removeFromWishlist(wishlistID);

                // Cập nhật lại số lượng wishlist trong session
                int newWishlistQty = wishlistDAO.getWishlistByUser(user.getUserID()).size();
                session.setAttribute("wishlistQuantity", newWishlistQty);

                response.sendRedirect(request.getContextPath() + "/Wishlist");
                return; // Quan trọng: return để không đi tiếp tới redirect ở cuối
            } 
            
            // Thêm xử lý tăng/giảm quantity
            else if ("increaseQty".equals(action) || "decreaseQty".equals(action)) {
                int wishlistID = Integer.parseInt(request.getParameter("wishlistID"));
                List<Wishlist> wishlistList = wishlistDAO.getWishlistByUser(user.getUserID());
                Wishlist w = null;
                for (Wishlist item : wishlistList) {
                    if (item.getWishlistID() == wishlistID) {
                        w = item;
                        break;
                    }
                }
                if (w != null) {
                    int newQty = w.getQuantity();
                    if ("increaseQty".equals(action)) newQty++;
                    if ("decreaseQty".equals(action) && newQty > 1) newQty--; // không giảm dưới 1
                    w.setQuantity(newQty);
                    wishlistDAO.updateQuantity(wishlistID, newQty);

                    // Cập nhật số lượng wishlist trong session
                    int totalQty = 0;
                    for (Wishlist wi : wishlistDAO.getWishlistByUser(user.getUserID())) {
                        totalQty += wi.getQuantity();
                    }
                    session.setAttribute("wishlistQuantity", totalQty);
                }
                response.sendRedirect(request.getContextPath() + "/Wishlist");
                return;
            }

            // Cập nhật lại tổng số lượng wishlist cho các action khác (nếu cần)
            List<Wishlist> wishlistUpdated = wishlistDAO.getWishlistByUser(user.getUserID());
            int totalQty = 0;
            for (Wishlist wItem : wishlistUpdated) {
                totalQty += wItem.getQuantity();
            }
            session.setAttribute("wishlistQuantity", totalQty);

            response.sendRedirect(request.getContextPath() + "/Wishlist");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
