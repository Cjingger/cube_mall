package com.kkb.cubemall.auth.dao;

import com.kkb.cubemall.auth.entity.UserLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author it
 * @email it@gmail.com
 * @date 2021-05-18 11:22:48
 */
@Mapper
public interface UserLevelDao extends BaseMapper<UserLevelEntity> {
	
}
