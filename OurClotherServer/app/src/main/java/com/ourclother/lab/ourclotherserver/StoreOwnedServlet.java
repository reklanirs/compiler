package com.ourclother.lab.ourclotherserver;
import java.io.IOException;
import java.util.List;
import com.ourclother.lab.dao.StoreDaoImpl;
import com.ourclother.lab.dao.StoreDao;
import com.ourclother.lab.model.Store;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * Created by Administrator on 2015/4/24.
 */
public class StoreOwnedServlet extends HttpServlet {
private static final long serialVersionUID = -2801273997474096868L;

private StoreDao dao = new StoreDaoImpl();

/**
 * 用户收藏的所有衣服ID号
 */
@SuppressWarnings("unchecked")
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String owned=null;
        String storeID = request.getParameter("storeID").trim();
        /**
         * 判断用户是否存在
         * */
        boolean isExist = false;
        isExist = dao.isStoreIdExists(storeID);
        if(true==isExist){
        owned= dao.getStoreClothById(storeID);

        System.out.println("90909090");
        out.print(owned);

        }else{
        out.print("-1");
        System.out.println("99999999");
        out.flush();
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


