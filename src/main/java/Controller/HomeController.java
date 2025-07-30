package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import Model.Cart;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/")
public class HomeController extends HttpServlet {

    private ProductDAO dao = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int productsPerPage = 16;
        int currentPage = 1;
        String pageParams = request.getParameter("page");
        try {
//            List<Product> productList = dao.getAllProducts();
//            request.setAttribute("productList", productList);
//            request.getRequestDispatcher("homePage.jsp").forward(request, response);
            if (pageParams != null) {
                try {
                    currentPage = Integer.parseInt(pageParams);
                } catch (Exception e) {
                    currentPage = 1;
                }
            }

            //Count total pages
            List<Product> productList = dao.getAllProducts();
            int totalProducts = productList.size();
            int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);

            //Product for 1 page
            int start = (currentPage - 1) * productsPerPage;
            int end = Math.min(start + productsPerPage, totalProducts);
            List<Product> productsPage = productList.subList(start, end);

            HttpSession session = request.getSession();
            Object userObj = request.getSession().getAttribute("userID");
            if (userObj == null) {
                // Chưa đăng nhập, chuyển hướng về login hoặc báo lỗi
                response.sendRedirect("login.jsp"); // hoặc custom thông báo
                return;
            }

            int userID = (int) userObj; // An toàn vì đã kiểm tra null
            System.out.println("UserID: " + userID);
            List<Cart> cartList = cartDAO.getProductInCart(userID);
            session.setAttribute("cartQuantity", cartList.size());

            request.setAttribute("productList", productsPage);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("homePage.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
