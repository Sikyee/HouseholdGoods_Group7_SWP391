/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.ProductDAO;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchController extends HttpServlet {

    private ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("search");
            List<Product> products;

            if (keyword != null && !keyword.trim().isEmpty()) {
                products = dao.searchProducts(keyword.trim());
                request.setAttribute("search", keyword);
            } else {
                products = dao.getAllProducts();
            }

            request.setAttribute("productList", products);
            request.getRequestDispatcher("homePage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error while searching for products.");
            request.getRequestDispatcher("homePage.jsp").forward(request, response);
        }
    }
}
