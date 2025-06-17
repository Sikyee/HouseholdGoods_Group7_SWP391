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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

public class VoucherController extends HttpServlet {
    VoucherDAO dao = new VoucherDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("voucher", dao.getById(editId));
                break;
            case "delete":
                int deleteId = Integer.parseInt(request.getParameter("id"));
                dao.delete(deleteId);
                break;
        }

        request.setAttribute("list", dao.getAll());
        request.getRequestDispatcher("ManageVoucher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Voucher v = new Voucher();
        v.setCode(request.getParameter("code"));
        v.setDescription(request.getParameter("description"));
        v.setDiscountType(request.getParameter("discountType"));
        v.setDiscountValue(Integer.parseInt(request.getParameter("discountValue")));
        v.setStartDate(Date.valueOf(request.getParameter("startDate")));
        v.setEndDate(Date.valueOf(request.getParameter("endDate")));
        v.setMinOrderValue(Integer.parseInt(request.getParameter("minOrderValue")));
        v.setMaxUsage(Integer.parseInt(request.getParameter("maxUsage")));
        v.setUsedCount(Integer.parseInt(request.getParameter("usedCount")));
        v.setIsActive(Boolean.parseBoolean(request.getParameter("isActive")));

        String voucherID = request.getParameter("voucherID");
        if (voucherID == null || voucherID.isEmpty()) {
            dao.insert(v);
        } else {
            v.setVoucherID(Integer.parseInt(voucherID));
            dao.update(v);
        }

        response.sendRedirect("VoucherController");
    }
}

