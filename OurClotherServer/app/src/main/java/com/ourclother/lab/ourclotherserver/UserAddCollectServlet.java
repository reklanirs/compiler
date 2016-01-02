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
 * Created by Administrator on 2015/4/24.
 */
public class UserAddCollectServlet extends HttpServlet {
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

        String loginid = request.getParameter("loginId");
        String clothId = request.getParameter("clothId");



        User u = new User();
        u.setLoginid(loginid);
        u.setCollect(clothId);

       int res = -1;
        res=dao.addCollectCloth(clothId, loginid);
        System.out.println(res);
        out.print(res);
        out.flush();
        out.close();
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
