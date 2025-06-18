/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CategoryDAO;
import Model.Category;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
@WebServlet("/Category")
@MultipartConfig
public class CategoryController extends HttpServlet {

    CategoryDAO dao = new CategoryDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                int subCategoryID = Integer.parseInt(request.getParameter("id"));
                Category subCategory = dao.getSubCategoryByID(subCategoryID);
                List<Category> categoryList = dao.getAllParentCategories();

                request.setAttribute("category", subCategory);
                request.setAttribute("categoryList", categoryList);

                request.getRequestDispatcher("category-form.jsp").forward(request, response);
            } else if ("add".equals(action)) {
                List<Category> categoryList = dao.getAllParentCategories();
                request.setAttribute("categoryList", categoryList);
                request.getRequestDispatcher("category-form.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
            } else {
                List<Category> categories = dao.getAllCategories();
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("manageCategory.jsp").forward(request, response);
            }

        } catch (Exception ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            String idStr = request.getParameter("id");

            Category c = new Category();
            c.setCategoryID(Integer.parseInt(request.getParameter("categoryID")));
            c.setSubCategoryName(request.getParameter("subCategoryName"));

            if (idStr == null || idStr.isEmpty()) {
                dao.addCategory(c);
            } else {
                c.setSubCategoryID(Integer.parseInt(idStr));
                dao.updateCategory(c);
            }

            response.sendRedirect(request.getContextPath() + "/Category");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
