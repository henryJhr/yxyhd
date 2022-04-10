package com.myg.yxy.controller.weixin;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.security.token.SSOToken;
import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.component.db.enums.Direction;
import com.hxsoft.hsf.boot.component.db.model.OrderItem;
import com.hxsoft.hsf.boot.core.result.Response;
import com.hxsoft.hsf.boot.core.utils.ResponseUtil;
import com.myg.yxy.bo.wx.ActivityListReqBO;
import com.myg.yxy.bo.wx.LoginReqBO;
import com.myg.yxy.constant.PlatformCode;
import com.myg.yxy.model.ActivityModel;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/wx/api/login/")
public class WXLoginController {

    @Autowired
    private UserService userService;
    /**
     * 获取userid
     * @return
     */
    @RequestMapping(value = "/login")
    public Response<Object> getUserOpenid( UserModel code) {


        return ResponseUtil.success(userService.getWxUserOpenid(code));
    }
    /**
     * 储存用户信息
     * @return
     */
    @RequestMapping(value = "/putUser")
    public Response<Object> putUser( UserModel code) {


        return ResponseUtil.success(userService.updateById(code));
    }
}
