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
public class BodyDataServlet extends HttpServlet{
    private static final long serialVersionUID = -4701632877664998115L;
    private UserDao dao = new UserDaoImpl();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String loginId = request.getParameter("loginId");
        String height = request.getParameter("height");
        String weight = request.getParameter("weight");
        String shoulder = request.getParameter("shoulder");
        String chest = request.getParameter("chest");
        String waist = request.getParameter("waist");
        String hip = request.getParameter("hip");
        String trouser = request.getParameter("trouser");

        User u = new User();
        u.setLoginid(loginId);
        u.setHeight(height);
        u.setWeight(weight);
        u.setShoulder(shoulder);
        u.setChest(chest);
        u.setWaist(waist);
        u.setHip(hip);
        u.setTrouser(trouser);

        /**
         *  -1 修改错误
         *  0 信息添加成功
         */
        int res = -1;
        if(dao.addBodyDataByLoginid(u) != -1)
            res = 0;
        else
            res = -1;

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

