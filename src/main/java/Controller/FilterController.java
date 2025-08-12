package Controller;

import DAO.ProductDAO;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/filter")
public class FilterController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String brandStr = request.getParameter("brand");
            String categoryStr = request.getParameter("category");
            String minStr = request.getParameter("min");
            String maxStr = request.getParameter("max");

            Integer brandID = (brandStr != null && !brandStr.isEmpty()) ? Integer.parseInt(brandStr) : null;
            Integer categoryID = (categoryStr != null && !categoryStr.isEmpty()) ? Integer.parseInt(categoryStr) : null;
            Long minPrice = (minStr != null && !minStr.isEmpty()) ? Long.parseLong(minStr) : null;
            Long maxPrice = (maxStr != null && !maxStr.isEmpty()) ? Long.parseLong(maxStr) : null;

            ProductDAO dao = new ProductDAO();
            List<Product> productList = dao.filterProducts(brandID, categoryID, minPrice, maxPrice);

            request.setAttribute("productList", productList);
            request.setAttribute("filterActive", true);
            request.getRequestDispatcher("homePage.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
