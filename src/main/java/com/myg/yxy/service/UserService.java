package com.myg.yxy.service;

import com.alibaba.fastjson.JSONObject;
import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.service.BaseService;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.model.rul.rulID;

import java.util.Map;

public interface UserService extends BaseService<UserModel> {

    Page<UserModel> getUserList(UserModel userModel, PaginationParam paginationParam);
    /**
     * @Author:henry
     * @Description:注册/登录
     *
     * @return
     */
    rulID getWxUserOpenid(UserModel code);

    /**
     * @Author:henry
     * @Description:map转换json
     *
     */
    Map<String, Object> parseJSON2Map(JSONObject json);

    int updateById(UserModel userModel);

}
