package com.myg.yxy.bo.wx;

import lombok.Data;

/**
 * @Classname ActivityListRspBO
 * @Description TODO
 * @Date 2022/3/20 21:53
 * @Created 刘振华
 */
@Data
public class ActivityListRspBO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 时间
     */
    private String time;
    /**
     * 地址
     */
    private String address;
    /**
     * 主持人
     */
    private String compere;
    /**
     * 封面图
     */
    private String coverImg;

}
