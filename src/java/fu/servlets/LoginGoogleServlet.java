/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fu.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import GoogleAPI.GooglePojo;
import GoogleAPI.GoogleUtils;
import fu.daos.MemberDAO;
import fu.entities.Member;
import javax.servlet.http.HttpSession;

/**
 *
 * @author LENOVO
 */
@WebServlet(name = "LoginGoogleServlet", urlPatterns = {"/login-google"})
public class LoginGoogleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String INDEX_PAGE = "paging";
    private static final String ADMIN_PAGE = "AdminListServlet";
    private static final String LOGIN_PAGE = "LoginServlet";

    public LoginGoogleServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String uri = INDEX_PAGE;
        try {            
            HttpSession session = request.getSession();
            String code = request.getParameter("code");
            if (code == null || code.isEmpty()) {               
               uri=LOGIN_PAGE;
               request.setAttribute("errormessage", "Please login!");
            } else {
                String accessToken = GoogleUtils.getToken(code);               
                GooglePojo googlePojo = GoogleUtils.getUserInfo(accessToken);

                Member member = new Member(googlePojo.getId(), googlePojo.getName(), googlePojo.getEmail(), googlePojo.getPicture(), "Your profile here", 1, 1, 0);
                MemberDAO mdao = new MemberDAO();
                String[] email = member.getMemberEmail().split("@");
                //check form mail
                if (email[1].equals("fpt.edu.vn") || email[1].equals("fe.edu.vn")) {
                    boolean check = mdao.checkMemberById(member.getMemberID());
                    //N???u user ch??a c?? th?? add
                    if (!check) {
                        mdao.addNewMember(member);
                        session.setAttribute("userdata", member);  
                    //N???u c?? r???i th?? l???y data c???a user ra ????? ph???c v??? hi???n th??? tr??n c??c view kh??c
                    } else {
                        member = mdao.find(member.getMemberID());
                        if(member.getStatus()==0){
                        request.setAttribute("errormessage", "Your account has been banned!"); 
                        uri=LOGIN_PAGE;
                        }else if(member.getMemberRole()==1){
                        session.setAttribute("userdata", member);
                        uri=INDEX_PAGE;
                        }else if(member.getMemberRole()==0){
                         session.setAttribute("userdata", member);
                        uri=ADMIN_PAGE;   
                        }
                    }
                }else{  // mail ko ????ng form
                    uri=LOGIN_PAGE;
                    request.setAttribute("errormessage", "Your email can not join this web!");
                }
            }
        } catch (Exception e) {
           System.out.println("error at LoginGoogleServlet");
           uri=LOGIN_PAGE;
           request.setAttribute("errormessage", "Something error here. Please login again!");
        } finally {
            request.getRequestDispatcher(uri).forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
