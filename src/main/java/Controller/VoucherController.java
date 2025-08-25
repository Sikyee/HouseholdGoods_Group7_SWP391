/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author TriTM
 */
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

    @Override
    public void init() throws ServletException {
        try {
            dao = new VoucherDAO();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize VoucherDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Voucher> all = dao.getAllVouchers();
                List<Voucher> deleted = dao.getDeletedVouchers();
                request.setAttribute("list", all);
                request.setAttribute("deletedList", deleted);
                request.setAttribute("voucher", null);
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Voucher promo = dao.getVoucherById(id);
                if (promo != null) {
                    request.setAttribute("voucher", promo);
                    request.setAttribute("showModal", true);
                } else {
                    request.setAttribute("error", "Voucher not found!");
                }
                request.setAttribute("list", dao.getAllVouchers());
                request.setAttribute("deletedList", dao.getDeletedVouchers());
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.softDeleteVoucher(id);
                response.sendRedirect("Voucher?action=list");
            } else if (action.equals("reactivate")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.reactivateVoucher(id);
                response.sendRedirect("Voucher?action=list");
            } else if (action.equals("prepareAdd")) {
                // Reset voucher form and open modal
                request.setAttribute("voucher", null); // Đảm bảo không giữ dữ liệu edit
                request.setAttribute("showModal", true); // Cho phép JSP mở modal
                request.setAttribute("list", dao.getAllVouchers());
                request.setAttribute("deletedList", dao.getDeletedVouchers());
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Voucher p = new Voucher();

            String idStr = request.getParameter("voucherID");
            if (idStr != null && !idStr.isEmpty()) {
                p.setVoucherID(Integer.parseInt(idStr));
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

            if (p.getEndDate().before(p.getStartDate())) {
                request.setAttribute("error", "End date must be after start date.");
                request.setAttribute("voucher", p); // giữ lại dữ liệu vừa nhập
                request.setAttribute("showModal", true);
                request.setAttribute("list", dao.getAllVouchers());
                request.setAttribute("deletedList", dao.getDeletedVouchers());
                request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                return; // dừng, không add/update nữa
            }

                Voucher existing = dao.getVoucherByCode(p.getCode());
                if (p.getVoucherID() == 0 && existing != null) {
                    if (existing.isIsActive()) {
                        request.setAttribute("codeExists", true);
                        request.setAttribute("list", dao.getAllVouchers());
                        request.setAttribute("deletedList", dao.getDeletedVouchers());
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    } else {
                        request.setAttribute("voucher", existing);
                        request.setAttribute("reactivate", true);
                        request.setAttribute("list", dao.getAllVouchers());
                        request.setAttribute("deletedList", dao.getDeletedVouchers());
                        request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
                        return;
                    }
                }

                if (p.getVoucherID() == 0) {
                    dao.addVoucher(p);
                } else {
                    dao.updateVoucher(p);
                }

                response.sendRedirect("Voucher?action=list");
            }catch (Exception e) {
            request.setAttribute("error", "Invalid input: " + e.getMessage());
            try {
                request.setAttribute("list", dao.getAllVouchers());
                request.setAttribute("deletedList", dao.getDeletedVouchers());
            } catch (Exception ex) {
                request.setAttribute("error", "DB Error: " + ex.getMessage());
            }
            request.getRequestDispatcher("/manageVoucher.jsp").forward(request, response);
        }
        }
    }
