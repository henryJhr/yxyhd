package com.myg.yxy.interceptor;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.security.token.SSOToken;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json;charset=utf-8");

//        SSOToken ssoToken = SSOHelper.getSSOToken(request);
//        if (ssoToken == null){
//            response.getWriter().write("{\"code\":\"1001\",\"msg\":\"用户未登录\",\"data\":{}}");
//            return false;
//        }
//
//        UserModel userModel = userService.getModelById(Long.parseLong(ssoToken.getId()));
//        if (userModel == null){
//            response.getWriter().write("{\"code\":\"1001\",\"msg\":\"用户未登录\",\"data\":{}}");
//            return false;
//        }
//
//        if (!userModel.getPassword().equals(ssoToken.getUserNo()) || userModel.getLocked() == 1){
//            response.getWriter().write("{\"code\":\"1001\",\"msg\":\"用户未登录\",\"data\":{}}");
//            return false;
//        }

        return true;
    }
}
