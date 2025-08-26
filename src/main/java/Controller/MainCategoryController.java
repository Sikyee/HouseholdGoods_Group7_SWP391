package Controller;

import DAO.CategoryDAO;
import Model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/MainCategory")
@MultipartConfig
public class MainCategoryController extends HttpServlet {

    private CategoryDAO dao = new CategoryDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Category mc = dao.getMainCategoryById(id);
                request.setAttribute("mainCategory", mc);
                request.getRequestDispatcher("mainCategory-form.jsp").forward(request, response);

            } else if ("add".equals(action)) {
                request.getRequestDispatcher("mainCategory-form.jsp").forward(request, response);
                //            } else if ("delete".equals(action)) {
                //                int id = Integer.parseInt(request.getParameter("id"));
                //                dao.deleteProduct(id);
                //                response.sendRedirect(request.getContextPath() + "/Product");
                //
                //            } 
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteMainCategory(id);
                response.sendRedirect(request.getContextPath() + "/MainCategory");
            } else {
                List<Category> list = dao.getAllMainCategories();
                request.setAttribute("categories", list);
                request.getRequestDispatcher("manageMainCategory.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            String idStr = request.getParameter("id");

            Category c = new Category();
            c.setCategoryName(request.getParameter("categoryName"));

            if (idStr == null || idStr.isEmpty()) {
                dao.addMainCategory(c);
            } else {
                c.setCategoryID(Integer.parseInt(idStr));
                dao.updateMainCategory(c);
            }

            response.sendRedirect(request.getContextPath() + "/MainCategory");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
