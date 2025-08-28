package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.BrandDAO;
import DAO.CategoryDAO;
import Model.Cart;
import Model.Product;
import Model.Brand;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/")
public class HomeController extends HttpServlet {

    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private BrandDAO brandDAO;
    private CategoryDAO categoryDAO;

    // Initialize DAOs in init() method with proper exception handling
    @Override
    public void init() throws ServletException {
        try {
            this.productDAO = new ProductDAO();
            this.cartDAO = new CartDAO();
            this.brandDAO = new BrandDAO();
            this.categoryDAO = new CategoryDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize DAO objects", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Pagination setup
            int productsPerPage = 16;
            int currentPage = getCurrentPage(request);

            // Get all products
            List<Product> allProducts = productDAO.getAllProducts();

            // Calculate pagination
            int totalProducts = allProducts.size();
            int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);

            // Get products for current page
            List<Product> productsForPage = getProductsForPage(allProducts, currentPage, productsPerPage);

            // Handle cart quantity for logged-in users
            handleCartQuantity(request);

            // Load brands and categories for filter
            loadFilterOptions(request);

            // Set request attributes
            request.setAttribute("productList", productsForPage);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalProducts", totalProducts);

            // Forward to JSP
            request.getRequestDispatcher("homePage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error gracefully
            request.setAttribute("error", "Unable to load products. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private int getCurrentPage(HttpServletRequest request) {
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                int page = Integer.parseInt(pageParam);
                return page > 0 ? page : 1;
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    private List<Product> getProductsForPage(List<Product> allProducts, int currentPage, int productsPerPage) {
        int start = (currentPage - 1) * productsPerPage;
        int end = Math.min(start + productsPerPage, allProducts.size());
        return allProducts.subList(start, end);
    }

    private void handleCartQuantity(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("userID");

        if (userObj != null) {
            try {
                int userID = (int) userObj;
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                session.setAttribute("cartQuantity", cartList.size());
                session.setAttribute("isLoggedIn", true);
            } catch (Exception e) {
                System.err.println("Error loading cart for user: " + e.getMessage());
                session.setAttribute("cartQuantity", 0);
            }
        } else {
            // User not logged in - set default cart quantity
            session.setAttribute("cartQuantity", 0);
            session.setAttribute("isLoggedIn", false);
        }
    }

    private void loadFilterOptions(HttpServletRequest request) {
        try {
            // Load brands for filter dropdown
            List<Brand> brands = brandDAO.getAllBrands();
            request.setAttribute("brands", brands);

            // Load categories for filter dropdown
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

        } catch (Exception e) {
            System.err.println("Error loading filter options: " + e.getMessage());
            // Set empty lists to prevent JSP errors
            request.setAttribute("brands", new java.util.ArrayList<>());
            request.setAttribute("categories", new java.util.ArrayList<>());
        }
    }
}
