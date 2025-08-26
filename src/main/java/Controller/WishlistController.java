package Controller;

import DAO.WishlistDAO;
import Model.User;
import Model.Wishlist;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/WishlistController")
public class WishlistController extends HttpServlet {
    private WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if ("add".equals(action)) {
            int productID = Integer.parseInt(request.getParameter("productID"));
            wishlistDAO.addWishlist(user.getUserID(), productID);
            response.sendRedirect("ProductController"); // quay lại trang quản lý sản phẩm
        } else if ("list".equals(action)) {
            List<Wishlist> list = wishlistDAO.getWishlistByUser(user.getUserID());
            request.setAttribute("wishlist", list);
            request.getRequestDispatcher("wishlist.jsp").forward(request, response);
        } else if ("delete".equals(action)) {
            int wishlistID = Integer.parseInt(request.getParameter("id"));
            wishlistDAO.deleteWishlist(wishlistID);
            response.sendRedirect("WishlistController?action=list");
        }
    }
}
