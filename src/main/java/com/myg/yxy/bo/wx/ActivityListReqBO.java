package com.myg.yxy.bo.wx;

import com.myg.yxy.bo.BaseReqPageBO;
import lombok.Data;

/**
 * @Classname ActivityListReqBO
 * @Description TODO
 * @Date 2022/3/20 21:53
 * @Created 刘振华
 */
@Data
public class ActivityListReqBO extends BaseReqPageBO {

    private Integer status;
    private Integer openid;

}
