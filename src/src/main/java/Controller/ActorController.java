/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import DAO.ActorDAO;
import Model.Actor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author Le Quang Giang - CE182527
 */
@WebServlet("/Actor")
public class ActorController extends HttpServlet {
    private final ActorDAO dao = new ActorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int id = request.getParameter("id") != null ? Integer.parseInt(request.getParameter("id")) : -1;

            if ("ban".equals(action)) {
                dao.setActorStatus(id, false);
            } else if ("unban".equals(action)) {
                dao.setActorStatus(id, true);
            } else if ("delete".equals(action)) {
                dao.deleteActor(id);
            } else if ("view".equals(action) || "update".equals(action)) {
                Actor actor = dao.getActorByID(id);
                if (actor == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Actor not found");
                    return;
                }
                request.setAttribute("actor", actor);
                String target = "viewActor.jsp";
                if ("update".equals(action)) target = "updateActor.jsp";
                request.getRequestDispatcher(target).forward(request, response);
                return;
            } else if ("search".equals(action)) {
                String keyword = request.getParameter("keyword");
                List<Actor> list = (keyword != null && !keyword.trim().isEmpty())
                        ? dao.searchActors(keyword.trim())
                        : dao.getAllActors();
                request.setAttribute("actors", list);
                request.setAttribute("keyword", keyword);
                request.getRequestDispatcher("manageActor.jsp").forward(request, response); // ✅ Đã sửa ở đây
                return;
            }

            String keyword = request.getParameter("keyword");
            List<Actor> list = (keyword != null && !keyword.trim().isEmpty())
                    ? dao.searchActors(keyword.trim())
                    : dao.getAllActors();
            request.setAttribute("actors", list);
            request.setAttribute("keyword", keyword);
            request.getRequestDispatcher("manageActor.jsp").forward(request, response); // ✅ Và cả ở đây nữa

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("updateSubmit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("actorID"));
                String name = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");

                Actor actor = dao.getActorByID(id);
                actor.setFullName(name);
                actor.setEmail(email);
                actor.setPhone(phone);

                dao.updateActor(actor);
                response.sendRedirect("Actor");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
            }
        }
    }
}
