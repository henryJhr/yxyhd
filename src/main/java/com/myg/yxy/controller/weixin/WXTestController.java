package com.myg.yxy.controller.weixin;

import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.component.db.enums.Direction;
import com.hxsoft.hsf.boot.component.db.model.OrderItem;
import com.hxsoft.hsf.boot.core.result.Response;
import com.hxsoft.hsf.boot.core.utils.ResponseUtil;
import com.myg.yxy.bo.wx.ActivityListReqBO;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.TestModel;
import com.myg.yxy.service.ActivityService;

import com.myg.yxy.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx/api/test/")
public class WXTestController {

    @Autowired
    private TestService test;

    @RequestMapping(value = "/getTest")
    public Response<Object> getActivityList(@RequestBody TestModel testModel) {
        return ResponseUtil.success( test.test(testModel));
    }

}
