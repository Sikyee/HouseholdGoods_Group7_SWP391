package Controller;

import DAO.ProductDAO;
import Model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/Product")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class ProductController extends HttpServlet {

    private ProductDAO dao = new ProductDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Product p = dao.getProductById(id);
                request.setAttribute("product", p);
                request.getRequestDispatcher("product-form.jsp").forward(request, response);

            } else if ("add".equals(action)) {
                request.getRequestDispatcher("product-form.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteProduct(id);
                response.sendRedirect(request.getContextPath() + "/Product");

            } else {
                List<Product> list = dao.getAllProducts();
                request.setAttribute("products", list);
                request.getRequestDispatcher("manageProduct.jsp").forward(request, response);
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

            Product p = new Product();
            p.setProductName(request.getParameter("productName"));
            p.setDescription(request.getParameter("description"));
            p.setSubCategory(Integer.parseInt(request.getParameter("subCategory")));
            p.setPrice(Long.parseLong(request.getParameter("price")));
            p.setStonkQuantity(Integer.parseInt(request.getParameter("stonkQuantity")));
            p.setBrandID(Integer.parseInt(request.getParameter("brandID")));

            // Lấy file ảnh upload từ form
            Part filePart = request.getPart("imageFile");
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            if (fileName != null && !fileName.isEmpty()) {
                // Đường dẫn lưu ảnh (thư mục /images trong project)
                String uploadPath = getServletContext().getRealPath("/images");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                filePart.write(uploadPath + File.separator + fileName);
                p.setImage(fileName);
            } else {
                // Nếu không upload ảnh mới thì giữ ảnh cũ (nếu có)
                p.setImage(request.getParameter("image"));
            }

            if (idStr == null || idStr.isEmpty()) {
                dao.addProduct(p);
            } else {
                p.setProductID(Integer.parseInt(idStr));
                dao.updateProduct(p);
            }

            response.sendRedirect(request.getContextPath() + "/Product");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
