package com.myg.yxy.controller;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.security.token.SSOToken;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.component.db.enums.Direction;
import com.hxsoft.hsf.boot.component.db.model.OrderItem;
import com.hxsoft.hsf.boot.core.result.Response;
import com.hxsoft.hsf.boot.core.utils.ResponseUtil;
import com.myg.yxy.constant.PlatformCode;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/modifyPwd")
    public Response<Object> modifyPwd(HttpServletRequest request,String password) {
        SSOToken ssoToken = SSOHelper.getSSOToken(request);
        if (ssoToken == null){
            return ResponseUtil.fail(null,PlatformCode.NO_LOGIN);
        }

        UserModel userModel = userService.getModelById(Long.parseLong(ssoToken.getId()));
        if (userModel == null){
            return ResponseUtil.fail(null);
        }
        else{
            userModel.setPassword(password);
            userService.getUpdateById(userModel);
            return ResponseUtil.success(userModel);
        }
    }

    @RequestMapping(value = "/getUserInfo")
    public Response<Object> getUserInfo(HttpServletRequest request) {
        SSOToken ssoToken = SSOHelper.getSSOToken(request);
        if (ssoToken == null){
            return ResponseUtil.fail(null,PlatformCode.NO_LOGIN);
        }

        UserModel userModel = userService.getModelById(Long.parseLong(ssoToken.getId()));
//        UserModel userModel = userService.getModelById(Long.parseLong("2"));
        if (userModel == null){
            return ResponseUtil.fail(null);
        }
        else{
//            userModel.setRoleId(userModel.getUserName().toLowerCase().contains("admin") ? 1 : 0);
            return ResponseUtil.success(userModel);
        }
    }

    @RequestMapping(value = "/addUser")
    public Response<Object> addUser(UserModel userModel, HttpServletRequest request) {
        SSOToken ssoToken = SSOHelper.getSSOToken(request);
        if (ssoToken == null){
            return ResponseUtil.fail(null, PlatformCode.NO_LOGIN);
        }

        userModel.setCreateDate(new Date());
        userModel.setCreateUserId(Long.parseLong(ssoToken.getId()));
        if (userService.getInsert(userModel)){
            return ResponseUtil.success(null);
        }
        else{
            return ResponseUtil.fail(null);
        }
    }

    @RequestMapping(value = "/editUser")
    public Response<Object> editUser(UserModel userModel, HttpServletRequest request) {
        SSOToken ssoToken = SSOHelper.getSSOToken(request);
        if (ssoToken == null){
            return ResponseUtil.fail(null, PlatformCode.NO_LOGIN);
        }

        UserModel userModel1 = userService.getModelById(userModel.getId());
        if (userModel1 != null){
            userModel1.setUserName(userModel.getUserName());
//            userModel1.setUserNo(userModel.getUserNo());
            userModel1.setPassword(userModel.getPassword());
            userModel1.setEmail(userModel.getEmail());
            userModel1.setLinkMobile(userModel.getLinkMobile());
            userModel1.setLocked(userModel.getLocked());
            userModel1.setModifyDate(new Date());
            userModel1.setModifyUserId(Long.parseLong(ssoToken.getId()));
        }
        if (userService.getUpdateById(userModel1)){
            return ResponseUtil.success(null);
        }
        else{
            return ResponseUtil.fail(null);
        }
    }

    @RequestMapping(value = "/queryById")
    public Response<Object> queryById(Long id) {
        UserModel userModel = userService.getModelById(id);
        if (userModel != null){
            return ResponseUtil.success(userModel);
        }
        else{
            return ResponseUtil.fail(null);
        }
    }

    @RequestMapping(value = "/deleteById")
    public Response<Object> deleteById(Long[] ids) {
        try{
            if (ids != null && ids.length > 0){
                for (Long id : ids){
                    userService.getDeleteById(id);
                }
                return ResponseUtil.success(null);
            }
            else{
                return ResponseUtil.fail(null,"参数信息不完整");
            }
        }
        catch (Exception e){
            return ResponseUtil.fail(null);
        }
    }

//    @RequestMapping(value = "/getUserRole")
//    public Response<Object> getUserRole(HttpServletRequest request) {
//        SSOToken ssoToken = SSOHelper.getSSOToken(request);
//        return ResponseUtil.success(ssoToken.getRoleId());
//    }

    @RequestMapping(value = "/getUserList")
    public Response<Object> getUserList(Integer pageNo,Integer pageSize,String userName,String email,String linkMobile) {
        UserModel userModel = new UserModel();

        if (!StringUtils.isEmpty(userName)){
            userModel.setUserName(userName);
        }

        if (!StringUtils.isEmpty(email)){
            userModel.setUserName(email);
        }

        if (!StringUtils.isEmpty(linkMobile)){
            userModel.setLinkMobile(linkMobile);
        }

        PaginationParam paginationParam = PaginationParam.builder()
                .page(pageNo)
                .pageSize(pageSize)
                .addOrderItem(new OrderItem("id", Direction.DESC))
                .build();

        return ResponseUtil.success(userService.getUserList(userModel, paginationParam));
    }
}
