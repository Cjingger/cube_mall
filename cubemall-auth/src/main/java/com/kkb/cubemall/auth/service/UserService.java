package com.kkb.cubemall.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.auth.entity.UserEntity;
import com.kkb.cubemall.auth.pojo.UserDTO;

import java.util.Map;

/**
 * 会员
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-18 11:22:48
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * @Description: 登录认证的接口实现
     * @Author: 
     * @CreateDate: 2021/5/18 16:44
     * @UpdateUser: 
     * @UpdateDate: 2021/5/18 16:44
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    public R login(UserDTO userDTO);

    /**
     * @Description: 根据token查询用户身份信息
     * @Author: 
     * @CreateDate: 2021/5/19 18:27
     * @UpdateUser: 
     * @UpdateDate: 2021/5/19 18:27
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     * @url : http://localhost:8082/user/info/"+token
     */
    R userInfo(String token);
}

