package com.myg.yxy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hxsoft.hsf.boot.component.db.Page;
import com.hxsoft.hsf.boot.component.db.PaginationParam;
import com.hxsoft.hsf.boot.framework.service.impl.BaseServiceImpl;
import com.myg.yxy.dao.UserDao;
import com.myg.yxy.model.UserModel;
import com.myg.yxy.model.rul.rulID;
import com.myg.yxy.service.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserModel> implements UserService {

    @Autowired
    private UserDao userDao;

    public Page<UserModel> getUserList(UserModel userModel, PaginationParam paginationParam) {
        return Page.of(userDao.getListPage(userModel,paginationParam));
    }

    @Override
    public rulID getWxUserOpenid(UserModel code) {
        //拼接url
        StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");
        url.append("appid=");//appid设置
        url.append("wx1e18988314ed68e5");
        url.append("&secret=");//secret设置
        url.append("75ed996c006c5ecd573e3a993dc02250");
        url.append("&js_code=");//code设置
        url.append(code.getConfigName());
        url.append("&grant_type=authorization_code");
        Map<String, Object> map = null;
        System.out.println(url.toString());
        try {
            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            System.out.println(content);//打印返回的信息
            JSONObject res = JSONObject.parseObject(content);//把信息封装为json
            //把信息封装到map
            map = parseJSON2Map(res);//这个小工具的代码在下面
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(map.get("openid"));
        System.out.println(map.toString());
        code.setConfigName(map.get("openid").toString());

        rulID rul = new rulID();
        System.out.println(code);
        if(userDao.checkById(code) > 0){
            rul.setUserid(userDao.getUserId(code));
            return rul;
        }else {
            userDao.insert(code);
        }
        rul.setUserid(userDao.getUserId(code));
        return rul;
    }

    @Override
    public Map<String, Object> parseJSON2Map(JSONObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 最外层解析
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是数组的话，继续解析
            if (v instanceof JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                @SuppressWarnings("unchecked")
                Iterator<Object> it = ((JSONArray) v).iterator();
                while (it.hasNext()) {
                    JSONObject json2 = (JSONObject) it.next();
                    list.add(parseJSON2Map(json2));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    @Override
    public int updateById(UserModel userModel) {


        return  userDao.updateById(userModel);
    }
}
