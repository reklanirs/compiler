package com.ourclother.lab.dao;
import com.ourclother.lab.model.Store;
import com.ourclother.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Created by Administrator on 2015/4/24.
 */
public class StoreDaoImpl implements com.ourclother.lab.dao.StoreDao {
    /**
     * 判断衣服Id是否存在
     * @param id
     * @return boolean
     */
    public boolean isStoreIdExists(String storeId){
        boolean res = false;

        String sql = "select * from stores where storeID=?";
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{storeId});
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
    public String getStoreClothById(String storeId){
        Store store=null;
        String owned=null;
        String sql = "select * from stores where storeID=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnForMySql();
            pstmt = DBUtil.getPreparedStatemnt(conn, sql, new String[]{storeId});
            rs = pstmt.executeQuery();
            while(rs.next()){
                store = new Store();
                store.setStoreId(rs.getString("storeID"));
                store.setPassword(rs.getString("password"));
                store.setOwned(rs.getString("owned"));

            }
        } catch (SQLException e) {
            store = null;
            e.printStackTrace();
        } finally{
            DBUtil.CloseResources(conn, pstmt, rs);
        }

        owned=store.getOwned();
        System.out.println(store);
        return owned;
    }
}

