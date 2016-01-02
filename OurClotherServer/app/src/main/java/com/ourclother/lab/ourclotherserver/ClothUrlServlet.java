package com.ourclother.lab.ourclotherserver;
import javax.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.ourclother.lab.dao.ClothDao;
import com.ourclother.lab.dao.ClothDaoImpl;
import com.ourclother.lab.model.Cloth;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
/**
 * Created by Administrator on 2015/3/15.
 */
public class ClothUrlServlet extends HttpServlet{
    private static final long serialVersionUID = -2801273997474096868L;

    private ClothDao dao = new ClothDaoImpl();

    /**
     * 获取图片url
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String clothId = request.getParameter("clothId").trim();
        
        System.out.println("clothid-----"+clothId);

       
        int clothIdTemp=Integer.valueOf(clothId).intValue();



        System.out.println(clothId+"----------");
        /**
         * 判断用户是否存在
         * */
        Cloth u;
        String str = null;

        boolean isExist = false;
        isExist = dao.isClothIdExists(clothIdTemp);
        if(true==isExist){
            str = dao.getClothById(clothIdTemp);
            System.out.println("json"+str);
            System.out.println("90909090");
            out.print(str);
            System.out.println(str);
            System.out.println("result");
        }else{
            out.print("获取图片JSON出错");
            System.out.println("99999999");
            out.flush();
        }
        out.flush();
        out.close();
    }

    private String user2String(Cloth u) {
        StringBuilder s = new StringBuilder();
        s .append(u.getId()).append(",")
                .append(u.getDescrib()).append(",")
                // .append(u.getNikename()==null?"昵称":u.getNikename()).append(",")
                // .append(u.getPhone()==null?"手机":u.getPhone()).append(",")
                .append(u.getPicture()).append(",")
                .append(u.getMainPict());

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





