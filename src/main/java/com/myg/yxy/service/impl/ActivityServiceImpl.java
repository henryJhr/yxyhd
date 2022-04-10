package com.myg.yxy.service.impl;

import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.service.impl.BaseServiceImpl;
import com.myg.yxy.bo.wx.ActivityApplyReqBO;
import com.myg.yxy.dao.ActivityDao;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl extends BaseServiceImpl<ActivityDao, ActivityModel> implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Override
    public Page<ActivityModel> getActivityList(ActivityModel activityModel, PaginationParam paginationParam) {
        return Page.of(activityDao.getActivityListPage(activityModel,paginationParam));
    }
}
