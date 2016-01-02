package com.ourclother.lab.ourclotherserver;

import javax.servlet.http.HttpServlet;
import com.ourclother.lab.dao.UserDaoImpl;
import com.ourclother.lab.dao.UserDao;
import com.ourclother.lab.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
/**
 * Created by Administrator on 2015/2/11.
 */
public class UserLoginServlet extends HttpServlet{
    private static final long serialVersionUID = -2801273997474096868L;

    private UserDao dao = new UserDaoImpl();

    /**
     * 登陆
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String loginid = request.getParameter("loginId").trim();
        String password = request.getParameter("password").trim();

        System.out.println(loginid+"----------"+password);
        /**
         * 判断用户是否存在
         * */
        User u;
        boolean isExist = false;
        isExist = dao.isLoginIdExists(loginid);
        if(true==isExist){
            u= dao.getUserByIdAndPwd(loginid, password);
            System.out.println("user的loginid"+u.getLoginid());
            out.print(user2String(u));
            System.out.println("90909090");
            System.out.println(user2String(u));
        }else{
            String jtest="好";
            java.net.URLEncoder.encode(jtest, "utf-8");
            out.print("-2");

            System.out.println(jtest+"99999999");
            out.flush();
        }
        out.flush();
        out.close();
    }

    private String user2String(User u) {
        StringBuilder s = new StringBuilder();
        s .append(u.getLoginid()).append(",")
                .append(u.getPassword()).append(",")
               // .append(u.getNikename()==null?"昵称":u.getNikename()).append(",")
               // .append(u.getPhone()==null?"手机":u.getPhone()).append(",")
                .append(u.getEmail()).append(",")
                .append(u.getGender());

        return s.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

}
