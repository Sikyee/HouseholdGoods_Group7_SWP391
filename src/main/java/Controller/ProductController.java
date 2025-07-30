package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import Model.Product;
import Model.Attribute;
import Model.Cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@WebServlet("/Product")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class ProductController extends HttpServlet {

    private ProductDAO dao = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Product p = dao.getProductById(id);
                List<Attribute> attributes = dao.getAttributesByProductId(id);
                request.setAttribute("product", p);
                request.setAttribute("attributes", attributes);
                request.getRequestDispatcher("product-form.jsp").forward(request, response);

            } else if ("add".equals(action)) {
                request.getRequestDispatcher("product-form.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.softDeleteProduct(id); // ✅ Soft delete
                response.sendRedirect(request.getContextPath() + "/Product");
            } else if ("productDetail".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Product productDetail = dao.getProductById(id);
                List<Attribute> attributes = dao.getAttributesByProductId(id);
                productDetail.setAttributes(attributes);

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

                request.setAttribute("productDetail", productDetail);
                request.getRequestDispatcher("productDetail.jsp").forward(request, response);
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
            Map<String, String> errors = new HashMap<>();

            p.setProductName(request.getParameter("productName"));
            p.setDescription(request.getParameter("description"));

            try {
                long price = Long.parseLong(request.getParameter("price"));
                if (price < 0) {
                    errors.put("price", "Price must be non-negative");
                }
                p.setPrice(price);
            } catch (NumberFormatException e) {
                errors.put("price", "Invalid price format");
            }

            try {
                int quantity = Integer.parseInt(request.getParameter("stonkQuantity"));
                if (quantity < 0) {
                    errors.put("stonkQuantity", "Stock must be non-negative");
                }
                p.setStonkQuantity(quantity);
            } catch (NumberFormatException e) {
                errors.put("stonkQuantity", "Invalid stock quantity");
            }

            try {
                int brandID = Integer.parseInt(request.getParameter("brandID"));
                if (brandID < 0) {
                    errors.put("brandID", "Brand ID must be non-negative");
                }
                p.setBrandID(brandID);
            } catch (NumberFormatException e) {
                errors.put("brandID", "Invalid Brand ID");
            }

            try {
                int subCategory = Integer.parseInt(request.getParameter("subCategory"));
                if (subCategory < 0) {
                    errors.put("subCategory", "SubCategory must be non-negative");
                }
                p.setSubCategory(subCategory);
            } catch (NumberFormatException e) {
                errors.put("subCategory", "Invalid SubCategory ID");
            }

            Part filePart = request.getPart("imageFile");
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String contentType = filePart.getContentType();

            if (fileName != null && !fileName.isEmpty()) {
                if (!contentType.startsWith("image/")) {
                    errors.put("imageFile", "Only image files are allowed.");
                } else {
                    String uploadPath = getServletContext().getRealPath("/images");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    filePart.write(uploadPath + File.separator + fileName);
                    p.setImage(fileName);
                }
            } else {
                p.setImage(request.getParameter("image"));
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("product", p);
                request.getRequestDispatcher("product-form.jsp").forward(request, response);
                return;
            }

            int productID;
            if (idStr == null || idStr.isEmpty()) {
                dao.addProduct(p);
                productID = dao.getLastInsertedProductIdByName(p.getProductName()); // lấy id
            } else {
                productID = Integer.parseInt(idStr);
                p.setProductID(productID);
                dao.updateProduct(p);
            }

            // Handle attributes
            String[] attributeNames = request.getParameterValues("attributeName");
            String[] attributeValues = request.getParameterValues("attributeValue");
            if (attributeNames != null && attributeValues != null) {
                dao.updateProductAttributes(productID, attributeNames, attributeValues);
            }

            response.sendRedirect(request.getContextPath() + "/Product");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
