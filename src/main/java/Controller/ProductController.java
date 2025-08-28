package Controller;

import DAO.BrandDAO;
import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.SubCategoryDAO;
import Model.Product;
import Model.Attribute;
import Model.Brand;
import Model.Cart;
import Model.SubCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            // ✅ Load SubCategory + Brand từ DB cho form
            SubCategoryDAO subCategoryDAO = new SubCategoryDAO();
            BrandDAO brandDAO = new BrandDAO();
            List<SubCategory> subCategories = subCategoryDAO.getAllSubCategories();
            List<Brand> brands = brandDAO.getAllBrands();
            request.setAttribute("subCategories", subCategories);
            request.setAttribute("brands", brands);
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
                dao.softDeleteProduct(id);
                response.sendRedirect(request.getContextPath() + "/Product");

            } else if ("productDetail".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Product productDetail = dao.getProductById(id);
                List<Attribute> attributes = dao.getAttributesByProductId(id);
                productDetail.setAttributes(attributes);

                HttpSession session = request.getSession();
                Object userObj = request.getSession().getAttribute("userID");
                if (userObj == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }

                int userID = (int) userObj;
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

            // ✅ Gán các field
            p.setProductName(request.getParameter("productName"));
            p.setDescription(request.getParameter("description"));

            // ✅ Validate Price
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
                int quantity = Integer.parseInt(request.getParameter("stockQuantity"));
                if (quantity < 0) {
                    errors.put("stockQuantity", "Stock must be non-negative");
                }
                p.setStonkQuantity(quantity);
            } catch (NumberFormatException e) {
                errors.put("stockQuantity", "Invalid stock quantity");
            }

            // ✅ Validate Brand
            try {
                int brandID = Integer.parseInt(request.getParameter("brandID"));
                if (brandID < 0) {
                    errors.put("brandID", "Brand ID must be non-negative");
                }
                p.setBrandID(brandID);
            } catch (NumberFormatException e) {
                errors.put("brandID", "Invalid Brand ID");
            }

            // ✅ Validate SubCategory
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
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String contentType = filePart.getContentType();

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
                // Không upload ảnh mới thì giữ ảnh cũ
                p.setImage(request.getParameter("image"));
            }

            // 🔴 NGAY SAU KHI VALIDATE XONG
            if (!errors.isEmpty()) {
                // ✅ load lại dropdown để hiển thị tiếp
                SubCategoryDAO subCategoryDAO = new SubCategoryDAO();
                BrandDAO brandDAO = new BrandDAO();
                List<SubCategory> subCategories = subCategoryDAO.getAllSubCategories();
                List<Brand> brands = brandDAO.getAllBrands();
                request.setAttribute("subCategories", subCategories);
                request.setAttribute("brands", brands);

                request.setAttribute("errors", errors);
                request.setAttribute("product", p);
                request.getRequestDispatcher("product-form.jsp").forward(request, response);
                return; // ⬅️ Dừng luôn, không chạy insert/update nữa
            }

            // ✅ Nếu không có lỗi thì mới xử lý Insert/Update
            int productID;
            if (idStr == null || idStr.isEmpty()) {
                // Add
                dao.addProduct(p);
                productID = dao.getLastInsertedProductIdByName(p.getProductName());
                request.getSession().setAttribute("successMessage", "Product added successfully!");

                // về form Add trống
                response.sendRedirect(request.getContextPath() + "/Product?action=add");

            } else {
                // Edit
                productID = Integer.parseInt(idStr);
                p.setProductID(productID);
                dao.updateProduct(p);
                request.getSession().setAttribute("successMessage", "Product update successful!");

                // về lại form Edit của sp vừa sửa
                response.sendRedirect(request.getContextPath() + "/Product?action=edit&id=" + productID);
            }

            // ✅ Handle Attributes
            String[] attributeNames = request.getParameterValues("attributeName");
            String[] attributeValues = request.getParameterValues("attributeValue");
            if (attributeNames != null && attributeValues != null) {
                dao.updateProductAttributes(productID, attributeNames, attributeValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
