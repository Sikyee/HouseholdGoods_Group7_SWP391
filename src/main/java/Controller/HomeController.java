package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.BrandDAO;
import DAO.CategoryDAO;
import DAO.FeedbackDAO;
import DAO.WishlistDAO;
import Model.Cart;
import Model.Product;
import Model.Brand;
import Model.Category;
import Model.Wishlist;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/")
public class HomeController extends HttpServlet {

    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private BrandDAO brandDAO;
    private CategoryDAO categoryDAO;
    private FeedbackDAO feedbackDAO;
    private WishlistDAO wishlistDAO;

    // Initialize DAOs in init() method with proper exception handling
    @Override
    public void init() throws ServletException {
        try {
            this.productDAO = new ProductDAO();
            this.cartDAO = new CartDAO();
            this.brandDAO = new BrandDAO();
            this.categoryDAO = new CategoryDAO();
            this.feedbackDAO = new FeedbackDAO();
            this.wishlistDAO = new WishlistDAO();
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

            // Rating filter setup
            Integer ratingFilter = getRatingFilter(request);

            // Get all products
            List<Product> allProducts = productDAO.getAllProducts();

            // Calculate pagination
            int totalProducts = allProducts.size();
            int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);

            // Get products for current page
            List<Product> productsForPage = getProductsForPage(allProducts, currentPage, productsPerPage);

            // Set average rating for each product
            setAverageRatings(productsForPage);

            // Handle cart quantity and wishlist for logged-in users
            handleUserSession(request, response);

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

    private Integer getRatingFilter(HttpServletRequest request) {
        String ratingParam = request.getParameter("rating");
        if (ratingParam != null && !ratingParam.isEmpty()) {
            try {
                return Integer.parseInt(ratingParam);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private List<Product> getProductsForPage(List<Product> allProducts, int currentPage, int productsPerPage) {
        int start = (currentPage - 1) * productsPerPage;
        int end = Math.min(start + productsPerPage, allProducts.size());
        return allProducts.subList(start, end);
    }

    private void setAverageRatings(List<Product> products) {
        for (Product p : products) {
            try {
                Double avg = feedbackDAO.getAverageRatingByProduct(p.getProductID());
                p.setAverageRating(avg != null ? avg : 0.0);
            } catch (Exception e) {
                System.err.println("Error setting average rating for product " + p.getProductID() + ": " + e.getMessage());
                p.setAverageRating(0.0);
            }
        }
    }

    private void handleUserSession(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("userID");

        if (userObj != null) {
            try {
                int userID = (int) userObj;
                System.out.println("UserID: " + userID);

                // Handle cart quantity
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                session.setAttribute("cartQuantity", cartList.size());
                session.setAttribute("isLoggedIn", true);

                // Handle wishlist
                List<Wishlist> wishlistList = wishlistDAO.getWishlistByUser(userID);
                List<Integer> wishlistProductIDs = new ArrayList<Integer>();
                for (Wishlist wishlist : wishlistList) {
                    wishlistProductIDs.add(wishlist.getProductID());
                }
                request.setAttribute("wishlistProductIDs", wishlistProductIDs);

            } catch (Exception e) {
                System.err.println("Error loading user data: " + e.getMessage());
                session.setAttribute("cartQuantity", 0);
                session.setAttribute("isLoggedIn", false);
                // Optional: redirect to login if user session is invalid
                // response.sendRedirect("login.jsp");
                // return;
            }
        } else {
            // User not logged in - set default values
            session.setAttribute("cartQuantity", 0);
            session.setAttribute("isLoggedIn", false);
            // Optional: redirect to login page for non-logged users
            // response.sendRedirect("login.jsp");
            // return;
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
            request.setAttribute("brands", new ArrayList<>());
            request.setAttribute("categories", new ArrayList<>());
        }
    }
}
