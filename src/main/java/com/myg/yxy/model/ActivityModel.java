package com.myg.yxy.model;

import com.hxsoft.hsf.boot.framework.model.BaseModel;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName: ActivityPo
 * @TableName: busi_activity
 * @Description: TODO
 * @Author: 刘振华
 * @Date: 2022-03-20 08:40:04
 */
@Data
public class ActivityModel extends BaseModel {

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
	private Date time;

    /**
     * 活动结束时间
     */
    private Date endTime;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 报名人数
	 */
	private Integer applyNumber;
	/**
	 * 主持人
	 */
	private String compere;
	/**
	 * 手机号
	 */
	private String linkMobile;
	/**
	 * 封面图
	 */
	private String coverImg;
	/**
	 * 活动描述
	 */
	private String description;
	/**
	 * 活动状态
	 */
	private Integer status;

	/**
	 * 报名状态
	 */
	private Integer applyStatus;

}
