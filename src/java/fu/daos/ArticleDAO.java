/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fu.daos;

import fu.dbhelper.DBUtils;
import fu.entities.Article;
import fu.entities.ArticleType;
import fu.entities.Item;
import fu.entities.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class ArticleDAO {

    private List<Article> articles;
    private Connection con;
    private PreparedStatement stm;
    private ResultSet rs;

    public ArticleDAO() {
        try {
            this.articles = getAllArticles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Article> findAll() {
        return this.articles;
    }

    public Article find(String id) {
        for (Article a : this.articles) {
            if (a.getArticleID().equalsIgnoreCase(id)) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<Article> getAllArticles() throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "Select * From Article "
                        + "Where ArticleStatus = 1 or ArticleStatus = 0 "
                        + "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }

    public boolean updateContentArticle(Article b) throws Exception {
        Connection conn = null;
        PreparedStatement preStm = null;
        ResultSet rs = null;
        boolean check = false;        
        
        try {
            conn = DBUtils.makeConnection();
            if (conn != null) {
                if(b.getItem()==null){
                String sql = "UPDATE Article SET ArticleTitle = ?, ArticleContent = ?, ArticleStatus=?, ArticleTypeID=? Where ArticleID=?";
                preStm = conn.prepareStatement(sql);
                preStm.setString(1, b.getTitle());
                preStm.setString(2, b.getArticleContent());              
                preStm.setInt(3, b.getArticleStatus());
                preStm.setInt(4, b.getType().getTypeID());
                preStm.setString(5, b.getArticleID()); 
                }
                else if(b.getItem()!=null){
                String sql = "UPDATE Article SET ArticleTitle = ?, ArticleContent = ?, PostTime=?, ArticleStatus=?, ArticleTypeID=?, ItemID=? Where ArticleID=?";
                preStm = conn.prepareStatement(sql);
                preStm.setString(1, b.getTitle());
                preStm.setString(2, b.getArticleContent());
                preStm.setString(3, b.getPostTime());
                preStm.setInt(4, b.getArticleStatus());
                preStm.setInt(5, b.getType().getTypeID());
                preStm.setInt(6, b.getItem().getItemID());
                preStm.setString(7, b.getArticleID()); 
                }
                              
                preStm.executeUpdate();
                check = preStm.executeUpdate() > 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preStm != null) {
                preStm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean createNewArticle(Article b) throws SQLException {
        Connection con = null;
        PreparedStatement stm = null;
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                if(b.getImgUrl()==null && b.getItem()==null){
                String sql = "INSERT INTO Article (ArticleID, ArticleTitle, ArticleContent, PostTime, ArticleStatus, MemberID, ArticleTypeID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                stm = con.prepareStatement(sql);
                stm.setString(1, b.getArticleID());
                stm.setString(2, b.getTitle());
                stm.setString(3, b.getArticleContent());
                stm.setString(4, b.getPostTime());
                stm.setInt(5, 1);
                stm.setString(6, b.getMember().getMemberID());
                stm.setInt(7, b.getType().getTypeID());
                }
                else if(b.getImgUrl()!=null && b.getItem()==null){
                String sql = "INSERT INTO Article (ArticleID, ArticleTitle, ArticleContent, ImgURL, PostTime, ArticleStatus, MemberID, ArticleTypeID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                stm = con.prepareStatement(sql);
                stm.setString(1, b.getArticleID());
                stm.setString(2, b.getTitle());
                stm.setString(3, b.getArticleContent());
                stm.setString(4, b.getImgUrl());
                stm.setString(5, b.getPostTime());
                stm.setInt(6, 1);
                stm.setString(7, b.getMember().getMemberID());
                stm.setInt(8, b.getType().getTypeID()); 
                }
                else if(b.getImgUrl()==null && b.getItem()!=null){
                String sql = "INSERT INTO Article (ArticleID, ArticleTitle, ArticleContent, PostTime, ArticleStatus, MemberID, ArticleTypeID, ItemID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                stm = con.prepareStatement(sql);
                stm.setString(1, b.getArticleID());
                stm.setString(2, b.getTitle());
                stm.setString(3, b.getArticleContent());
                stm.setString(4, b.getPostTime());
                stm.setInt(5, 1);
                stm.setString(6, b.getMember().getMemberID());
                stm.setInt(7, b.getType().getTypeID());
                stm.setInt(8, b.getItem().getItemID()); 
                }
                else if(b.getImgUrl()!=null && b.getItem()!=null){
                String sql = "INSERT INTO Article (ArticleID, ArticleTitle, ArticleContent, ImgURL, PostTime, ArticleStatus, MemberID, ArticleTypeID, ItemID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stm = con.prepareStatement(sql);
                stm.setString(1, b.getArticleID());
                stm.setString(2, b.getTitle());
                stm.setString(3, b.getArticleContent());
                stm.setString(4, b.getImgUrl());
                stm.setString(5, b.getPostTime());
                stm.setInt(6, 1);
                stm.setString(7, b.getMember().getMemberID());
                stm.setInt(8, b.getType().getTypeID());
                stm.setInt(9, b.getItem().getItemID()); 
                }
//                String sql = "INSERT INTO Article (ArticleID, ArticleTitle, ArticleContent, ImgURL, PostTime, ArticleStatus, MemberID, ArticleTypeID, ItemID) "
//                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                stm = con.prepareStatement(sql);
//                stm.setString(1, b.getArticleID());
//                stm.setString(2, b.getTitle());
//                stm.setString(3, b.getArticleContent());
//                stm.setString(4, b.getImgUrl());
//                stm.setString(5, b.getPostTime());
//                stm.setInt(6, 1);
//                stm.setString(7, b.getMember().getMemberID());
//                stm.setInt(8, b.getType().getTypeID());
//                stm.setInt(9, b.getItem().getItemID());                 
                int row = stm.executeUpdate();
                if (row > 0) {
                    return true;
                }
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return false;
    }

    public boolean deleteArticle(String aId)
            throws Exception, SQLException {
        Connection con = null;
        PreparedStatement stm = null;
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "UPDATE Article "
                        + "SET ArticleStatus = -1 "
                        + "Where ArticleID = ?";
                stm = con.prepareStatement(sql);
                stm.setString(1, aId);
                int row = stm.executeUpdate();
                if (row > 0) {
                    return true;
                }
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return false;
    }
    
    //H??m n??y ????? ????ng t???t c??? c??c b??i vi???t c???a account b??? ban
    public boolean updateStatusArticlesOfMemberBanned(Member m) throws Exception {
        Connection conn = null;
        PreparedStatement preStm = null;
        ResultSet rs = null;
        boolean check = false;
        String sql = ("UPDATE Article SET ArticleStatus=0 Where MemberID=?");
        try {
            conn = DBUtils.makeConnection();
            if (conn != null) {
                preStm = conn.prepareStatement(sql);
                preStm.setString(1, m.getMemberID());                           
                preStm.executeUpdate();
                check = preStm.executeUpdate() > 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preStm != null) {
                preStm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
    //H??m n??y ????? ????ng b??i vi???t c???a account b??? c???nh c??o
    public boolean closeArticle(String aId) throws Exception {
        Connection conn = null;
        PreparedStatement preStm = null;
        ResultSet rs = null;
        boolean check = false;
        String sql = ("UPDATE Article SET ArticleStatus=0 Where ArticleID=?");
        try {
            conn = DBUtils.makeConnection();
            if (conn != null) {
                preStm = conn.prepareStatement(sql);
                preStm.setString(1, aId);                           
                preStm.executeUpdate();
                check = preStm.executeUpdate() > 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preStm != null) {
                preStm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
    //H??m n??y ????? ????ng b??i vi???t c???a account b??? c???nh c??o
    public boolean openArticle(String aId) throws Exception {
        Connection conn = null;
        PreparedStatement preStm = null;
        ResultSet rs = null;
        boolean check = false;
        String sql = ("UPDATE Article SET ArticleStatus=1 Where ArticleID=?");
        try {
            conn = DBUtils.makeConnection();
            if (conn != null) {
                preStm = conn.prepareStatement(sql);
                preStm.setString(1, aId);                           
                preStm.executeUpdate();
                check = preStm.executeUpdate() > 0;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (preStm != null) {
                preStm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
    
  // L???y t???t c??? c??c b??i lo???i "T??m ?????"
    public ArrayList<Article> getAllArticlesFind() throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "Where A.ArticleTypeID = 1 and ArticleStatus = 1  Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y t???t c??? c??c b??i lo???i "Tr??? ?????"
    public ArrayList<Article> getAllArticlesReturn() throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "Where A.ArticleTypeID = 2 and ArticleStatus = 1  Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    
    // L???y t???t c??? c??c b??i lo???i "Share"
    public ArrayList<Article> getAllArticlesShare() throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "Where A.ArticleTypeID = 3 and ArticleStatus = 1  Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    
    // L???y t???t c??? c??c b??i lo???i "Th??ng b??o"
    public ArrayList<Article> getAllArticlesNotice() throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "Where A.ArticleTypeID = 4 and ArticleStatus = 1  Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    
    // L???y t???t c??? c??c b??i lo???i "T??m ?????" v?? lo???i ????? v???t theo y??u c???u
    public ArrayList<Article> getAllArticlesFindByItemType(Item i) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "				inner join ItemType I on I.ItemID = A.ItemID\n" +
                            "Where A.ArticleTypeID = 1 and A.ArticleStatus = 1 and A.ItemID = ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setInt(1, i.getItemID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y t???t c??? c??c b??i lo???i "Tr??? ?????" v?? lo???i ????? v???t theo y??u c???u
    public ArrayList<Article> getAllArticlesReturnByItemType(Item i) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "				inner join ItemType I on I.ItemID = A.ItemID\n" +
                            "Where A.ArticleTypeID = 2 and A.ArticleStatus = 1 and A.ItemID = ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setInt(1, i.getItemID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y t???t c??? c??c b??i lo???i "Th??ng b??o" v?? lo???i ????? v???t theo y??u c???u
    public ArrayList<Article> getAllArticlesNoticeByItemType(Item i) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +
                            "				inner join ItemType I on I.ItemID = A.ItemID\n" +
                            "Where A.ArticleTypeID = 4 and A.ArticleStatus = 1 and A.ItemID = ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setInt(1, i.getItemID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search t???t c??? c??c b??i lo???i "T??m ?????" theo t??? kh??a
    public ArrayList<Article> searchAllArticlesFindByName(String key) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +                          
                            "Where A.ArticleTypeID = 1 and A.ArticleStatus =1 and A.ArticleContent Like ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, "%"+key+"%");
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search t???t c??? c??c b??i lo???i "Tr??? ?????" theo t??? kh??a
    public ArrayList<Article> searchAllArticlesReturnByName(String key) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +                          
                            "Where A.ArticleTypeID = 2 and A.ArticleStatus = 1 and A.ArticleContent Like ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, "%"+key+"%");
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search t???t c??? c??c b??i lo???i "Notice" theo t??? kh??a
    public ArrayList<Article> searchAllArticlesNoticeByName(String key) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join ArticleType AType on A.ArticleTypeID = AType.ArticleTypeID\n" +                          
                            "Where A.ArticleTypeID = 4 and A.ArticleStatus = 1 and A.ArticleContent Like ?\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, "%"+key+"%");
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search all post find voi hashtag
    public ArrayList<Article> searchAllArticlesFindByHashtag(String hId) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID,A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID\n" +
                            "from Article A inner join ArticleHashtag AH on A.ArticleID = AH.ArticleID\n" +
                            "				inner join Hashtag H on AH.HashtagID = H.HashtagID\n" +
                            "where H.HashtagID = ? and A.ArticleTypeID = 1\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, hId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search all post find voi hashtag
    public ArrayList<Article> searchAllArticlesReturnByHashtag(String hId) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID,A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID\n" +
                            "from Article A inner join ArticleHashtag AH on A.ArticleID = AH.ArticleID\n" +
                            "				inner join Hashtag H on AH.HashtagID = H.HashtagID\n" +
                            "where H.HashtagID = ? and A.ArticleTypeID = 2\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, hId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // Search all post find voi hashtag
    public ArrayList<Article> searchAllArticlesNoticeByHashtag(String hId) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.MemberID, A.ArticleTypeID, A.ItemID\n" +
                            "from Article A inner join ArticleHashtag AH on A.ArticleID = AH.ArticleID\n" +
                            "				inner join Hashtag H on AH.HashtagID = H.HashtagID\n" +
                            "where H.HashtagID = ? and A.ArticleTypeID = 4\n" +
                            "Order By PostTime DESC";
                stm = con.prepareStatement(sql);
                stm.setString(1, hId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");
                    String memberId = rs.getString("MemberID");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y all b??i vi???t t??m ????? m?? member ???? ????ng
    public ArrayList<Article> getAllArticlesFindByMemberID(Member m) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join Member M on M.MemberID = A.MemberID\n" +
                            "Where A.ArticleTypeID = 1 and A.ArticleStatus not like -1 and M.MemberID Like ?";
                stm = con.prepareStatement(sql);
                stm.setString(1,m.getMemberID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");                    
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");                    
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);                    
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y all b??i vi???t m?? member ???? ????ng
    public ArrayList<Article> getAllArticlesByMemberID(Member m) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join Member M on M.MemberID = A.MemberID\n" +
                            "Where A.ArticleStatus not like -1 and M.MemberID Like ?";
                stm = con.prepareStatement(sql);
                stm.setString(1,m.getMemberID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");                    
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");                    
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);                    
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y all b??i vi???t tr??? ????? m?? member ???? ????ng
    public ArrayList<Article> getAllArticlesReturnByMemberID(Member m) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join Member M on M.MemberID = A.MemberID\n" +
                            "Where A.ArticleTypeID = 2 and A.ArticleStatus not like -1 and M.MemberID Like ?";
                stm = con.prepareStatement(sql);
                stm.setString(1,m.getMemberID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");                    
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");                    
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    // L???y all b??i vi???t chia s??? m?? member ???? ????ng
    public ArrayList<Article> getAllArticlesShareByMemberID(Member m) throws ClassNotFoundException, SQLException, Exception {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "select A.ArticleID, A.ArticleTitle, A.ArticleContent, A.ImgURL, A.PostTime, A.ArticleStatus, A.ArticleTypeID, A.ItemID \n" +
                            "from Article A inner join Member M on M.MemberID = A.MemberID\n" +
                            "Where A.ArticleTypeID = 3 and A.ArticleStatus not like -1 and M.MemberID Like ?";
                stm = con.prepareStatement(sql);
                stm.setString(1,m.getMemberID());
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String title = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");                    
                    int articleStatus = rs.getInt("ArticleStatus");                    
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    int itemId = rs.getInt("ItemID");                    
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId, title, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return lb;
    }
    public int getNumberPage() {
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String query = "Select count(*) from Article where ArticleTypeID = 1";
                stm = con.prepareStatement(query);
                rs = stm.executeQuery();
                while (rs.next()) {
                    int total = rs.getInt(1);
                    int coutPage = 0;
                    coutPage = total / 10;
                    if (total % 10 != 0) {
                        coutPage++;
                    }
                    return coutPage;
                }
            }
        } catch (Exception e) {

        }
        return 0;
    }

    public ArrayList<Article> getPaging(int index) {
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "Select * from Article\n"
                        + "Where ArticleTypeID = 1 and ArticleStatus = 1\n"
                        + "order by PostTime DESC \n"
                        + "OFFSET ? ROWS\n"
                        + "FETCH FIRST 10 ROWS ONLY;";
                stm = con.prepareStatement(sql);
                stm.setInt(1, (index - 1) * 10);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String articleTitle = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");
                    int articleStatus = rs.getInt("ArticleStatus");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    String memberId = rs.getString("MemberID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId,articleTitle, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } catch (Exception e) {

        }
        return lb;
    }
    public int getNumberPageReturn() {
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String query = "Select count(*) from Article where ArticleTypeID = 2";
                stm = con.prepareStatement(query);
                rs = stm.executeQuery();
                while (rs.next()) {
                    int total = rs.getInt(1);
                    int coutPage = 0;
                    coutPage = total / 10;
                    if (total % 10 != 0) {
                        coutPage++;
                    }
                    return coutPage;
                }
            }
        } catch (Exception e) {

        }
        return 0;
    }

    public ArrayList<Article> getPagingReturn(int index) {
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "Select * from Article\n"
                        + "Where ArticleTypeID = 2  and ArticleStatus = 1\n "
                        + "order by PostTime DESC \n"
                        + "OFFSET ? ROWS\n"
                        + "FETCH FIRST 10 ROWS ONLY;";
                stm = con.prepareStatement(sql);
                stm.setInt(1, (index - 1) * 10);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String articleTitle = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");
                    int articleStatus = rs.getInt("ArticleStatus");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    String memberId = rs.getString("MemberID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId,articleTitle, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                    System.out.println(art.getType().getTypeID());
                }
            }
        } catch (Exception e) {

        }
        return lb;
    }
    public int getNumberPageExperience() {
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String query = "Select count(*) from Article where ArticleTypeID = 4";
                stm = con.prepareStatement(query);
                rs = stm.executeQuery();
                while (rs.next()) {
                    int total = rs.getInt(1);
                    int coutPage = 0;
                    coutPage = total / 10;
                    if (total % 10 != 0) {
                        coutPage++;
                    }
                    return coutPage;
                }
            }
        } catch (Exception e) {

        }
        return 0;
    }

    public ArrayList<Article> getPagingExperience(int index) {
        ArrayList<Article> lb = new ArrayList<>();
        try {
            con = DBUtils.makeConnection();
            if (con != null) {
                String sql = "Select * from Article\n"
                        + "Where ArticleTypeID = 4 and ArticleStatus = 1 \n"
                        + "order by PostTime DESC \n"
                        + "OFFSET ? ROWS\n"
                        + "FETCH FIRST 10 ROWS ONLY;";
                stm = con.prepareStatement(sql);
                stm.setInt(1, (index - 1) * 10);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String articleId = rs.getString("ArticleID");
                    String articleTitle = rs.getString("ArticleTitle");
                    String articleContent = rs.getString("ArticleContent");
                    String articleURL = rs.getString("ImgURL");
                    String articleTime = rs.getString("PostTime");
                    int articleStatus = rs.getInt("ArticleStatus");
                    int articleTypeId = rs.getInt("ArticleTypeID");
                    String memberId = rs.getString("MemberID");
                    int itemId = rs.getInt("ItemID");
                    MemberDAO mdao = new MemberDAO();
                    Member m = mdao.find(memberId);
                    ItemTypeDAO idao = new ItemTypeDAO();
                    Item i = idao.getItemByID(itemId);
                    ArticleTypeDAO adao = new ArticleTypeDAO();
                    ArticleType a = adao.getArticleTypeByID(articleTypeId);
                    Article art = new Article(articleId,articleTitle, articleContent, articleURL, articleTime, articleStatus, i, m, a);
                    lb.add(art);
                }
            }
        } catch (Exception e) {

        }
        return lb;
    }
}
