package com.lizhi.xiaohashu.user.biz.constant;

/**
 * @author: lizhi
 * @date: 2025.2.13
 * @version: v1.0.0
 * @description: TODO
 **/
public class RedisKeyConstants {


/**角色对应的权限集合key前缀*/
    private static final String  ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";

    //用户信息
    public static final String USER_INFO_KEY_PREFIX = "user:info:";


    /**
     * 小哈书全局 ID 生成器 KEY
     */
    public static final String XIAOHASHU_ID_GENERATOR_KEY = "xiaohashu.id.generator";
    /**
     * 用户角色数据 KEY 前缀
     */
    private static final String USER_ROLES_KEY_PREFIX = "user:roles:";
    public static String buildUserInfoKey(Long userId){
        return USER_INFO_KEY_PREFIX + userId;
    }
    /**
     * 角色集合 KEY
     * @param userId
     * @return
     */
    public static String buildUserRoleKey(Long userId) {
        return USER_ROLES_KEY_PREFIX + userId;}
    /**构建角色对应的权限集合*/
    public static String buildRolePermissionsKey(String roleKey) {
        return ROLE_PERMISSIONS_KEY_PREFIX + roleKey;//角色ID
    }
}
