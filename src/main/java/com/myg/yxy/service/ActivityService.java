package com.myg.yxy.service;

import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.service.BaseService;
import com.myg.yxy.bo.wx.ActivityApplyReqBO;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.UserModel;
import java.util.List;

public interface ActivityService extends BaseService<ActivityModel> {

    Page<ActivityModel> getActivityList(ActivityModel activityModel, PaginationParam paginationParam);


}
