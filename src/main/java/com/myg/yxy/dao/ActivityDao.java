package com.myg.yxy.dao;

import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.dao.BaseDao;
import com.myg.yxy.model.ActivityModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName: ActivityDao
 * @TableName: busi_activity
 * @Description: TODO
 * @Author: 刘振华
 * @Date: 2022-03-20 08:40:04
 */
@Mapper
public interface ActivityDao extends BaseDao<ActivityModel> {

    List<ActivityModel> getActivityListPage(ActivityModel activityModel, PaginationParam paginationParam);
    int getUpdateById(ActivityModel activityModel);
    List<ActivityModel> getList(ActivityModel activityModel);
}
