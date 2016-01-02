package com.ourclother.lab.dao;

import com.ourclother.lab.model.User;
/**
 * Created by Administrator on 2015/2/11.
 */
public interface UserDao {
    /**
     * 判断登陆账号是否存在
     * @param loginid
     * @return boolean
     */
    boolean isLoginIdExists(String loginid);

    /**
     * 判断邮箱是否存在
     * @param email
     * @return boolean
     */
    boolean isEmailExists(String email);

    /**
     * 注册用户
     * @param u
     * @return int
     */
    int addUsers(User u);

    /**
     * 登陆
     * @param loginid
     * @param password
     * @return User
     */
    User getUserByIdAndPwd(String loginid, String password);

    /**
     * 判断邮箱是否存在
     * @param loginid
     * @param email
     * @return boolean
     */
    boolean isEmailExists(User u);

    /**
     * 修改用户
     * @param u
     * @return int
     */
    int modifyUserByLoginid(User u);
    /**
     * 添加用户身材数据
     * @param u
     * @return int
     */
    int addBodyDataByLoginid(User u);

    /**
     * 判断原密码是否错误
     * @param loginid
     * @param oldpwd
     * @return
     */
    boolean isOldPasswordError(String loginid, String oldpwd);

    /**
     * 修改密码
     * @param loginid
     * @param newpwd
     * @return
     */
    int modifyUserPassword(String loginid, String newpwd);
    /**
     * 获得用户收藏列表
     * @param loginid
     * @return
     */
    String getClothCollect(String loginid);
    /**
     * 用户收藏衣服
     * @param clothId
     * @return
     */
    int addCollectCloth(String clothId,String loginid);
    /**
     * 用户取消收藏衣服
     * @param clothId
     * @return
     */
    int deleteCollectCloth(String clothId,String loginid);
}
