package com.kkb.cubemall.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.UserLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-18 11:22:48
 */
public interface UserLoginLogService extends IService<UserLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

