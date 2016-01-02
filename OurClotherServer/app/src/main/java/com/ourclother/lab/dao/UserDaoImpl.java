package com.ourclother.lab.dao;

import com.ourclother.lab.model.User;
import com.ourclother.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Created by Administrator on 2015/2/10.
 */
public class UserDaoImpl implements UserDao{
    /**
     * 判断登陆账号是否存在
     * @param loginid
     * @return User
     */
    public boolean isLoginIdExists(String loginid) {
        boolean res = false;
        String sql = "select * from admins where loginId=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{loginid});
            rs = pstmt.executeQuery();
            if(rs.next())
                res = true;
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }

        return res;
    }

    /**
     * 判断邮箱是否存在
     * @param email
     * @return boolean
     */
    public boolean isEmailExists(String email) {
        boolean res = false;
        String sql = "select * from admins where email=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{email});
            rs = pstmt.executeQuery();
            if(rs.next())
                res = true;
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }

        return res;
    }

    /**
     * 注册用户
     * @param u
     * @return int
     */
    public int addUsers(User u) {
        int flag = 0;
        String sql = "insert into admins(loginId,password,email,gender)values(?,?,?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnForMySql();
           pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{u.getLoginid(),u.getPassword(),u.getEmail(),u.getGender()});

            flag = pstmt.executeUpdate();

        } catch (SQLException e) {
            flag = 0;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt);
        }

        return flag;
    }

    /**
     * 登陆
     * @param loginid
     * @param password
     * @return User
     */
    public User getUserByIdAndPwd(String loginid, String password) {
        User u = null;
        String sql = "select * from admins where loginId=? and password=?";
        //String sqluser = "select * from users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{loginid,password});

            rs = pstmt.executeQuery();
            while(rs.next()){
                u = new User();
                //u.setId(rs.getString("id"));
                u.setLoginid(rs.getString("loginId"));
                u.setPassword(rs.getString("password"));
               // u.setNikename(rs.getString("nikename"));
                u.setEmail(rs.getString("email"));
               // u.setPhone(rs.getString("phone"));
                u.setGender(rs.getString("gender"));

            }
        } catch (SQLException e) {
            u = null;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }
        System.out.println(u);
        return u;
    }

    /**
     * 判断邮箱是否存在
     * @param u
     * @return boolean
     */
    public boolean isEmailExists(User u) {
        boolean res = false;
        String sql = "select * from admins where email=? and loginId<>?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{u.getEmail(),u.getLoginid()});
            rs = pstmt.executeQuery();
            if(rs.next())
                res = true;
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }

        return res;
    }

    /**
     * 修改用户
     * @param u
     * @return int
     */
    public int modifyUserByLoginid(User u) {

        String sql = "update admins set nikename=?,email=?,phone=?,gender=? where loginId=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int res=-1;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{u.getNickname(),u.getEmail(),u.getPhone(),u.getGender(),u.getLoginid()});
            res = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt);
        }

        return res;
    }
    /**
     * 修改用户
     * @param u
     * @return int
     */
    public int addBodyDataByLoginid(User u) {

        String sql = "update admins set height=?,weight=?,shoulder=?,chest=?,waist=?,hip=?,trouser=?  where loginId=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int res=-1;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{u.getHeight(),u.getWeight(),u.getShoulder(),u.getChest(),u.getWaist(),u.getHip(),u.getTrouser(),u.getLoginid()});
            res = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt);
        }

        return res;
    }

    /**
     * 判断原密码是否错误
     * @param loginid
     * @param oldpwd
     * @return
     * @throws SQLException
     */
    public boolean isOldPasswordError(String loginid, String oldpwd) {
        boolean res = true;
        String sql = "select count(*) count from admins where loginId=? and password=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        conn = DBUtil.getConnForMySql();
        pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{loginid,oldpwd});
        int c = 0;
        try {
            rs = pstmt.executeQuery();
            if(rs.next())
                c = rs.getInt("count");
        } catch (SQLException e) {
            res = true;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }
        if(c > 0)
            res = false;

        return res;
    }

    /**
     * 修改密码
     * @param loginid
     * @param newpwd
     * @return
     */
    public int modifyUserPassword(String loginid, String newpwd) {
        String sql = "update admins set password=? where loginId=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int res=-1;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{newpwd,loginid});
            res = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt);
        }

        return res;
    }
    public String getClothCollect(String loginid){
        User u = null;
        String collect=null;
        String sql = "select * from admins where loginId=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{loginid});
            rs = pstmt.executeQuery();
            while(rs.next()){
                u = new User();
                u.setLoginid(rs.getString("loginId"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setGender(rs.getString("gender"));
                u.setHeight(rs.getString("height"));
                u.setWeight(rs.getString("weight"));
                u.setShoulder(rs.getString("shoulder"));
                u.setChest(rs.getString("chest"));
                u.setWaist(rs.getString("waist"));
                u.setHip(rs.getString("hip"));
                u.setTrouser(rs.getString("trouser"));
                u.setCollect(rs.getString("collect"));

            }
        } catch (SQLException e) {
            u = null;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }
        System.out.println(u);
        collect=u.getCollect();
        return collect;
    }
   public int addCollectCloth(String clothId,String loginid){

       int res=-1;
       User u = null;
       String collect=null;
       String clothid=getClothCollect(loginid);
       String[] collectSingle=null;
        collectSingle=clothid.split(",");
       System.out.println(clothId+"      ");
                for(int i=0;i<collectSingle.length;i++){
                    System.out.print(collectSingle[i]+"colcol+++++++");
                    if(clothId.equals(collectSingle[i]))
                    {
                        res=-2;//已收藏
                        System.out.println(res+"\n");
                        break;
                    }

                }
       System.out.println(res);
       if(res==-2)
       {
           return res;
       }else{
           if(clothid.equals(""))
               clothid=clothid+clothId;
           else
           {
               clothid=clothid+",";
               clothid=clothid+clothId;
           }

       String sql = "update admins set collect = ? where loginId = ?";
       Connection conn = null;
       PreparedStatement pstmt = null;


       try {
           conn = DBUtil.getConnForMySql();
           pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{clothid,loginid});
           System.out.println(pstmt);
           res = pstmt.executeUpdate();


       } catch (SQLException e) {
           u = null;
           e.printStackTrace();

       } finally{
           DBUtil.CloseResources(conn, pstmt);
       }



       return res;
    }
   }
    public int deleteCollectCloth(String clothId,String loginid){

        int res=-1;
        User u = null;
        String collect="";
        String clothid=getClothCollect(loginid);
        String[] collectSingle=null;
        collectSingle=clothid.split(",");
        System.out.println(clothId+"      ");
        for(int i=0;i<collectSingle.length;i++){
            System.out.print(collectSingle[i]+"colcol+++++++");

            if(clothId.equals(collectSingle[i]))
            {

                System.out.println(res+"\n");

            }else if(collect.equals(""))
                   collect=collect+collectSingle[i];
            else
            collect=collect+","+collectSingle[i];
        }
        System.out.println(res);



            String sql = "update admins set collect = ? where loginId = ?";
            Connection conn = null;
            PreparedStatement pstmt = null;


            try {
                conn = DBUtil.getConnForMySql();
                pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{collect,loginid});
                System.out.println(pstmt);
                res = pstmt.executeUpdate();


            } catch (SQLException e) {
                u = null;
                e.printStackTrace();

            } finally{
                DBUtil.CloseResources(conn, pstmt);
            }



            return res;

    }
}
