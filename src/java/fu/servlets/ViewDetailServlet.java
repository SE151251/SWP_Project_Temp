/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fu.servlets;

import fu.daos.ArticleDAO;
import fu.daos.CommentDAO;
import fu.daos.HashtagDAO;
import fu.daos.MemberDAO;
import fu.daos.ReportDAO;
import fu.entities.Article;
import fu.entities.Hashtag;
import fu.entities.Member;
import fu.entities.Report;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author LENOVO
 */
@WebServlet(name = "ViewDetailSevlet", urlPatterns = {"/ViewDetailServlet"})
public class ViewDetailServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {           
            HttpSession session = request.getSession(false);
            if (session == null) {
                request.setAttribute("errormessage", "Please login!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            if (session.getAttribute("userdata") != null) {
                Member member = (Member) session.getAttribute("userdata");
                String aId = request.getParameter("aId");
                String memRpId = request.getParameter("memReportID");
                ArticleDAO aDao = new ArticleDAO();
                Article a = aDao.find(aId);
                request.setAttribute("postDetail", a);
                CommentDAO cdao = new CommentDAO();
                request.setAttribute("listCmt", cdao.getAllCommentsByArticles(a));
                HashtagDAO hDao = new HashtagDAO();
                List<Hashtag> listAH = hDao.getAllHashtagByArticleID(aId);
                request.setAttribute("listAH", listAH);
                MemberDAO mdao = new MemberDAO();
                ReportDAO rdao = new ReportDAO();
                Report r = rdao.checkReport(aId, member.getMemberID());
                if(memRpId != null){
                Report rView = rdao.checkReport(aId, memRpId);
                request.setAttribute("viewReport", rView);
                }
                request.setAttribute("checkReport", r);
                
                request.getRequestDispatcher("detail.jsp").forward(request, response);

            } else {
                request.setAttribute("errormessage", "Please login!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("ERROR at ViewDetailServlet: " + e.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
