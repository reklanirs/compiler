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
/**
 * Created by Administrator on 2015/2/11.
 */
public class UserPasswordModifyServlet extends HttpServlet{
    private static final long serialVersionUID = -4701877664998115L;
    private UserDao dao = new UserDaoImpl();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        /**
         * res 结果判断
         * -1 修改错误
         * 0 修改成功
         * 1 原密码错误
         */
        String loginid = request.getParameter("loginid");
        String oldpwd = request.getParameter("oldpwd");
        String newpwd = request.getParameter("newpwd");
        String res = "";

        if(dao.isOldPasswordError(loginid, oldpwd))
            res = "1";
        else if(dao.modifyUserPassword(loginid, newpwd) != -1)
            res = "0";
        else
            res = "-1";

        out.print(res);

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

