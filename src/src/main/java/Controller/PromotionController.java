/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author TriTM
 */
import DAO.PromotionDAO;
import Model.Promotion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Promotion> all = dao.getAllPromotions();
                List<Promotion> deleted = dao.getDeletedPromotions();
                request.setAttribute("list", all);
                request.setAttribute("deletedList", deleted);
                request.setAttribute("promotion", null);
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Promotion promo = dao.getPromotionById(id);
                if (promo != null) {
                    request.setAttribute("promotion", promo);
                    request.setAttribute("showModal", true);
                } else {
                    request.setAttribute("error", "Promotion not found!");
                }
                request.setAttribute("list", dao.getAllPromotions());
                request.setAttribute("deletedList", dao.getDeletedPromotions());
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.softDeletePromotion(id);
                response.sendRedirect("Promotion?action=list");
            } else if (action.equals("reactivate")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.reactivatePromotion(id);
                response.sendRedirect("Promotion?action=list");
            } else if (action.equals("prepareAdd")) {
                // Reset promotion form and open modal
                request.setAttribute("promotion", null); // Đảm bảo không giữ dữ liệu edit
                request.setAttribute("showModal", true); // Cho phép JSP mở modal
                request.setAttribute("list", dao.getAllPromotions());
                request.setAttribute("deletedList", dao.getDeletedPromotions());
                request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Promotion p = new Promotion();

            String idStr = request.getParameter("promotionID");
            if (idStr != null && !idStr.isEmpty()) {
                p.setPromotionID(Integer.parseInt(idStr));
            }

            p.setCode(request.getParameter("code"));
            p.setDescription(request.getParameter("description"));
            p.setDiscountType(request.getParameter("discountType"));
            p.setDiscountValue(Long.parseLong(request.getParameter("discountValue")));
            p.setStartDate(Date.valueOf(request.getParameter("startDate")));
            p.setEndDate(Date.valueOf(request.getParameter("endDate")));
            p.setMinOrderValue(Long.parseLong(request.getParameter("minOrderValue")));
            p.setMaxUsage(Integer.parseInt(request.getParameter("maxUsage")));
            p.setUsedCount(Integer.parseInt(request.getParameter("usedCount")));
            p.setIsActive(Boolean.parseBoolean(request.getParameter("isActive")));

            Promotion existing = dao.getPromotionByCode(p.getCode());
            if (p.getPromotionID() == 0 && existing != null) {
                if (existing.isIsActive()) {
                    request.setAttribute("codeExists", true);
                    request.setAttribute("list", dao.getAllPromotions());
                    request.setAttribute("deletedList", dao.getDeletedPromotions());
                    request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
                    return;
                } else {
                    request.setAttribute("promotion", existing);
                    request.setAttribute("reactivate", true);
                    request.setAttribute("list", dao.getAllPromotions());
                    request.setAttribute("deletedList", dao.getDeletedPromotions());
                    request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
                    return;
                }
            }

            if (p.getPromotionID() == 0) {
                dao.addPromotion(p);
            } else {
                dao.updatePromotion(p);
            }

            response.sendRedirect("Promotion?action=list");
        } catch (Exception e) {
            request.setAttribute("error", "Invalid input: " + e.getMessage());
            try {
                request.setAttribute("list", dao.getAllPromotions());
                request.setAttribute("deletedList", dao.getDeletedPromotions());
            } catch (Exception ex) {
                request.setAttribute("error", "DB Error: " + ex.getMessage());
            }
            request.getRequestDispatcher("/managePromotion.jsp").forward(request, response);
        }
    }
}
