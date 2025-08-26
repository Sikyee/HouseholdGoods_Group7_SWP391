/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.VoucherDAO;
import Model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/Voucher")
public class VoucherController extends HttpServlet {

    private VoucherDAO dao;
    private static final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        try {
            dao = new VoucherDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize VoucherDAO", e);
        }
    }

    /* ------------------------ Helpers ------------------------ */

    private void preloadListsPaged(HttpServletRequest request, int page) throws Exception {
        int total = dao.countActiveVouchers();
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        List<Voucher> active = dao.getActiveVouchersPage(page, PAGE_SIZE);
        List<Voucher> deleted = dao.getDeletedVouchers();

        request.setAttribute("list", active);
        request.setAttribute("deletedList", deleted);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", total);
        request.setAttribute("pageSize", PAGE_SIZE);
    }

    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private long parseLongOrDefault(String s, long def) {
        try { return (s == null || s.isEmpty()) ? def : Long.parseLong(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    /* ------------------------ GET ------------------------ */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        int pageParam = parseIntOrDefault(request.getParameter("page"), 1);

        try {
            if (action == null || action.equals("list")) {
                preloadListsPaged(request, pageParam);
                request.setAttribute("voucher", null);

                // optional messages via query params
                String err = request.getParameter("err");
                String msg = request.getParameter("msg");
                if (err != null && !err.isEmpty()) request.setAttribute("error", err);
                if (msg != null && !msg.isEmpty()) request.setAttribute("message", msg);

                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);

            } else if (action.equals("edit")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                Voucher v = (id > 0) ? dao.getVoucherById(id) : null;

                if (v != null) {
                    request.setAttribute("voucher", v);
                    request.setAttribute("showModal", true);
                } else {
                    request.setAttribute("error", "Voucher not found!");
                }
                preloadListsPaged(request, pageParam);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);

            } else if (action.equals("delete")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                if (id > 0) dao.softDeleteVoucher(id);
                response.sendRedirect("Voucher?action=list");

            } else if (action.equals("reactivate")) {
                int id = parseIntOrDefault(request.getParameter("id"), 0);
                if (id > 0) dao.reactivateVoucher(id);
                response.sendRedirect("Voucher?action=list");

            } else if (action.equals("prepareAdd")) {
                request.setAttribute("voucher", null);
                request.setAttribute("showModal", true);
                preloadListsPaged(request, pageParam);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);

            } else {
                preloadListsPaged(request, pageParam);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
            }

        } catch (Exception e) {
            try { preloadListsPaged(request, pageParam); } catch (Exception ignore) {}
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
        }
    }

    /* ------------------------ POST ------------------------ */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            Voucher p = new Voucher();

            int voucherId = parseIntOrDefault(request.getParameter("voucherID"), 0);
            p.setVoucherID(voucherId);

            String code = trimOrEmpty(request.getParameter("code"));
            p.setCode(code);

            p.setDescription(trimOrEmpty(request.getParameter("description")));

            String discountType = trimOrEmpty(request.getParameter("discountType"));
            if (!"percentage".equalsIgnoreCase(discountType) && !"fixed".equalsIgnoreCase(discountType)) {
                discountType = "fixed";
            }
            p.setDiscountType(discountType.toLowerCase());

            long discountValue = parseLongOrDefault(request.getParameter("discountValue"), 0L);
            if (discountValue < 0) discountValue = 0L;
            p.setDiscountValue(discountValue);

            Date startDate = Date.valueOf(trimOrEmpty(request.getParameter("startDate")));
            Date endDate   = Date.valueOf(trimOrEmpty(request.getParameter("endDate")));
            p.setStartDate(startDate);
            p.setEndDate(endDate);

            long minOrderValue = parseLongOrDefault(request.getParameter("minOrderValue"), 0L);
            if (minOrderValue < 0) minOrderValue = 0L;
            p.setMinOrderValue(minOrderValue);

            int maxUsage = parseIntOrDefault(request.getParameter("maxUsage"), 1);
            if (maxUsage < 1) maxUsage = 1;
            p.setMaxUsage(maxUsage);

            int usedCount = parseIntOrDefault(request.getParameter("usedCount"), 0);
            if (voucherId == 0) usedCount = 0;
            if (usedCount < 0) usedCount = 0;
            if (usedCount > maxUsage) usedCount = maxUsage;
            p.setUsedCount(usedCount);

            boolean isActive = true;
            if (voucherId == 0) {
                isActive = Boolean.parseBoolean(trimOrEmpty(request.getParameter("isActive")));
            } else {
                Voucher current = dao.getVoucherById(voucherId);
                if (current != null) isActive = current.isIsActive();
            }
            p.setIsActive(isActive);

            if (endDate.before(startDate)) {
                request.setAttribute("error", "End date must be after start date.");
                request.setAttribute("voucher", p);
                request.setAttribute("showModal", true);
                preloadListsPaged(request, 1);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                return;
            }

            if ("percentage".equals(p.getDiscountType()) && (p.getDiscountValue() < 0 || p.getDiscountValue() > 100)) {
                request.setAttribute("error", "Percentage discount must be from 0 to 100.");
                request.setAttribute("voucher", p);
                request.setAttribute("showModal", true);
                preloadListsPaged(request, 1);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                return;
            }

            Voucher existingByCode = dao.getVoucherByCode(p.getCode());
            if (voucherId == 0) {
                if (existingByCode != null) {
                    if (existingByCode.isIsActive()) {
                        request.setAttribute("codeExists", true);
                        preloadListsPaged(request, 1);
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    } else {
                        request.setAttribute("voucher", existingByCode);
                        request.setAttribute("reactivate", true);
                        preloadListsPaged(request, 1);
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    }
                }
                dao.addVoucher(p);
            } else {
                if (existingByCode != null && existingByCode.getVoucherID() != voucherId) {
                    if (existingByCode.isIsActive()) {
                        request.setAttribute("error", "Code already exists on another active voucher.");
                        request.setAttribute("voucher", p);
                        request.setAttribute("showModal", true);
                        preloadListsPaged(request, 1);
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    } else {
                        request.setAttribute("error", "Code belongs to an inactive voucher. Reactivate that one or choose another code.");
                        request.setAttribute("voucher", p);
                        request.setAttribute("showModal", true);
                        preloadListsPaged(request, 1);
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    }
                }
                dao.updateVoucher(p);
            }

            response.sendRedirect("Voucher?action=list");

        } catch (IllegalArgumentException ie) {
            safeForwardError(request, response, "Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            safeForwardError(request, response, "Invalid input: " + e.getMessage());
        }
    }

    private void safeForwardError(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException {
        request.setAttribute("error", msg);
        try { preloadListsPaged(request, 1); } catch (Exception ignore) {}
        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
    }
}
