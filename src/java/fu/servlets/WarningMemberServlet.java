/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fu.servlets;

import fu.daos.ArticleDAO;
import fu.daos.MemberDAO;
import fu.daos.ReportDAO;
import fu.entities.Article;
import fu.entities.Member;
import java.io.IOException;
import java.util.ArrayList;
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
@WebServlet(name = "WarningMemberServlet", urlPatterns = {"/WarningMemberServlet"})
public class WarningMemberServlet extends HttpServlet {
    private static final String ADMIN = "AdminListServlet";
    private static final String LIST_MEMBERS_PAGE = "ListMemberServlet";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = ADMIN;
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                request.setAttribute("errormessage", "Please login!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            if (session.getAttribute("userdata") != null) {
                Member member = (Member) session.getAttribute("userdata");
                String uId = request.getParameter("uId");
                String aId = request.getParameter("aId");
                String adminAction = request.getParameter("adminAction");

                ArticleDAO aDao = new ArticleDAO();
                Article a = aDao.find(aId);

                MemberDAO mdao = new MemberDAO();

                ReportDAO rdao = new ReportDAO();

                // Trường hợp member bị cảnh cáo
                if (adminAction.equalsIgnoreCase("warn")) {
                    // Tìm member đăng bài post by id rồi tăng count của member post bài lên +1
                    if (aId != null) {
                        Member memberWarn = mdao.find(a.getMember().getMemberID());
                        memberWarn.setMemberCount(memberWarn.getMemberCount() + 1);
                        mdao.warningMember(memberWarn);

                        //Thay đổi trạng thái của tất cả report về bài viết này thành 0 (tức là đã xử lý all report của bài viết)
                        rdao.updateStatusReport(aId);

                        //Thay đổi trạng thái bài viết thành InActive
                        aDao.closeArticle(aId);
                    } else if (uId != null) {
                        Member memberWarn = mdao.find(uId);
                        memberWarn.setMemberCount(memberWarn.getMemberCount() + 1);
                        mdao.warningMember(memberWarn);
                        url=LIST_MEMBERS_PAGE;
                    }

                    // Trường hợp member bị ban
                } else if (adminAction.equalsIgnoreCase("ban")) {
                    // Tìm member đăng bài post by id rồi tăng count của member post bài lên +1
                    if (aId != null) {
                        Member memberBan = mdao.find(a.getMember().getMemberID());
                        memberBan.setStatus(0);
                        mdao.banOrUnbanMember(memberBan);

                        //Thay đổi trạng thái tất cả bài viết của member bị ban thành InActive
                        aDao.updateStatusArticlesOfMemberBanned(memberBan);

                        //Thay đổi trạng thái của tất cả report của tất cả các bài viết của member bị ban này thành 0
                        //Step1: Lấy ra tất cả bài viết của member bị ban
                        ArrayList<Article> listPostOfMemBan = aDao.getAllArticlesByMemberID(memberBan);
                        for (Article article : listPostOfMemBan) {
                            //Step 2: Xử lý hết tất cả report liên quan tới member này.
                            rdao.updateStatusReport(article.getArticleID());
                        }
                    } else if (uId != null) {
                        Member memberBan = mdao.find(uId);
                        memberBan.setStatus(0);
                        mdao.banOrUnbanMember(memberBan);
                        aDao.updateStatusArticlesOfMemberBanned(memberBan);
                         url=LIST_MEMBERS_PAGE;
                    }
                    // Trường hợp member bị tố cáo không có vấn đề gì
                } else if (adminAction.equalsIgnoreCase("none")) {
                    //Thay đổi trạng thái của tất cả report về bài viết này thành 0 (tức là đã xử lý all report của bài viết)
                    rdao.updateStatusReport(aId);
                    // Trả bài viết về trạng thái Active
                    aDao.openArticle(aId);
                } else if (adminAction.equalsIgnoreCase("unban")) {
                    Member memberBan = mdao.find(uId);
                    memberBan.setStatus(1);
                    mdao.banOrUnbanMember(memberBan);
                     url=LIST_MEMBERS_PAGE;
                }
                

            } else {
                request.setAttribute("errormessage", "Please login!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("ERROR at WarningMemberServlet: " + e.getMessage());
        }finally{
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
