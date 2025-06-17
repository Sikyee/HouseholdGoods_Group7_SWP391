/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Model.Feedback;
import dao.FeedbackDAO;


import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author thach
 */


@WebServlet("/feedback")
public class FeedbackController extends HttpServlet {
    private FeedbackDAO feedbackDAO;

    @Override
    public void init() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=HouseholdGoods;user=sa;password=123456");
            feedbackDAO = new FeedbackDAO(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gọi: /feedback?action=view&id=...
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            switch (action) {
                case "edit":
                    Feedback fb = feedbackDAO.getFeedbackById(id);
                    req.setAttribute("fb", fb);
                    req.getRequestDispatcher("editFeedback.jsp").forward(req, resp);
                    break;

                case "delete":
                    feedbackDAO.deleteFeedback(id);
                    resp.sendRedirect("manageFeedback.jsp");
                    break;

                case "view":
                    Feedback detail = feedbackDAO.getFeedbackById(id);
                    req.setAttribute("fb", detail);
                    req.getRequestDispatcher("viewFeedback.jsp").forward(req, resp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("error.jsp");
        }
    }

    // Gọi khi POST submit form chỉnh sửa hoặc phản hồi
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String action = req.getParameter("action");

            if ("edit".equals(action)) {
                String content = req.getParameter("content");
                feedbackDAO.updateFeedback(id, content);
            } else if ("reply".equals(action)) {
                String reply = req.getParameter("reply");
                feedbackDAO.replyToFeedback(id, reply);
            }

            resp.sendRedirect("manageFeedback.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("error.jsp");
        }
    }
}
