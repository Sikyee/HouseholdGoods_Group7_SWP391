package Controller;

import DAO.ProductDAO;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/")
public class HomeController extends HttpServlet {

    private ProductDAO dao = new ProductDAO();  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Product> productList = dao.getAllProducts();
            request.setAttribute("productList", productList);
            request.getRequestDispatcher("homePage.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
