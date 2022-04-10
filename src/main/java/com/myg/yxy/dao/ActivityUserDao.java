package com.myg.yxy.dao;

import com.hxsoft.hsf.boot.framework.dao.BaseDao;
import com.myg.yxy.model.ActivityUserModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: BusiActivityUserDao
 * @TableName: busi_activity_user
 * @Description: TODO
 * @Author: 刘振华
 * @Date: 2022-03-27 08:09:55
 */
@Mapper
public interface ActivityUserDao extends BaseDao<ActivityUserModel> {

}