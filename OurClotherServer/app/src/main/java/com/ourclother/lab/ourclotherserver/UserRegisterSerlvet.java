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
public class UserRegisterSerlvet extends HttpServlet {
    private static final long serialVersionUID = -5845523250716130074L;

    private UserDao dao = new UserDaoImpl();

    /**
     * 注册成功，返回User对象的字符串
     * 用户名存在，返回 1
     * 邮箱从在，返回 2
     * 异常 返回 0
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String loginid = request.getParameter("loginId");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String gender = request.getParameter("gender");

        User u = new User();
        u.setLoginid(loginid);
        u.setPassword(password);
        u.setEmail(email);
        u.setGender(gender);

        if(!dao.isLoginIdExists(u.getLoginid())){
            if(!dao.isEmailExists(u.getEmail())){
                int flag = dao.addUsers(u);
                if(flag > 0)
                    out.print(flag);
                else
                    out.print("0");
            } else{
                out.print("2");
            }
        } else {
            out.print("3");
        }

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
