//package com.myg.yxy.controller;
//
//import com.baomidou.kisso.SSOHelper;
//import com.baomidou.kisso.security.token.SSOToken;
//import com.hxsoft.hsf.boot.core.result.Response;
//import com.hxsoft.hsf.boot.core.utils.ResponseUtil;
//import com.myg.yxy.constant.PlatformCode;
//import com.myg.yxy.model.UserModel;
//import com.myg.yxy.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class LoginController {
//
//    @Autowired
//    private UserService userService;
//
//    @RequestMapping(value = "/logout")
//    public Response<Object> logout(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            SSOHelper.clearLogin(request,response);
//            return ResponseUtil.success(null);
//        }catch (Exception e){
//            return ResponseUtil.fail(null);
//        }
//    }
//
//    @RequestMapping(value = "/login")
//    public Response<Object> login(String userName, String password, HttpServletRequest request, HttpServletResponse response) {
//        UserModel userModel = new UserModel();
//        userModel.setUserName(userName);
//
//        UserModel result = userService.getModelBy(userModel);
//        if (result != null){
//            if (result.getLocked() == 1){
//                return ResponseUtil.fail(null, PlatformCode.USER_LOCKED);
//            }
//
//            if (result.getPassword().equals(password)){
//                SSOToken ssoToken = SSOToken.create().setIp(request)
//                        .setId(result.getId())
//                        .setRealName(result.getUserName())
//                        .setUserNo(result.getPassword())
//                        .setRoleId(result.getUserName().toLowerCase().contains("admin") ? 1 : 0);
//                SSOHelper.setCookie(request,response,ssoToken,true);
//
//                return ResponseUtil.success(null);
//            }
//            else{
//                return ResponseUtil.fail(null,PlatformCode.PASSWORD_ERROR);
//            }
//        }
//        else{
//            return ResponseUtil.fail(null,PlatformCode.NO_USER_EXIST);
//        }
//    }
//}
