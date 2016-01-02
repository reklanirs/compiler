package com.ourclother.lab.ourclotherserver;

/**
 * Created by Guestyang on 2016/1/3.
 */
import java.io.IOException;
import java.util.List;

import com.ourclother.lab.dao.ClothDao;
import com.ourclother.lab.dao.ClothDaoImpl;
import com.ourclother.lab.dao.UserDaoImpl;
import com.ourclother.lab.dao.UserDao;
import com.ourclother.lab.model.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
public class HomeServlet extends HttpServlet{
    private static final long serialVersionUID = -2801273997474096868L;
    private ClothDao dao = new ClothDaoImpl();
    /**
     * 所有衣服ID号
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String collect=null;
        collect= dao.getAllCloth();
        out.print(collect);
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
