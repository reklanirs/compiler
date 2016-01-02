package com.ourclother.lab.dao;
import com.ourclother.lab.model.Cloth;
import com.ourclother.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2015/3/15.
 */
public class ClothDaoImpl implements ClothDao {
    /**
     * 判断衣服Id是否存在
     * @param id
     * @return boolean
     */
    public boolean isClothIdExists(int clothId){
        boolean res = false;
        String idTemp= String.valueOf(clothId);
        String sql = "select * from cloths where ID=?";
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{idTemp});
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
     * 返回图片
     * @param clothId
     * @return String
     */
    public String getClothById(int clothId){
        JSONObject clothInfos =  new JSONObject();
        JSONArray clothArray = new JSONArray();
        Cloth cloth = null;
        String idTemp= String.valueOf(clothId);
        String sql = "select * from cloths where ID=?";
        String DESCRIB=null;
        String PICTURE=null;
        String MAINPIC=null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{idTemp});

            rs = pstmt.executeQuery();
            while(rs.next()){
                cloth = new Cloth();
                //u.setId(rs.getString("id"));
                DESCRIB=rs.getString("DESCRIB");
                PICTURE=rs.getString("PICTURE");
                MAINPIC=rs.getString("MAINPIC");
                java.net.URLEncoder.encode(DESCRIB, "utf-8");
                java.net.URLEncoder.encode(PICTURE, "utf-8");
                java.net.URLEncoder.encode(MAINPIC, "utf-8");
                cloth.setId(rs.getInt("ID"));
                cloth.setDescrib(DESCRIB);

                cloth.setPicture(PICTURE);

                cloth.setMainPict(MAINPIC);
                JSONObject clothinfo = new JSONObject();
                clothinfo.put("clothID", rs.getInt("ID"));
                clothinfo.put("clothDESCRIB",DESCRIB);
                System.out.println(DESCRIB+"     ");
                clothinfo.put("clothPICTURE", PICTURE);
                clothinfo.put("clothMAINPIC",MAINPIC);
                clothArray.add(clothinfo);


            }
            clothInfos.put("cloths", clothArray);
        } catch (SQLException e) {
            cloth = null;
            e.printStackTrace();
        }catch(java.io.UnsupportedEncodingException e)
        {

        }
        finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }
        System.out.println(cloth);
        //return cloth;
        String clothString=null;
        try {
            clothString = java.net.URLEncoder.encode(clothInfos.toString(), "utf-8");
        }catch(java.io.UnsupportedEncodingException e)
        {

        }
        return clothString;
    }
    /**
     * 返回图片
     * @param none
     * @return Cloth Ids
     */
    public String getAllCloth(){
        JSONObject clothInfos =  new JSONObject();
        JSONArray clothArray = new JSONArray();
        Cloth cloth = null;
        String sql = "select ID from cloths where 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String clothString="";
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql);

            rs = pstmt.executeQuery();
            while(rs.next()){
                clothString+=rs.getInt("ID");
                if(!rs.isLast())
                    clothString+=",";
            }

        } catch (SQLException e) {
            cloth = null;
            e.printStackTrace();
        }
        finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }
        //return clothes
        return clothString;

    }
}
