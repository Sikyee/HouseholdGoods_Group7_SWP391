package Controller;

import DAO.PromotionDAO;
import DAO.BrandDAO;
import Model.Promotion;
import Model.Brand;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/Promotion")
public class PromotionController extends HttpServlet {

    private PromotionDAO dao;

    @Override
    public void init() throws ServletException {
        try {
            dao = new PromotionDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize PromotionDAO", e);
        }
    }

    /* ------------------------ Helpers ------------------------ */
    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private boolean isScheduled(Promotion p, LocalDate today) {
        Date sd = p.getStartDate();
        return (sd != null) && sd.toLocalDate().isAfter(today); // startDate > today
    }

    /**
     * Lấy toàn bộ promotions, phân loại Active vs Deactive theo quy tắc:
     * Active: isActive == true && startDate <= today
     * Deactive: isActive == false || startDate > today
     * Có phân trang cho danh sách Active (server-side).
     */
    private void preloadPagedLists(HttpServletRequest request, int page, int pageSize) throws Exception {
        LocalDate today = LocalDate.now();

        // Gợi ý: nếu DAO của bạn chưa có, thêm dao.getAllPromotions()
        List<Promotion> all = dao.getAllPromotions();

        List<Promotion> active = new ArrayList<>();
        List<Promotion> deactive = new ArrayList<>();

        for (Promotion p : all) {
            if (p.isIsActive() && !isScheduled(p, today)) {
                active.add(p);
            } else {
                deactive.add(p); // Inactive hoặc Scheduled
            }
        }

        // Sort nhẹ cho dễ nhìn: Active mới nhất trước, Deactive sắp tới trước
        active.sort(Comparator.comparing(Promotion::getStartDate, Comparator.nullsLast(Comparator.reverseOrder()))
                              .thenComparing(Promotion::getPromotionID));
        deactive.sort(Comparator.comparing(Promotion::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Promotion::getPromotionID));

        // Pagination cho Active
        int totalItems = active.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(totalItems, from + pageSize);
        List<Promotion> activePage = active.subList(from, to);

        List<Brand> brands = new BrandDAO().getAllBrands();

        request.setAttribute("list", activePage);
        request.setAttribute("deactiveList", deactive);
        request.setAttribute("brands", brands);

        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        // để JSP biết hôm nay nhằm hiển thị “Scheduled/Inactive”
        request.setAttribute("today", Date.valueOf(today));
    }

    /* ------------------------ GET ------------------------ */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int pageSize = 5; // 5 promotion/trang
        int page = parseIntOrDefault(request.getParameter("page"), 1);

        try {
            if (action == null || action.equals("list")) {
                preloadPagedLists(request, page, pageSize);
                request.setAttribute("promotion", null);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);

            } else if (action.equals("edit")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                Promotion promo = (id > 0) ? dao.getPromotionById(id) : null;

                if (promo != null) {
                    request.setAttribute("promotion", promo);
                    request.setAttribute("showModal", true);
                } else {
                    request.setAttribute("error", "Promotion not found!");
                }
                preloadPagedLists(request, page, pageSize);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);

            } else if (action.equals("deactivate")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                if (id > 0) dao.setActive(id, false); // thay cho softDelete
                response.sendRedirect("Promotion?action=list&page=" + page);

            } else if (action.equals("activate")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                if (id > 0) dao.setActive(id, true); // thay cho reactivate
                response.sendRedirect("Promotion?action=list&page=" + page);

            } else if (action.equals("prepareAdd")) {
                request.setAttribute("promotion", null);
                request.setAttribute("showModal", true);
                preloadPagedLists(request, page, pageSize);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);

            } else {
                preloadPagedLists(request, page, pageSize);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
            }

        } catch (Exception e) {
            try { preloadPagedLists(request, page, pageSize); } catch (Exception ignore) {}
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
        }
    }

    /* ------------------------ POST ------------------------ */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int page = parseIntOrDefault(request.getParameter("page"), 1);
        int pageSize = 5;

        try {
            Promotion p = new Promotion();

            String idStr = request.getParameter("promotionID");
            if (idStr != null && !idStr.isEmpty()) p.setPromotionID(Integer.parseInt(idStr));

            p.setCode(Objects.toString(request.getParameter("code"), "").trim());
            p.setTitle(Objects.toString(request.getParameter("title"), "").trim());
            p.setDescription(Objects.toString(request.getParameter("description"), "").trim());

            String dt = Objects.toString(request.getParameter("discountType"), "").trim().toLowerCase();
            p.setDiscountType(dt);

            p.setDiscountValue(Long.parseLong(request.getParameter("discountValue")));
            p.setStartDate(Date.valueOf(request.getParameter("startDate")));
            p.setEndDate(Date.valueOf(request.getParameter("endDate")));
            p.setMinOrderValue(Long.parseLong(request.getParameter("minOrderValue")));
            p.setIsActive(Boolean.parseBoolean(request.getParameter("isActive")));

            String brandParam = request.getParameter("brand"); // select gửi số brandID
            if (brandParam != null && !brandParam.isEmpty()) p.setBrandID(Integer.parseInt(brandParam));
            else p.setBrandID(null);

            if (p.getEndDate().before(p.getStartDate())) {
                request.setAttribute("error", "End date must be after start date.");
                request.setAttribute("promotion", p);
                request.setAttribute("showModal", true);
                preloadPagedLists(request, page, pageSize);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
                return;
            }

            // Validate theo loại discount (server-side)
            if ("percentage".equals(p.getDiscountType())) {
                if (p.getDiscountValue() < 1 || p.getDiscountValue() > 100) {
                    throw new IllegalArgumentException("Percentage must be 1–100");
                }
            } else if ("fixed".equals(p.getDiscountType())) {
                if (p.getDiscountValue() < 1000) {
                    throw new IllegalArgumentException("Fixed amount must be >= 1000");
                }
            } else {
                throw new IllegalArgumentException("Invalid discount type");
            }

            // Kiểm tra trùng code (logic cũ còn hợp lý)
            Promotion existing = dao.getPromotionByCode(p.getCode());
            if (p.getPromotionID() == 0 && existing != null) {
                if (existing.isIsActive()) {
                    request.setAttribute("codeExists", true);
                    preloadPagedLists(request, page, pageSize);
                    request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
                    return;
                } else {
                    // Gợi ý khôi phục bản cũ
                    request.setAttribute("promotion", existing);
                    request.setAttribute("reactivate", true);
                    preloadPagedLists(request, page, pageSize);
                    request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
                    return;
                }
            }

            if (p.getPromotionID() == 0) dao.addPromotion(p);
            else dao.updatePromotion(p);

            response.sendRedirect("Promotion?action=list&page=" + page);

        } catch (IllegalArgumentException ie) {
            safeForwardError(request, response, "Invalid input: " + ie.getMessage(), page, pageSize);
        } catch (Exception e) {
            safeForwardError(request, response, "Invalid input: " + e.getMessage(), page, pageSize);
        }
    }

    private void safeForwardError(HttpServletRequest request, HttpServletResponse response, String msg, int page, int pageSize)
            throws ServletException, IOException {
        request.setAttribute("error", msg);
        try { preloadPagedLists(request, page, pageSize); } catch (Exception ignore) {}
        request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
    }
}
