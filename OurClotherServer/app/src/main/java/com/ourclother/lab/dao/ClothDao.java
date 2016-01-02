package com.ourclother.lab.dao;
import com.ourclother.lab.model.Cloth;

/**
 * Created by Administrator on 2015/3/15.
 */
public interface ClothDao {
    /**
     * 判断衣服Id是否存在
     * @param id
     * @return boolean
     */
    boolean isClothIdExists(int id);
     /**
     * 返回图片
     * @param clothId
     * @return Clothinfostring
     */
    String getClothById(int clothId);
    /**
     * 返回图片
     * @param none
     * @return Cloth Ids
     */
    String getAllCloth();

}
