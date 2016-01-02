package com.ourclother.lab.dao;

/**
 * Created by Administrator on 2015/4/24.
 */
public interface StoreDao {
    /**
     * 判断shopId是否存在
     * @param id
     * @return boolean
     */
    boolean isStoreIdExists(String id);
    /**
     * 返回图片
     * @param clothId
     * @return User
     */
    String getStoreClothById(String clothId);
}
