package com.ourclother.lab.ourclotherserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/2/9.
 */
public class FirstServlet extends HttpServlet{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID=217251451801586160L;

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {
        //设定内容类型为HTML网页UTF-8编码
        resp.setContentType("text/html;charset=UTF-8");
        //输出页面
        PrintWriter out=resp.getWriter();
        out.println("<html><head>");
        out.println("<title>First Servlet Hello</title>");
        out.println("</head><body>");
        out.println("Hello!大家好!");
        out.println("</body></html>");
        out.close();

    }
}
