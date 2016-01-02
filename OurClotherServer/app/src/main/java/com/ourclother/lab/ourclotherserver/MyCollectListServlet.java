package com.ourclother.lab.ourclotherserver;

/**
 * Created by Administrator on 2015/4/1.
 */
import java.io.IOException;
import java.util.List;
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


public class MyCollectListServlet extends HttpServlet {
	private static final long serialVersionUID = -2801273997474096868L;

	private UserDao dao = new UserDaoImpl();

	/**
	 * 用户收藏的所有衣服ID号
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String collect=null;
        String loginid = request.getParameter("loginId").trim();
        /**
         * 判断用户是否存在
         * */
        boolean isExist = false;
        isExist = dao.isLoginIdExists(loginid);
        if(true==isExist){
            collect= dao.getClothCollect(loginid);

            System.out.println("90909090");
            out.print(collect);

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

