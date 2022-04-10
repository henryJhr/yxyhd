package com.myg.yxy.controller.weixin;

import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.component.db.enums.Direction;
import com.hxsoft.hsf.boot.component.db.model.OrderItem;
import com.hxsoft.hsf.boot.core.result.Response;
import com.hxsoft.hsf.boot.core.utils.ResponseUtil;
import com.myg.yxy.bo.wx.ActivityByIdReqBO;
import com.myg.yxy.bo.wx.ActivityListReqBO;
import com.myg.yxy.bo.wx.ActivityApplyReqBO;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.ActivityUserModel;
import com.myg.yxy.service.ActivityService;
import com.myg.yxy.service.ActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/wx/api/activity/")
public class WXActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityUserService activityUserService;

    @RequestMapping(value = "/getActivityById")
    public Response<Object> getActivityById(@RequestBody ActivityByIdReqBO activityByIdReqBO) {
        ActivityModel activityModel = activityService.getModelById(activityByIdReqBO.getActivityId());

        //查看是否已报名
        ActivityUserModel activityUserModelBy = new ActivityUserModel();
        if (activityByIdReqBO.getUserId() != null){
            activityUserModelBy.setUserId(activityByIdReqBO.getUserId());
            activityUserModelBy.setActivityId(activityByIdReqBO.getActivityId());

            ActivityUserModel activityUserModel = activityUserService.getModelBy(activityUserModelBy);
            if (activityUserModel != null && activityUserModel.getStatus() != null && activityUserModel.getStatus() ==1){
                activityModel.setApplyStatus(1);
            }
            else{
                activityModel.setApplyStatus(0);
            }
        }
        else{
            activityModel.setApplyStatus(0);
        }

        //已过期
        if (activityModel.getStatus() == 2){
            activityModel.setApplyStatus(2);
        }

        return ResponseUtil.success(activityModel);
    }

    @RequestMapping(value = "/getActivityList")
    public Response<Object> getActivityList(@RequestBody ActivityListReqBO activityListReqBO) {
        ActivityModel activityModel = new ActivityModel();

        activityModel.setStatus(activityListReqBO.getStatus());

        PaginationParam paginationParam = PaginationParam.builder()
                .page(activityListReqBO.getPage())
                .pageSize(activityListReqBO.getPageSize())
                .addOrderItem(new OrderItem("time", Direction.ASC))
                .build();

        Page<ActivityModel> page = activityService.getActivityList(activityModel,paginationParam);

        return ResponseUtil.success(page);
    }

    /**
     * 活动报名
     * @return
     */
    @RequestMapping(value="/getActivityApply",method = RequestMethod.POST)
    @ResponseBody
    public Response<Object> getActivityApply(@RequestBody ActivityApplyReqBO activityApplyReqBO) {
        ActivityModel activityModel = activityService.getModelById(activityApplyReqBO.getActivityId());
        if (activityModel != null){
            //查看是否已报名
            ActivityUserModel activityUserModelBy = new ActivityUserModel();
            activityUserModelBy.setUserId(activityApplyReqBO.getUserId());
            activityUserModelBy.setActivityId(activityApplyReqBO.getActivityId());

            ActivityUserModel activityUserModel = activityUserService.getModelBy(activityUserModelBy);
            if (activityUserModel == null || activityUserModel.getStatus() ==0){
                activityModel.setApplyNumber(activityModel.getApplyNumber() + 1);

                activityService.getUpdateById(activityModel);

                activityUserModel = new ActivityUserModel();
                activityUserModel.setActivityId(activityApplyReqBO.getActivityId());
                activityUserModel.setUserId(activityApplyReqBO.getUserId());
                activityUserModel.setCreateDate(new Date());
                activityUserModel.setStatus(1);

                activityUserService.getInsert(activityUserModel);

                return ResponseUtil.success(null);
            }
            else{
                return ResponseUtil.fail(null);
            }
        }
        else{
            return ResponseUtil.fail(null);
        }
    }



}
